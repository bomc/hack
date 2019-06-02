docker run -d -p 8080:8080 --name bomc-flyway-db-ejb bomc/bomc-flyway-db-ejb

______________________________________________________
Was ist wichtig:

- Der in 'initial_db_setup.sql' angegebene Schema-name muss identisch sein wie der in Wildfly konfigurierten datasource:

                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
 								                              ________________               				
                    <connection-url>jdbc:h2:tcp://localhost/~/bomc_flyway_demo</connection-url>
                    <driver>h2</driver>
                    <security>
                        <user-name>sa</user-name>
                    </security>
                </datasource>
                
																																			      _______	 
- Wenn Du im Entity - @Table("T_Product") definierst und der Klassenname - Product heisst, MUSST Du zwingend in 'createQuery' z.B. 'select p from Product p', Gross- und Kleinschreibung beachten.
  Das heisst im Query muss Product gross geschrieben werden. Hier wird Product in der Datenbank nach T_Table gemapped.
  
- Run mvn clean install -Parq-wildlfy-remote, beachte dass im Profil '<datasource.name>ExampleDS</datasource.name>' gesetzt ist.
