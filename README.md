# aquabasilea

Automatisches Buchen von Sportkursen von Aquabasilea / Migros

Die gew�nschten Kurse sowie ihre Startzeit k�nnen in der Datei 'weeklyCourses.yml' konfiguriert werden. Das File
befindet sich im Ordner 'courses' Der Kurs wird dann jeweils 24h vorher gebucht. Vor dem "scharfen" Buchen findet
jeweils ein Testlauf statt. Es kann konfiguriert werden, wie viel Minuten der Testlauf fr�her stattfindet

# Konfiguration:

Die Konfiguration wird in der Datei 'config/aquabasilea-kurs-bucher-config.yml' vorgenommen. Ebenfalls k�nnen dort die
URL der Login-Seite und der Seite mit der Kurs-�bersicht konfiguriert werden

# Web-Ui

## Status

Die linke Kachel zeigt den Status der app an. D.h. wann findet der n�chste Testlauf bzw. die n�chste Buchung statt.
Ebenfall kann dort die App pausiert bzw. reaktiviert werden

## Kurse verwalten

Die Kurse k�nnen im web-ui in der Kachel 'Kurse verwalten' via rest-api entfernt, ge�ndert oder pausiert werden.
Pausierte Kurse sind inaktiv und werden nicht f�r die Ermittlung des n�chsten Kurses herangezogen. Der aktuelle Kurs,
d.h. derjenige auf den die App wartet und welcher als n�chstes Gebucht wird, wird durch eine blaue Glocke signalisiert.

Werden alle Kurse entfernt pausiert sich die App automatisch. Werden dann wieder Kurse hinzugef�gt, muss die App manuell
wieder reaktiviert werden

## Hinzuf�gen von Kursen

In der linken unteren Kachel k�nnen neue Kurse hinzugef�gt werden

# SMS-Alerting:

Jeweils nach dem Test,- sowie dem scharfen Lauf wird ein sms versendet, mit Informationen �ber den Ausgang. Die
Konfiguration dazu erfolgt im File 'config/alert/aquabasilea-alert-notification.yml'.
