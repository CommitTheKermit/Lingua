package com.example.lingua;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import com.example.lingua.Papago;

public class MainActivity extends AppCompatActivity {
//    TextView txtRead;
//    ImageButton btnMenu;
    ActionBar actionBar;
    Button  btnPreviousLine, btnNextLine, btnInsert;
    TextView txtOriginalText, txtTranslatedText, txtTitle, txtPapago;
    LinearLayout scrollButtons;
//    TextView tvWordDefinitions;
    int index = 0;
    ArrayList<String> sentences;
    HashSet<String> stopWordSet = new HashSet<>();
    private static Handler mHandler ;
    public ArrayList<String> definitions = new ArrayList<>();
    public static Context context;
    static String resultWord;
    static String originalWord;

    ArrayList<Button> buttonArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mHandler = new Handler() ;

        actionBar = getSupportActionBar();
        actionBar.hide();
//        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
//        registerForContextMenu(btnMenu);

        btnPreviousLine = (Button) findViewById(R.id.btnPreviousLine);
        btnNextLine = (Button) findViewById(R.id.btnNextLine);
        btnInsert = (Button) findViewById(R.id.btnInsert);

        txtOriginalText = (TextView) findViewById(R.id.txtOriginalText);
        txtTranslatedText = (TextView) findViewById(R.id.txtTranslatedText);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPapago = (TextView) findViewById(R.id.txtPapago);
        scrollButtons = (LinearLayout) findViewById(R.id.scrollButtons);
//        tvWordDefinitions = (TextView) findViewById(R.id.tvWordDefinitions);

        btnPreviousLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index <= 0) return;
                index -= 1;
                String originalText = sentences.get(index) + ".";
                txtOriginalText.setText(originalText);
                Log.d("kermit",originalText + "   index : " + index);
            }
        });

        btnNextLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sentences.size() <= index) return;
                index += 1;
                String originalSentence = sentences.get(index) + ".";
                txtOriginalText.setText(originalSentence);
                Log.d("kermit",originalSentence + "   index : " + index);
//                tvWordDefinitions.setText("");
                new Thread(){
                    @Override
                    public void run() {

                        Papago papago = new Papago();
                        String resultSentence;

                        resultSentence= papago.getTranslation(originalSentence,"en","ko");

                        Bundle papagoBundle = new Bundle();
                        Log.d("kermit", "resultSentence" + resultSentence);

                        papagoBundle.putString("resultSentence",resultSentence);
                        Message msg = papago_handler.obtainMessage();
                        msg.setData(papagoBundle);
                        papago_handler.sendMessage(msg);

                        Log.d("kermit", "msg" + msg);
                    }
                }.start();

                for (String token : originalSentence.split("[^a-zA-Z_\\-0-9]+")) {
                    originalWord = token.toLowerCase();

                    if(stopWordSet.contains(originalWord) || originalWord.length() == 0){
                        continue;
                    }

                    // AsyncTask를 통해 HttpURLConnection 수행.
                    NetworkTask networkTask = new NetworkTask(originalWord);
                    networkTask.execute();

                }
            }
        });


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu,menu);
//        return true;
//    }

    public void mOnFileRead(View v){
        try {
            InputStream in = getResources().openRawResource(R.raw.brothers_of_snake);
            byte[] b = new byte[in.available()];

            try {
                in.read(b);
            } catch (IOException e) {
                Log.d("kermit",e.getMessage());
            }
            sentences = new ArrayList<String>(Arrays.asList((new String(b)).split("[.?!\n]")));

            InputStream inStopWord = getResources().openRawResource(R.raw.stop);
            byte[] bytesStopWord = new byte[inStopWord.available()];

            try {
                inStopWord.read(bytesStopWord);
            } catch (IOException e) {
                Log.d("kermit",e.getMessage());
            }

            Scanner scanStop = new Scanner(new String(bytesStopWord));
            while(scanStop.hasNext()){
                stopWordSet.add(scanStop.next());
            }

            String temp;
            for(int i = 0; i < 100; i++){
                if(sentences.get(i).length() == 0){
                    sentences.remove(i);
                    i--;
                    continue;
                }

                temp =  sentences.get(i).trim();
                sentences.remove(i);
                sentences.add(i,temp);
                txtTitle.setText(sentences.get(0));
                txtOriginalText.setText(sentences.get(0));

//                Log.d("kermit",sentences.get(i).trim() + "   length : " + sentences.get(i).length());

            }

        }
        catch (IOException e) {
            Log.d("kermit",e.getMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultSentence = bundle.getString("resultSentence");
            txtPapago.setText(resultSentence);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler papagoWordHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
        }
    };

    class WordTranslateThread extends Thread {
        String resultWord;
        String originalWord;
        public WordTranslateThread(String word){
            this.originalWord = word;
        }

        @Override
        public void run() {

            Papago papago = new Papago();
            resultWord= papago.getTranslation(originalWord,"en","ko");

            Bundle papagoBundle = new Bundle();
            Log.d("kermit", "resultWord" + resultWord);

            papagoBundle.putString("resultWord",resultWord);
            Message msg = papagoWordHandler.obtainMessage();
            msg.setData(papagoBundle);
            papagoWordHandler.sendMessage(msg);

            Log.d("kermit", "msg" + msg);

            MainActivity.this.resultWord = resultWord;
        }
    }


}