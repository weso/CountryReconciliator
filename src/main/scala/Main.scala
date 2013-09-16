import es.weso.reconciliator.CountryReconciliator

object Main {

  def main(args: Array[String]): Unit = {
    val countryReconciliation = new CountryReconciliator("files/countries.json", true)
    //println(countryReconciliation.searchCountry("Saint-Martin (French part)"))
  }

}