package com.example.lingua;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import com.example.lingua.Papago;

public class MainActivity extends AppCompatActivity {
    Activity main = this;

//    TextView txtRead;
    ImageButton btnMenu, btnBook;
    ActionBar actionBar;
    Button  btnPreviousLine, btnNextLine, btnInsert;
    Button itemReadFile, itemWriteFile;
    TextView txtOriginalText;
    TextView txtTitle;
    TextView tvIndexCurrent;
    static TextView txtPapago;
    EditText etTranslatedText;
//    HorizontalScrollView scrollButtons;
    Dialog dialogView;
    Dialog dialogIndexSeek;


    public static RecyclerView recyclerWords;
    public static HorizontalAdapter horizontalAdapter;
    LinearLayoutManager linearLayoutManager;

//    TextView tvWordDefinitions; //TODO delete or implement
    int index = 0;
    int indexForDialog = 0;
    ArrayList<String> sentences;
    HashSet<String> stopWordSet = new HashSet<>();
    private static Handler mHandler ;
    public ArrayList<String> definitions = new ArrayList<>();
    public static Context context;
    static String resultWord;
    static String originalWord;
    String bookTitle = "";

    String[] resultWords;

    ArrayList<Button> buttonArrayList = new ArrayList<>();

    public static ArrayList<ButtonData> listWords = new ArrayList<>();

    public static HashMap<String, String> mapWords = new HashMap<>();
    public static HashMap<String, String> mapWordsTranslated = new HashMap<>();



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mInflater = getMenuInflater();
        if (v == btnMenu){
            mInflater.inflate(R.menu.menu, menu);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemReadFile:
                try {
                    InputStream in = getResources().openRawResource(R.raw.severed_partial);
                    byte[] b = new byte[in.available()];

                    try {
                        in.read(b);
                    } catch (IOException e) {
                        Log.d("kermit",e.getMessage());
                    }
                    sentences = new ArrayList<String>(Arrays.asList((new String(b)).split("[.\n]")));

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
                    bookTitle = sentences.remove(0);
                    for(int i = 0; i < sentences.size(); i++) {
                        if (sentences.get(i).trim().length() == 0) {
                            sentences.remove(i);
                            i--;
                            continue;
                        }


                        temp = sentences.get(i).trim();
                        sentences.remove(i);
                        sentences.add(i, temp);
                    }
                    resultWords = new String[sentences.size()];
                    txtTitle.setText(bookTitle);
                    txtOriginalText.setText("시작");
                    index = -1;
//                    btnInsert.setEnabled(false);
//                    btnPreviousLine.setEnabled(false);

//                Log.d("kermit",sentences.get(i).trim() + "   length : " + sentences.get(i).length());


                    in.close();
                    inStopWord.close();
                }
                catch (IOException e) {
                    Log.d("kermit",e.getMessage());
                }
                break;
            case R.id.itemWriteFile:

                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {
                    LocalTime now = LocalTime.now();
                    System.out.println(now);
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("hh시 mm분 ss초");
                    String formattedNow = now.format(format);

                    FileOutputStream outputStream = openFileOutput(LocalDate.now().toString() + " " + formattedNow + ".txt",
                            Context.MODE_PRIVATE);

                    String output = "";
                    // 현재 조건 상태에선 중간에 번역안한 문장이 있으면 그 지점부터 다음 번역문은 무시됨.
                    //resultWords[i] != null
                    for(int i = 0; i < resultWords.length; i++){
                        output += resultWords[i] + "\n";
                    }
                    outputStream.write(output.getBytes());
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.itemIndexSearch:
                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                dialogIndexSeek = new Dialog(MainActivity.this);       // Dialog 초기화
                dialogIndexSeek.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
                dialogIndexSeek.setContentView(R.layout.dialog_index);

                WindowManager.LayoutParams params = dialogIndexSeek.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialogIndexSeek.getWindow().setAttributes(params);
                dialogIndexSeek.show();

                Button btnIndex;
                SeekBar seekIndex;
                EditText etIndex;
                TextView tvIndexChange, tvIndexSentence;

                btnIndex = (Button) dialogIndexSeek.findViewById(R.id.btnIndex);
                seekIndex = (SeekBar) dialogIndexSeek.findViewById(R.id.seekIndex);
                etIndex = (EditText) dialogIndexSeek.findViewById(R.id.etIndex);
                tvIndexChange = (TextView) dialogIndexSeek.findViewById(R.id.tvIndexChange);
                tvIndexSentence = (TextView) dialogIndexSeek.findViewById(R.id.tvIndexSentence);

                if(index < 0){
                    index = 0;
                }

                tvIndexChange.setText("0 / " + sentences.size());
                seekIndex.setMax(sentences.size() - 1);
                tvIndexSentence.setText(sentences.get(index));
                seekIndex.setProgress(index);
                etIndex.setText(index+"");




                btnIndex.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int targetIndex = 0;
                        try{
                            targetIndex = Integer.parseInt(etIndex.getText().toString());
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(),"잘못된 입력", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dialogIndexSeek.dismiss();

                        index = targetIndex;
                        indexForDialog = index;
                        String originalSentence = sentences.get(index) + ".";
                        txtOriginalText.setText(originalSentence);
                        Log.d("kermit",originalSentence + "   index : " + index);

                        int maxIndex = sentences.size() - 1;
                        tvIndexCurrent.setText(index + " / " + maxIndex );
                        etTranslatedText.setText("");

                        if(resultWords[index] != null)
                            etTranslatedText.setText(resultWords[index]);

//                파파고 번역부분 start();까지 주석처리하면 번역기능 정지
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

                        listWords.clear();
                        horizontalAdapter.clear();

                        for (String token : originalSentence.split("[^a-zA-Z_\\-0-9]+")) {
                            originalWord = token.toLowerCase();

                            if(stopWordSet.contains(originalWord) || originalWord.length() == 0){
                                continue;
                            }

                            MainActivity.listWords.add(new ButtonData(originalWord));

                            MainActivity.horizontalAdapter.setData(MainActivity.listWords);
                            MainActivity.recyclerWords.setAdapter(MainActivity.horizontalAdapter);


                        }
                    }
                });
                seekIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int maxIndex = sentences.size() - 1;

                        tvIndexChange.setText(seekBar.getProgress() +" / "  + maxIndex);
                        etIndex.setText(seekBar.getProgress()+"");
                        tvIndexSentence.setText(sentences.get(seekBar.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        mHandler = new Handler() ;

        actionBar = getSupportActionBar();
        actionBar.hide();
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        registerForContextMenu(btnMenu);
        btnBook = (ImageButton) findViewById(R.id.btnBook);

        btnPreviousLine = (Button) findViewById(R.id.btnPreviousLine);
        btnNextLine = (Button) findViewById(R.id.btnNextLine);
        btnInsert = (Button) findViewById(R.id.btnInsert);

        txtOriginalText = (TextView) findViewById(R.id.txtOriginalText);
        etTranslatedText = (EditText) findViewById(R.id.etTranslatedText);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPapago = (TextView) findViewById(R.id.txtPapago);
        tvIndexCurrent = (TextView) findViewById(R.id.tvIndexCurrent);
//        scrollButtons = (LinearLayout) findViewById(R.id.scrollButtons);
//        tvWordDefinitions = (TextView) findViewById(R.id.tvWordDefinitions);
        recyclerWords = (RecyclerView) findViewById(R.id.recyclerWords);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerWords.setLayoutManager(linearLayoutManager);

        horizontalAdapter = new HorizontalAdapter();
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
//                R.layout.word_button,listWords);

//        recyclerWords.setAdapter(adapter);


        dialogView = new Dialog(MainActivity.this);       // Dialog 초기화
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialogView.setContentView(R.layout.dialog_book);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.openContextMenu(btnMenu);
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                WindowManager.LayoutParams params = dialogView.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialogView.getWindow().setAttributes(params);
                dialogView.show();

                Button btnPrevPage, btnNextPage, btnConfirm;
                TextView tvDialogTitle, tvContent, tvDialogIndex;
                LinearLayout linearButtonBar;


                btnPrevPage = (Button) dialogView.findViewById(R.id.btnPrevPage);
                btnNextPage = (Button) dialogView.findViewById(R.id.btnNextPage);
                btnConfirm = (Button) dialogView.findViewById(R.id.btnConfirm);

                tvDialogTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
                tvContent = (TextView) dialogView.findViewById(R.id.tvContent);
                tvDialogIndex = (TextView) dialogView.findViewById(R.id.tvDialogIndex) ;

                linearButtonBar = (LinearLayout) dialogView.findViewById(R.id.linearButtonBar);
                linearButtonBar.bringToFront();



                String bookContext = "";
                int prevIndex, nextIndex, offsetSentence;
                offsetSentence = 8;

                prevIndex = indexForDialog;
                nextIndex = indexForDialog + offsetSentence;
                if(prevIndex < 0){
                    nextIndex = offsetSentence;
                    prevIndex = 0;
                }
                else if(nextIndex > sentences.size()){
                    nextIndex = sentences.size();
                    prevIndex = nextIndex - offsetSentence;
                }
                for(int i = prevIndex; i < nextIndex; i++){
                    bookContext += sentences.get(i) + ". ";
                }
                bookContext += "\n\n";


                tvDialogTitle.setText(bookTitle);
                tvContent.setText(bookContext);

                int maxIndex = sentences.size() - 1;
                tvDialogIndex.setText(prevIndex + "~" + Integer.toString(nextIndex - 1) +  " / " + maxIndex );

                btnPrevPage.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String bookContext = "";
                       int prevIndex, nextIndex, offsetSentence;
                       offsetSentence = 8;

                       prevIndex = indexForDialog - offsetSentence;
                       nextIndex = indexForDialog;
                       indexForDialog = prevIndex;
                       if(prevIndex < 0){
                           nextIndex = offsetSentence;
                           prevIndex = 0;
                       }
                       else if(nextIndex > sentences.size()){
                           nextIndex = sentences.size();
                           prevIndex = nextIndex - offsetSentence;
                       }
                       for(int i = prevIndex; i < nextIndex; i++){
                           bookContext += sentences.get(i) + ". ";
                       }
                       bookContext += "\n\n";
                       tvContent.setText(bookContext);
                       tvDialogIndex.setText(prevIndex + "~" + Integer.toString(nextIndex - 1) +  " / " + maxIndex );
                   }
               });
                btnNextPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String bookContext = "";
                        int prevIndex, nextIndex, offsetSentence;
                        offsetSentence = 8;

                        prevIndex = indexForDialog + offsetSentence;
                        nextIndex = indexForDialog + offsetSentence * 2;
                        indexForDialog = prevIndex;
                        if(prevIndex < 0){
                            nextIndex = offsetSentence;
                            prevIndex = 0;
                        }
                        else if(nextIndex > sentences.size()){
                            nextIndex = sentences.size();
                            prevIndex = nextIndex - offsetSentence;
                        }
                        for(int i = prevIndex; i < nextIndex; i++){
                            bookContext += sentences.get(i) + ". ";
                        }
                        bookContext += "\n\n";
                        tvContent.setText(bookContext);
                        tvDialogIndex.setText(prevIndex + "~" + Integer.toString(nextIndex - 1) +  " / " + maxIndex );
                    }
                });
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                        indexForDialog = index;
                    }
                });

            }
        });
        btnPreviousLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(index <= 0){
                    return;
                }

                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                index -= 1;
                String originalSentence = sentences.get(index) + ".";
                txtOriginalText.setText(originalSentence);
                Log.d("kermit",originalSentence + "   index : " + index);

                int maxIndex = sentences.size() - 1;
                tvIndexCurrent.setText(index + " / " + maxIndex );
                etTranslatedText.setText("");

                if(resultWords[index] != null)
                    etTranslatedText.setText(resultWords[index]);
//                tvWordDefinitions.setText("");

//                파파고 번역부분 start();까지 주석처리하면 번역기능 정지
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

                listWords.clear();
                horizontalAdapter.clear();

                for (String token : originalSentence.split("[^a-zA-Z_\\-0-9]+")) {
                    originalWord = token.toLowerCase();

                    if(stopWordSet.contains(originalWord) || originalWord.length() == 0){
                        continue;
                    }

                    MainActivity.listWords.add(new ButtonData(originalWord));

                    MainActivity.horizontalAdapter.setData(MainActivity.listWords);
                    MainActivity.recyclerWords.setAdapter(MainActivity.horizontalAdapter);


                }
            }
        });

        btnNextLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(sentences.size()-1 <= index) return;
                index += 1;
                indexForDialog = index;
                String originalSentence = sentences.get(index) + ".";
                txtOriginalText.setText(originalSentence);
                Log.d("kermit",originalSentence + "   index : " + index);
                int maxIndex = sentences.size() - 1;
                tvIndexCurrent.setText(index + " / " + maxIndex );
                etTranslatedText.setText("");

                if(resultWords[index] != null)
                    etTranslatedText.setText(resultWords[index]);
//                tvWordDefinitions.setText("");

//                파파고 번역부분 start();까지 주석처리하면 번역기능 정지
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

                listWords.clear();
                horizontalAdapter.clear();

                for (String token : originalSentence.split("[^a-zA-Z_\\-0-9]+")) {
                    originalWord = token.toLowerCase();

                    if(stopWordSet.contains(originalWord) || originalWord.length() == 0){
                        continue;
                    }

                    MainActivity.listWords.add(new ButtonData(originalWord));


                    MainActivity.horizontalAdapter.setData(MainActivity.listWords);
                    MainActivity.recyclerWords.setAdapter(MainActivity.horizontalAdapter);


                }
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sentences == null){
                    Toast.makeText(getApplicationContext(),"파일을 열어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                resultWords[index] = etTranslatedText.getText().toString();
            }
        });


    }

    @SuppressLint("HandlerLeak")
    static Handler papago_handler = new Handler(){
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
