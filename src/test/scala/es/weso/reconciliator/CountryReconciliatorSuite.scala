package es.weso.reconciliator

import org.scalatest.BeforeAndAfter
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.io.FileNotFoundException
import org.apache.lucene.search.Query
import es.weso.reconciliator.results.CountryResult
import org.apache.lucene.document.Document
import es.weso.reconciliator.results.NullCountryResult

@RunWith(classOf[JUnitRunner])
class CountryReconciliatorSuite extends FunSuite with BeforeAndAfter 
	with ShouldMatchers{
  
  var reconciliator : CountryReconciliator = null
  
  before{
    reconciliator = new CountryReconciliator("files/countries.json", true)
  }
  
  test("Try to obtain file path from null parameter") {
    intercept[IllegalArgumentException] {
      reconciliator.getFilePath(null, true)
    }
  }
  
  test("Try to obtain file path from a non-existing resource") {
    intercept[FileNotFoundException] {
      reconciliator.getFilePath("countries.json", true)
    }
  }
  
  test("Obtain file path from an absolute route") {
    val expected : String = "countries.json"
    val result : String = reconciliator.getFilePath("countries.json", false)
    result should be (expected)
  }
  
  test("Obtain file path from a relative route") {
    val expected = getClass.getClassLoader.getResource("files/countries.json")
    		.getPath()
    val result = reconciliator.getFilePath("files/countries.json", true)
    result should be (expected)
  }
  
  test("Try to load countries information from null path") {
    intercept[IllegalArgumentException] {
      reconciliator.load(null)
    }
  }
  
  test("Try to load countries information from non-existing file") {
    intercept[FileNotFoundException] {
      reconciliator.load("files/countries.json")
    }
  }
  
  test("Load correctly countries infomation") {
    val path : String = getClass.getClassLoader
		.getResource("files/countries.json").getPath()
	reconciliator.load(path)
  }
  
  test("Try to load countries information from non json file") {
    val path : String = getClass.getClassLoader
		.getResource("es/weso/reconciliator/CountryReconciliator.feature")
		.getPath()
	intercept[IllegalArgumentException] {
      reconciliator.load(path)
    }
  }
  
  test("Create Lucene query from string with only one token") {
    val textQuery : String = "test"
    val expected : String = "altName:test~2"
    val result : Query = reconciliator.createQueryFromString(textQuery)
    result.toString() should be (expected)
  }
  
  test("Create Lucene query from string with more than one token") {
    val textQuery : String = "junit test"
    val expected : String = "altName:junit~2 altName:test~2"
    val result : Query = reconciliator.createQueryFromString(textQuery) 
    result.toString() should be (expected)
  }
  
  test("Search a country that exist in The Web Index"){
    val expected : String = "Occupied Palestinian Territory"
    val result : String = reconciliator.searchCountry("Palestine")
    result should be (expected)
  }
  
  test("Search a country that is no present in The Web Index") {
    val result = reconciliator.searchCountry("testing")
    result should be (NullCountryResult.iso3Code)
  }
  
  test("Obtain all information about a country present in The Web Index") {
    val expectedIso2 : String = "LA"
    val expectedIso3 : String = "LAO"
    val expectedName : String = "Lao People's Democratic Republic"
    val result : CountryResult = reconciliator.searchCountryResult("Lao")
    result.iso2Code should be (expectedIso2)
    result.iso3Code should be (expectedIso3)
    result.webIndexName should be (expectedName)
  }
  
  test("Try to obtain all information about a country that is no present in The Web Index") {
      val result = reconciliator.searchCountryResult("London")
      result should be (NullCountryResult)
  }
  
  test("Obtain the lucene Document that contains all information about a country") {
    val nameExpected : String = "Libyan Arab Jamahiriya"
    val result : Document = reconciliator.search("Libya")
    result should not be (null)
    result.getField("wiName").stringValue() should be (nameExpected)
  }
  
  test("Try to obtain the lucene Document that contains all information about a country") {
      val result = reconciliator.search("oviedo")
      result should be (null)
  }

}