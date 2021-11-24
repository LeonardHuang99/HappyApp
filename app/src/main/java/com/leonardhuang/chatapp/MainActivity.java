package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager mPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void loadUserDetails() {
    }
}