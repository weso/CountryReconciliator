package es.weso.reconciliator

import org.scalatest.Matchers
import cucumber.api.scala.EN
import cucumber.api.scala.ScalaDsl
import es.weso.reconciliator.results.CountryResult

class CountryReconciliatorSteps extends ScalaDsl with EN with Matchers{

  var reconciliator : CountryReconciliator = null
  var result : Option[String] = None
  var resultObj : Option[CountryResult] = None
  
  Given("""I want to load names, iso-codes and alternative names for all countries presents in WebIndex$""") { () =>
    reconciliator = new CountryReconciliator("files/countries.json", true)
  }
  
  When("""I check the country with the name "([^"]*)"$"""){(searchName : String)=>
    result = reconciliator.searchCountry(searchName)
  }
  
  When("""I check the country object with the name "([^"]*)"$"""){(searchName : String) =>
    resultObj = reconciliator.searchCountryResult(searchName)
  }
  
  Then("""The name according Web Index should be "([^"]*)"$"""){(wiName : String) => 
  	wiName should be (result)
  }
  
  Then("""The iso-2 code should be "([^"]*)"$"""){(iso2Code : String) =>
    resultObj.get.iso2Code should be (iso2Code)
  }
  
  And("""The iso-3 code should be "([^"]*)"$"""){(iso3Code : String) =>
    resultObj.get.iso3Code should be (iso3Code)
  }
  
  Then("""The iso-2 code should not be "([^"]*)"$"""){(iso2Code : String) => 
    resultObj.get.iso2Code should not be (iso2Code)
  }
  
  And("""The iso-3 code should not be "([^"]*)"$"""){(iso3Code : String) => 
    resultObj.get.iso3Code should not be (iso3Code)  
  }
  
  Then("""The name according Web Index should not be "([^"]*)"$"""){(wiName : String) => 
  	wiName should not be (result)
  }
  
}