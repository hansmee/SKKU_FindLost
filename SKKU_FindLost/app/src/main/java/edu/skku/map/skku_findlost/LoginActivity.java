package edu.skku.map.skku_findlost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ImageButton login;
    ImageButton sign_up;
    String currentID;
    String currentName;
    EditText edit_id;
    EditText edit_pw;
    SharedPreferences loginPref;
    String shakey;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pw = (EditText) findViewById(R.id.edit_pw);
        login = (ImageButton) findViewById(R.id.login);
        sign_up = (ImageButton) findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();
        loginPref = this.getSharedPreferences("user_SP", this.MODE_PRIVATE);
        dbReference = FirebaseDatabase.getInstance().getReference().child("account_list");

        // Login SharedPreferences
        final SharedPreferences.Editor editor = loginPref.edit();
        String defaultValue = loginPref.getString("login", null);
        if (defaultValue != null) {
            Toast.makeText(LoginActivity.this, "자동로그인 중입니다.", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MenuActivity.class);
            // firebase database에서 현재 user의 name, id 가져오기
            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("LOG:login ID", ":"+currentID);
                    Map<String, Object> account_list = (Map<String, Object>) dataSnapshot.getValue();
                    for(String childKey: account_list.keySet()){
                        if(childKey.equals(mAuth.getUid()))
                        {
                            Map<String, Object> currentObject = (Map<String, Object>) account_list.get(childKey);
                            currentID=currentObject.get("ID").toString();
                            currentName=currentObject.get("name").toString();
                            intent.putExtra("currentID", currentID);
                            intent.putExtra("currentName", currentName);
                            Log.d("LOG:login ID", ":"+currentID);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //signup button
        sign_up.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                edit_id.getText().clear();
                edit_pw.getText().clear();
                intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //login button
        login.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                final String id = edit_id.getText().toString();
                final String pw = edit_pw.getText().toString();
                shakey = id + " " + pw;
                if (!id.equals("") && !pw.equals("")) {
                    mAuth.signInWithEmailAndPassword(id, pw)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Save LOGIN information in shared preferences
                                        editor.putString("login", shakey);
                                        editor.commit();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(LoginActivity.this, MenuActivity.class);
                                        // firebase database에서 현재 user의 name, id 가져오기
                                        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Map<String, Object> account_list = (Map<String, Object>) dataSnapshot.getValue();
                                                for(String childKey: account_list.keySet()){
                                                    if(childKey.equals(mAuth.getUid()))
                                                    {
                                                        Map<String, Object> currentObject = (Map<String, Object>) account_list.get(childKey);
                                                        currentID=currentObject.get("ID").toString();
                                                        currentName=currentObject.get("name").toString();
                                                        intent.putExtra("currentID", currentID);
                                                        intent.putExtra("currentName", currentName);
                                                        startActivity(intent);
                                                        finish();
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        edit_pw.getText().clear();
                                    }
                                }
                            });
                } else
                    Toast.makeText(LoginActivity.this, "잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
