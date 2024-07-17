package com.example.diseasetrackerfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReportDiagnosis extends AppCompatActivity {
    private String selection;
    private String choice = null;
    private String date = null;

    private String userid = null;

    private NetworkCalls networkCalls = new NetworkCalls();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_diagnosis);

        Bundle extras = getIntent().getExtras();

        String id = extras.getString("id");
        userid = id;
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Yes", "No"};
        TextView reportdiagnosis = findViewById(R.id.reportDiagnosis);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selection = items[i];
                if (selection.equals("Yes")){
                    choice = "1";
                    datePicker.setVisibility(View.VISIBLE);
                }else{
                    choice = "0";
                    datePicker.setVisibility(View.INVISIBLE);
                    date = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                reportdiagnosis.setText("Please select an option");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    date = i + "-" + (i1 + 1) + "-" + i2;
                }
            });
        }
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> confirm());

        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void confirm(){
        if(choice == null || (choice.equals("1") && date == null)){
            Toast.makeText(getApplicationContext(),"Please select the date and the diagnoses result",Toast.LENGTH_LONG).show();
        }else {
            networkCalls.diagnoses(userid, date, choice, new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String responseBody  = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(responseBody.trim().equals("success")){
                                    Toast.makeText(getApplicationContext(),"Diagnoses recorded successfully, keep safe!",Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(),responseBody,Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }else{
                        String message = response.message();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            });

        }
    }
}
