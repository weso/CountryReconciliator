# CountryReconciliator

CountryReconciliator is a library that allows to reconcile countries (Country Name, ISO2, ISO3) given the name of a country.

## Download 

### Maven Central
CountryReconciliator is available on Maven Central:
 - Add the next dependency:
 ```
libraryDependencies += "es.weso" % "countryreconciliator_2.10" % "0.3.0-SNAPSHOT"
 ```
 - Add the next resolver:
 ```
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
 ```
 
### Locally
 
In order to install CountryReconciliator locally, download the code and run the next command from the project's root directory:
 ```
sbt compile publish-local
 ```

## License

```
  Copyright 2012-2013 WESO Research Group

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```