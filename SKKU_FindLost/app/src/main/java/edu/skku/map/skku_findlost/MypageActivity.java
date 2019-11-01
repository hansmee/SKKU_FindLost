package edu.skku.map.skku_findlost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import edu.skku.map.skku_findlost.SendBird.SendBirdActivity;

public class MypageActivity extends AppCompatActivity {

    TextView nameTV;
    TextView idTV;
    Button logoutButton;
    Button chatButton;
    private FirebaseAuth mAuth;
    SharedPreferences loginPref;
    String currentID;
    String currentName;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        Intent intent = getIntent();
        nameTV = (TextView) findViewById(R.id.mypage_nameTV);
        idTV = (TextView) findViewById(R.id.mypage_idTV);
        logoutButton = (Button) findViewById(R.id.logout_button);
        mAuth = FirebaseAuth.getInstance();
        loginPref = getSharedPreferences("user_SP", this.MODE_PRIVATE);
        currentID=intent.getStringExtra("currentID");
        currentName=intent.getStringExtra("currentName");
        nameTV.setText(currentName+" 님");
        idTV.setText(currentID);

        logoutButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = loginPref.edit();
                editor.clear();
                editor.commit();
            }
        });

        chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, SendBirdActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("create", "false");

                startActivity(intent);
            }
        });
    }
}
