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

public class PostLostActivity extends AppCompatActivity {

    String currentID;
    String currentName;
    String title;
    String username;
    String userID;
    String content;
    String place;
    String type;
    String date;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_lost);

        Intent intent = getIntent();
        currentID = intent.getStringExtra("currentID");
        currentName = intent.getStringExtra("currentName");
        title = intent.getStringExtra("l_title");
        username = intent.getStringExtra("l_username");
        userID = intent.getStringExtra("l_userID");
        content = intent.getStringExtra("l_content");
        place = intent.getStringExtra("l_place");
        type = intent.getStringExtra("l_type");
        date = intent.getStringExtra("l_date");
        image = intent.getStringExtra("l_photo");

        TextView titleTV = (TextView)findViewById(R.id.lost_titleTV);
        TextView contentTV = (TextView)findViewById(R.id.lost_contentTV);
        TextView locationTV = (TextView)findViewById(R.id.lost_locTV);
        TextView typeTV = (TextView)findViewById(R.id.lost_typeTV);
        TextView dateTV = (TextView)findViewById(R.id.lost_dateTV);
        TextView usernameTV = (TextView) findViewById(R.id.lost_usernameTV);
        final ImageView imageview = (ImageView)findViewById(R.id.lost_image);
        ImageButton go_chat = (ImageButton) findViewById(R.id.go_chat);

        titleTV.setText(title);
        contentTV.setText(content);
        locationTV.setText(place);
        typeTV.setText(type);
        dateTV.setText(date);
        usernameTV.setText(username);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("lost_image");
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
                    Toast.makeText(PostLostActivity.this, "본인과 채팅을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(PostLostActivity.this, SendBirdActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("create", "true");
                intent.putExtra("targetID", userID);
                startActivity(intent);
            }
        });
    }
}