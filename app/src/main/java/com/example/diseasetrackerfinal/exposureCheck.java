package com.example.diseasetrackerfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class exposureCheck extends AppCompatActivity {
    private int user_id;
    Button back;

    private NetworkCalls networkCalls = new NetworkCalls();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exposure_check);
        Bundle extras = getIntent().getExtras();
        user_id = Integer.parseInt(extras.getString("id"));
        TextView t = findViewById(R.id.results);
        networkCalls.exposureCheck(user_id, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseBody.trim();
                            String risk = risk(responseBody);
                            String response = "Based on my calculation,by evaluating your check-ins and diagnoses,as well as others "+risk;
                            type(response,t);

                        }
                    });
                }else{
                    String responseBody = response.message();
                    Toast.makeText(getApplicationContext(),responseBody,Toast.LENGTH_LONG).show();
                }

            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void type(String word, TextView t){

        long delayInMillis = 20;

        final int[] charIndex = {0};
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (charIndex[0] < word.length()) {
                    t.setText(word.substring(0, charIndex[0] + 1));
                    charIndex[0]++;
                    handler.postDelayed(this, delayInMillis);
                }
            }
        };

        handler.postDelayed(runnable, delayInMillis);
    }

    public String risk(String t){
        String risk = null;
        int x = Integer.parseInt(t);

        if(x == 0){
            risk = "Your estimated risk is less than 0";
        }else if(x < 10 && x > 0){
            risk = "Your estimated risk is low";
        } else if (x > 10 && x < 15) {
            risk = "Your estimated risk is medium";

        }else{
            risk = "Your estimated risk is high";
        }
        return risk;
    }
}