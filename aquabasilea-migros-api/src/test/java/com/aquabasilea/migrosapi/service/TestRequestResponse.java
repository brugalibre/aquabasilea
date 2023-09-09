package com.aquabasilea.migrosapi.service;

public class TestRequestResponse {

   public static String COURSE_NAME_1 = "Aqua Power 50 Min.";
   public static String COURSE_NAME_2 = "Pilates G1";
   public static String CENTER_ID_1 = "129";
   public static String CENTER_ID_2 = "139";

   static final String GET_COURSES_REQUEST = "{\"language\":\"de\",\"skip\":0,\"take\":8,\"selectMethod\":2,\"memberIdTac\":0,\"centerIds\":[129,139],\"daytimeIds\":[],\"weekdayIds\":[],\"coursetitles\":[]}";
   static final String GET_COURSE_TAC_ID_REQUEST = "{\"language\":\"de\",\"skip\":0,\"take\":8,\"selectMethod\":2,\"memberIdTac\":0,\"centerIds\":[139],\"daytimeIds\":[],\"weekdayIds\":[7],\"coursetitles\":[{\"centerId\": 139,\"coursetitle\":\"" + COURSE_NAME_1 + "\"}]}";
   static final String BOOK_COURSE_REQUEST = "";

   static final String BOOK_COURSE_RESPONSE = "{";

   static final String GET_BOOKED_COURSES_RESPONSE = "[\n" +
           "{\n" +
           "\"centerId\": " + CENTER_ID_2 + ",\n" +
           "\"centerIdTac\": 80,\n" +
           "\"courseIdTac\": 18012472,\n" +
           "\"title\": \"" + COURSE_NAME_1 + "\",\n" +
           "\"description\": \"deepWork ist athletisch, einfach, anstrengend, und doch ganz anders als alle bekannten Programme - ein Workout voller Energie! Die deepWork Bewegungsabläufe sind so konzipiert, dass sie sich immer in der Verbindung zwischen Anspannung und Entspannung befinden und mit Atemübungen kombiniert werden.\",\n" +
           "\"activity\": \"\",\n" +
           "\"instructor\": \" Gemma M.\",\n" +
           "\"location\": \" Gym 1\",\n" +
           "\"start\": \"2023-09-10T11:15:00\",\n" +
           "\"end\": \"2023-09-10T12:05:00\",\n" +
           "\"maxPersons\": 30,\n" +
           "\"actualPersons\": 3,\n" +
           "\"booked\": true,\n" +
           "\"bookable\": false,\n" +
           "\"bookingIdTac\": 18173491,\n" +
           "\"reservationDescription\": \"\",\n" +
           "\"linkCalendar\": \"course/139/18012472.ics\"\n" +
           "\t},\n" +
           "\t{\n" +
           "\"centerId\": " + CENTER_ID_1 + ",\n" +
           "\"centerIdTac\": 80,\n" +
           "\"courseIdTac\": 34515942,\n" +
           "\"title\": \"" + COURSE_NAME_2 + "\",\n" +
           "\"description\": \"Solem Ipsum dubsum\",\n" +
           "\"activity\": \"\",\n" +
           "\"instructor\": \" Emma M.\",\n" +
           "\"location\": \" Gym 2\",\n" +
           "\"start\": \"2023-08-12T11:15:00\",\n" +
           "\"end\": \"2023-08-12T12:05:00\",\n" +
           "\"maxPersons\": 30,\n" +
           "\"actualPersons\": 5,\n" +
           "\"booked\": true,\n" +
           "\"bookable\": false,\n" +
           "\"bookingIdTac\": 18173491,\n" +
           "\"reservationDescription\": \"\",\n" +
           "\"linkCalendar\": \"course/139/34515942.ics\"\n" +
           "\t}\n" +
           "]";

   static final String GET_COURSES_RESPONSE = "{" +
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
           "                \"coursetitle\": \"" + COURSE_NAME_1 + "\"," +
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

   static final String GET_COURSE_TAC_ID_RESPONSE = "{" +
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
           "                \"id\": 7," +
           "                \"label\": \"Sonntag\"" +
           "            }" +
           "        ]," +
           "        \"coursetitles\": [" +
           "            {" +
           "                \"centerId\": 139," +
           "                \"coursetitle\": \"" + COURSE_NAME_1 + "\"," +
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
           "            \"title\": \"" + COURSE_NAME_1 + "\"," +
           "            \"description\": \"Intensives Training im Wasser mit Jogging- und Kraftübungen im Wechsel. Gelenkschonend trotz hoher Intensität. Aktiviert die Fettverbrennung.\"," +
           "            \"activity\": \"\"," +
           "            \"instructor\": \" Andrea Tr.\"," +
           "            \"location\": \" Pool\"," +
           "            \"start\": \"2022-12-06T19:15:00\"," +
           "            \"end\": \"2022-12-06T20:05:00\"," +
           "            \"maxPersons\": 25," +
           "            \"actualPersons\": 6," +
           "            \"booked\": false," +
           "            \"bookable\": false," +
           "            \"bookingIdTac\": 0," +
           "            \"reservationDescription\": \"\"," +
           "            \"linkCalendar\": \"course/139/14389398.ics\"" +
           "        }," +
           "        {" +
           "            \"centerId\": 129," +
           "            \"centerIdTac\": 80," +
           "            \"courseIdTac\": 14387885," +
           "            \"title\": \"" + COURSE_NAME_1 + "\"," +
           "            \"description\": \"Intensives Training im Wasser mit Jogging- und Kraftübungen im Wechsel. Gelenkschonend trotz hoher Intensität. Aktiviert die Fettverbrennung.\"," +
           "            \"activity\": \"\"," +
           "            \"instructor\": \" Sina A.\"," +
           "            \"location\": \" Gym 1\"," +
           "            \"start\": \"2022-12-06T19:15:00\"," +
           "            \"end\": \"2022-12-06T20:05:00\"," +
           "            \"maxPersons\": 25," +
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
