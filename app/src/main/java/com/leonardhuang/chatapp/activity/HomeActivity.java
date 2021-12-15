package com.leonardhuang.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leonardhuang.chatapp.R;
import com.leonardhuang.chatapp.utilities.Constants;
import com.leonardhuang.chatapp.utilities.PreferenceManager;

public class HomeActivity extends AppCompatActivity {

    private TextView tw_signup;
    private EditText signIn_email;
    private EditText signIn_password;
    private PreferenceManager mPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPreferenceManager = new PreferenceManager(getApplicationContext());
        signIn_email = findViewById(R.id.input_email_signIn);
        signIn_password = findViewById(R.id.input_password_signIn);

        tw_signup = findViewById(R.id.textview_signup);

    }

    public void signupListeners(View view) {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }


    public void buttonSignIn(View view) {
        if (isValidSignInDetails()){
            signIn();
        }
    }

    private void signIn(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,signIn_email.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,signIn_password.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() >0 ){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        mPreferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        mPreferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        mPreferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        mPreferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        mPreferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        showToast("Unable to sign in");
                    }
                });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){
        if(signIn_email.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(signIn_email.getText().toString()).matches()){
            showToast("Enter a valid email");
            return false;
        }else if (signIn_password.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else {
            return true;
        }
    }

}