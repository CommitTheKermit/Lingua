package com.example.lingua;

import  javax.net.ssl.HttpsURLConnection;
import  java.io.BufferedReader;
import  java.io.IOException;
import  java.io.InputStreamReader;
import  java.net.URL;

public class Oxford {

    String request(){
        final String language = "en-gb";
        final String word = "Ace";
        final String fields = "definitions";
        final String word_id = word.toLowerCase();
        final String restUrl = "https://od-api.oxforddictionaries.com:443/api/v2/words/" + language + "?q=" + word_id + "&" + "fields=" + fields;
//        https://od-api.oxforddictionaries.com/api/v2/words/en-gb?q=swimming&fields=definitions
        //TODO: replace with your own app id and app key
        final String app_id = "35cd517f";
        final String app_key = "91283b25ed1caa9af6fa45e844b012ca";
        try {
            URL url = new URL(restUrl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", app_id);
            urlConnection.setRequestProperty("app_key", app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "no receive";
        }

    }
}