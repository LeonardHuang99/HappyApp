package com.leonardhuang.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.leonardhuang.chatapp.R;
import com.leonardhuang.chatapp.adapters.ChatAdapter;
import com.leonardhuang.chatapp.models.ChatMessage;
import com.leonardhuang.chatapp.models.User;
import com.leonardhuang.chatapp.utilities.Constants;
import com.leonardhuang.chatapp.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private User receiverUser;
    private TextView textName;
    private ImageView imageBack;
    private RecyclerView chatRecyclerView;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private PreferenceManager mPreferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textName = findViewById(R.id.textName);
        imageBack = findViewById(R.id.imageBack);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        layoutSend = findViewById(R.id.layoutSend);
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();

    }

    private void init(){
        mPreferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                mPreferenceManager.getString(Constants.KEY_USER_ID)
        );
        chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, mPreferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE,inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId != null){
            updateConversion(inputMessage.getText().toString());
        } else{
          HashMap<String, Object> conversion = new HashMap<>();
          conversion.put(Constants.KEY_SENDER_ID, mPreferenceManager.getString(Constants.KEY_USER_ID));
          conversion.put(Constants.KEY_SENDER_FNAME, mPreferenceManager.getString(Constants.KEY_FIRST_NAME));
          conversion.put(Constants.KEY_SENDER_LNAME, mPreferenceManager.getString(Constants.KEY_LAST_NAME));
          conversion.put(Constants.KEY_SENDER_IMAGE, mPreferenceManager.getString(Constants.KEY_SENDER_IMAGE));
          conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
          conversion.put(Constants.KEY_RECEIVER_FNAME, receiverUser.firstName);
          conversion.put(Constants.KEY_RECEIVER_LNAME, receiverUser.lastName);
          conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
          conversion.put(Constants.KEY_LAST_MESSAGE, inputMessage.getText().toString());
          conversion.put(Constants.KEY_TIMESTAMP, new Date());
          addConversion(conversion);
        }
        inputMessage.setText(null);
    }

    private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,mPreferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(mEventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,mPreferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(mEventListener);
    }

    private final EventListener<QuerySnapshot> mEventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            chatRecyclerView.setVisibility(View.VISIBLE);
        }
        if (conversionId == null){
            checkForConversion();
        }
    };

    // We have to encode the image in order to upload images to the Firebase DB as a string
    private Bitmap getBitmapFromEncodedString (String encodedImage) {
        // Decode the encoded Image in input and return the data in a new byte array
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        // BitmapFactory is to create a bitmap object
        // With decodeByteArray, we are decoding the image into a Bitmap
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        textName.setText(receiverUser.firstName + " " +receiverUser.lastName);
    }

    private void setListeners(){
        imageBack.setOnClickListener(v -> onBackPressed());
        layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime (Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversion(){
        if (chatMessages.size() != 0){
            checkForConversionRemotely(
                    mPreferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    mPreferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };
}