package edu.skku.map.skku_findlost;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.skku.map.skku_findlost.SendBird.ConnectionManager;
import edu.skku.map.skku_findlost.SendBird.GroupChannelActivity;
import edu.skku.map.skku_findlost.SendBird.PreferenceUtils;
import edu.skku.map.skku_findlost.SendBird.PushUtils;
import edu.skku.map.skku_findlost.SendBird.SendBirdActivity;

public class MenuActivity extends AppCompatActivity {

    ImageButton report_lost;
    ImageButton report_found;
    ImageButton list_lost;
    ImageButton list_found;
    ImageButton mypage;
    String currentID;
    String currentName;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference().child("account_list");
        Intent intent = getIntent();
        currentID  = intent.getStringExtra("currentID");
        Log.d("LOG:menu ID", ":"+currentID);
        currentName  = intent.getStringExtra("currentName");

        report_lost = (ImageButton)findViewById(R.id.report_lost);
        report_lost.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MenuActivity.this, ReportLostActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });

        report_found = (ImageButton)findViewById(R.id.report_found);
        report_found.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MenuActivity.this, ReportFoundActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });

        list_lost = (ImageButton)findViewById(R.id.list_lost);
        list_lost.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MenuActivity.this, ListLostActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });

        list_found = (ImageButton)findViewById(R.id.list_found);
        list_found.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MenuActivity.this, ListFoundActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });

        mypage = (ImageButton)findViewById(R.id.mypage);
        mypage.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MenuActivity.this, MypageActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                startActivity(intent);
            }
        });

        //Chat
        SendBird.init("9964E1A3-6A87-4508-8EF4-FCA7C6602C5E", this.getApplicationContext());
        connectToSendBird(currentID, currentName);

    }

    // 뒤로 안가지게
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void connectToSendBird(final String userId, final String userNickname) {
        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
//                    PreferenceUtils.setConnected(false);
                    return;
                }
                //modify
                //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                //Log.d("LOG! Refreshed token: " ,refreshedToken);

//                PreferenceUtils.setConnected(true);

                // Update the user's nickname
                updateCurrentUserInfo(userNickname);
                updateCurrentUserPushToken();


                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MenuActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                            @Override
                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                if (e != null) {        // Error.
                                    return;
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateCurrentUserPushToken() {
        PushUtils.registerPushTokenForCurrentUser(MenuActivity.this, null);
    }

    private void updateCurrentUserInfo(final String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    return;
                }

            }
        });
    }
}
