package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.leonardhuang.chatapp.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager mPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void loadUserDetails() {
    }
}