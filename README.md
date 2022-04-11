# aquabasilea
Automatisches Buchen von Sportkursen von Aquabasilea / Migros

Die gewünschten Kurse sowie ihre Startzeit können in der Datei 'weeklyCourses.yml' konfiguriert werden. Das File befindet sich im Ordner 'courses' 
Der Kurs wird dann jeweils 24h vorher gebucht. 
Vor dem "scharfen" Buchen findet jeweils ein Testlauf statt. Es kann konfiguriert werden, wie viel Minuten der Testlauf früher stattfindet

#Konfiguration der Kurse:
Die Konfiguration wird in der Datei 'aquabasilea-kurs-bucher-config.yml' vorgenommen. Ebenfalls können dort die URL der Login-Seite und der Seite mit der Kurs-Ãœbersicht
konfiguriert werden

#SMS-Alerting:
Jeweils nach dem Test,- sowie dem scharfen Lauf wird ein sms versendet, mit Informationen über den Ausgang. Die Konfiguration dazu erfolgt im File 'aquabasilea-alert-notification.yml'
root-module 'aquabasilea')
