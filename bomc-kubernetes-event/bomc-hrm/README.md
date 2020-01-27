# Getting Started

## Build and run

Build: /*mvn clean install*/

Start app with embedded Tomcat: /*mvn spring-boot:run*/

Swagger-UI: /*http://localhost:8080/bomc-hrm/swagger-ui.html*/
NOTE: see tomcat startup sequence for port:
...
 ProtocolHandler ["http-nio-8080"]
...

### Reference Documentation

## Find all customer

curl -v -X GET "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8"

## createCustomer

curl -v -X POST "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8/json;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"city\": \"brexit-city\", \"country\": \"UK\", \"dateOfBirth\": \"2000-12-24\", \"emailAddress\": \"boris@brexit.org\", \"firstName\": \"boris\", \"houseNumber\": \"42\", \"id\": 0, \"lastName\": \"brexit\", \"phoneNumber\": \"0424242\", \"postalCode\": \"4242\", \"street\": \"Downingstreet\"}"

{
  "id": 1,
  "emailAddress": "boris@brexit.org",
  "phoneNumber": "0424242",
  "firstName": "boris",
  "lastName": "brexit",
  "dateOfBirth": "2000-12-24",
  "city": "brexit-city",
  "postalCode": "4242",
  "street": "Downingstreet",
  "houseNumber": "42",
  "country": "UK"
}


## updateCustomer

curl -v -X PUT "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"id\": 1, \"emailAddress\": \"bomc@bomc.org\", \"phoneNumber\": \"042-4242424\", \"firstName\": \"michi\", \"lastName\": \"bomcy\", \"dateOfBirth\": \"2011-11-13\", \"city\": \"Honululu\", \"postalCode\": \"42424\", \"street\": \"Downing Street\", \"houseNumber\": \"42\", \"country\": \"CH\"}"

{
  "id": 1,
  "emailAddress": "bomc@bomc.org",
  "phoneNumber": "042-4242424",
  "firstName": "michi",
  "lastName": "bomcy",
  "dateOfBirth": "2011-11-13",
  "city": "Honululu",
  "postalCode": "42424",
  "street": "Downing Street",
  "houseNumber": "42",
  "country": "CH"
}


## getCustomerById

curl -v -X GET "http://localhost:8080/bomc-hrm/customer/1" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8"


## deleteCustomer

curl -v -X DELETE "http://localhost:8080/bomc-hrm/customer/1" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8"


## getCustomerByEmailAddress

curl -v -X POST "http://localhost:8080/bomc-hrm/customer/email-address" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"emailAddress\": \"bomc@bomc.org\"}"

{
  "emailAddress": "bomc@bomc.org"
}


echo "rouk" | nc localhost 2181 ; echo

rouk = are you ok
nc = netcat



Clients werden mit SSL/TLS authentisert.

ACLs (Access Control List) unterstützt 2 unterschiedliche Möglichkeiten

1. Topics: Schränkt ein welcher Cliient Daten lesen/schreiben kann.
2. Consumer Groups: Welcher Client kann von welcher Consumer Group konsumieren.
3. Cluster: Welcher Client kann create/delete Topics oder kann Konfigurationsänderungen durchführen.

Weiterhin gibt es das Konzept des "Super Users", der über alle obengenannten Rechte verfügt,
ohne spezielle ACLs.
ACLs werden in Zookeeper gespeichert und in Strimzi über einen "KafkaUser" angelegt.

Deshalb ist es zwingend den Zugriff auf den Cluster einzuschränken (Security oder Network Rules),
nur Kafka admins sollten das Recht haben Topics oder ACLs zu erstellen.



Check server.properties vom Kafka Broker

suche "super.users=User:admin;User:kafka"
und   "allow.everyone.if.no.acl.found=false"


LIst all ACLs:

./kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list --topic my_topiy

see logs, gehe zum Kafka-Broker server:

ll kafka/logs/kafka-authorizer.logs

______________________________________________________
Sollten wir Zugang haben.
------------------------------------------------------

Ändern des Log-Levels

cat kafka/config/log4j.properties

Change in the file, level from INFO TO DEBUG
log4j.logger.kafka.authorizer.logger=DEBUG, authorizeAppender




Mit SSL, Clients haben auch Zertifikate die vom Broker validiert werden, und der Clinet hat einen Identität.

Der Kafka-Client hat einen Keystore, der Client startet eine Request um die Zertifikate zu signieren bei einem CA.
Das CA sendet ein signiertes Zertifikat (Client.crt) zurück, dass im Keystore des Clients gespeichert wird.

Im Encryption Fall:
- Nur der Broker hat signierte Zertifikate.
- Der Client verifiziert die Broker Zertifikate um eine SSL Connection herzustellen.
- Der Client ist "Anonymous" zum Broker (no identity)

Im Authentication Fall:
- Die Clients UND der Broker haben signierte Server Zertifikate.
- Der Client und Broker verfizieren gegenseitig ihre Zertifikate.
- Der Client hat nun eine Identität "Identity" gegenüber dem Broker (ACLs können hinzugefügt werden).