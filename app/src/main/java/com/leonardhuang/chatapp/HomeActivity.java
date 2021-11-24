package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private TextView tw_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tw_signup = findViewById(R.id.textview_signup);

    }

    public void signupListeners(View view) {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }

}