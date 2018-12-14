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
         "Base":"https://api-gov-au.apps.y.cld.gov.au/",
         "AuthURI":"keys/producer/",
         "LogURI":"logs/api/",
         "BaseRepoURI":"repository/",
         "DefinitionsBase":"definitions/"
       },
        "staging.api.gov.au":{
         "Base":"https://staging.api-gov-au.apps.y.cld.gov.au/",
         "AuthURI":"keys/producer/",
         "LogURI":"logs/api/",
         "BaseRepoURI":"repository/",
         "DefinitionsBase":"definitions/"
       },
       "api.gov.au":{
         "Base":"https://api.gov.au/",
         "AuthURI":"keys/producer/",
         "LogURI":"logs/api/",
         "BaseRepoURI":"repository/",
         "DefinitionsBase":"definitions/"
       },
       "local":{
         "Base":"http://localhost:5000/",
         "AuthURI":"keys/producer/",
         "LogURI":"logs/api/",
         "BaseRepoURI":"repository/",
         "DefinitionsBase":"definitions/"
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
        Assert.assertEquals("keys/producer/", config.data["y.cld.gov.au"]!!["AuthURI"])


    }


    @Test
    fun test_mocked_config(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = json 

        Assert.assertEquals("https://api.gov.au/",Config.get("api.gov.au","Base"))
        Assert.assertEquals("https://api.gov.au/logs/api/",Config.get("api.gov.au","LogURI"))
    }

    @Test
    fun test_defaults_to_hardcoded_values(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        //fetcher.map[Config.DATA_URI] = "" 

        Assert.assertEquals("https://api.gov.au/test/",Config.get("api.gov.au","Base"))
        Assert.assertEquals("https://api.gov.au/test/logs/api/",Config.get("api.gov.au","LogURI"))
    }


    @Test
    fun test_will_use_canned_if_malformed_config(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = "[}" 

        Assert.assertEquals("https://api.gov.au/test/",Config.get("api.gov.au","Base"))
    }


    @Test
    fun test_use_config_environment(){
        var fetcher = MockURIFetcher()
        var configCache = ResourceCache<Configuration>(fetcher, 5, convert = { serial -> Klaxon().parse<Configuration>(serial)!! })
        Config.configCache = configCache
        fetcher.map[Config.DATA_URI] = "[}" 

        Assert.assertEquals("https://api.gov.au/test/",Config.get("Base"))
    }
}

