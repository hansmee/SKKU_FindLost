package edu.skku.map.skku_findlost;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.skku.map.skku_findlost.SendBird.SendBirdActivity;

public class PostFoundActivity extends AppCompatActivity {

    String currentID;
    String currentName;
    String title;
    String username;
    String userID;
    String content;
    String place;
    String type;
    String date;
    String store;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_found);

        Intent intent = getIntent();
        currentID = intent.getStringExtra("currentID");
        currentName = intent.getStringExtra("currentName");
        title = intent.getStringExtra("f_title");
        username = intent.getStringExtra("f_username");
        userID = intent.getStringExtra("f_userID");
        content = intent.getStringExtra("f_content");
        place = intent.getStringExtra("f_place");
        type = intent.getStringExtra("f_type");
        date = intent.getStringExtra("f_date");
        store = intent.getStringExtra("f_keeping_place");
        image = intent.getStringExtra("f_photo");

        TextView titleTV = (TextView)findViewById(R.id.found_titleTV);
        TextView contentTV = (TextView)findViewById(R.id.found_contentTV);
        TextView locationTV = (TextView)findViewById(R.id.found_locTV);
        TextView typeTV = (TextView)findViewById(R.id.found_typeTV);
        TextView dateTV = (TextView)findViewById(R.id.found_dateTV);
        TextView storeTV = (TextView)findViewById(R.id.found_storeTV);
        TextView usernameTV = (TextView)findViewById(R.id.found_usernameTV);
        final ImageView imageview = (ImageView)findViewById(R.id.found_image);
        ImageButton go_chat = (ImageButton) findViewById(R.id.go_chat);

        titleTV.setText(title);
        contentTV.setText(content);
        locationTV.setText(place);
        typeTV.setText(type);
        dateTV.setText(date);
        storeTV.setText(store);
        usernameTV.setText(username);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("found_image");
        final String fileName = image;

        storageRef = storageRef.child(fileName);

        StorageReference pathReference = storageRef;

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(imageview.getContext()).load(u.toString()).into(imageview);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        go_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentID.equals(userID))
                {
                    Toast.makeText(PostFoundActivity.this, "본인과 채팅을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(PostFoundActivity.this, SendBirdActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("create", "true");
                intent.putExtra("targetID", userID);
                startActivity(intent);
            }
        });
    }
}