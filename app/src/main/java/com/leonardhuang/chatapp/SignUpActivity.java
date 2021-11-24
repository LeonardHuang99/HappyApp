package com.leonardhuang.chatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.leonardhuang.chatapp.utilities.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

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
    private RoundedImageView image_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        image_profile = findViewById(R.id.image_profile);
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

    public void imageProfileListeners(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME,input_fname.getText().toString());
        user.put(Constants.KEY_LAST_NAME,input_lname.getText().toString());
        user.put(Constants.KEY_EMAIL,input_email.getText().toString());
        user.put(Constants.KEY_PASSWORD,input_password.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(exception ->{

                } );
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            image_profile.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }
                        catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    )

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