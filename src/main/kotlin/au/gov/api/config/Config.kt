package au.gov.api.config

import com.beust.klaxon.*


data class Configuration(var data:Map<String,Map<String,String>>)

class Config{
    companion object {

        @JvmStatic val DATA_URI = "https://raw.githubusercontent.com/apigovau/config/master/src/main/resources/config.route.json"

        @JvmStatic var configCache = ResourceCache<Configuration>(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })


        fun get(env:String, key:String):String{

            if(configCache.isEmpty()) loadCache()


            val configuration = configCache.get(DATA_URI)
            if(env !in configuration.data) throw RuntimeException("No configuration value for '$env'+'$key' found @ '$DATA_URI'")
            if(key !in configuration.data[env]!!) throw RuntimeException("No configuration value for '$env'+'$key' found @ '$DATA_URI'")
            return configuration.data[env]!![key]!!
        }

        private fun loadCache(){
            val hardcodedJson = this::class.java.classLoader.getResource("config.route.json").readText()
            val hardcodedConfiguration = Klaxon().parse<Configuration>(hardcodedJson)!!
            configCache.load(DATA_URI, hardcodedConfiguration)
        }

    }
}
