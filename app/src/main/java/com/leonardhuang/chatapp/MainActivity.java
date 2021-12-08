package com.leonardhuang.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leonardhuang.chatapp.utilities.Constants;
import com.leonardhuang.chatapp.utilities.PreferenceManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager mPreferenceManager;
    private TextView textName;
    private ImageView imageProfileMain;
    private ImageView imageSignOutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textName = findViewById(R.id.textName);
        imageProfileMain = findViewById(R.id.imageProfileMain);
        imageSignOutMain = findViewById(R.id.imageSignOutMain);
        mPreferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
        //LEo
    }

    private void setListeners(){
        imageSignOutMain.setOnClickListener(v -> signOut());
    }

    private void loadUserDetails() {
        textName.setText(mPreferenceManager.getString(Constants.KEY_FIRST_NAME) + " " + mPreferenceManager.getString(Constants.KEY_LAST_NAME));
        byte[] bytes = Base64.decode(mPreferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageProfileMain.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        mPreferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut(){
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        mPreferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    mPreferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));

    }

}