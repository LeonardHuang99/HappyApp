package com.leonardhuang.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardhuang.chatapp.R;
import com.leonardhuang.chatapp.models.User;
import com.leonardhuang.chatapp.utilities.Constants;

public class ChatActivity extends AppCompatActivity {

    private User receiverUser;
    private TextView textName;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textName = findViewById(R.id.textNameChat);
        imageBack = findViewById(R.id.imageBackChat);
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        textName.setText(receiverUser.firstName + " " +receiverUser.lastName);
    }

    private void setListeners(){
        imageBack.setOnClickListener(v -> onBackPressed());
    }
}