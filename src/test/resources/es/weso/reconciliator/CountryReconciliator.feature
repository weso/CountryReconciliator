Feature: Cucumber
  In order to obtain the name of a country according to the list of the Web Index
  As a machine
  I want to be able to validate the data

  Scenario: Validate the data loading from countries json File
    
  	Given I want to load names, iso-codes and alternative names for all countries presents in WebIndex
  	When I check the country with the name "Tanzania"
    Then The name according Web Index should be "United Republic of Tanzania"
    
    Given I want to load names, iso-codes and alternative names for all countries presents in WebIndex
  	When I check the country with the name "Venezuela"
    Then The name according Web Index should be "Venezuela (Bolivarian Republic of)"
    
    Given I want to load names, iso-codes and alternative names for all countries presents in WebIndex
  	When I check the country with the name "Russian"
    Then The name according Web Index should not be "Russia"
    
    Given I want to load names, iso-codes and alternative names for all countries presents in WebIndex
  	When I check the country object with the name "United States"
    Then The iso-2 code should be "US"
    And The iso-3 code should be "USA"
    
    Given I want to load names, iso-codes and alternative names for all countries presents in WebIndex
  	When I check the country object with the name "Virgin Islands"
    Then The iso-2 code should not be "US"
    And The iso-3 code should not be "USA"