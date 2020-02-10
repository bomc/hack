#!/bin/bash

echo
echo "----- Delete files"
echo
rm -f ca-key.pem
rm -f ca-root.pem
rm -f ca-root.srl
rm -f client.csr
rm -f client-identity.jks
rm -f client-key.pkcs12
rm -f client-private-key.pem
rm -f client-public-cert.pem
rm -f client-truststore.jks
rm -f server.csr
rm -f server-identity.jks
rm -f server-key.pkcs12
rm -f server-private-key.pem
rm -f server-public-cert.pem
rm -f server-truststore.jks


echo
echo "----- Create ca-key"
echo
winpty openssl genrsa -aes256 -passout pass:secret -out ca-key.pem 1024

echo
echo "----- Create ca-root"
winpty openssl req -x509 -new -nodes -passin pass:secret -subj '//C=DE\OU=Bomc Trustcenter\L=\O=Bomc\CN=Bomc Root CA' -key ca-key.pem -days 1024 -passout pass:secret -out ca-root.pem -sha512

echo
echo "----- Create server-private-key.pem"
echo
winpty openssl genrsa -des3 -passout pass:secret -out server-private-key.pem 1024

echo
echo "----- Create server.csr"
echo
winpty openssl req -subj '//C=DE\ST=BW\L=\O=Server\CN=localhost' -passin pass:secret -new -key server-private-key.pem -out server.csr -sha512

echo
echo "----- Create server-public-cert.pem"
echo
winpty openssl x509 -req -in server.csr -CA ca-root.pem -passin pass:secret -CAkey ca-key.pem -CAcreateserial -out server-public-cert.pem -days 365 -sha512

echo
echo "----- Convert Server public/private PEM to PKCS12"
echo
winpty openssl pkcs12 -export -out server-key.pkcs12 -passin pass:secret -inkey server-private-key.pem -in server-public-cert.pem -passout pass:secret

echo
echo "----- Import Server PCKS12 into a JKS file"
echo
keytool -importkeystore -srckeystore server-key.pkcs12 -srcstoretype pkcs12 -destkeystore server-identity.jks -storepass secret -keypass secret -srcstorepass secret

echo
echo "----- Create client-private-key.pem"
echo
winpty openssl genrsa -des3 -passout pass:secret -out client-private-key.pem 1024

echo
echo "----- Create client.csr"
echo
winpty openssl req -subj '//C=DE\ST=BW\L=\O=Client\CN=localhost' -passin pass:secret -new -key client-private-key.pem -out client.csr -sha512

echo
echo "----- Create client-public-cert.pem"
echo
winpty openssl x509 -req -in client.csr -CA ca-root.pem -passin pass:secret -CAkey ca-key.pem -CAcreateserial -out client-public-cert.pem -days 365 -sha512

echo
echo "----- Convert Client public/private PEM to PKCS12"
echo
winpty openssl pkcs12 -export -out client-key.pkcs12 -passin pass:secret -inkey client-private-key.pem -in client-public-cert.pem -passout pass:secret

echo
echo "----- Import Client PCKS12 into a JKS file"
echo
keytool -importkeystore -srckeystore client-key.pkcs12 -srcstoretype pkcs12 -destkeystore client-identity.jks -storepass secret -keypass secret -srcstorepass secret

echo
echo "----- Create Server/Client trust stores"
echo
keytool -keystore client-truststore.jks -importcert -file server-public-cert.pem -alias server -storepass secret -noprompt

keytool -keystore server-truststore.jks -importcert -file client-public-cert.pem -alias client -storepass secret -noprompt

echo
echo "----- Move Server/Client trust/key stores"
echo

mv -v -f server-truststore.jks server-identity.jks ../src/main/resources

mv -v -f client-truststore.jks client-identity.jks ../src/test/resources

# keytool -import -trustcacerts -keystore "C:/Program Files/Java/jdk1.8.0_202/jre/lib/security/cacerts" -storepass changeit -alias Bomc -import -file ca-root.pem
# keytool -delete -alias Bomc -keystore "C:/Program Files/Java/jdk1.8.0_202/jre/lib/security/cacerts"
# keytool -export -alias Bomc -file ca-root.pem -keystore "C:/Program Files/Java/jdk1.8.0_202/jre/lib/security/cacerts"

# keytool -list -v -keystore "C:/Program Files/Java/jdk1.8.0_202/jre/lib/security/cacerts"
# keytool -list -v -keystore "C:/Program Files/Java/jdk1.8.0_202/jre/lib/security/cacerts" -alias Bomc

# keytool -printcert -v -file ca-root.pem

# winpty openssl s_client -connect localhost:8443 -showcerts
# winpty openssl s_client -host localhost -port 8443 -cert client-public-cert.pem -key client-private-key.pem -passin pass:secret

# mvn clean install -Djavax.net.debug=all

winpty openssl genrsa -out diagserverCA.key 2048
winpty openssl req -x509 -new -nodes -key diagserverCA.key -sha256 -days 1024 -out diagserverCA.pem
winpty openssl pkcs12 -export -name server-cert -in diagserverCA.pem -inkey diagserverCA.key -out serverkeystore.p12
keytool -importkeystore -destkeystore server.keystore -srckeystore serverkeystore.p12 -srcstoretype pkcs12 -alias server-cert
______________________________________________
keytool -import -alias client-cert -file diagclientCA.pem -keystore server.truststore

keytool -import -alias server-cert -file diagserverCA.pem -keystore server.truststore

winpty openssl genrsa -out diagclientCA.key 2048
winpty openssl req -x509 -new -nodes -key diagclientCA.key -sha256 -days 1024 -out diagclientCA.pem
winpty openssl pkcs12 -export -name client-cert -in diagclientCA.pem -inkey diagclientCA.key -out clientkeystore.p12
keytool -importkeystore -destkeystore client.keystore -srckeystore clientkeystore.p12 -srcstoretype pkcs12 -alias client-cert
keytool -import -alias server-cert -file diagserverCA.pem -keystore client.truststore
keytool -import -alias client-cert -file diagclientCA.pem -keystore client.truststore

curl -v https://localhost:8443/api/tls --key client-private-key.pem  --cert client-public-cert.pem -k

winpty openssl s_client -showcerts -connect localhost:8443
