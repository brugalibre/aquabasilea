# aquabasilea
Automatisches Buchen von Sportkursen von Aquabasilea / Migros

Die gew�nschten Kurse sowie ihre Startzeit k�nnen in der Datei 'weeklyCourses.yml' konfiguriert werden. Das File befindet sich im Ordner 'courses' 
Der Kurs wird dann jeweils 24h vorher gebucht. 
Vor dem "scharfen" Buchen findet jeweils ein Testlauf statt. Es kann konfiguriert werden, wie viel Minuten der Testlauf fr�her stattfindet

#Konfiguration der Kurse:
Die Konfiguration wird in der Datei 'aquabasilea-kurs-bucher-config.yml' vorgenommen. Ebenfalls k�nnen dort die URL der Login-Seite und der Seite mit der Kurs-Übersicht
konfiguriert werden

#SMS-Alerting:
Jeweils nach dem Test,- sowie dem scharfen Lauf wird ein sms versendet, mit Informationen �ber den Ausgang. Die Konfiguration dazu erfolgt im File 'aquabasilea-alert-notification.yml'
root-module 'aquabasilea')
