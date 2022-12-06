package com.aquabasilea.migrosapi.service;

public class TestRequestResponse {

   static final String REQUEST_1 = "{\"language\":\"de\",\"skip\":0,\"take\":7,\"selectMethod\":2,\"memberIdTac\":0,\"centerIds\":[129,139],\"daytimeIds\":[],\"weekdayIds\":[],\"coursetitles\":[]}";

   public static String COURSE_NAME_1 = "Aqua Power 50 Min.";
   public static String COURSE_NAME_2 = "Pilates G1";

   static final String RESPONSE_1 = "{" +
           "    \"memberIdTac\": 0," +
           "    \"filter\": {" +
           "        \"daytimes\": [" +
           "            {" +
           "                \"id\": 4," +
           "                \"label\": \"Abend\"" +
           "            }" +
           "        ]," +
           "        \"weekdays\": [" +
           "            {" +
           "                \"id\": 2," +
           "                \"label\": \"Dienstag\"" +
           "            }" +
           "        ]," +
           "        \"coursetitles\": [" +
           "            {" +
           "                \"centerId\": 139," +
           "                \"coursetitle\": \""+ COURSE_NAME_1 +"\"," +
           "                \"courseCount\": 1" +
           "            }," +
           "            {" +
           "                \"centerId\": 139," +
           "                \"coursetitle\": \"" + COURSE_NAME_2 + "\"," +
           "                \"courseCount\": 1" +
           "            }" +
           "        ]," +
           "        \"centers\": [" +
           "            {" +
           "                \"centerId\": 139," +
           "                \"centerIdMapi\": \"0028650_migros_fitnesscenter\"," +
           "                \"centerIdTac\": 80," +
           "                \"title\": \"Migros Fitnesscenter Aquabasilea\"," +
           "                \"linkBalance\": \"https://shop-aquabasilea.migrosfitnesscenter.ch/account/dashboard/balance/\"," +
           "                \"linkRenewal\": \"https://shop-aquabasilea.migrosfitnesscenter.ch/?contractid=[[contractid]]&prolong=1\"," +
           "                \"linkShop\": \"https://shop-aquabasilea.migrosfitnesscenter.ch\"," +
           "                \"linkAthome\": null" +
           "            }" +
           "        ]" +
           "    }," +
           "    \"courses\": [" +
           "        {" +
           "            \"centerId\": 129," +
           "            \"centerIdTac\": 80," +
           "            \"courseIdTac\": 14389398," +
           "            \"title\": \"Aqua Power 50 Min.\"," +
           "            \"description\": \"Intensives Training im Wasser mit Jogging- und Kraftübungen im Wechsel. Gelenkschonend trotz hoher Intensität. Aktiviert die Fettverbrennung.\"," +
           "            \"activity\": \"\"," +
           "            \"instructor\": \" Andrea Tr.\"," +
           "            \"location\": \" Pool\"," +
           "            \"start\": \"2022-12-06T19:15:00\"," +
           "            \"end\": \"2022-12-06T20:05:00\"," +
           "            \"maxPersons\": 25," +
           "            \"actualPersons\": 3," +
           "            \"booked\": false," +
           "            \"bookable\": false," +
           "            \"bookingIdTac\": 0," +
           "            \"reservationDescription\": \"\"," +
           "            \"linkCalendar\": \"course/139/14389398.ics\"" +
           "        }," +
           "        {" +
           "            \"centerId\": 139," +
           "            \"centerIdTac\": 80," +
           "            \"courseIdTac\": 14387885," +
           "            \"title\": \"Pilates G1\"," +
           "            \"description\": \"Ruhige Kräftigungsstunde mit und ohne Hilfsmittel mit Schwerpunkt Bauch und Rücken nach Methode J. Pilates. In dieser Lektion werden die tiefliegenden Bauch und Rückenmuskeln trainiert, was zum einen eine Verbesserung der Haltung und zum anderen eine Prävention gegen Rückenleiden bewirkt.\"," +
           "            \"activity\": \"\"," +
           "            \"instructor\": \" Sina A.\"," +
           "            \"location\": \" Gym 1\"," +
           "            \"start\": \"2022-12-06T19:15:00\"," +
           "            \"end\": \"2022-12-06T20:05:00\"," +
           "            \"maxPersons\": 18," +
           "            \"actualPersons\": 6," +
           "            \"booked\": false," +
           "            \"bookable\": false," +
           "            \"bookingIdTac\": 0," +
           "            \"reservationDescription\": \"\"," +
           "            \"linkCalendar\": \"course/139/14387885.ics\"" +
           "        }" +
           "    ]," +
           "    \"courseCount\": 2," +
           "    \"code\": 0," +
           "    \"message\": null" +
           "}";

}
