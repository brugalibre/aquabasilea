# Migros-Kurs-Bucher:

Dies ist eine App, welche zeitgesteuert und voll automatisch das Buchen von Sportkursen von Aquabasilea / Migros übernimmt. 
Dabei handelt es sich um eine Standalone Java-Spring-Boot Applikation mit einer Rest-API. Der Kursbucher ist unter
piranha.synology.me:9000/migros-kurs-bucher erreichbar

## Funktionsweise

Kurse werden hinzugefügt, in dem sie aus einer Liste mit vordefinierten "Migros-Kursen" gewählt werden. Ausgewählte Kurse 
können pausiert oder wieder entfernt werden. Pausierte Kurse sind nur für eine Woche pausiert und werden in der darauf 
folgenden Woche automatisch wieder gebucht. Hinzugefügte und aktive Kurse werden 24h vor Kursbeginn automatisch gebucht. 
Eine sms informiert darüber, ob die Buchung erfolgreich war oder nicht.

Die zu auswählbaren Kurse können im UI in der Kachel 'Neuen Kurs hinzufügen' im unteren Teil der Kachel aktualisiert werden. 
Dazu einfach die gewünschten Kursorte aus dem Dropdown auswählen und auf 'Migros Kurse aktualisieren' drücken.
Dadurch befüllt sich die Auswahlliste 'Kurs auswählen..'. Ein dort ausgewählter Kurs wird sofort hinzugefügt.

Hinzugefügte Kurse, die nicht pausiert sind, werden automatisch der Reihe nach abgearbeitet. Ist der letzte Kurs gebucht,
fängt es wieder von neuem an. Der aktuelle Kurs wird jeweils 24h vor Kursbeginn gebucht. 

Der Testlauf findet jeweils rund 90 Minuten vor der Buchung statt. Der Testlauf ist notwendig, damit frühzeitig fest-
gestellt werden kann, ob ein Kurs nicht mehr existiert (z.B. wenn der Kurs ausfällt)
oder wenn auf der Kursseite Anpassungen von Migros vorgenommen wurden und dieser Kursbucher nicht mehr 
ordnungsgemäss funktioniert.

# Web-Ui
![image](https://user-images.githubusercontent.com/29772244/171997138-54a79ac4-c9d1-43df-8d0d-f0634755b9d7.png)

# Registrierung

Damit die Applikation genutzt werden kann, muss ein gültiges Migros-Fitness Login vorhanden sein. Für die Registrierung 
unter piranha.synology.me:9000/migros-kurs-bucher/register müssen exakt die Login-Informationen vom Migros-Fitness an-
gegeben werden (d.h. e-Mail & Password). Mit diesen Informationen wird dann auch das Login gemacht. Nach dem Login wirst 
du auf die Übersichtsseite weitergeleitet

## Status

Die Kachel links oben zeigt den Status der App an. D.h. wann findet der nächste Testlauf bzw. die nächste Buchung statt.
Ebenfalls kann dort die App pausiert bzw. reaktiviert werden. 
Ein Klick auf den Button 'Statistik' klappt die eine Sektion mit statistischen Informationen über die App aus. D.h
Informationen wann das letzte Mal die Kurse anhand der Aquabasilea-Kursseite aktualisiert wurden, wann das nächste Update
erfolgen wird oder wie lange die Applikation bereits läuft, wie viele Buchungen erfolgt sind und wie viele davon 
erfolgreich waren.

## Hinzufügen von Kursen
In der linken unteren Kachel können neue Kurse aus einer Liste von definierten Migros-Kursen ausgewählt und hinzugefügt 
werden. Grundsätzlich werden die auswählbaren Kurse automatisch aktualisiert (s. Abschnitt 'Funktionsweise'). 
Aus der Liste mit Aquabasilea-Kursen kann der zu hinzufügende Kurs ausgewählt werden.
Das anklicken eines Kurses in der Dropdown fügt ihn automatisch hinzu.

Das Eingabefeld, welches links von den auszuwählenden Kursen platziert ist, ist eine Volltextsuche. 
Z.B. kann mit der Eingabe 'Funct Mittwoch 1815' nach dem Kurs 'Functional Training', welche jeweils am Mittwoch um 
18:15 Uhr stattfindet. In der Dropdown-Liste werden die Treffer bzw. Kurse, welcher mit der Eingabe am besten über-
einstimmen, zu oberst aufgeführt. Es kann jeweils nur ein Kurs auf einmal ausgewählt werden.

## Aktualisieren der Kursorte

Die konfigurierten standard Kursorte können geändert werden, in dem in der Sektion 'Auswählbare Kurse aktualisieren' 
die gewünschten Kursorte ausgewählt werden. Sind alle Kursorte gewählt, kann der Button 'Aquabasilea Kurse aktualisieren'
gedrückt werden. Daraufhin wird der Knopf ausgegraut und im Hintergrund startet die Aktualisierung der Kurse, anhand der
ausgewählten Kursorte.  

Dieser Vorgang kann einige Minuten dauern. Aktualisiere daher nach ein paar Minuten den Browser, bis der 
Aktualisieren-Button wieder aktiv und die Liste mit vordefinierten Migros-Kursen abgefüllt ist. 

## Kurse verwalten

Kurse die über das Dropdown 'Kurs auswählen..' hinzugefügt wurden, können in der rechten Kachel 'Wöchentliche Kurse 
verwalten' entfernt oder pausiert werden. Der aktuelle Kurs, d.h. derjenige auf den die App wartet und welcher als 
nächstes Gebucht wird, wird durch eine blaue Glocke signalisiert. Pausierte Kurse sind inaktiv und werden grau dar-
gestellt. Pausierte Kurse werden nur für eine einzige Iteration bzw. Woche nicht gebucht. D.h. sobald der nächste Kurs 
gebucht wurde, wird der übersprungene Kurs wieder reaktiviert. So ist es möglich, einen oder mehrere Kurse für eine 
bestimmte Woche auszusetzen, wobei sie für die kommende Woche automatisch wieder gebucht werden. 

Rot dargestellte Kurse können nicht gebucht werden. Du erhälst trotzdem eine SMS mit einer entsprechenden Nachricht.
Ein Kurs kann 'rot' angezeigt werden, wenn es diesen Kurs nicht (mehr) auf der offiziellen Migros Seite gibt. Z.B. wenn
der Kurs ausfällt oder wenn der Namen vom Migros-Kurs geändert wurde, nach dem du ihn in der App hinzugefügt hast.
Lösche in diesem Fall den Kurs und füge ihn erneut hinzu.

Werden alle Kurse entfernt, so pausiert sich die App automatisch. Nach dem Hinzufügen vom ersten Kurs wird die App auto-
matisch reaktiviert. 

# SMS-Alerting:

Jeweils nach dem Test,- sowie dem scharfen Buchen wird eine SMS versendet, mit Informationen über den Ausgang. Die Nr, an
die die SMS gesendet wird, wird bei der Registrierung angegeben werden und kann unter 
piranha.synology.me:9000/migros-kurs-bucher/profile geändert werden.
