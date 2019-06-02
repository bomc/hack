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
