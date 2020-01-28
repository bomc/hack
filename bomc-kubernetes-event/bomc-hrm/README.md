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


List all ACLs:

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





______________________________________________________
1. Create Certifcate Authority (CA):
------------------------------------------------------
Create a working dirctory: /ssl 

openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" -keyout ca-key -out ca-cert -nodes

This command requested a new key (params: req -new -newkey), with rsa encryption and keylength 4096 bits, valid time. Als result wird der ca-key (this is the private key) und ca-cert (public key, used for importing in the truststores) generiert.

To get the content: use 'cat ca-key' und 'cat ca-cert' 

ca-key sollte niemals öffentlich gemacht werden. Im Gegensatz zum ca-cert, das verteilt werden kann an jedermann, der das erstellte CA trusten soll.

ca-cert hat kein "private" Eintrag. Das ca-cert kann zu jederman verteilt werden, der das weiter oben erzeugt CA 'trusten' soll.

______________________________________________________
1a. Beschreibung vom Marek
------------------------------------------------------
# Setup CA
create CA => result: file ca-cert and the priv.key ca-key  

```
mkdir ssl
cd ssl 
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=Kafka-Security-CA" -keyout ca-key -out ca-cert -nodes

cat ca-cert
cat ca-key
keytool -printcert -v -file ca-cert
```


### Add-On: public certificates check
```
echo |
  openssl s_client -connect www.google.com:443 2>/dev/null |
  openssl x509 -noout -text -certopt no_header,no_version,no_serial,no_signame,no_pubkey,no_sigdump,no_aux -subject -nameopt multiline -issuer
```
______________________________________________________
Ende Beschreibung vom Marek
------------------------------------------------------

______________________________________________________
2. Konfiguration auf der Kafka Seite
------------------------------------------------------
Create the Kafka Broker certificate

______________________________________________________
2a. Beschreibung vom Marek
------------------------------------------------------

## create a server certificate !! put your public EC2-DNS here, after "CN="
```
export SRVPASS=serversecret
cd ssl

keytool -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass $SRVPASS -keypass $SRVPASS  -dname "CN=ec2-18-196-169-2.eu-central-1.compute.amazonaws.com" -storetype pkcs12

---
-genkey: Parameter to generate a key, as output in a -keystore with the name kafka.server.keystore.jks
Der wichtigste Parameter ist der CommonName (CN), der der public dns Name der Kafka Instanz sein soll.
pkcs12 ist das Format, was der defacto Industriestandard ist.
---

#> ll

keytool -list -v -keystore kafka.server.keystore.jks
```

## create a certification request file, to be signed by the CA

---
The certificate should be used by all clients, and be able to verify if the send kafka broker certificate is valid.
Das Zertifikat soll extracted werden vom keystore.
-certreq: doing a certificateRequest
-cert-file: should be the output file

Signing is a to step process. The first step is calling "getting a signing request" out from the keystore

Nach dem Ausführen des unten stehenden Kommandos, wurde ein cert-file erstellt. Das ist der signing request, der nun zu dem CA gesendet wird, und dieser ist somit in der Lage unser Zertifikate zu signieren.

In einem Real-World Verfahren, würde das cert-file zu unserem Security Officer schicken und von diesem ein signiertes Zertifikat erhalten.
---

```
keytool -keystore kafka.server.keystore.jks -certreq -file cert-file -storepass $SRVPASS -keypass $SRVPASS

---

#> ll
```

## sign the server certificate on our created CA above => output: file "cert-signed"
```
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:$SRVPASS

---
-req specifying a request, -CA, ca-key is the public key, -in: is the signing request, der einen Schritt weiter oben erzeugt wurde, und mit dem ausführen in das file cert-signal geschrieben wird.
Am Ende haben wir ein neues file cert-signed, das beinhaltet das signed certificate für unseren broker.
---


#> ll
``` 

## check certificates
### our local certificates
```
keytool -printcert -v -file cert-signed
keytool -list -v -keystore kafka.server.keystore.jks
```



# Trust the CA by creating a truststore for the kafka broker and importing the ca-cert
```
keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

```

---
kafka.server.truststore.jks ist ein java truststore, das Komando oben fügt ein alias CARoot hinzu,
und importiert das public ca-cert certificate file.
so that our kafka broker in the end is trusting all certificates which has been issued by our CA

---
# Import CA and the signed server certificate into the keystore

here we import our certificates to our keystore. remember initially we created or server certficate, which has been put kafka.server.keystore.jks. But now we have a signed version of it.
we now need to import this signed certificate in our keystore in addition to the ca certficate itself. Both of them need to be start in the keystore. That is going to be in the both commands: 

```
keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca-cert -storepass $SRVPASS -keypass $SRVPASS -noprompt

After above, the next command is doing it for the siged certificate of our kafka broker

keytool -keystore kafka.server.keystore.jks -import -file cert-signed -storepass $SRVPASS -keypass $SRVPASS -noprompt
```

# Adjust Broker configuration  
Replace the server.properties in AWS, by using [this one](./server.properties).   
*Ensure that you set your public-DNS* !!

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# see kafka.server.KafkaConfig for additional details and defaults

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
broker.id=0

############################# Socket Server Settings #############################

# The address the socket server listens on. It will get the value returned from
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
____
_____
_______Das ist der wesentliche Eintrag
_________

listeners=PLAINTEXT://0.0.0.0:9092,SSL://0.0.0.0:9093
advertised.listeners=PLAINTEXT://##your-public-DNS##:9092,SSL://##your-public-DNS##:9093
zookeeper.connect=##your-public-DNS##:2181

____
_____
_______Das ist der wesentliche Eintrag
_________
ssl.keystore.location=/home/ubuntu/ssl/kafka.server.keystore.jks
ssl.keystore.password=serversecret
ssl.key.password=serversecret
ssl.truststore.location=/home/ubuntu/ssl/kafka.server.truststore.jks
ssl.truststore.password=serversecret


# Maps listener names to security protocols, the default is for them to be the same. See the config documentation for more details
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL

# The number of threads that the server uses for receiving requests from the network and sending responses to the network
num.network.threads=3

# The number of threads that the server uses for processing requests, which may include disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600


auto.create.topics.enable=false

############################# Log Basics #############################

# A comma seperated list of directories under which to store log files
log.dirs=/home/ubuntu/kafka-logs

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=1

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

############################# Internal Topic Settings  #############################
# The replication factor for the group metadata internal topics "__consumer_offsets" and "__transaction_state"
# For anything other than development testing, a value greater than 1 is recommended for to ensure availability such as 3.
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1

############################# Log Flush Policy #############################

# Messages are immediately written to the filesystem but by default we only fsync() to sync
# the OS cache lazily. The following configurations control the flush of data to disk.
# There are a few important trade-offs here:
#    1. Durability: Unflushed data may be lost if you are not using replication.
#    2. Latency: Very large flush intervals may lead to latency spikes when the flush does occur as there will be a lot of data to flush.
#    3. Throughput: The flush is generally the most expensive operation, and a small flush interval may lead to exceessive seeks.
# The settings below allow one to configure the flush policy to flush data after a period of time or
# every N messages (or both). This can be done globally and overridden on a per-topic basis.

# The number of messages to accept before forcing a flush of data to disk
#log.flush.interval.messages=10000

# The maximum amount of time a message can sit in a log before we force a flush
#log.flush.interval.ms=1000

############################# Log Retention Policy #############################

# The following configurations control the disposal of log segments. The policy can
# be set to delete segments after a period of time, or after a given size has accumulated.
# A segment will be deleted whenever *either* of these criteria are met. Deletion always happens
# from the end of the log.

# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# A size-based retention policy for logs. Segments are pruned from the log unless the remaining
# segments drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=1073741824

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000

############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000


############################# Group Coordinator Settings #############################

# The following configuration specifies the time, in milliseconds, that the GroupCoordinator will delay the initial consumer rebalance.
# The rebalance will be further delayed by the value of group.initial.rebalance.delay.ms as new members join the group, up to a maximum of max.poll.interval.ms.
# The default value for this is 3 seconds.
# We override this to 0 here as it makes for a better out-of-the-box experience for development and testing.
# However, in production environments the default value of 3 seconds is more suitable as this will help to avoid unnecessary, and potentially expensive, rebalances during application startup.











# Restart Kafka
```
sudo systemctl restart kafka
sudo systemctl status kafka  
```
# Verify Broker startup
```
sudo grep "EndPoint" /home/ubuntu/kafka/logs/server.log
```
# Adjust SecurityGroup to open port 9093

# Verify SSL config
from your local computer
```
openssl s_client -connect <<your-public-DNS>>:9093
```
______________________________________________________
Ende Beschreibung vom Marek
------------------------------------------------------




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