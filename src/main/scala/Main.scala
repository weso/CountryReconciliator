import es.weso.reconciliator.CountryReconciliator

object Main {

  def main(args: Array[String]): Unit = {
    val countryReconciliation = new CountryReconciliator("files/countries.json", true)
    println(countryReconciliation.searchCountry("Saint-Martin (French part)"))
    println(countryReconciliation.searchCountry("United Kingdom of Great Britain and Northern Ireland"))
    println(countryReconciliation.searchCountry("United Kingdom"))
    println(countryReconciliation.searchCountry("United Republic of Tanzania"))
    println(countryReconciliation.searchCountry("Bolivarian Republic"))
  }

}