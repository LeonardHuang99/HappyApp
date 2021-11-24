package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private TextView tw_signin;
    private String encodedImage;
    private Button button_signup;
    private EditText input_fname;
    private EditText input_lname;
    private EditText input_email;
    private EditText input_password;
    private EditText input_confirm_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tw_signin = findViewById(R.id.textview_have_account);
    }

    public void signInListeners(View view) {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void signUpListeners(View view){
        button_signup = findViewById(R.id.button_signup);
        button_signup.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signUp();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void signUp(){

    }

    private Boolean isValidSignUpDetails(){

        input_fname = findViewById(R.id.input_fname);
        input_lname = findViewById(R.id.input_lname);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_confirm_password = findViewById(R.id.input_confirm_password);

        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        }else if(input_fname.getText().toString().trim().isEmpty()){
            showToast("Enter first name");
            return false;
        }else if(input_lname.getText().toString().trim().isEmpty()){
            showToast("Enter last name");
            return false;
        }else if(input_email.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(input_password.getText().toString().trim().isEmpty()){
            showToast("Enter valid password");
            return false;
        }else if(input_password.getText().toString().equals(input_confirm_password.getText().toString())){
            showToast("Password and confirm password don't match");
            return false;
        }else{
            return true;
        }
    }


}