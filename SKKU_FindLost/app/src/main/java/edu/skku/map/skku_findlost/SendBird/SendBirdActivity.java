package edu.skku.map.skku_findlost.SendBird;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import edu.skku.map.skku_findlost.R;

public class SendBirdActivity extends AppCompatActivity {

    String currentID;
    String currentName;
    String targetID;
    String create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendbird);

        Intent intent = getIntent();
        currentID = intent.getStringExtra("currentID");
        currentName = intent.getStringExtra("currentName");
        create = intent.getStringExtra("create");
        if(create.equals("true"))
            targetID = intent.getStringExtra("targetID");

        connectToSendBird(currentID, currentName);
    }

    private void connectToSendBird(final String userId, final String userNickname) {
        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                //modify
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("LOG! Refreshed token: " ,refreshedToken);
                updateCurrentUserInfo(userNickname);

                // Proceed to MainActivity
                Intent intent = new Intent(SendBirdActivity.this, GroupChannelActivity.class);
                intent.putExtra("create", create);
                if(create.equals("true"))
                {
                    intent.putExtra("currentID", currentID);
                    intent.putExtra("targetID", targetID);
                }

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SendBirdActivity.this, new OnSuccessListener<InstanceIdResult>() {
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
                startActivity(intent);
                finish();
            }
        });
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
