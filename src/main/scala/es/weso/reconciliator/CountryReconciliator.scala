package es.weso.reconciliator

import java.io.FileNotFoundException

import scala.io.Source
import scala.util.parsing.json.JSON

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
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import es.weso.reconciliator.results.CountryResult
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.index.Term

class CountryReconciliator(path: String, relativePath: Boolean) {

  import CountryReconciliator._

  private val idx: RAMDirectory = new RAMDirectory
  private val analyzer: CountryAnalyzer = new CountryAnalyzer

  load(getFilePath(path, relativePath))

  def getFilePath(path: String, relativePath: Boolean): String = {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null")
    }
    if (relativePath) {
      val resource = getClass.getClassLoader().getResource(path)
      if (resource == null)
        throw new FileNotFoundException("File especifies does not exist")
      resource.getPath()
    } else
      path
  }

  private val indexSearcher: IndexSearcher = new IndexSearcher(
    DirectoryReader.open(idx))

  private def load(path: String) = {
    val deletionPolicy: IndexDeletionPolicy = new KeepOnlyLastCommitDeletionPolicy
    val indexConfiguration: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, new CountryAnalyzer)
    indexConfiguration.setIndexDeletionPolicy(deletionPolicy)
    val indexWriter: IndexWriter = new IndexWriter(idx, indexConfiguration)
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
      val doc: Document = new Document
      val wiName = country.get("webIndexName").getOrElse(
        throw new IllegalArgumentException).asInstanceOf[String]
      val iso2 = country.get("iso-2").getOrElse(
        throw new IllegalArgumentException).asInstanceOf[String]
      val iso3 = country.get("iso-3").getOrElse(
        throw new IllegalArgumentException).asInstanceOf[String]
      val names = country.get("names").getOrElse(
        throw new IllegalArgumentException).asInstanceOf[List[String]]
      val iso2Field: Field = new TextField(CountryIso2CodeField, iso2,
        Field.Store.YES)
      val iso3Field: Field = new TextField(CountryIso3CodeField, iso3,
        Field.Store.YES)
      val wiNameField: Field = new TextField(CountryWinameField, wiName,
        Field.Store.YES)
      doc.add(iso2Field)
      doc.add(iso3Field)
      doc.add(wiNameField)
      val altNames = new StringBuilder()
      names.foreach(name => {
        altNames.append(name).append("; ")
      })
      val altNamesField: Field = new TextField(CountryAltName, altNames.toString, Field.Store.YES)
      doc.add(altNamesField)
      logger.debug("Indexing country with name " + wiName)
      indexWriter.addDocument(doc)
    })
    indexWriter.close
  }

  def searchCountry(name: String): Option[String] = {
    val document = search(name)
    document match {
      case Some(doc) => Some(doc.getField(CountryWinameField).stringValue())
      case None => None
    }
  }

  def searchCountryResult(name: String): Option[CountryResult] = {
    val document = search(name)
    document match {
      case Some(doc) =>
        val wiName = doc.getField(CountryWinameField).stringValue()
        val iso2Code = doc.getField(CountryIso2CodeField).stringValue()
        val iso3Code = doc.getField(CountryIso3CodeField).stringValue()
        Some(CountryResult(wiName, iso2Code, iso3Code))
      case None => None
    }
  }

  private[reconciliator] def createQueryFromString(query: String): Query = {
    val parser: QueryParser = new QueryParser(Version.LUCENE_40,
      CountryAltName, analyzer)
    logger.debug("QUERY: " + query)
    val tempQuery = new StringBuilder()
    query.replace("-", " ").split(" ").foreach(part => {
      logger.debug("PART: " + part)
      tempQuery.append(part).append(" OR ")
      logger.debug("PART QUERY: " + tempQuery)
    })
    val strQuery = tempQuery.toString.replace(".", "")
      .replace("(", "").replace(")", "")
    logger.debug("Fuzzy Query: " + strQuery)
    parser.parse {
      if (strQuery.contains("OR"))
        strQuery.substring(0, strQuery.lastIndexOf("OR"))
      else
        strQuery
    }
  }
  
  private[reconciliator] def search(name: String): Option[Document] = {
    val collector = TopScoreDocCollector.create(MaxResults, true)
    val query = createQueryFromString(name)
    logger.debug(query.toString())
    indexSearcher.search(query, collector)
    logger.debug("Searching country from given string " + name)
    val scoreDocs: Array[ScoreDoc] = collector.topDocs().scoreDocs
    if (scoreDocs.size == 0) {
      None
    } else {
      val doc: Document = indexSearcher.doc(scoreDocs.head.doc)
      Some(doc)
    }
  }

}

object CountryReconciliator {
  private val CountryWinameField = "wiName"
  private val CountryIso2CodeField = "iso2"
  private val CountryIso3CodeField = "iso3"
  private val CountryAltName = "altName"
  private val MaxResults = 1

  private val logger: Logger = LoggerFactory.getLogger(this.getClass())
}