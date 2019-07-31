package au.gov.api.config

import com.beust.klaxon.*


data class Configuration(var data:Map<String,Map<String,String>>)

class Config{
    companion object {

        @JvmStatic val DATA_URI = "https://raw.githubusercontent.com/apigovau/config/master/src/main/resources/config.route.json"

        @JvmStatic var configCache = ResourceCache<Configuration>(NaiveAPICaller(), 60, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })



        fun get(key:String):String{

            // allow overrides of config with env of 'apigov.config.BaseRepoURI' or similar
            // these need to be fully qualified
            // otherwise the env options specified by config_environment will be used
            val overriden = Systen.getenv("apigov.config.${key}")
            if(overriden != null) return overriden

            val environment = System.getenv("config_environment")?: throw RuntimeException("No environment variable: 'config_environment'")
            return get(environment, key)


        }


        fun get(env:String, key:String):String{

            if(configCache.isEmpty()) loadCache()


            val configuration = configCache.get(DATA_URI)
            if(env !in configuration.data) throw RuntimeException("No configuration value for '$env'+'$key' found @ '$DATA_URI'")
            if(key !in configuration.data[env]!!) throw RuntimeException("No configuration value for '$env'+'$key' found @ '$DATA_URI'")

            val base =  configuration.data[env]!!["Base"]!!
            return if(key != "Base"){base}else{""} + configuration.data[env]!![key]!!
        }

        private fun loadCache(){
            val hardcodedJson = this::class.java.classLoader.getResource("config.route.json").readText()
            val hardcodedConfiguration = Klaxon().parse<Configuration>(hardcodedJson)!!
            configCache.load(DATA_URI, hardcodedConfiguration)
        }

    }
}
