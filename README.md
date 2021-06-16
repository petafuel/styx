# Styx

A Berlin Group NextGenPSD2-compliant XS2A client.

## About

The Revised Payment Services Directive (PSD2) is intended to harmonise digital payment transactions within the European Economic Area. In addition to banks, PSD2 also provides many opportunities for third-party service providers (TTPs) to offer innovative services through the payment services provided by banks. Although PSD2 is based on a standard for bank interfaces, there remains considerable scope for implementation. For TTPs, developing and maintaining an XS2A interface is a time-consuming challenge. With the PSD2 XS2A Client Styx we offer TTPs a free and easy solution to interact with the different PSD2 interfaces of banks from Germany and Europe.

## Features

* Payment initiation service (PIS)
* Account information service (AIS)
* Confirmation of funds service (PIIS)
* SCA methods (redirect, OAuth2, decoupled, embedded)

For more information please have a look at the [Styx wiki](https://github.com/petafuel/styx/wiki) and [petafuel.github.io/styx/](https://petafuel.github.io/styx/).

## Client API

The Styx Client API is a REST interface that follows the Berlin Group NextGenPSD2 specification. The documentation is available in OpenAPI format.

[Styx Client API documentation](https://petafuel.github.io/styx/api/)

## Installation

Styx is build using the Amazon Corretto 8 - the production-ready distribution of Open Java Development Kit (OpenJDK) and uses a PostgreSQL database.

1. Clone the repository

   `git clone https://github.com/petafuel/styx.git`

2. Styx uses packages provided through GitHub Packages

   Edit your *~/.2/settings.xml* and add your GitHub username and your personal access token. Also see [here](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages).

   ```xml
   <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                         http://maven.apache.org/xsd/settings-1.0.0.xsd">    
   
       <servers>
           <server>
               <id>styxgithub</id>
               <username>GITHUB_NAME</username>
               <password>GITHUB_TOKEN</password>
           </server>
       </servers>
   
   </settings>
   ```

3. Configure Styx

   You have to adjust *connectionpool.properties* to configure the connection to your PostgreSQL database.
   Also adjust *API/src/main/resources/api.properties* and *Core/src/main/resource/core.properties* according to your Styx deployment.

4. Build Styx

   Run `mvn clean -DskipTests=true install` to build Styx.

5. Execute Styx

   ```sh
   cd API/target
   java -jar styxRest.jar
   ```

## Third Party Libraries

Styx is build using great third party projects such as

* OWASP Dependency-Check,
* Eclipse Yasson,
* REST-assured and many others.

You can find the [complete list of third party libraries](https://github.com/petafuel/styx/blob/main/third_party/README.md) and their respective licenses in the 'third_party' directory.
