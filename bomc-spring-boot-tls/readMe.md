# Overview
* This example shows how to setup a mutual TLS connection between a server and client
* Server is setup with a key pair that is trusted by the Client
* Client is setup with a key pair that is trusted by the Server
* Server will not accept request from a Client that is not trusted
* Client will not call a Server that is not trusted

## Sever setup
* The spring boot server app is setup with a `server-identity.jks` file to configure the HTTPS listener
* Another jks file named `server-truststore.jks` is configured with the trusted client certificate
* Server is configured in `src/main/resources/application.yml`
* The `client-auth: need` setting tells the app that the client needs to be authenticated using the client certificate

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: server-identity.jks
    key-password: secret
    key-store-password: secret
    trust-store: server-truststore.jks
    trust-store-password: secret
    client-auth: need
```

or 

```properties
server.port=8443

server.ssl.key-store=classpath:server-identity.jks
server.ssl.key-store-type=JKS
server.ssl.key-store-password=secret
#server.ssl.key-alias=secure-server

server.ssl.trust-store=classpath:server-truststore.jks
server.ssl.trust-store-password=secret
server.ssl.trust-store-type=JKS

server.ssl.enabled=true
server.ssl.client-auth=need
    
logging.level.org.springframework.security=DEBUG
```

## Run the tests
```bash
mvn clean test
```

## Testing with Curl
```bash
mvn clean spring-boot:run

curl -v -ik https://localhost:8443/api/tls --key ./ssl/client_key.pem --pass secret --cert ./ssl/client_cert.pem
```

## Example 1: Using JKS files

* This Example loads keys into a Java Key Store (JKS) file that is protected by a secret
* Using JKS requires creating a file using the `keytool` CLI
* The JSK file is loaded into a `KeyStore` object for use of configuring the `SSLContext`

```java
KeyStore identityKeyStore = KeyStore.getInstance("jks");
KeyStore trustKeyStore = KeyStore.getInstance("jks");

try (InputStream inputStream = new FileInputStream("client-identity.jks")) {
    identityKeyStore.load(inputStream, "secret".toCharArray());
}

trustKeyStore.load(null, "secret".toCharArray());

try (InputStream inputStream = new FileInputStream("client-truststore.jks")) {
    trustKeyStore.load(inputStream, "secret".toCharArray());
}

SSLContext sslContext = SSLContexts.custom()
    .loadKeyMaterial(identityKeyStore, "secret".toCharArray(), (Map<String, PrivateKeyDetails> aliases, Socket socket) -> {
        return "test";
    })
    .loadTrustMaterial(trustKeyStore, null)
    .build();

SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
RestTemplate restTemplate = new RestTemplate(requestFactory);

ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:" + port + "/api/tls", String.class);
```

### Example 2: Using PEM format
* This example bypasses the need for JKS files by loading the keys directly from their original format
* An empty `KeyStore` object initialized with a secret and the key is read into the `KeyStore` instance

```java
KeyFactory keyFactory = KeyFactory.getInstance("RSA");
CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

KeyStore identityKeyStore = KeyStore.getInstance("jks");
KeyStore trustKeyStore = KeyStore.getInstance("jks");

// need to initialize Keystores with any secret
trustKeyStore.load(null, "secret".toCharArray());
identityKeyStore.load(null, "secret".toCharArray());

try (InputStream publicKeyInput = new FileInputStream("client-public-cert.pem")) {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Files.copy(Paths.get("client-private-key.pem"), byteArrayOutputStream);
    String privateKeyString = new String(byteArrayOutputStream.toByteArray(), "UTF-8");

    privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----\n", "");
    privateKeyString = privateKeyString.replace("-----END PRIVATE KEY-----", "");
    privateKeyString = privateKeyString.replace("\n", "");

    byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);

    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    PrivateKey privateKey = keyFactory.generatePrivate(spec);
    X509Certificate cert = (X509Certificate)certFactory.generateCertificate(publicKeyInput);
    identityKeyStore.setKeyEntry("test", privateKey, "secret".toCharArray(), new X509Certificate[] {cert});
}

try (InputStream inputStream = new FileInputStream("server-public-cert.pem")) {
    X509Certificate cert = (X509Certificate)certFactory.generateCertificate(inputStream);
    trustKeyStore.setCertificateEntry("test", cert);
}

SSLContext sslContext = SSLContexts.custom()
    .loadKeyMaterial(identityKeyStore, "secret".toCharArray(), (Map<String, PrivateKeyDetails> aliases, Socket socket) -> {
        return "test";
    })
    .loadTrustMaterial(trustKeyStore, null)
    .build();

SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
RestTemplate restTemplate = new RestTemplate(requestFactory);

ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:" + port + "/cred", String.class);
```
