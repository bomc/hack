- https://medium.com/@jinnerbichler/securing-spring-boot-applications-with-keycloak-on-kubernetes-76cdb6b8d674

- Keycloak is defined with a nodeport type: 30080

- Open keycloak with URL: http://192.168.99.101:30080/auth

- username and password are defined in bomc-kubernetes-event\kubernetes-deployment\keycloak\bomc-deployment.yml
        see: KEYCLOAK_USER value: "admin" and
             KEYCLOAK_PASSWORD value: "password"