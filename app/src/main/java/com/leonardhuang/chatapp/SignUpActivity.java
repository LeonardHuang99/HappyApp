package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private TextView tw_signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tw_signin = findViewById(R.id.textview_have_account);
    }

    public void signinListeners(View view) {

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}