package de.bomc.poc.tls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		
		http.authorizeRequests().anyRequest().authenticated().and().x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)")
				.userDetailsService(userDetailsService());
	}

	@Bean
	public UserDetailsService userDetailsService() {
		
		return (username -> {
			if (username.equals("localhost")/* || username.equals("codependent-client2")*/) {
				return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
			} else {
				return null;
			}
		});
	}

//    /*
//     * Enable x509 client authentication.
//     */
//    @Override
//    protected void configure(final HttpSecurity http) throws Exception {
//        http.x509();
//    }
//
//    /*
//     * Create an in-memory authentication manager. We create 1 user (localhost which
//     * is the CN of the client certificate) which has a role of USER.
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    	
//        auth.inMemoryAuthentication()
//                .withUser("localhost_")
//                .password("none")
//                .roles("USER");
//    }
    
	
/*
Dies ist die archivierte Version des Blogs vom 05.01.2017. Aktuelle Beiträge findest du unter thomas-leister.de
RSS Feed abonnierenKontaktÜber mich und diesen BlogBlog UnterstützenPGP VerschlüsselungKeybase.iotrashserver.net XMPP Server
THOMAS-LEISTER.DE
AktuellLinuxMeine MeinungMailserver
 
Eine eigene OpenSSL CA erstellen und Zertifikate ausstellen
OpenSSL bringt umfassende Werkzeuge mit, um eine eigene, kleine Certificate Authority (CA) betreiben zu können. Die Nutzung einer eigenen CA ist besonders dann sinnvoll, wenn mehrere Dienste über SSL/TLS kostenlos abgesichert werden sollen. Neben dem Nachteil, dass die eigene CA vor Benutzung zuerst auf den Clientrechnern bekannt gemacht werden muss, gibt es aber auch einen Vorteil: Mit einer CA unter der eigenen Kontrolle ist man im Zweifel auf der sicheren Seite: In den letzten Jahren wurden immer wieder Fälle bekannt, in denen große Certificate Authorities falsche Zertifikate ausgestellt haben. Es gibt Grund genug, die Vertrauenswürdigkeit großer CAs anzuzweifeln.

Mit dieser Anleitung werdet ihr in der Lage sein, beliebig viele Zertifikate für eure Dienste ausstellen zu können, die in jedem Browser als gültig erkannt werden, sofern vorher das Root-Zertifikat eurer CA importiert wurde.


Certificate Authority (CA) erstellen
Zu Beginn wird die Certificate Authority generiert. Dazu wird ein geheimer Private Key erzeugt:

openssl genrsa -aes256 -out ca-key.pem 2048
Der Key trägt den Namen „ca-key.pem“ und hat eine Länge von 2048 Bit. Wer es besonders sicher haben will, kann auch eine Schlüssellänge von 4096 Bit angeben. Die Option „-aes256“ führt dazu, dass der Key mit einem Passwort geschützt wird. Die Key-Datei der CA muss besonders gut geschützt werden. Ein Angreifer, der den Key in die Hände bekommt, kann beliebig gefälsche Zertifikate ausstellen, denen die Clients trauen. Die Verschlüsselung dieses Keys mit einem Passwort gibt zusätzlichen Schutz. Das gewünschte Passwort wird bei der Generierung abgefragt.

Einen geheimen Key für die CA gibt es nun also schon – fehlt noch das Root-Zertifikat, das von den Clients später importiert werden muss, damit die von der CA ausgestellten Zertifikate im Browser als gültig erkannt werden. Das Root-Zertifikat „ca-root.pem“ wird mit folgendem Befehl erzeugt: (ggf. wird das Passwort für den vorher erstellen Key abgefragt!)

openssl req -x509 -new -nodes -extensions v3_ca -key ca-key.pem -days 1024 -out ca-root.pem -sha512
In diesem Fall wird die CA 1024 Tage lang gültig bleiben. Während der Generierung werden das Passwort für die CA und einige Attribute abgefragt (hier ein Beispiel):

Country Name (2 letter code) [AU]:DE
State or Province Name (full name) [Some-State]:BY
Locality Name (eg, city) []:Landshut
Organization Name (eg, company) [Internet Widgits Pty Ltd]:trashserver.net
Organizational Unit Name (eg, section) []:IT
Common Name (eg, YOUR name) []:trashserver.net
Email Address []:sslmaster@domain.com
Root-Zertifikat auf den Clients importieren
Damit ein Rechner die selbst ausgestellten Zertifikate akzeptiert, muss auf diesem Rechner das Root-Zertifikat (Public Key der CA) importiert worden sein. Die Root-CA Datei ist „ca-root.pem“.

Mozilla Firefox / Thunderbird
Mozilla Firefox verwaltet Zertifikate selbst. Ein neues Zertifikat wird importiert unter „Einstellungen => Erweitert => Zertifikate => Zertifikate anzeigen => Zertifizierungsstellen => Importieren“. Wählt die Datei „ca-root.pem“ aus. „Wählt die Option „Dieser CA vertrauen, um Websites zu identifizieren“.

Chromium / Google Chrome
„Einstellungen“ => „Erweiterte Einstellungen anzeigen“ (unten) => „HTTPS/SSL“ => „Zertifikate verwalten“ => „Zertifizierungsstellen“ => „Importieren“ => „ca-root-pem“ auswählen => „Diesem Zertifikat zur Identifizierung von Websites vertrauen“

Debian, Ubuntu
sudo cp ca-root.pem /usr/share/ca-certificates/myca-root.crt
sudo dpkg-reconfigure ca-certificates
=> Neue Zertifikate akzeptieren

Ein neues Zertifikat ausstellen
Die CA ist nun fertig und kann genutzt werden. Zeit, das erste Zertifikat auszustellen!

Grundlage ist immer ein privater Schlüssel. Wie auch bei der CA wird dieser Private Key erzeugt:

openssl genrsa -out zertifikat-key.pem 4096
An dieser Stelle ein Passwort zu setzen ist in den meisten Fällen nicht besonders sinnvoll. Ein Webserver, der des Zertifikat verarbeitet, müsste bei jedem Start das Passwort abfragen. Das ist in der Praxis mehr lästig und hinderlich als nützlich. (=> Passwortfelder einfach leer lassen). Die Schlüssellänge wurde hier auf paranoide 4096 Bit gesetzt. 2048 sind auch okay ;)

Nun wird eine Zertifikatsanfrage erstellt, bei der wieder einige Attribute abgefragt werden. Besonderheit ist hier: Das Feld „Common Name“ muss den Hostnamen des Servers tragen, für den es gültig sein soll. Soll z.B. die Verbindung zum Rechner mit der IP-Adresse „192.168.2.2“ mit dem Zertifikat abgesichert werden, muss die IP-Adresse hier angegeben werden. Soll das Zertifikat dagegen für die Domain thomas-leister.de gelten, muss das ebenso eingetragen werden. Es ist auch möglich, sog. Wildcard-Zertifikate zu erstellen. Wird z.B. „*.thomas-leister.de“ als Common Name angegeben, gilt das Zertifikat für alle Domains von thomas-leister.de, also login.thomas-leister.de, start.thomas-leister.de usw. – nicht aber für thomas-leister.de selbst. Das Challenge Passwort wird nicht gesetzt (leer lassen).

openssl req -new -key zertifikat-key.pem -out zertifikat.csr -sha512
Sobald die Zertifikatsanfrage „zertifikat.csr“ fertiggestellt ist, kann sie von der CA verarbeitet werden. Dabei entsteht der öffentliche Schlüssel (Public Key) zum angefragten Zertifikat. Dieser wird zusammen mit dem Private Key des Zertifikats für die Verschlüsselung benötigt.

Mit folgendem Befehl wird ein Public Key „zertifikat-pub.pem“ausgestellt, der 365 Tage lang gültig ist:

openssl x509 -req -in zertifikat.csr -CA ca-root.pem -CAkey ca-key.pem -CAcreateserial -out zertifikat-pub.pem -days 365 -sha512
(Das Passwort für die CA wird erneut abgefragt.) Die Zertifizierungsanfrage zertifikat.csr kann gelöscht werden – sie wird nicht mehr benötigt. Übrig bleiben Private Key und Public Key des neuen Zertifikats (zertifikat-key.pem und zertifikat-pub.pem) sowie Private- und Public Key der CA (ca-key.pem und ca-root.pem)

Die Zertifikate nutzen
In der Webserver-Konfiguration müssen üblicherweise drei Zertifikatsdateien angegeben werden:

Private Key des Zertifikats (zertifikat-key.pem)
Public Key des Zertifikats (zertifikat-pub.pem)
Public Key der CA (ca-root.pem)
Der Public Key der CA kann auch an die Public Key Datei des Zertifikats angehängt werden:

cat ca-root.pem >> zertifikat-pub.pem
Diese Integration ist immer dann nötig, wenn es keinen Parameter in der Konfiguration gibt, bei dem man das Rootzertifikat einer CA angeben kann – beim XMPP Server Prosody und beim Webserver Nginx ist das z.B. der Fall: Hier können nur Public- und Private Key des Zertifikats angegeben werden.

Wie ihr SSL/TLS für euren Webserver nutzt könnt ihr in diesen beiden Beiträgen nachlesen:

Apache Webserver mit SSL
Nginx Webserver mit SSL
 

Quellen: http://datacenteroverlords.com/2012/03/01/creating-your-own-ssl-certificate-authority/

Post published on 1. September 2014 | Last updated on 17. Dezember 2014
Tags: #CA  #Open Source  #Server  #SSL  #TLS  #Ubuntu  #Verschlüsselung  
Diesen Blog unterstützen
Wenn Dir der Beitrag gefallen hat, freue ich mich über einen kleinen Obolus :-)Bitcoin QR Code
PayPal-Seite: https://www.paypal.me/ThomasLeister
Meine Bitcoin-Adresse: 15z8 QkNi dHsx q9WW d8nx W9XU hsdf Qe5B 4s

Siehe auch: Unterstützung
Diesen Beitrag teilen
spenden 
spenden 
teilen 
e-mail 
twittern 
teilen 
teilen 
teilen 
mitteilen 
Informationen zum Autor

Thomas Leister
Geb. 1995, Kurzhaar-Metaller, Geek und Blogger. Nutzt seit Anfang 2013 ausschließlich Linux auf Desktop und Servern. Student der Automobilinformatik an der Hochschule für angewandte Wissenschaften in Landshut.
Weitere Beiträge zum Thema
SSH Anmeldung über privaten Schlüssel und Passwortauthentifizierung abschalten
Android startet nicht mehr nach Verschlüsselung und neuer ROM – Lösung
Nginx: PHP-FPM unter Ubuntu Server 14.04 installieren und einrichten
Einzeiler: Dateien mal eben über’s Netzwerk schubsen
76 thoughts on “Eine eigene OpenSSL CA erstellen und Zertifikate ausstellen”
Mööp
On 2. September 2014 at 19:24
Hallo, danke für die Anleitung. Ich habe sie verwendet, damit ich ein selbstsigniertes Zertifikat erstellen kann, das Android akzeptiert. Hat leider nicht geklappt, die App CAdroid zeigt an, dass die CA-flags nicht gesetzt sind. Liegt der Fehler bei mir oder geht das mit der Methode überhaupt nicht?
Grüße

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 2. September 2014 at 19:43
Hi, danke für den Hinweis. Ich habe nachgebessert und bei der Zeile

openssl req -x509 -new -nodes -key ca-key.pem -days 1024 -out ca-root.pem

Ein -extensions v3_ca hinzugefügt (siehe im Beitrag oben).

Damit sollte es funktionieren :)

LG Thomas

Reply
Mööp
On 2. September 2014 at 20:31
Hallo, wow, das nenne ich eine schnelle Antwort. Das hat mich motiviert das Ganze gleich noch mal durchzuführen.

Leider ändert sich nichts. Die CA-flags scheinen nicht gesetzt zu sein. Muss man das CA-root-Zertifikat noch bei Android importieren?
Hier ein Screenshot was CAdroid sagt:
https://dl.dropboxusercontent.com/u/6245414/Screenshot_2014-09-02-20-12-55.png

Wenn ich es per Hand mache, also zertifikat-pub.pem aufs Handy kopiere und über die Einstellungen importiere, dann sagt er zwar es wurde installiert, im Zertifikatespeicher taucht es aber nicht auf. Das ist das, was man bei einem nicht-offiziell-CA-Zertifikat bei Android erwartet. Meine Hoffnung dieser Anleitung war, dass es mit einem selbsignierten Zertifikat funktioniert.

Ich habe mal testweise das CA-Zertifikat bei Apache2 in der default-ssl mit dem Tag „SSLCACertificateFile“ mit angegeben, damit hat dann aber CAdroid ein Problem:
https://dl.dropboxusercontent.com/u/6245414/Screenshot_2014-09-02-20-11-59.png

Hintergrund ist, dass ich DavDroid (CalDav/CardDav-Adapter für Android) in Verbindung Owncloud zum laufen bringen will, damit ich mich endlich von google-Sync verabschieden kann. Hätte nicht gedacht, dass es so absurd umständlich ist. ^^
Vllt wäre das einfachste, sich bei startssl.com ein CA-Zertifikat zu holen.

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 2. September 2014 at 20:34
Hi,

auf deinem Smartphone wird nur (!) Das root-Zertifikat importiert, nicht das zertifikat-pub.pem! ;)
Hast du die CA nochmal mit den neuen Einstellungen erstellt und darauf ein Zertifikat erstellt?

LG Thomas

Reply
Mööp
On 2. September 2014 at 20:47
Huch, ich Dödel. Ja, ich bin irgenwie davon ausgegangen, dass man mit dem ca-root.pem ein Zertifikat erstellt und dieses dann importiert. Mit dem ca-root.pem funktionierts tatsächlich. Vielen Dank!

Irgendwie blöd, dass Android jetzt dauerhaft ne Benachrichtigung mit Warnung zeigt. Unter Einstellungen-Apps-Zertifikats-Installer ist die Option zum Ausblenden der Benachrichtigung leider ausgegraut.

Schönes Blog btw.

Reply
Mööp
On 2. September 2014 at 20:47
Mist, nicht auf Antworten geklickt…

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 2. September 2014 at 20:49
Das mit der Warnung ist wirklich ärgerlich :-/ Welche Android Version nutzt du? Ich habe unter Android 4.4 keine solche Benachrichtigung.

LG Thomas

Reply
Mööp
On 2. September 2014 at 21:08
Ok, das ist interessant, da ich die selbe Version verwende. Ich verwende OmniRom, Android 4.4.4.

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 2. September 2014 at 21:13
Bei mir ist es CyanogenMod -ebenfalls mit Android 4.4.4 …

Reply
Mööp
On 2. September 2014 at 22:04
nvm, war beim ersten mal zu blöd zum wegwischen und drauftippen hatte sie auch nicht entfernt. Aber jetzt ist alles gut^^

Reply
Paul
On 5. September 2014 at 12:18
Ich finde bei mir leider kein „zertifikat-key.pem“ kann es auch aus der Beschreibung nicht ersehen an welcher Stelle das erzeugt werden soll. Habe ich etwas übersehen?

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 5. September 2014 at 12:22
Hi,

tatsächlich hatte ich einen Fehler in meiner Anleitung. An einer Stelle hatte ich „zertifikat-priv.pem“ statt „zertifikat-key.pem“ geschrieben. Das war gleich am Anfang der Generierung des End-Zertifikats. Hab das ausgebessert.

LG Thomas

Reply
Anonymous
On 6. September 2014 at 15:20
Junge, lass lieber die Finger von Dingen, die du nicht verstehst. Schnapp dir lieber ein Zertifikat von CACert und spiel damit noch etwas im Sandkasten. Wenn du kapiert hast, dass es kontraproduktiv ist, wenn jeder seinen eigene CA erstellt, darfst du an Kinderspieltisch…

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 6. September 2014 at 15:31
Du scheint nicht ganz verstanden zu haben, um was es hier geht. Es geht hier nicht darum, Dienste für tausende Nutzer abzusichern, sondern nur meine eigenen, die nur ich und ein paar andere Leute nutzen. (Freunde, Familie, …).

Dafür braucht es keine große CA und schon gar keine kostenpflichtigen Zertifikate. Es ist dann völlig egal, ob jeder seine eigene CA betreibt oder nicht, weil ich auf die Signaturen anderer Instanzen total verzichten kann. Mir selbst vertraue ich doch am meisten, oder nicht?

Gruß, Thomas

Reply
Oliver
On 20. Oktober 2014 at 11:55
Hallo Thomas,

das sehe ich genauso wie Dir. Momentan würde ich sogar sagen, dass ich meiner CA mehr traue als einer großen CA, weil ich hier eben genau weiss welche Zertifikate mit welcher Qualität erzeugt wurden und wer die Schlüssel hat. bei einer großen CA weiss ich das nicht.

Gruß und Danke
Oliver

Reply
Klaus
On 10. September 2014 at 17:06
Da arbeitet wohl jemand für eine CA, die für ein paar Handgriffe monatlich hunderte Euro haben will, ohne einen gesicherten Sicherheitsvorteil zu bieten…

Reply
c
On 25. Dezember 2014 at 15:37
Das klingt nach richtig viel Ahnung, was CAcert betrifft. Bitte mal die Ereignisse des letzten Jahres reflektieren und dann nochmals schlau daherreden.

Reply
Reiner Saddey
http://blog.saddey.netOn 17. März 2015 at 22:08
Naja, vielleicht komt bei dem Spielen ja doch was heraus?

Z.B. Private Privacy, oder Good Piovacy. Hm… oder Pretty Privacy.

Ich hab’s: Pretty Good Privacy!

Was, gibt’s schon? Ja, nennt sich PGP :-)

Reply
Marco
On 11. September 2014 at 09:44
Hallo Thomas,

ich finde deinen Blog sehr inspirierend und auch qualitativ sehr hochwertig!
Bitte mach weiter so!

Zum Thema-CA: Da hat der Vorredner wohl wirklich keine Ahnung von der Materie, denn eine eigene CA wird oftmals benötigt, also schade das solche Kommentare überhaupt auftauchen.

Ich finde deinen Post sehr gut und werde ihn bald mal ausprobieren.

Vielen Dank dafür!

Reply
Simon
On 4. November 2014 at 00:16
Ein wirklich sehr hilfreicher Beitrag von hoher Qualität. Hat mir sehr geholfen im SSL-Jungle.

Leider schaffe ich es nicht das CA Zertifikat in Plesk zu laden. Ich bekomme den Fehler „Fehler: Das CA-Zertifikat kann nicht festgelegt erden: Ungültiges Zertifikatsformat“, wobei ich bereits das von Plesk angebotene Plain-Text-Field zur Eingabe genutzt habe.
Hast Du zufälligerweise eine Ahnung was das Problem sein könnte?

Danke!
Simon

Reply
Torsten
On 4. November 2014 at 08:06
Hallo,
Erst einmal Danke für die Anleitung. Die Erstellung der Zertifikate hat damit wunderbar funktioniert. Die Desktop Clients (Windows/Linux) können sich auch einwandfrei mit dem SSL Server verbinden. Ein Problem habe ich allerdings mit meinem Android Phone (CyanogenMod 4.2.2). Das CA Zertifikat konnte ich erfolgreich importieren. Wenn ich jetzt allerdings auf meinen SSL Server (https://192.168.0.30/owncloud/) zugreifen möchte, bekomme ich die Fehlermeldung das der Name der Webseite nicht mit dem Namen im Zertifikat übereinstimmt! Kann mir da einer weiterhelfen?
Als Common Name hatte ich die IP eingetragen (192.168.0.30), im Server Zertifikat.

Gruß

Reply
Florian
On 16. November 2014 at 08:01
Super Anleitung! Danke!
Wichtig wäre noch für JEDE Key-Erstellung den Parameter -sha512 einzufügen. Andernfalls gibt es bei https://www.ssllabs.com/ssltest/ Probleme wegen der Signatur (SHA1! -ist bei openssl von debian wheezy so voreingestellt).

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 16. November 2014 at 09:30
Danke für den Hinweis! Hab das im Beitrag korrigiert.

LG Thomas

Reply
Florian
On 16. November 2014 at 16:03
openssl x509 -req -in zertifikat.csr -CA ca-root.pem -CAkey ca-key.pem -CAcreateserial -out zertifikat-pub.pem -days 365 -sha512

fehlt noch. ;)

Reply
Florian
On 16. November 2014 at 16:04
Bein Signieren durch die CA unter Ein neues Zertifikat ausstellen fehlt es noch. ;)

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 16. November 2014 at 16:27
So, jetzt müsste es aber passen … ;)

Reply
Florian
On 16. November 2014 at 19:20
Ja. Perfekt. Nur des3 würde ich noch durch aes256 ersetzen. Scheint derzeit zukunftssicherer. :)

Reply
Andreas
On 22. November 2014 at 20:29
Wie kann ich aus den pem-Files ein pfx-File für Windows Server erzeugen?

Reply
Florian
On 24. November 2014 at 09:28
openssl pkcs12 -export -out certificate.pfx -inkey privateKey.pem -in certificate.pem-certfile CACert.pem

Reply
Anonymous
https://matthias-frech.deOn 26. November 2014 at 22:01
Hi Thomas,

ich habe mal wieder eine Frage.
Ich habe eine SSL Verbindung eingerichtet und falls die Seite über http aufgerufen wird, dann findet eine Weiterleitung statt.
So weit alles gut.
Wenn ich auf die owncloud oder auch auf phpmyadmin zugreife dann funktioniert alles reibungslos.
Jedoch wenn ich auf wordpress bzw. meine aktuelle Startseite zugreiffe, dann meldet Fierefox bzw. IE, das er unsichern Inhalt blockiert. Firefox meint dazu das ein Teil nicht per https übertragen wird.

Hast du eine Idee an was das liegen kann? Bzw. wie ich herausfinden kann an was das liegt?

Danke.

Gruß Matthias

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 27. November 2014 at 11:57
Hi,

das liegt wahrscheinlich daran, dass in WordPress deine Ressourcen (CSS File, Bilder, Videos etc) nicht mit einer https Adresse eingebunden werden sondern immer noch via http. Das muss man in WordPress dann entsprechend ändern und z.B. die Adressen anpassen.

LG Thomas

Reply
Matthias
https://matthias-frech.deOn 27. November 2014 at 14:26
Hi,

super Danke.
Genau das war der Fehler.

Reply
Matthias
On 29. November 2014 at 15:48
Vielen Dank für den Beitrag. Sehr hilfreich und verständlich. Kalender und Kontakte funktionieren jetzt mit ownCloud und Android einwandfrei. Musste nur suchen, wo die Dateien abgelegt wurden… unter /root. Wieder was gelernt :-) Bei der Erzeugung von Zertifikaten mittels Plesk fehlte stets das CA-Zertifikat.

Reply
Sabine Bergemann – Onlineshop
http://www.shopcloud.io/On 17. Dezember 2014 at 12:48
Hier ist ein kleiner Fehler in der Beschreibung. Die Option -des3 wird im Befehl nicht verwendet, sondern -aes256
„… Die Option „-des3″ führt dazu, dass der Key mit einem Passwort geschützt wird. „

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 17. Dezember 2014 at 12:57
Danke! Ist korrigiert.

Reply
Micha
On 25. Dezember 2014 at 03:12
Hallo Thomas.
Ich habe ein kleines Problem.
Ich habe mir nach deiner Richtig guten anleitung eine CA erstellt. Mit dieser CA habe ich dann unter CAs erstellt (3). Bis dahin klappt alles wunderbar auch mit den Zertifizierungspfaden. Wenn ich jetzt aber hingehe und mit einer der unter CAs ein Server Zertifikat erstelle, kann ich das Zertifikat zwar erstellen, aber die Zertifizierungspfade sind nicht so wirklich da. Es steht als aussteller der name der unter CA da alerdings ist diese bei den Zertifizierungspfaden nicht eingetragen. Wenn ich mir aber die Zertifizierungspfade der unter CA anschaue ist dort sowohl die unter als auch die haupt CA eingetragen.

Hoffe das du verstehst was mein Problem ist und mir helfen kannst.
MFG Micha

Reply
tim
On 29. Dezember 2014 at 03:24
Danke für die tolle Anleitung!

Reply
Andy
On 30. Dezember 2014 at 17:51
Hallo Thomas,
Wie kann man eigentlich ein EV-Zertifikat selbst erstellen?
Gruß Andy

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 2. Januar 2015 at 11:51
Hi,

nein, ein EV Zertifikat kann man nicht selbst erstellen, weil dieses beim Browserhersteller bzw OS Hersteller hinterlegt sein muss.

Reply
Schmitho
On 16. Januar 2015 at 09:27
Schöne Anleitung, aber dennoch doch was verwirrend, wenn man mal die Datei Endungen einfach nur als „Unwissender“ betrachtet. Hier schreibst du immer was von PEM File, in anderen Anleitung heißen die CRT….Haben die untershciedliche FUnktionen?

Die zweite Frage ist, ich habe nach Deiner Anleitung ein Zertifikat erstellt, welche ich für Lighttpd gebrauchen möchte.

Habe ich rigenwo ein Denkfehler, dennich bekomme bei folgender Konfiguration:

ssl.pemfile = „/srv/ssl/zertifikat-pub.pem“
ssl.ca-file = „/srv/ssl/ca-root.pem“

folgende FEhlermeldung beim restart des Dienstes:

[….] Starting web server: lighttpd2015-01-16 09:27:03: (network.c.572) SSL: couldn’t read private key from ‚/srv/ssl/zertifikat-pub.pem‘
failed!

Die Berechtigungen sind so gesetzt das zum Testen jeder „Lesen“ kann.

Gruss,
Schmitho

Reply
rurcoasteagle
https://alexgast.deOn 24. Januar 2015 at 14:25
Hi Schmitho,

das PEM-File und das CRT-File können gleich sein, nur mit einer anderen Endung. Mehr dazu findest du auch unter http://serverfault.com/questions/9708/what-is-a-pem-file-and-how-does-it-differ-from-other-openssl-generated-key-file

Wo die einzelnen Endungen noch einmal erläutert werden.

Die LightHTTPD-Fehlermeldung rührt daher, dass er in dem zertifikat-pub.pem deinen privaten Schlüssel nicht finden kann. Entweder weil er nicht vorhanden ist (wo ich von ausgehe, aufgrund des Namens) oder weil die Passphrase dazu fehlt.

Probiere es mal, indem du bei dem letzten Befehl mit dem du das zertifikat-pub.pem erzeugst noch ein “ -keyout zertifikat-pub.pem“ hinzufügst.
Dann sollte openssl deinen privaten Schlüssel da mit hinzufügen und lighthttpd kann damit was anfangen.

Kurz zur Erläuterung:

Den privaten Schlüssel benötigt der HTTP-Server um den Traffic zu verschlüsseln. Dein Browser entschlüsselt das dann mit dem öffentlichen Schlüssel. Sendest du nun weitere Anfragen (GET, POST, PUT, etc..) werden diese mit dem öffentlichen Schlüssel verschlüsselt und der Server entschlüsselt diese mit dem Privaten. Ohne privaten Schlüssel ist also keine Kommunikation möglich.

Reply
brubbel
On 13. Februar 2015 at 18:14
Hi,

eine wirklich gute Anleitung. Was für mich (und eventuell auch andere) interessant ist, wie man verschiedene Subdomains und die Hauptdomain in einem Zertifikat unterbringt. Ich habs mittlerweile geschafft, und das Zauberwort heißt „subjectAltNames“ (SAN). Dazu muss man etwas in der openssl.cnf rumspielen, aber wäre cool, wenn du das vielleicht noch in diese oder eine andere Anleitung mit aufnehmen könntest.

So braucht man nämlich keine zwei Zertifikate für die Hauptdomain und z.B. die www Subdomain.

Reply
Holm
On 28. April 2015 at 18:28
Hi,
bin ebenfalls dank dieser Anleitung auf der Spur zur eigenen CA. Gern möchte ich auch SubDomains mit absichern. Habe aber leider noch keinen Weg gefunden. Kannst Du mir deine Vorgehensweise schildern? Danke.
Gruß

Reply
Ernst Neger
On 9. November 2015 at 23:52
Hallo Holm und brabbel,

der Befehl, den Ihr sucht, lautet:

# nano multipleHostnames.cnf

subjectAltName=DNS:*.whatever.com,DNS:whatever.com

Bei der Erstellung des Zertifkates dann mittels -extfile Switch die Config-Datei angeben (aus OpenSSL Cookbook):

openssl x509 -req -days 365 -in fd.csr -signkey fd.key -out fd.crt -extfile multipleHostnames.cnf

Reply
Tom
On 7. April 2015 at 22:12
Hallo, wirklich tolle Anleitung!

Ich habe nur das Problem, dass die Zertifikate keinen „BasicConstraints=CA:false“ haben. Kann man das irgendwie per Commandline angeben, oder muss ich dafür eine config-Datei erstellen?

LG Tom

Reply
Ernst Neger
On 9. November 2015 at 23:48
Hallo Tom,

Du musst eine Config-Datei (in diesem Fall conf.cnf) erstellen, in die Du – beispielsweise – Folgendes reinschreibst:

# nano conf.cnf

subjectAltName=DNS:*.whatever.com,DNS:whatever.com # Gültigkeit eines Zertfikats für mehrere Subdomains

basicConstraints=critical,CA:true # Setzt das von Dir gewünschte Flag im endgültigen Zertifikat auf TRUE

keyUsage=digitalSignature,keyEncipherment # Einschränkung der Nutzbarkeit des zu erstellenden Zertfikats

extendedKeyUsage=serverAuth,clientAuth # Weitere Einschränkung der Nutzbarkeit des zu erstellenden Zertfikats

Beim Erstellen bzw. Signieren des Zertifikats mittels bspw. „openssl x509 -req -days 365 -in owncloud.csr -signkey owncloud.key -out owncloud.crt -extfile conf.cnf“ musst Du dann diese Config-Datei über den -extfile Switch angeben (merke: Beim Erstellen des eig. Zertifikats aka CRT, nicht schon beim Erstellen eines Certificate Signing Requests aka CSR).

Reply
Eddy
On 29. April 2015 at 19:10
Hallo,
ich wollte mal meinen besten Dank für die tolle Anleitung aussprechen.

VG Eddy

Reply
Felix
On 19. Mai 2015 at 03:10
Ich stelle mir gerade eine blöde – aber vielleicht doch nicht so blöde Frage: Wie ist eigentlich der geordnete Weg, um Zertifikate zu erneuern, wenn die alten auslaufen?

Nebenbei: Was hältst Du von XCA?

PS: Es ist toll und hilfreich, was Du hier machst!

Reply
Chris
On 9. Juni 2015 at 23:41
Hallo Thomas,

wie von vielen schon gesagt: Danke!

Ich bin hier gelandet weil ich mit ein ssl key/cert fuer dovecot erstellen wollte. Leider bin ich wohl zu blöd. Welche der generierten Dateien bzw deren pfad muss ich denn in die 10-ssl.conf schreiben?

danke

Reply
Hannes
http://www.gibbetnich.deOn 1. September 2015 at 10:03
Thomas, „Geb. 1995“ – ein echtes Frühtalent… ;-)

Reply
Steffen
On 16. September 2015 at 13:36
Hallo Thomas,
eine sehr gute Anleitung.
Allerdings ist mir ein Umstand aufgefallen, auf den ich mir keinen Reim machen kann. Evtl. hast Du oder ein Mitleser eine Idee:

Ich betriebe einen Webserver mit mehreren VHost. Die einzelnen VHost`s bedienen die entsprechenden Domänen. Dies habe ich mit der o.a. Anleitung per SSL und Zertifikat abgesichert. Das funktioniert wunderbar.
Jetzt wollte ich das auch mit der IP Adresse direkt machen. Also einen VHost erstellt, welcher auf die IP lautet und dazu ein Zertifikat erstellt. Wenn ich diese Seite aufrufe bekomme ich eine Zertifikatswarnung, dass der ausgestellte Name nicht zu dem Namen der Webseite passt. Wenn ich mir die Eigenschaften des Zertifikates in Safari anzeigen lasse, stimmen sie mit dem Namen der Webseite überein. Wenn ich sie mir im Internetexplorer anzeigen lasse, steht ausgestellt für: der selbe Name wie in der CA.

Genutzt wird ein Ubuntu 14.04 System mit Nginx.

Viele Grüße
Steffen

Reply
Stammi
On 3. Oktober 2015 at 13:00
Hallo zusammen!
Vielen Dank für diese hervorragende Anleitung. Hat selbst bei mir, der keine Ahnung hat, funktioniert:-).
Jetzt fehlt mir aber der letzte Schritt: Für meine Server-Applikation benötige ich einen keystore-file. Wenn ich es richtig verstanden habe, muss der keystore aus den Dateien ca-root.pem, zertifikat-key.pem und zertifikat-pub.pem bestehen. Ich habe zahlreiche Tools herunter geladen, mit den man keytores erstellen kann, allerdings bekomme ich nie alle drei Dateien da eingebunden, was auf meinem Server immer zu einer Fehlermeldung im keystore führt (java.security.UnrecoverableKeyException: Cannot recover key). Meine alten keystore-files hatten immer die Endung *.keystore, das kommt mir auch irgendwie ’spanisch‘ vor, weil ich in den Tools immer nur jks etc. wählen kann. Ansonsten wird mein ca-root.pem, auf meinem Android- und IOs-Gerät ordnungsgemäß angenommen…
Vielen Dank für eure Mühen und schöne Grüße…

Stammi

Reply
Sefan
On 27. Oktober 2015 at 22:47
Hallo
Hier wird ja beschrieben wie ein SSL Zertifikat authorisiert wird. Wie kann ein S/MIME Zertifikat ausgestellt werden?
Thx
St

Reply
Ernst Neger
On 9. November 2015 at 23:40
CA heisst „Certification Authority“ (laut OpenSSL Cookbook). Verleiht dem an sich sehr gut gelungenen Artikel noch den letzten Funken Korrektheit :)

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 10. November 2015 at 07:41
Wikipedia sagt, es geht beides ;)

LG Thomas

Reply
Andre
https://andrelutz.deOn 13. November 2015 at 11:25
Hallo,

kannst du mal einen Beitrag über https://letsencrypt.org/ schreiben?
Ich glaube da wäre viele daran intressiert

Gruß
Andre

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 13. November 2015 at 11:28
Kommt demnächst ;) LG Thomas

Reply
Peter
On 16. Dezember 2015 at 23:42
Hallo Thomas

Ich habe eine server.crt von startssl.com und möchte daraus eigene Client-Zertifikate erstellen, geht das überhaupt?

Reply
Bernd Schmitz
http://www.bernieschmitz.deOn 9. Januar 2016 at 13:38
Hallo,
ich betreibe einen HP Proliant DL380 G4p. Um auf die HP Ilo zugreifen zu können
benötige ich von einer CA ein bestätigtes Zertifikat, ansosnten wird das Zertifikat
als vertrauensunwürdig abgelehnt, ebenso verweigert mit das zugehörige
Java Applet die Zusammenarbeit, Fehler Zertifikat nicht vertrauenswürdig.
Deshalb habe ich mir nach Ihrer Naleitung mit OpenSSL eine eigene CA
eingerichtet, hat auch geklappt.
Nur der letzte Befehl beim öffentlichen Zertifikat (Public Certficate) funktioniert
nicht.
C:\OpenSSL-Win32\bin>openssl x509 -req -in zertifikat.csr -CA ca-root.pem -CAkey
ca-key.pem -CAcreateserial -out zertifikat-pub.pem -days 365 -sha1
Signature ok
subject=/C=US/ST=Texas/L=Houston/O=Hewlett-Packard Company/OU=ISS/CN=ilogb8638mf36
Getting CA Private Key
Error opening CA Private Key ca-key.pem
3916:error:02001002:system library:fopen:No such file or directory:.\crypto\bio\bss_file.c:398:fopen(‚ca-key.pem‘,’rb‘)
3916:error:20074002:BIO routines:FILE_CTRL:system lib:.\crypto\bio\bss_file.c:400:
unable to load CA Private Key
Danke für die tolle Anleitung!
Danke für die Fehlerbeseitigung!
Gruß
Bernie

Reply
Eppi
http://--On 26. Januar 2016 at 06:56
Hallo Thomas
Kompliment für Deinen tollen Artikel. Wirklich auch als Laie durchaus zu verstehen. Mir stellt sich noch folgende Frage:

Ich betreibe einen Raspi mit einer SmartHome Software darauf, welche ich von aussen, über ssl zugänglich machen möchte. Da ich über keine feste IP verfüge, geht das über no-iP.com (sprich epxxx.ddns.net)

Mir stellt sich nun die Frage, auf welchen CommenName ich das Zertifikat ausstellen muss? Muss ich angeben epxxxx.ddns.net, oder http://www.epxxxx.ddns.net, oder https://epxxxx.ddns.net oder gar die interne IP meines Raspi (192.168.xx.xx)?

Wenn ich das Zertifikat auf epxxx.ddns.net ausstelle und den Zugriff über den Browser teste, bekomme ich eine Warnmeldungen. Diese kann ich jedoch mit „ich kenne das Risiko, Ausnahme hinzufügen“ einfach weg klicken und komme so trotzdem von aussen auf den Raspi, ohne das ich mir das Zertifikat in den Browser importiert habe :-(

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 31. Januar 2016 at 20:06
Hallo, das Zertifikat musst du immer auf die Domain ausstellen, unter der du die SmartHome Software erreichst. bei dir also: epxxx.ddns.net
Möglicherweise ist die Smarthome Software allerdings so ungünstig gemacht (oder falsch konfiguriert) dass der Browser auf einmal nicht mehr mit deiner ddns.net Domain arbeitet, sondern mit einer lokalen IP-Adresse oder einem Hostnamen. In dem Fall solltest du diese anderen, ebenfalls genutzten Adressen als SubjectAlternate Names (Stichwort SAN) zu deinem Zertifikat hinzufügen)

Reply
Chris
On 13. Februar 2016 at 18:33
Hallo Thomas,

ich bekomme keine Eingabeaufforderungen bei „openssl genrsa -aes256 -out ca-key.pem 2048“ (für Passwort) und „openssl req -x509 -new -nodes -extensions v3_ca -key ca-key.pem -days 1024 -out ca-root.pem -sha512″ (für die Attribute). Hast Du eine Ahnung woran das liegt und ob das schlimm ist?
Außerdem meckert bei mir openssl bei openssl req -x509 -new -nodes -extensions v3_ca -key ca-key.pem -days 1024 -out ca-root.pem -sha512“ wegen der Option „-sha512“. Die finde ich in den manpage auch gar nicht.

Danke für deine Hilfe.
Chris

Reply
Hermes, Robert
On 15. Februar 2016 at 08:46
Hallo Herr Leister,

ich möchte ein Fremdzertifikat auf meiner Diskstation installieren, damit die Seite hermes-mix.eu in Zukunft über ssl ohne Zertifikatswarnung erreichbar ist. – Die Domain hermes-mix.eu wird also über DynDns in mein Büro auf meinen Büroserver umgeleitet, dafür nutze ich den Service von selfhost.eu.

Leider scheint das bei StartSSL generierbare Zertifikat (schon ausprobiert) dennoch die Fehlermeldung auszulösen.

Vielleicht liegt es daran, dass die Diskstation eben nicht auf einer Subdomain läuft, so wie bei den Videos von idomix beschrieben, was ich auch nicht will und ich nur über den Weg, die Diskstation auf einer Subdomain laufen zu lassen,
ferner für die Domain hermes-mix.eu ein separates SSL-Zertifikat zu kaufen, weiterkomme.

Für hilfreiche Tipps wäre ich sehr dankbar!

Mit freundlichen Grüßen

R.Hermes

Reply
Ein Leser
On 5. März 2016 at 08:03
Sehr geehrter Herr Leister!

Vielleicht wäre es sinnvoller wenn man schon so groß eine Anleitung ins Netz stellt, zu erklären mit welchen Programmen man dies macht und nicht nur zu garantieren und zu prahlen.

Mfg ein Leser

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 5. März 2016 at 09:24
Klingt für mich stark nach Trollen, trotzdem antworte ich mal:

Welche Programme genutzt werden dürfte für jemanden, der eine CA betreiben will, offensichtlich sein. Im großen und ganzen braucht man eine Kommandozeile und das OpenSSL Tool, welches bei gängigen Linux-Distributionen bereits installiert ist.

Reply
jonas
On 22. März 2016 at 14:13
Gibt es irgend einen sinnvollen Ort die erstellten Dateien abzulegen?
Und welche Dateiberechtigungen, Besitzer und Gruppen wären sinnvoll?

Reply
Thomas Leister
https://legacy.thomas-leister.de/ueber-mich-und-blog/On 22. März 2016 at 21:44
Wohin man die Dateien legt, ist geschmackssache. Ich lege sie gerne unter /etc/myssl/ ab. Key-Dateien so, dass nur root darauf zugreifen kann… und die Zertifikatsdateien (public) so ,dass jeder lesen kann, aber nur root schreiben kann.

LG Thomas

Reply
jonas
On 23. März 2016 at 10:38
Wow vielen Dank für die schnelle Antwort!

Reply
Norbert
http://fmg-gmbh.deOn 18. April 2016 at 17:12
Erstmal ein Lob für die gute Anleitung.
Leider schaff ich es nicht einen WEBDAV ssl Netzwerkordner im Windows Explorer einzurichten.
Habe eine pfx mit
openssl pkcs12 -export -out MeinZertifikat.pfx -inkey zertifikat-key.pem -in zertifikat-pub.pem -certfile ca-root.pem
erstellt und diese mit certmgr in Vertrauenswürdige Stammzertifikate importiert.
Bei Zertifikatsinformation steht, dass keine ausreichenden Informationen vorliegen, um dieses Zertifikat zu verifizieren.
Wo liegt der Fehler oder wie kann man das Problem eingrenzen.

Gruß
Norbert

Reply
DerSven
On 10. Juni 2016 at 21:33
Ich hatte meine owncloud so eingerichtet, dass der Zugriff nur mit Client-Zertifikat möglich war. In den Browsern und im Thunderbird-Kalender funktionierte das auch prima. Allerdings kann ich den OSX Kalender und den Owncloud Desktop Client für OSX nicht dazu bewegen, das Client-Zertifikat abzufragen. Schade, da musste ich diese Sicherung sehr ungern wieder rausnehmen…

Reply
Fabian Thielen
https://www.fabian-thielen.deOn 6. Juli 2016 at 21:28
Wenn ich versuche, die ca-root.pem in Chrome zu importieren, erhalte ich folgende Meldung:
http://puu.sh/pSzYV/cfb864606a.png (Der Dateityp ist nicht erkennbar. Wählen Sie eine andere Datei aus.)

Reply
David
On 11. Oktober 2016 at 16:06
Hallo Thomas,

vielen Dank für dein übersichtliches und nachvollziehbares Tutorial!
Ich denke ich konnte alle Schritte gut umsetzen, nur habe ich beim letzten Schritt doch ein ein Problem.

Du hast zwar einen Link zu einem Artikel genannt, welcher das erstellen eines VHosts beschreibt, aber leider bekomme ich es nicht hin.

Könnte mir jemand freundlicherweise eine Beispiel-Conf Datei für apache2 formulieren, welche die nötigen SSL-Dateien einbindet und die Dateinamen aus diesem Tutorial verwenden, damit ich es 100% nachvollziehen kann?

Vielen Dank im voraus!

VG

David

Reply
dibu
On 15. November 2016 at 12:05
# Mit folgendem Befehl wird ein Public Key „zertifikat-pub.pem“ausgestellt, der 365 Tage lang gültig ist: #
Müsste hier nicht „Public Zertifikat“ stehen?

Reply
ThomasW
On 16. November 2016 at 14:52
Hallo Thomas,

übersichtliche und verständliche Anleitung. Vielen Dank.

Reply
Reiner
On 1. Dezember 2016 at 17:52
Hallo,
erstmal Danke für die super Anleitung! Endlich auf die owncloud ohne diese nervige sicherheitswarnung :) Aber ein Problem habe ich: Für meinen Passwort manager „Safe in Cloud“ kann ich unter Android die Zertifikate installieren und auch nutzen, jedoch auf meine Win10 Pc nicht. Hab es über die owncloud Seite heruntergeladen und dann in „Vertrauenswürdige Stammzertifizierungsstellen“ installiert. Jedoch kommt dann imer der Fehler „Das Remotezertifikat ist laut Validierungstelle ungültig“ Hast du hierfür auch eine Idee? danke Reiner

Reply
Reiner
On 1. Dezember 2016 at 19:21
Hab die Lösung gefunden, hier der Weg unter Win 10, sollte unter Win 7 und 8 auch so (ähnlich funktionieren)falls in wer braucht:

Falls erforderlich die Dateien ca-root.pem und zertifikat-pub.pem von Apache auf PC kopieren

Start -> „mmc“ als Administrator aufsühren

Datei -> SnapIn hinzufügen/entfernen -> Zertifikate -> hinzufügen -> Compuerkonto auswählen -> Lokalen Computer -> Fertig stellen

Vertrauenwürdige Stammzertifizierungsstellen Rechts anklicken -> Alle Aufgaben -> Impotieren ->
Weiter -> Datei „ca-root.pem“ auswählen -> Weiter -> unter Vertrauenwürdige Stammzertifizierungsstellen installieren lassen

Vertrauenwürdige Stammzertifizierungsstellen Rechts anklicken -> Alle Aufgaben -> Impotieren
Weiter -> Datei „zertifikat-pub.pem“ auswählen -> Weiter -> unter Vertrauenwürdige Stammzertifizierungsstellen installieren lassen

Fertig!

Reply
Schreibe einen Kommentar
Deine E-Mail-Adresse wird nicht veröffentlicht.

[Your comment on this post]
Name 
E-Mail Address 
Website
ImpressumDatenschutztrashserver.net Jabber / XMPP Server
Wordpress Theme by Thomas Leister	
 */
	
}
