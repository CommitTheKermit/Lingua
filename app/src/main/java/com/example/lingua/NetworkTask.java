package com.example.lingua;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String originalWord;

    public NetworkTask(String word) {
        this.originalWord = word;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
//        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
//        result = requestHttpURLConnection.request(url); // 해당 URL로 부터 결과물을 얻어온다.
        Oxford requestOxford = new Oxford();
        result = requestOxford.request(this.originalWord);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ArrayList<String> definitions = new ArrayList<String>(Arrays.asList(s.split("\"definitions\":")));
        String[] temp;
        if (definitions.size() < 2) {
//            this.cancel(true);
            return;
        } else {
            for (int i = 1; i < definitions.size(); i++) {
                temp = definitions.get(i).split("\"");
                definitions.remove(i);
                definitions.add(i, temp[1]);
                Log.d("kermit", "in networkTask definitions : " + definitions.get(i));
            }

            String tempDef;
            String defs = "";
            tempDef = ((MainActivity) MainActivity.context).tvWordDefinitions.getText().toString();
            for (int i = 1; i < 3 && i < definitions.size(); i++) {
                defs += definitions.get(i) + ", ";
            }
            String trimmed = defs.trim();
            trimmed = trimmed.substring(0,trimmed.length() - 1);
            String set = this.originalWord + " : " + trimmed  + ".\n\n";
            ((MainActivity) MainActivity.context).tvWordDefinitions.setText(tempDef + set);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }
    }

}