package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    private TextView tw_signin;
    private String encodedImage;
    private Button button_signup;
    private ProgressBar progressBar;
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
        button_signup = findViewById(R.id.button_signup);
        input_fname = findViewById(R.id.input_fname);
        input_lname = findViewById(R.id.input_lname);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_confirm_password = findViewById(R.id.input_confirm_password);
    }

    public void signInListeners(View view) {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void signUpListeners(View view){
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

    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private Boolean isValidSignUpDetails(){
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

    private void loading(Boolean isLoading){
        if(isLoading){
            button_signup.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            button_signup.setVisibility(View.VISIBLE);
        }
    }


}