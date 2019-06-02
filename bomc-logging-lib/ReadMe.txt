Wildfly in das Projekt integrieren:
-----------------------------------
______________________________
Projekt vom SVN herunterladen.
Im Projektverzeichnis 'mvn clean install' ausfuehren, Klassen werden kompiliert und Unit Tests sollten erfolgreich ausgefuehrt werden.

_________________________________________________________________________________
Profile zum Initialen Download ausfuehren (Profile NUR zum Runterladen verwenden):
'mvn wildfly:start -Pwildfly-local-download'

Wildfly wird vom Nexus heruntergeladen und in das Projektverzeichnis 'project.parent.basedir/wildfly-run' kopiert, entpackt und gestartet.

HINWEIS:
Wildfly wird bei diesem Profile mit den Default-Paramtern gestartet, wie host=127.0.0.1 und port-offset=0, d.h. http=8080.

Wildfly kann jetzt beendet werden, mit CTRL+C, oder im Projektverzeichnis weiteres Fenster oeffnen und 'mvn wildfly:stop'-Befehl ausfuehren.

____________________________
Wildfly starten und stoppen:

Vorangegangener Schritt muss erfolgreich ausgefuehrt worden sein.

Start: 'mvn wildfly:start -Pwildfly-local'
Stop: 'mvn wildfly:shutdown -Pwildfly-local'

HINWEIS:
Konfigurationsaenderungen werden hier in diesem Profile (pom.xml) angegeben.
Der Wildfly startet per default mit host=127.0.0.1, einem port-offset=100 und damit http=8180


Wildfly aus der Entwicklungsumgebung mit IntelliJ debuggen:
-----------------------------------------------------------
1. Wildfly starten
2. Intellij starten
3. Ausfuehren Run -> Edit Configurations.
4. Im geoeffneten Fenster 'Run/Debug Configurations', auf der linken Seite -> das grosse gruene '+' anklicken, Baum oeffnet sich und 'Remote' anklicken.
5. Beliebigen Namen oben eingeben (z.B. 'wildfly-remote-debug'), Debug Port auf 8787 anpassen.
   (Vorsicht: kann auch ein anderer sein, kann aus dem Log vom Wildfly ermittelt werden (wird beim Start geloggt). -> 'Listening for transport dt_socket at address: ????')
6. Bestaetigen mit 'Apply' und 'OK''.
7. Sollte jetzt oben in der DropDown-Box 'Select Run/Debug Configurations' zusehen sein.
8. Auswaehlen -> 'wildfly-remote-debug' und das 'Kaeferchen' anklicken.
9. In der Konsole von IntelliJ sollte folgende Meldung stehen: 'Connected to the target VM, address: 'localhost:8787', transport: 'socket''.
10. Breakpoint setzen.
11. Testfall starten: 'mvn clean install -Parq-wildfly-remote -Dtest=test.class#test_methode'.


Egov logging lib
-----------------
Die Library liefert eine Funktionalitaet für ein einheitliches Logging im Projekt.

_________________________________________________________________________________
Einheitliches Erstellen einer Logger-Instanz bei Verwendung eines CDI-Containers:

@Inject
@LoggerQualifier
private org.apache.log4j.Logger logger;

logger.debug(LOG_PREFIX + "testMethode - [test=" + test + "]");

________________________________________________________________________________________
ThreadTrackerInterceptor soll helfen beim Naming des auszuführenden Cdi-Beans oder EJBs.
Threads bekommen einen eindeutigen Namen und koennen in der JConsole oder VisualVM eindeutig zugewiesen werden.
Hilft z.B. bei Dead-Locks, muss im beans.xml aktiviert werden.
Jedes Cdi-Bean oder EJB muss entsprechend annotiert werden:

@ThreadTrackerQualifier
@Interceptors(ThreadTrackerInterceptor.class)

Der Interceptor muss im beans.xml-File nicht registriert werden.
__________________________________________________________________________________
MDCFilter und UIDHeaderRequestFilter
Bei jedem REST-Request soll dem Endpoint eine UID mitgeliefert werden, damit der auszufuehrende Request im Code besser ge-'trackt' werden kann.
Die UID wird im Request als Header-Parameter mitgeliefert.
Der MDCFilter (MappedDiagnosticChannel) fuegt die UID im ThreadLocal hinzu und von Log4j gelesen und jeder Logmeldung automatisch hinzugefuegt.
Sollte keine UID beim Request mitgeliefert werden, wird vom Filter eine eigene UID erzeugt und fuer den Request im Code verwendet.

Der UIDHeaderRequestFilter soll vom Client verwendet werden, um die UID zu generieren und die UID im als Header-Parameter zu speichern.

Anwendung siehe: MDCFilterTestIT#test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass

Damit die UID im Logfile erscheint muss folgendes konfiguriert werden (standalone.xml):

aendere
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <formatter>
                    <named-formatter name="PATTERN"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>

nach
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <formatter>
                    <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%X{X-EGOV-REQUEST-ID}] [%X{X-EGOV-BASE-URI}] %-5p [%c] (%t) %s%e%n"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>

oder mit neuem named-formatter:

            <formatter name="PATTERN-EXT-MDC-EGOV">
                <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} [%X{X-EGOV-REQUEST-ID}] [%X{X-EGOV-BASE-URI}] %-5p [%c] (%t) %s%e%n"/>
            </formatter>

nach
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <formatter>
                    <named-formatter name="PATTERN-EXT-MDC-EGOV"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>



