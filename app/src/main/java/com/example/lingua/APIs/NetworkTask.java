package com.example.lingua.APIs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lingua.Main.MainActivity;
import com.example.lingua.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String originalWord;
    private TextView tvWordContent;
    private Dialog wordDialogView;
    private Button btn;

    public NetworkTask(String word, Dialog wordDialogView, Button btnWordDict) {

        this.originalWord = word;
        this.tvWordContent = (TextView) wordDialogView.findViewById(R.id.tvWordContent);
        this.wordDialogView = wordDialogView;
        this.btn = btnWordDict;
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
//            MainActivity.listWords.remove(new ButtonData(this.originalWord));
//            MainActivity.mapWords.put(this.originalWord,"NO DATA FROM DICTIONARY");
//            this.tvWordContent.setText("NO DATA FROM DICTIONARY");
//            MainActivity.horizontalAdapter.setData(MainActivity.listWords);
//            MainActivity.recyclerWords.setAdapter(MainActivity.horizontalAdapter);
            this.btn.setEnabled(false);
            this.btn.setBackgroundColor(Color.GRAY);
            Toast.makeText(MainActivity.context,"NO DATA FROM DICTIONARY",Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (int i = 1; i < definitions.size(); i++) {
                temp = definitions.get(i).split("\"");
                definitions.set(i,temp[1]);
//                Log.d("kermit", "in networkTask definitions : " + definitions.get(i));
            }

            String defs = "";
            for (int i = 1; i < 4 && i < definitions.size(); i++) {
                defs += definitions.get(i) + ".\n\n";
            }
            this.tvWordContent.setText(defs);
            this.wordDialogView.show();
            MainActivity.mapWords.put(this.originalWord,defs);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }

    }

}