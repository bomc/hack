# Getting Started

## Build and run

Build: /*mvn clean install*/

Start app with embedded Tomcat: /*mvn spring-boot:ru*/n

Swagger-UI: /*http://localhost:8180/swagger-ui.html*/
NOTE: see tomcat startup sequence for port:
...
 ProtocolHandler ["http-nio-8180"]
...

### Reference Documentation

## createCustomer

curl -X POST "http://localhost:8080/customer" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"city\": \"cityTown\", \"country\": \"CH\", \"dateOfBirth\": \"2011-11-13\", \"emailAddress\": \"bomc@bomc.org\", \"firstName\": \"michi\", \"houseNumber\": \"42\", \"id\": 0, \"lastName\": \"bomcy\", \"phoneNumber\": \"042-4242424\", \"postalCode\": \"42424\", \"street\": \"myStreet\"}"

{
  "city": "Chinese-city",
  "country": "CH",
  "dateOfBirth": "2011-11-13",
  "emailAddress": "bomc@bomc.org",
  "firstName": "michi",
  "houseNumber": "42",
  "id": 0,
  "lastName": "bomcy",
  "phoneNumber": "042-4242424",
  "postalCode": "42424",
  "street": "Downing Street"
}


## updateCustomer

curl -X PUT "http://localhost:8080/customer" -H "accept: application/json;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d "{ \"id\": 2, \"emailAddress\": \"bomc@bomc.org\", \"phoneNumber\": \"042-4242424\", \"firstName\": \"michi\", \"lastName\": \"bomcy\", \"dateOfBirth\": \"2011-11-13\", \"city\": \"Honululu\", \"postalCode\": \"42424\", \"street\": \"Downing Street\", \"houseNumber\": \"42\", \"country\": \"CH\"}"

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

curl -X GET "http://localhost:8080/customer/1" -H "accept: application/json;charset=UTF-8"


## deleteCustomer

curl -X DELETE "http://localhost:8080/customer/1" -H "accept: */*"


## getCustomerByEmailAddress

curl -X POST "http://localhost:8080/customer/email-address" -H "accept: application/json;charset=UTF-8" -H "Content-Type: application/json;charset=UTF-8" -d "{ \"emailAddress\": \"bomc@bomc.org\"}"

{
  "emailAddress": "bomc@bomc.org"
}