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

Anwendung des ExceptionHandlings:

In der Applikation sollen 'unchecked'-Exceptions verwendet werden. Dies hat den Vorteil, dass nicht zu jeder Methode die eine Exception wirft, eine 'throws'-clause angefuegt werden.
Weiterhin muss bei nachtraeglichen Aenderungen nicht der komplette Methoden Stack angepasst werden.
Grundsaetzlich gilt: Exceptions werden da gefangen, wo sie geworfen werden. Die Exception wird in einem try-catch-Block gefangen und in eine der TopLevel-Exceptions und eine davon abgeleitete Klasse umgewandelt und weiter nach oben gereicht.
Ein Exception-Handling kann ueber eine Exception-Hierachie oder ueber Fehlercodes aufgebaut werden. In der Applikation soll eine Mischform verwendet werden.
In der Applikation werden 'WebRuntimeException' und die 'AppRuntimeException' als sogenannte TopLevel-Exception verwendet. 
Dies hat den Vorteil, das beim Aufrufer immer eine in der Applikation definierte Exception gefangen wird und entsprechend verarbeitet werden kann, das Ergebnis ist ein einheitliches Exception-Handling.

                     <<abstract>>
                ---------------------                            <<interface>>
                |RootRuntimeExeption|                        ---------------------
                ---------------------  1                   1 |     ErrorCode     |       
                |                   |<>---------------------> --------------------
                |                   |                        |                   | 
                ---------------------                        |                   |
                  |               |                          ---------------------
                  |               |                                   |
    --------------------      --------------------               <<interface>>
    |WebRuntimeExeption|      |AppRuntimeExeption|           ---------------------
    --------------------      --------------------           |      ApiError     |
    |                  |      |                  |           ---------------------
    |                  |      |                  |           |                   |
    --------------------      --------------------           |                   |
                                                             ---------------------

Die 'WebRuntimeException' ist die TopLevel-Exception fuer REST-Aufrufe und die 'AppRuntimeException' soll fuer die anderen Faelle angewendet werden. Um den Fehler genauer zu beschreiben werden Fehlercodes eingefuehrt.
Zwischen den Fehlercodes fuer den REST-Aufruf und den Applikationsfehlern, gibt es den Unterschied des 'Response.Status'-Codes der bei einer Applikations-Exception nicht von Bedeutung ist.
Die Fehlercodes sollten grob einen Fehlerbereich umschreiben und nicht einen einzelnen speziellen Fehler.
	
In der Anwendung sollte die TopLevel-Exception abgeleitet werden, und nach der Domaene bezeichnet werden, z.B. 'MonitorRuntimeException extends WebRuntimeException'.
Der Fehlercode sollte dann z.B. heissen 'MONITOR_00100' -> Beschreibung 'Speicher max ist erreicht.'

Das Handling sollte im 'catch-Block' wie folgt ausgefuehrt werden:

	...
	} catch(IllegalArgumentException iAE) {
      // Applikations-Exception erzeugen.	
	  WebRuntimeException w = new WebRuntimeException(iAE, MonitorApiError.MONITOR_0010);
	  // stackTrace loggen.
	  w.stackTraceToString();
	  // Exception nach oben weiterreichen.
	  throw w;
	}
	
Die Verwendung in der oben beschriebenen Form hat den Vorteil das alle Log-Meldungen gleich aussehen:

_______________________________
de.bomc.poc.exception.core.web.WebRuntimeException: java.lang.ArithmeticException: / by zero
uuid=ef26f7f6-7692-4cf9-bddd-3d2cc3ac5e89
TEST_API_00101(Invalid arguments): de.bomc.poc.exception.test.TestApiError
name1=[value1]
name2=[value2]
-------------------------------
 [cause: ArithmeticException: / by zero]
java.lang.ArithmeticException: / by zero
at de.bomc.poc.exception.test.unit.WebRuntimeExceptionTest.helperMethodCreatesWebRuntimeExceptionWithErrorCode(WebRuntimeExceptionTest.java:339)
 [wrapped] de.bomc.poc.exception.core.web.WebRuntimeException: java.lang.ArithmeticException: / by zero
at de.bomc.poc.exception.test.unit.WebRuntimeExceptionTest.helperMethodCreatesWebRuntimeExceptionWithErrorCode(WebRuntimeExceptionTest.java:341)
at de.bomc.poc.exception.test.unit.WebRuntimeExceptionTest.test130_wrapsWebRuntimeExceptionInWebRuntimeExceptionWithErrorCode_Pass(WebRuntimeExceptionTest.java:284)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:498)
at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
at org.junit.rules.ExpectedException$ExpectedExceptionStatement.evaluate(ExpectedException.java:168)
at org.junit.rules.RunRules.evaluate(RunRules.java:20)
at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
at org.junit.runner.JUnitCore.run(JUnitCore.java:160)
at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:78)
at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:212)
at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:68)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:498)
at com.intellij.rt.execution.application.AppMain.main(AppMain.java:140)

Es wird mit der Erzeugung einer Applikations-Exception eine UUID generiert die zu spaeteren Zwecken zur Suche verwendet werden kann. Die UUID wird auch bei einem REST-Aufrufen an den Aufrufer weitergeleitet.

Damit die passiert muss der Interceptor 'ExceptionHandlerInterceptor' an dem REST-endpoint annotiert werden, siehe unten.

	@ExceptionHandlerQualifier
	@Interceptors(ExceptionHandlerInterceptor.class)
	public class MockResource implements MockResourceInterface
	
Der Interceptor muss im beans.xml-File nicht registriert werden.
	
Im Interceptor werden ALLE Exceptions gefangen und in ein Response-Objekt umgewandelt. Bei NICHT Applikations-Exceptions werden fuer 'Response.Status', 'ErrorCode' default-Werte angenommen, sowie keine UUID.
Das Response Objekt sieht dann wie folgt aus:
Der Status wird gesetzt und kann im Client, wie folgt abgerufen werden (im Fehlerfall):

	if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
	
Das mitgeliefert Payload sieht, wie folgt aus:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<apiErrorResponseObject>
		<uuid>b4701388-ac27-47bb-ad26-711160fc71fc</uuid>
		<status>INTERNAL_SERVER_ERROR</status>
		<errorCode>TEST_00102</errorCode>
		<shortErrorCodeDescription>Invalid parameters</shortErrorCodeDescription>
	</apiErrorResponseObject>
	
	oder als JSON
	
	{"ErrorCode":"TEST_00102","ShortErrorCodeDescription":"Invalid parameters","Uuid":"10e14688-b342-4bd3-a0fc-bf064b35546d","Status":"Internal Server Error"}


