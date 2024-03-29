package com.example.lingua.APIs;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.lingua.Main.MainActivity;
import com.example.lingua.Utils.Crypt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;


public class OpenAI {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTranslation(String word) {
        String returnStr;
        try {
            String apiKey = Crypt.aes256Decode("98M767s8MGUIjRleEqIhYQ4OFYAlbrXXiBJNlz1rod38W7mCczFUt7J3aJqyb3LFUhpvbRv1vtDm661UELtksw==",
                    "jMlHgXqcAykdNWvFtiPZUSoRbJzreOxE"); // OpenAI API 키 입력
            String textToTranslate = word.trim(); // 번역할 텍스트 입력
            String targetLanguage = "ko"; // 번역할 언어 코드 입력

            // API 요청을 보내기 위한 URL 생성
            URL url = new URL("https://api.openai.com/v1/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            int status = conn.getResponseCode();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);


            // 번역할 텍스트와 목표 언어를 JSON 형식으로 작성
            JSONObject data = new JSONObject();
            data.put("model", "text-davinci-003");
            data.put("prompt", "Translate this into Korean : " + textToTranslate + ". answer only translated text");
            data.put("max_tokens", textToTranslate.length() * 4);
            data.put("top_p", 1.0);
            data.put("frequency_penalty", 0.0);
            data.put("presence_penalty", 0.0);
//            data.put("temperature", 1.0);

            conn.setDoOutput(true);
            conn.getOutputStream().write(data.toString().getBytes());

            String output = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                int status = conn.getResponseCode();
                InputStream temp = conn.getInputStream();

                output = new BufferedReader(new InputStreamReader(temp)).lines()
                        .reduce((a, b) -> a + b).get();
            }

            returnStr = new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");


            // 연결 종료
            conn.disconnect();
            return returnStr.trim();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "-1";
    }
}
