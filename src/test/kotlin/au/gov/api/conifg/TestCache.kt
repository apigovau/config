package au.gov.api.config

import au.gov.api.config.ResourceCache
import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test
class TestCache {

val json = """
{
    "data":{
       "y.cld.gov.au":{
         "AuthURI":"https://api-gov-au.apps.y.cld.gov.au/keys/producer/",
         "LogURI":"https://api-gov-au.apps.y.cld.gov.au/logs/api/",
         "BaseRepoURI":"https://api-gov-au.apps.y.cld.gov.au/repository/",
         "Base":"https://api-gov-au.apps.y.cld.gov.au/",
         "DefinitionsBase":"https://api-gov-au.apps.y.cld.gov.au/definitions/"
       },
        "staging.api.gov.au":{
         "AuthURI":"https://staging.api.gov.au/keys/producer/",
         "LogURI":"https://staging.api.gov.au/logs/api/",
         "BaseRepoURI":"https://staging.api.gov.au/repository/",
         "Base":"https://staging.api-gov-au.apps.y.cld.gov.au/",
         "DefinitionsBase":"https://staging.api-gov-au.apps.y.cld.gov.au/definitions/"
       },
       "api.gov.au":{
         "AuthURI":"https://api.gov.au/keys/producer/",
         "LogURI":"https://api.gov.au/logs/api/",
         "BaseRepoURI":"https://api.gov.au/repository/",
         "Base":"https://api.gov.au/",
         "DefinitionsBase":"https://api.gov.au/definitions/"
       },
       "local":{
         "AuthURI":"http://localhost:5000/keys/producer/",
         "LogURI":"http://localhost:5000/logs/api/",
         "BaseRepoURI":"http://localhost:5000/repository/",
         "Base":"http://localhost:5000/",
         "DefinitionsBase":"http://localhost:5000/definitions/"
       }
    }
}
"""

    @Test
    fun can_deserialise_config_json(){


        val config = Klaxon().parse<Configuration>(json)!!
        Assert.assertTrue("y.cld.gov.au" in config.data)
        Assert.assertTrue("staging.api.gov.au" in config.data)
        Assert.assertTrue("api.gov.au" in config.data)
        Assert.assertTrue("local" in config.data)
        Assert.assertEquals("https://api-gov-au.apps.y.cld.gov.au/keys/producer/", config.data["y.cld.gov.au"]!!["AuthURI"])


    }


    @Test
    fun test_mocked_config(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = json 

        Assert.assertEquals("https://api.gov.au/",Config.get("api.gov.au","Base"))
    }

    @Test
    fun test_defaults_to_hardcoded_values(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        //fetcher.map[Config.DATA_URI] = "" 

        Assert.assertEquals("https://api.gov.au/test",Config.get("api.gov.au","Base"))
    }


    @Test
    fun test_will_use_canned_if_malformed_config(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = "[}" 

        Assert.assertEquals("https://api.gov.au/test",Config.get("api.gov.au","Base"))
    }


    @Test
    fun test_use_config_environment(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = "[}" 

        Assert.assertEquals("https://api.gov.au/test",Config.get("Base"))
    }
}

