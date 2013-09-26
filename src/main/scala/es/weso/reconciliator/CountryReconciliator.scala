package es.weso.reconciliator

import java.io.FileNotFoundException
import scala.io.Source
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONType
import org.apache.log4j.Logger
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexDeletionPolicy
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import es.weso.reconciliator.results.CountryResult
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.index.Term
import es.weso.reconciliator.results.NullCountryResult

class CountryReconciliator (path : String, relativePath : Boolean){
  
  private val COUNTRY_WINAME_FIELD = "wiName"
  private val COUNTRY_ISO2_CODE_FIELD = "iso2"
  private val COUNTRY_ISO3_CODE_FIELD = "iso3"
  private val COUNTRY_ALT_NAME = "altName"
  private val MAX_RESULTS = 1
  private val idx : RAMDirectory = new RAMDirectory
  private val analyzer : CountryAnalyzer = new CountryAnalyzer
  private val logger : Logger = Logger.getLogger(this.getClass())
   
  load(getFilePath(path, relativePath))
   
  def getFilePath(path : String, relativePath : Boolean) : String = {
     if(path == null) {
      throw new IllegalArgumentException("Path cannot be null")
    }
    if(relativePath) {
      val resource = getClass.getClassLoader().getResource(path)
      if(resource == null)
        throw new FileNotFoundException("File especifies does not exist")
      resource.getPath()
    } else
      path
   }
  
  private val indexSearcher : IndexSearcher = new IndexSearcher(
      DirectoryReader.open(idx))
  
  def load(path : String) = {
    if(path == null) {
      throw new IllegalArgumentException("The path cannot be null")
    }
    val deletionPolicy : IndexDeletionPolicy = new KeepOnlyLastCommitDeletionPolicy
    val indexConfiguration : IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, new CountryAnalyzer)
   indexConfiguration.setIndexDeletionPolicy(deletionPolicy)
    val indexWriter : IndexWriter = new IndexWriter(idx, indexConfiguration) 
    val textSource = Source.fromFile(path, "UTF-8")
    val textContent = textSource.mkString("")
    textSource.close
    val json = JSON.parseFull(textContent).getOrElse(
        throw new IllegalArgumentException("File specified is not a json file"))
    val map = json.asInstanceOf[Map[Any, Any]]
    val countries = map.get("countries").getOrElse(
        throw new IllegalArgumentException("Invalid format to json file"))
        .asInstanceOf[List[Map[Any, Any]]]
    countries.foreach(country => {
      val doc : Document = new Document
      val wiName = country.get("webIndexName").getOrElse(
          throw new IllegalArgumentException).asInstanceOf[String]
      val iso2 = country.get("iso-2").getOrElse(
          throw new IllegalArgumentException).asInstanceOf[String]
      val iso3 = country.get("iso-3").getOrElse(
          throw new IllegalArgumentException).asInstanceOf[String]
      val names = country.get("names").getOrElse(
          throw new IllegalArgumentException).asInstanceOf[List[String]]
      val iso2Field : Field = new TextField(COUNTRY_ISO2_CODE_FIELD, iso2, 
          Field.Store.YES)
      val iso3Field : Field = new TextField(COUNTRY_ISO3_CODE_FIELD, iso3, 
          Field.Store.YES)
      val wiNameField : Field = new TextField(COUNTRY_WINAME_FIELD, wiName, 
          Field.Store.YES)
      doc.add(iso2Field)
      doc.add(iso3Field)
      doc.add(wiNameField)
      var altNames : String = "";
      names.foreach(name => {
        altNames += name + "; "
      })
      val altNamesField : Field = new TextField(COUNTRY_ALT_NAME, altNames, 
          Field.Store.YES)
      doc.add(altNamesField)
      logger.debug("Indexing country with name " + wiName)
      indexWriter.addDocument(doc)
    })
    indexWriter.close
  }
  
  def searchCountry(name : String) : String = {
    val doc = search(name)
    if(doc != null)
    	doc.getField(COUNTRY_WINAME_FIELD).stringValue()
	else 
	  null
  }
  
  def searchCountryResult(name : String) : CountryResult = {
    val doc = search(name)
    if(doc != null) {
      val wiName = doc.getField(COUNTRY_WINAME_FIELD).stringValue()
	  val iso2Code = doc.getField(COUNTRY_ISO2_CODE_FIELD).stringValue()
	  val iso3Code = doc.getField(COUNTRY_ISO3_CODE_FIELD).stringValue()
	  CountryResult(wiName, iso2Code, iso3Code)
    } else {
      NullCountryResult
    }
  }

  private[reconciliator] def createQueryFromString(query : String) : Query = {
    val parser : QueryParser = new QueryParser(Version.LUCENE_40,
        COUNTRY_ALT_NAME, analyzer)
    logger.debug("QUERY: " + query)
    var strQuery : String = ""
    query.replace("-", " ").split(" ").foreach(part => {
      logger.debug("PART: " + part)
      strQuery  += part + " OR "
      logger.debug("PART QUERY: " + strQuery)
    })
    strQuery = strQuery.replace(".", "").replace("(", "").replace(")", "")
    logger.debug("Fuzzy Query: " + strQuery)
    if(strQuery.contains("OR"))
    	strQuery = strQuery.substring(0, strQuery.lastIndexOf("OR")) 
    parser.parse(strQuery)
  }
  
  private[reconciliator] def search(name: String): Document = {
     var collector = TopScoreDocCollector.create(MAX_RESULTS, true)
     val query = createQueryFromString(name)
     logger.debug(query.toString())
     indexSearcher.search(query, collector)
     logger.debug("Searching country from given string " + name)
     val scoreDocs : Array[ScoreDoc] = collector.topDocs().scoreDocs
     if(scoreDocs.size == 0) {
       null
     } else {
       val doc : Document = indexSearcher.doc(scoreDocs.head.doc)
	   doc
     }
   }

}