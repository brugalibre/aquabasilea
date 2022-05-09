# aquabasilea

Automatisches Buchen von Sportkursen von Aquabasilea / Migros

Die gewünschten Kurse sowie ihre Startzeit können in der Datei 'weeklyCourses.yml' konfiguriert werden. Das File
befindet sich im Ordner 'courses' Der Kurs wird dann jeweils 24h vorher gebucht. Vor dem "scharfen" Buchen findet
jeweils ein Testlauf statt. Es kann konfiguriert werden, wie viel Minuten der Testlauf früher stattfindet

# Konfiguration:

Die Konfiguration wird in der Datei 'config/aquabasilea-kurs-bucher-config.yml' vorgenommen. Ebenfalls können dort die
URL der Login-Seite und der Seite mit der Kurs-Übersicht konfiguriert werden

# Web-Ui

## Status

Die linke Kachel zeigt den Status der app an. D.h. wann findet der nächste Testlauf bzw. die nächste Buchung statt.
Ebenfall kann dort die App pausiert bzw. reaktiviert werden

## Kurse verwalten

Die Kurse können im web-ui in der Kachel 'Kurse verwalten' via rest-api entfernt, geändert oder pausiert werden.
Pausierte Kurse sind inaktiv und werden nicht für die Ermittlung des nächsten Kurses herangezogen. Der aktuelle Kurs,
d.h. derjenige auf den die App wartet und welcher als nächstes Gebucht wird, wird durch eine blaue Glocke signalisiert.

Werden alle Kurse entfernt pausiert sich die App automatisch. Werden dann wieder Kurse hinzugefügt, muss die App manuell
wieder reaktiviert werden

## Hinzufügen von Kursen

In der linken unteren Kachel können neue Kurse hinzugefügt werden

# SMS-Alerting:

Jeweils nach dem Test,- sowie dem scharfen Lauf wird ein sms versendet, mit Informationen über den Ausgang. Die
Konfiguration dazu erfolgt im File 'config/alert/aquabasilea-alert-notification.yml'.

# Authentifizierung

Damit die Authentifizierung des Aquabasilea-Benutzers funktioniert, mÃ¼ssen zwei key-stores angelegt werden:
- aquabasilea.keystore
- aquabasilea-keystore.keystore

Letzter ist quasi der Super-Keystore, welcher die PasswÃ¶rter fÃ¼r den eigentlichen Keystore enthÃ¤lt. Dieser kann mit einem eigenen Password geschÃ¼tzt werden.
Zum Erstellen siehe auch AquabasileaKeyStore.java bzw. KeyUtils.java.

Die Klasse WriteSecretToKeyStore.java bietet Methoden, um fÃ¼r einen Benutzer (=alias) sein Password sowie den API-Key fÃ¼r den sms-Dienst zu speichern. Als Argument muss dasselbe Password
Passwort verwendet werden, welches schon zur Erstellung vom KeyStore verwendet wurde.
D.h. Der WriteSecretToKeyStore muss zweimal eingesetzt werden, einmal um das Passwort des Aquabasilea-Benutzers zu speichern und ein weiteres Mal fÃ¼r den API-Token des sms-Send Services