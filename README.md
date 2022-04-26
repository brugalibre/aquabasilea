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
