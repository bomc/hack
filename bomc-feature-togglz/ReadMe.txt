docker run -d -p 8080:8080 --name feature-togglz bomc/feature-togglz

Installation der togglz console ('http://192.168.4.1:8180/bomc-tooglz-war/togglz/index', 'http://127.0.0.1:8180/bomc-tooglz-war/togglz/index').

- Username und Passwort werden durch das SecurityRealm vom Wildfly definiert (default realm=other). Das heisst der angelegte Application-User ist dann gültig.
- Aktuell: user=bomc, password=bomc$1234

- WICHTIG ist die Rolle: Den user'n muss eine Role mitgegeben werden, bei der Definition im Wildfly -> add-user.bat. user: 'bomc', password: 'bomc$1234', roles: 'tooglz'
- Die definierte Rolle ist 'togglz', siehe im web.xml

	<!-- Security roles used by this web application, defined in wildfly. -->
	<security-role>
		<role-name>togglz</role-name>
	</security-role>
  
________________________________________________
- Dependencies im pom hinzufügen: togglz-console

________________________________________________________________________

- Da die default security-domain 'other' in diesem Beispiel genutzt wird, und diese auch f�r die ejbs verwendet wird, muss ein
jboss-ejb3.xml in das WEB-INF Verzeichnis kopieren mit dem Inhalt:

<?xml version="1.0"?>
<jboss:ejb-jar xmlns:jboss="http://www.jboss.com/xml/ns/javaee"
	xmlns="http://java.sun.com/xml/ns/javaee" xmlns:s="urn:security"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee/ejb-jar_3_1.xsd"
	version="3.1" impl-version="2.0">
	<assembly-descriptor>
		<s:security>
			<ejb-name>*</ejb-name>
			<s:security-domain>other</s:security-domain>
		</s:security>
	</assembly-descriptor>

</jboss:ejb-jar>

Weiter muessen die EJB's am Typ oder der Methode mit @PermitAll annotiert werden.
________________________________________________
- Im jboss-web.xml die security-domain erg�nzen:

<?xml version="1.0" encoding="UTF-8"?>
<!--
<jboss-web xmlns="http://www.jboss.com/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee http://www.jboss.org/j2ee/schema/jboss-web_10_0.xsd"
           version="10.0">
-->
<jboss-web>
	<!-- NOTE: The setting here, must necessarily be the same as in WildflyEnvConfigSingletonEJB defined! -->
    <context-root>bomc-tooglz-war</context-root>
    <security-domain>java:/jaas/other</security-domain>
</jboss-web>

__________________________________________________________________
- Ein web.xml in das WEB-INF Verzeichnis kopieren, mit dem Inahlt:

<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
	version="3.1">
	<display-name>Bomc tooglz Web App</display-name>

	<security-constraint>
		<web-resource-collection>
			<web-resource-name>Toggle Area</web-resource-name>
			<description>Security constraints for using the togglz-console.</description>
			<url-pattern>/togglz/*</url-pattern>
			<http-method>PUT</http-method>
			<http-method>DELETE</http-method>
			<http-method>GET</http-method>
			<http-method>POST</http-method>
		</web-resource-collection>
		<auth-constraint>
			<description>Only the admin has permission to enter the togglz-site. Here the default realm from wildfly is used.</description>
			<role-name>admin</role-name>
		</auth-constraint>
		<user-data-constraint>
			<transport-guarantee>NONE</transport-guarantee>
		</user-data-constraint>
	</security-constraint>
	
	<!--Login Prompt -->
	<login-config>
		<auth-method>BASIC</auth-method>
		<realm-name>ApplicationRealm</realm-name>
	</login-config>
	
	<!-- Security roles used by this web application, defined in wildfly. -->
	<security-role>
		<role-name>admin</role-name>
	</security-role>
</web-app>


Im Wildfly muss ein ApplicationUser angelegt werden der die Rolle 'admin' hat.

______________________________________________________
Aufgerufen wird der REST-Service mit:

http://127.0.0.1:8180/bomc-tooglz-war/rest/togglz/current-version

______________________________________________________
Für JDBC Repository 'datasource' konfigurieren:

                <datasource jndi-name="java:jboss/datasources/TooglzDS" pool-name="TooglzDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:h2:tcp://localhost/~/togglz;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
                    <driver>h2</driver>
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                
______________________________________________________    
Insert in Datenbank für Feature einfügen:


FEATURE_NAME  	FEATURE_ENABLED  	STRATEGY_ID  	STRATEGY_PARAMS
-------------------------------------------------------------------
FEATURE_1		0					null			null  
FEATURE_2		1					null			null
FEATURE_3		1					null			null


Insert Statements:
INSERT INTO TOGGLZ VALUES ('FEATURE_1', 1, NULL, NULL)
INSERT INTO TOGGLZ VALUES ('FEATURE_2', 1, NULL, NULL)
INSERT INTO TOGGLZ VALUES ('FEATURE_3', 1, NULL, NULL)

oder

INSERT INTO TOGGLZ VALUES ('FEATURE_1',	1, 'gradual', 'percentage=5')

oder 

INSERT INTO TOGGLZ VALUES ('FEATURE_1',	1, 'property', 'system-property=bomc value=wildfly')



 