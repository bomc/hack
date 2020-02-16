#!/bin/sh

echo
echo "## 0. Cleanup"

rm -f ca-cert
rm -f ca-cert.srl
rm -f ca-key
rm -f cert_and_key
rm -f cert-signed
rm -f cert-unsigned

rm -r ssl

rm -f ../src/main/resources/server-identity.jks
rm -f ../src/main/resources/server-truststore.jks
rm -f ../src/test/resources/client-identity.jks
rm -f ../src/test/resources/client-truststore.jks

echo
## 1. Create certificate authority (CA)"
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365 -passin pass:secret -passout pass:secret -subj "//CN=localhost\OU=DevOps\O=BomcCloud\L=Bomc Authority\ST=Bomc Kingdom\C=DE"

echo
echo "## 2. Create client keystore"
keytool -noprompt -keystore client-identity.jks -genkey -alias localhost -dname "CN=localhost, OU=DevOps, O=Client, L=Bomc Authority, ST=Bomc Kingdom, C=DE" -storepass secret -keypass secret

echo
echo "## 3. Sign client certificate"
keytool -noprompt -keystore client-identity.jks -alias localhost -certreq -file cert-unsigned -storepass secret
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-unsigned -out cert-signed -days 365 -CAcreateserial -passin pass:secret

echo
echo "## 4. Import CA and signed client certificate into client keystore"
keytool -noprompt -keystore client-identity.jks -alias CARoot -import -file ca-cert -storepass secret
keytool -noprompt -keystore client-identity.jks -alias localhost -import -file cert-signed -storepass secret

echo
echo "## 5. Import CA into client truststore (only for debugging with producer / consumer utilities)"
keytool -noprompt -keystore client-truststore.jks -alias CARoot -import -file ca-cert -storepass secret

echo
echo "## 6. Import CA into server truststore"
keytool -noprompt -keystore server-truststore.jks -alias CARoot -import -file ca-cert -storepass secret

echo
echo "## 7. Create PEM files for app clients"
mkdir -p ssl

echo
echo "## 8. Create server keystore"
keytool -noprompt -keystore server-identity.jks -genkey -alias localhost -dname "CN=localhost, OU=DevOps, O=Server, L=Bomc Authority, ST=Bomc Kingdom, C=DE" -storepass secret -keypass secret

echo
echo "## 9. Sign server certificate"
keytool -noprompt -keystore server-identity.jks -alias localhost -certreq -file cert-unsigned -storepass secret
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-unsigned -out cert-signed -days 365 -CAcreateserial -passin pass:secret

echo
echo "## 10. Import CA and signed server certificate into server keystore"
keytool -noprompt -keystore server-identity.jks -alias CARoot -import -file ca-cert -storepass secret
keytool -noprompt -keystore server-identity.jks -alias localhost -import -file cert-signed -storepass secret

echo
echo "## 11. Extract signed client certificate"
keytool -noprompt -keystore client-identity.jks -exportcert -alias localhost -rfc -storepass secret -file ssl/client_cert.pem

echo
echo "## 12. Extract client key"
keytool -noprompt -srckeystore client-identity.jks -importkeystore -srcalias localhost -destkeystore cert_and_key.p12 -deststoretype PKCS12 -srcstorepass secret -storepass secret
openssl pkcs12 -in cert_and_key.p12 -nocerts -nodes -passin pass:secret -out ssl/client_key.pem

echo
echo "## 13. Extract CA certificate"
keytool -noprompt -keystore client-identity.jks -exportcert -alias CARoot -rfc -file ssl/ca_cert.pem -storepass secret

echo
echo "## 14. Move files to resources directory"
mv server-identity.jks ../src/main/resources
mv server-truststore.jks ../src/main/resources
mv client-identity.jks ../src/test/resources
mv client-truststore.jks ../src/test/resources
