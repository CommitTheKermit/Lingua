package com.example.lingua.Views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lingua.APIs.OpenAI;
import com.example.lingua.Main.MainActivity;
import com.example.lingua.APIs.NetworkTask;
import com.example.lingua.APIs.Papago;
import com.example.lingua.R;

public class HorizontalViewHolder extends RecyclerView.ViewHolder {

    public Button btnWordDict;
    Dialog wordDialogView = new Dialog(MainActivity.context);// Dialog 초기화
    TextView tvWordContent;
    boolean isTranslated = false, isSearched = false;
    String originalWord = "";


    public HorizontalViewHolder(@NonNull View itemView) {
        super(itemView);
        wordDialogView.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        wordDialogView.setContentView(R.layout.word_dialog);
        WindowManager.LayoutParams params = wordDialogView.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wordDialogView.getWindow().setAttributes(params);

        btnWordDict = (Button) itemView.findViewById(R.id.btnWordDict);

        btnWordDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalWord = btnWordDict.getText().toString();

                Button btnWordTranslate, btnWordConfirm;
                TextView tvWordTitle;

                btnWordTranslate = (Button) wordDialogView.findViewById(R.id.btnWordTranslate);
                btnWordConfirm = (Button) wordDialogView.findViewById(R.id.btnWordConfirm);

                tvWordTitle = (TextView) wordDialogView.findViewById(R.id.tvWordTitle);
                tvWordTitle.setText(originalWord);


                if(isSearched == false){
                    NetworkTask networkTask = new NetworkTask(originalWord, wordDialogView, btnWordDict);
                    networkTask.execute();
                    isSearched = true;
                }
                else if(isSearched == true && isTranslated == false){
                    tvWordContent = (TextView) wordDialogView.findViewById(R.id.tvWordContent);
                    tvWordContent.setText(MainActivity.mapWords.get(originalWord));
                    wordDialogView.show();
                }
                else if(isSearched == true && isTranslated == true){
                    tvWordContent = (TextView) wordDialogView.findViewById(R.id.tvWordContent);
                    tvWordContent.setText(MainActivity.mapWordsTranslated.get(originalWord));
                    wordDialogView.show();
                }


                btnWordConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wordDialogView.dismiss();
                    }
                });
                btnWordTranslate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isTranslated == false) {
                            tvWordContent = (TextView) wordDialogView.findViewById(R.id.tvWordContent);
                            new Thread() {
                                @Override
                                public void run() {

                                    String resultSentence;
                                    String[] temp = tvWordContent.getText().toString().split("\\n\\n");
                                    String originalSentence = "";
                                    int count = 1;
                                    for (String str : temp) {
                                        originalSentence += count++ + ". " + str;
                                    }

                                    OpenAI openAI = new OpenAI();
                                    resultSentence = openAI.getTranslation(originalSentence);

                                    Bundle setTextBundle = new Bundle();
                                    Log.d("kermit", "resultSentence" + resultSentence);

                                    setTextBundle.putString("resultSentence",resultSentence);
                                    Message msg = setTextHandler.obtainMessage();
                                    msg.setData(setTextBundle);
                                    setTextHandler.sendMessage(msg);

                                    Log.d("kermit", "msg" + msg);
                                }
                            }.start();
                        }
                        else{
                            Toast.makeText(wordDialogView.getContext(),
                                    "이미 번역되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    @SuppressLint("HandlerLeak")
    Handler setTextHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultSentence = bundle.getString("resultSentence");

            String[] temp = resultSentence.split("<>");
            String translatedSentence = "";
            for (String item: temp) {
                translatedSentence += item + "\n\n";
            }

            MainActivity.mapWordsTranslated.put(originalWord, translatedSentence);
            tvWordContent.setText(translatedSentence);
            isTranslated = true;
        }
    };
}
