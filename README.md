# aquabasilea

Dies ist eine App, welche zeitgesteuert und voll  automatische das Buchen von Sportkursen von Aquabasilea / Migros übernimmt. Dabei handelt es sich um eine Standalone Java-Applikation mit einer Rest-API

## Funktionsweise

Kurse werden hinzugefügt, in dem sie aus einer Liste mit vordefinierten "Aquabasliea-Kursen" gewählt werden. Diese vordefinierten "Aquabasilea-Kurse" müssen vorgängig anhand der Aquabasilea-Kursseite aktualisiert bzw. initial geladen werden.

Hinzugefügte Kurse, die nicht pausiert sind, werden automatisch der Reihe nach abgearbeitet. Ist der letzte Kurs gebucht, fängt es wieder von neuem an. Der aktuelle Kurs wird jeweils 24h bevor er stattfindet gebucht. Vor diesem "scharfen" Buchen findet jeweils ein Testlauf statt. Es kann konfiguriert werden, wie viel Minuten der Testlauf früher stattfindet. 

Das Hinzfügen von Kursen bzw. das Laden von Kurs-Vorlagen findet über das web-ui statt, s. auch Abschnitt 'Kurse verwalten'. Das UI kann standardmässig im Browser über die Adresse 127.0.0.1:8080 abgerufen werden

# Konfiguration:

Der Testlauf findet jeweils vor der Buchung statt. Viele Minuten er vorher starten soll, kann in der Datei 'config/aquabasilea-kurs-bucher-config.yml' definiert werden. Ebenfalls können dort die URL der Login-Seite und der Seite mit der Kurs-Übersicht konfiguriert werden

# Web-Ui
<img width="1385" alt="image" src="https://user-images.githubusercontent.com/29772244/168870640-d960afd9-49c2-4b35-acd9-1116a8c06873.png">

## Status

Die Kachel links oben zeigt den Status der App an. D.h. wann findet der nächste Testlauf bzw. die nächste Buchung statt.
Ebenfall kann dort die App pausiert bzw. reaktiviert werden. 

## Hinzufügen von Kursen

In der linken unteren Kachel können neue Kurse aus einer Liste von definierten Aquabasilea-Kursen ausgewählt und hinzugefügt werden. Dazu müssen diese auswählbaren
Kurse erstmal aktualisiert werden. Dies wird erreicht, in dem in der Sektion 'Auswählbare Kurse aktualisieren' die gewünschten Kursorte ausgewählt werden, für welche Kurse geladen werden sollen. Sind alle Kursorte gewählt, kann der Button 'Aquabasilea Kurse aktualiseren' gedrückt werden.
Daraufhin wird der Knopf ausgegraut und im Hintergrund startet die Aktualisierung.

Dieser Vorgang kann einige Minuten dauern. Aktualisiere daher nach ein paar Minuten den Browser, bis der Aktualisieren-Button wieder aktiv und die 
Liste mit vordefinierten Aquabasilea-Kursen abgefüllt ist. 

Aus dieser Liste wird nun der zu hinzufügende Kurs ausgewählt und durch einen Klick auf den Button 'Kurs hinzufügen' hinzugefügt. 

Das Eingabefeld, welches links von den auszuwählenden Kursen platziert ist, ist eine Volltextsuche. Z.B. kann mit der Eingabe 'Funct Mittwoch 1815' nach dem Kurs 'Functional Training', welche jeweils am Mittwoch um 18:5 Uhr stattfindet. In der Dropdown-Liste werden die Treffer bzw. Kurse, welcher mit der Eingabe am Besten über einstimmen, zu oberst aufgeführt. Es kann jeweils nur ein Kurs auf einmal ausgewählt werden.


## Kurse verwalten

Hinzugefügte Kurse können im web-ui in der rechten Kachel 'Kurse verwalten' entfernt oder pausiert werden. Pausierte Kurse sind inaktiv und werden grau dargestellt.
Pausierte Kurse werden nur für eine einzige Iteration nicht gebucht. D.h. sobald der Kurs welcher anstelle des pausierten Kurses gebucht wurde, wird der übersprungene Kurs wieder reaktiviert. So ist es möglich, einen oder mehrere Kurse für eine bestimmte Woche auszusetzen, wobei sie für die kommende Woche automatisch wieder gebucht werden. Werden alle Kurse entfernt so pausiert sich die App automatisch. Nach dem hinzufügen von neuen Kursen muss die App manuell reaktiviert werden.

Der aktuelle Kurs, d.h. derjenige auf den die App wartet und welcher als nächstes Gebucht wird, wird durch eine blaue Glocke signalisiert.


# SMS-Alerting:

Jeweils nach dem Test,- sowie dem scharfen Buchen wird ein sms versendet, mit Informationen über den Ausgang. Die
Konfiguration dazu erfolgt im File 'config/alert/aquabasilea-alert-notification.yml'.

# Authentifizierung

Damit die Authentifizierung des Aquabasilea-Benutzers funktioniert, müssen zwei key-stores angelegt werden:
- aquabasilea.keystore
- aquabasilea-keystore.keystore

Letzter ist quasi der Super-Keystore, welcher die Passwörter für den eigentlichen Keystore enthält. Dieser kann mit einem eigenen Password geschützt werden.
Zum Erstellen siehe auch AquabasileaKeyStore.java bzw. KeyUtils.java.

Die Klasse WriteSecretToKeyStore.java bietet Methoden, um für einen Benutzer (=alias) sein Password sowie den API-Key für den sms-Dienst zu speichern. Als Argument muss dasselbe Password
Passwort verwendet werden, welches schon zur Erstellung vom KeyStore verwendet wurde.
D.h. Der WriteSecretToKeyStore muss zweimal eingesetzt werden, einmal um das Passwort des Aquabasilea-Benutzers zu speichern und ein weiteres Mal für den API-Token des sms-Send Services