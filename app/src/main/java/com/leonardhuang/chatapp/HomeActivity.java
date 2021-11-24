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
        Button button = (Button) findViewById(R.id.button_signin);
        button.setOnClickListener(v -> addDataToFirestore());
        //binding.buttonSignIn.setOnClickListener(v -> addDataToFirestore());
    }

    //a supprimer, pour test
    private void addDataToFirestore(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Leonard");
        data.put("last_name", "Le BG");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}