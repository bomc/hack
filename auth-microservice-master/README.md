# auth-microservice
This is a poc project.

### Running .war only with wildfly in-memory db.

The '\src\main\resources\META-INF\persistence.xml' consists per default two persistence units.

```xml
<persistence-unit name="poc-auth-mysql-pu">
```

and

```xml
<persistence-unit name="poc-auth-h2-pu">
```

#### Configuration for additional datasources (mySql and h2) in wildfly:

```xml
        <subsystem xmlns="urn:jboss:domain:datasources:4.0">
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
                    <driver>h2</driver>
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <datasource jndi-name="java:jboss/datasources/poc-auth-mysql-ds" pool-name="AuthDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/poc-auth-mysql-db</connection-url>
                    <driver>mysql</driver>
                    <transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
                    <pool>
                        <min-pool-size>10</min-pool-size>
                        <max-pool-size>100</max-pool-size>
                        <prefill>true</prefill>
                    </pool>
                    <security>
                        <user-name>root</user-name>
                    </security>
                    <statement>
                        <prepared-statement-cache-size>32</prepared-statement-cache-size>
                        <share-prepared-statements>true</share-prepared-statements>
                    </statement>
                </datasource>
                <datasource jndi-name="java:jboss/datasources/poc-auth-h2-ds" pool-name="H2DS" enabled="true" use-java-context="true" use-ccm="true">
                    <connection-url>jdbc:h2:mem:poc-h2-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
                    <driver>h2</driver>
                    <pool>
                        <min-pool-size>50</min-pool-size>
                        <max-pool-size>100</max-pool-size>
                    </pool>
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <drivers>
                    <driver name="mysql" module="com.mysql">
                        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
                    </driver>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>
```

- To run .war only with the in-memory db, delete the xml-element '<persistence-unit name="poc-auth-mysql-pu">' from the persistence.xml.
- Further delete the file '\src\main\java\de\bomc\poc\auth\dao\jpa\producer\DatabaseMySqlProducer' from .war archive.
- Change in '\src\main\java\de\bomc\poc\auth\dao\jpa\generic\AbstractJpaDao.java' from

 @PersistenceContext(unitName = DatabaseMySqlProducer.UNIT_NAME)

 to

 @PersistenceContext(unitName = DatabaseH2Producer.UNIT_NAME)

### Configuration for running wildfly with ELK-Stack and gelf-Appender:

```xml
         <subsystem xmlns="urn:jboss:domain:logging:3.0">
             <console-handler name="CONSOLE">
                 <level name="INFO"/>
                 <formatter>
                     <named-formatter name="COLOR-PATTERN"/>
                 </formatter>
             </console-handler>
             <file-handler name="BomcJsonLog">
                 <formatter>
                     <named-formatter name="JsonFormatter"/>
                 </formatter>
                 <file relative-to="jboss.server.log.dir" path="bomc-log.json"/>
             </file-handler>
             <periodic-rotating-file-handler name="FILE" autoflush="true">
                 <formatter>
                     <named-formatter name="PATTERN"/>
                 </formatter>
                 <file relative-to="jboss.server.log.dir" path="server.log"/>
                 <suffix value=".yyyy-MM-dd"/>
                 <append value="true"/>
             </periodic-rotating-file-handler>
             <size-rotating-file-handler name="bomc-handler" autoflush="true">
                 <level name="DEBUG"/>
                 <formatter>
                     <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%X{X-BOMC-REQUEST-ID}] [%X{X-BOMC-BASE-URI}] %-5p [%c] (%t) %s%e%n"/>
                 </formatter>
                 <file relative-to="jboss.server.log.dir" path="bomc.log"/>
                 <rotate-size value="100M"/>
                 <max-backup-index value="5"/>
                 <append value="true"/>
             </size-rotating-file-handler>
             <logger category="com.arjuna">
                 <level name="WARN"/>
             </logger>
             <logger category="org.jboss.as.config">
                 <level name="DEBUG"/>
             </logger>
             <logger category="sun.rmi">
                 <level name="WARN"/>
             </logger>
             <logger category="de.bomc" use-parent-handlers="true">
                 <level name="DEBUG"/>
                 <handlers>
                     <handler name="bomc-handler"/>
                     <handler name="CONSOLE"/>
                 </handlers>
             </logger>
             <root-logger>
                 <level name="INFO"/>
                 <handlers>
                     <handler name="CONSOLE"/>
                     <handler name="FILE"/>
                     <handler name="BomcJsonLog"/>
                 </handlers>
             </root-logger>
             <formatter name="PATTERN">
                 <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n"/>
             </formatter>
             <formatter name="COLOR-PATTERN">
                 <pattern-formatter pattern="%K{level}%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n"/>
             </formatter>
             <formatter name="JsonFormatter">
                 <custom-formatter module="biz.paluch.logging" class="biz.paluch.logging.gelf.wildfly.WildFlyJsonFormatter">
                     <properties>
                         <property name="version" value="1.0"/>
                         <property name="facility" value="java-test"/>
                         <property name="fields" value="Time,Severity,ThreadName,SourceClassName,SourceMethodName,LoggerName"/>
                         <property name="extractStackTrace" value="true"/>
                         <property name="filterStackTrace" value="true"/>
                         <property name="mdcProfiling" value="true"/>
                         <property name="timestampPattern" value="yyyy-MM-dd HH:mm:ss,SSSS"/>
                         <property name="mdcFields" value="X-BOMC-BASE-URI,X-BOMC-REQUEST-ID"/>
                         <property name="includeFullMdc" value="true"/>
                     </properties>
                 </custom-formatter>
             </formatter>
         </subsystem>
```

Add the directory structure and libraries to wildfly:
---------

create directory: 'biz\paluch\logging\main' in 'wildfly-10.0.0.Final\modules\'

* commmons-pool2-2.4.2.jar
* jedis-2.8.0.jar
* json-simple-1.1.1.jar
* logstash-gelf-1.8.1.jar
* module.xml

see: **https://github.com/mp911de/logstash-gelf**
