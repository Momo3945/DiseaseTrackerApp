package com.example.diseasetrackerfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpPage extends AppCompatActivity {

    private static final String AES = "AES";
    private static final byte[] keyValue = new byte[]{'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    private EditText dobEditText;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        dobEditText = findViewById(R.id.Dob);
        EditText idEditText = findViewById(R.id.identificationNumber);

        dobEditText.setFocusable(false);
        dobEditText.setClickable(true);

        initializeDatePicker();

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        idEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new AlertDialog.Builder(SignUpPage.this)
                            .setTitle("Privacy Notice")
                            .setMessage("Due to POPI regulations, ID number is not collected or shared")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        });
    }

    private void initializeDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                dobEditText.setText(selectedDate);
            }
        }, year, month, day);
    }

    public void SignUp(View view) {
        NetworkCalls networkCalls = new NetworkCalls();
        String name = ((EditText) findViewById(R.id.Name)).getText().toString();
        String surname = ((EditText) findViewById(R.id.Surname)).getText().toString();
        String ID = ((EditText) findViewById(R.id.identificationNumber)).getText().toString();
        try {
            ID = encrypt(ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String DoB = dobEditText.getText().toString();
        String email = ((EditText) findViewById(R.id.emailEdit)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEdit)).getText().toString();
        TextView signup = findViewById(R.id.signText);

        networkCalls.signUp(name, surname, ID, DoB, email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(responseBody.trim().equals("SignUp successful")){
                                Intent intent = new Intent(SignUpPage.this,LoginPage.class);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignUpPage.this).toBundle());
                            }
                            else{
                                signup.setText(responseBody);
                                signup.setBackground(getDrawable(R.drawable.error));
                                signup.setTextSize(18);
                            }
                        }
                    });
                }
                else{
                    String errorMessage = response.message();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signup.setText(errorMessage);
                            signup.setBackground(getDrawable(R.drawable.error));
                            signup.setTextSize(18);
                        }
                    });
                }
            }
        });
    }

    public static String encrypt(String Data) throws Exception {
        SecretKeySpec key = generateKey();
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private static SecretKeySpec generateKey() throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, AES);
        return key;
    }
}
