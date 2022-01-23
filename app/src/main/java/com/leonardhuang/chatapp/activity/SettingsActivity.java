package com.leonardhuang.chatapp.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.leonardhuang.chatapp.R;
import com.leonardhuang.chatapp.models.User;
import com.leonardhuang.chatapp.utilities.Constants;
import com.leonardhuang.chatapp.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private PreferenceManager mPreferenceManager;
    private ImageView button_back;
    private Button button_confirm;
    private String encodedImage;
    private EditText input_fname;
    private EditText input_lname;
    private EditText input_email;
    private EditText input_password;
    private RoundedImageView image_profile;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        image_profile = findViewById(R.id.image_profile);
        button_confirm = findViewById(R.id.button_confirm);
        button_back = findViewById(R.id.imageBack);
        input_fname = findViewById(R.id.input_fname);
        input_lname = findViewById(R.id.input_lname);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        mPreferenceManager = new PreferenceManager(getApplicationContext());

        loadUserDetails();
        setListeners();
    }

    private void setListeners(){

        button_back.setOnClickListener(v ->
        startActivity(new Intent(getApplicationContext(),MainActivity.class)));

    }

    private void loadUserDetails() {
        input_fname.setText(mPreferenceManager.getString(Constants.KEY_FIRST_NAME));
        input_lname.setText(mPreferenceManager.getString(Constants.KEY_LAST_NAME));
        byte[] bytes = Base64.decode(mPreferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image_profile.setImageBitmap(bitmap);
    }

    public void updateDetails(View view) {
        updateData();
    }

    private void updateData() {


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        String currentUid = mPreferenceManager.getString(Constants.KEY_USER_ID);
        user.put(Constants.KEY_FIRST_NAME,input_fname.getText().toString());
        user.put(Constants.KEY_LAST_NAME,input_lname.getText().toString());
        if(encodedImage==null){
            encodedImage = mPreferenceManager.getString(Constants.KEY_IMAGE);
        }else {
            user.put(Constants.KEY_IMAGE,encodedImage);
        }
        database.collection("users").document(currentUid)
                .update(user)
                .addOnSuccessListener(documentReference -> {
                    mPreferenceManager.putString(Constants.KEY_FIRST_NAME, input_fname.getText().toString());
                    mPreferenceManager.putString(Constants.KEY_LAST_NAME, input_lname.getText().toString());
                    mPreferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                })
                .addOnFailureListener(exception ->{
                    //loading(false);
                    showToast(exception.getMessage());
                } );

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

    public void imageProfileListeners(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
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
    );
}