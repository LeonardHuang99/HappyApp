package com.leonardhuang.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.leonardhuang.chatapp.adapters.UsersAdapter;
import com.leonardhuang.chatapp.listeners.UserListener;
import com.leonardhuang.chatapp.models.User;
import com.leonardhuang.chatapp.R;
import com.leonardhuang.chatapp.utilities.Constants;
import com.leonardhuang.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ProgressBar mProgressBarUsers;
    private PreferenceManager mPreferenceManager;
    private TextView mTextErrorUsers;
    private RecyclerView mRecyclerViewUsers;
    private ImageView mBackImageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mProgressBarUsers = findViewById(R.id.progressBar);
        mTextErrorUsers = findViewById(R.id.textErrorUsers);
        mRecyclerViewUsers = findViewById(R.id.usersRecyclerView);
        mBackImageUsers = findViewById(R.id.imageBack);
        mPreferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        mBackImageUsers.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = mPreferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.lastName = queryDocumentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.firstName = queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size()>0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            mRecyclerViewUsers.setAdapter(usersAdapter);
                            mRecyclerViewUsers.setVisibility(View.VISIBLE);
                        }
                        else{
                            showErrorMessage();
                        }
                    }
                });
    }

    private void showErrorMessage(){
        mTextErrorUsers.setText(String.format("%s","No user available"));
        mTextErrorUsers.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            mProgressBarUsers.setVisibility(View.VISIBLE);
        }else{
            mProgressBarUsers.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}