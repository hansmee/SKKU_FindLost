package edu.skku.map.skku_findlost;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String ID;
    String PW;
    String UID;
    String name;
    EditText edit_signup_id;
    EditText edit_signup_pw;
    EditText edit_pw_check;
    EditText edit_signup_name;
    Button checkButton;
    Button sendButton;
    Button verifyButton;
    ImageButton signupButton;
    TextView dup_tv;
    TextView verify_tv;
    boolean valid_id;
    boolean duplicate_checked = false;
    boolean is_duplicate;
    boolean is_inAuth;
    boolean is_verified=false;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edit_signup_id = (EditText) findViewById(R.id.edit_signup_id);
        edit_signup_pw = (EditText) findViewById(R.id.edit_signup_pw);
        edit_pw_check = (EditText) findViewById(R.id.edit_pw_check);
        edit_signup_name = (EditText) findViewById(R.id.edit_signup_name);
        checkButton = (Button) findViewById(R.id.check_button);
        sendButton = (Button) findViewById(R.id.send_button);
        verifyButton = (Button) findViewById(R.id.verify_button);
        signupButton = (ImageButton) findViewById(R.id.signup_enter);
        dup_tv = (TextView) findViewById(R.id.dup_tv);
        verify_tv = (TextView) findViewById(R.id.verify_tv);
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        // 아이디 유효성 확인
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = edit_signup_id.getText().toString();
                duplicate_checked = true;

                // 이메일 형식 확인
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                if (!ID.matches(regex)) {
                    valid_id = false;
                    duplicate_checked = false;
                    edit_signup_id.getText().clear();
                    dup_tv.setText("이메일 형식이 아닙니다.");
                    dup_tv.setTextColor(Color.RED);
                    return;
                } else
                    valid_id = true;

                // 이메일 중복 확인
                mAuth.fetchSignInMethodsForEmail(ID).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            is_duplicate = false;
                            dup_tv.setText("사용 가능한 아이디입니다.");
                            dup_tv.setTextColor(Color.BLUE);
                        } else {
                            duplicate_checked = false;
                            is_duplicate = true;
                            dup_tv.setText("이미 사용중인 아이디입니다.");
                            dup_tv.setTextColor(Color.RED);
                        }
                    }
                });
            }
        });

        // 인증메일 발송 버튼 클릭
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PW = edit_signup_pw.getText().toString();
                String pw_check = edit_pw_check.getText().toString();

                if(is_verified==true){
                    Toast.makeText(SignupActivity.this, "이미 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!ID.equals(edit_signup_id.getText().toString()))
                    duplicate_checked=false;

                if (valid_id == false) {
                    Toast.makeText(SignupActivity.this, "유효한 아이디가 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (is_duplicate == true) {
                    Toast.makeText(SignupActivity.this, "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (duplicate_checked == false) {
                    dup_tv.setText("중복확인이 필요합니다.");
                    dup_tv.setTextColor(Color.BLACK);
                    Toast.makeText(SignupActivity.this, "아이디 중복확인을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (PW.length() < 6 || PW.length() >= 14) {
                    edit_signup_pw.getText().clear();
                    edit_pw_check.getText().clear();
                    Toast.makeText(SignupActivity.this, "유효한 비밀번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PW.equals(pw_check)) {
                    edit_pw_check.getText().clear();
                    Toast.makeText(SignupActivity.this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ID.equals("") && !PW.equals("")) {
                    mAuth.createUserWithEmailAndPassword(ID, PW)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UID = mAuth.getUid();
                                        user = mAuth.getCurrentUser();
                                        if(!user.isEmailVerified()) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    verify_tv.setText("인증메일을 발송하였습니다.");
                                                }
                                            });
                                        }
                                    }
                                }
                            });

                    }
                }

        });

        // 인증확인 버튼 클릭
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_tv.setText("인증을 확인하고 있습니다.");
                mAuth.getCurrentUser().reload();
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(user.isEmailVerified()) {
                            verify_tv.setText("인증이 완료되었습니다.");
                            verify_tv.setTextColor(Color.BLUE);
                            is_verified=true;
                            edit_signup_id.setEnabled(false);
                            edit_signup_pw.setEnabled(false);
                            edit_pw_check.setEnabled(false);
                        }
                        else{
                            verify_tv.setText("인증이 완료되지 않았습니다.");
                            verify_tv.setTextColor(Color.RED);
                        }
                    }
                }, 3000);

            }
        });

        // 가입하기 버튼 클릭
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edit_signup_name.getText().toString();

                if (name.equals(""))
                    Toast.makeText(SignupActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();

                else if (!is_verified)
                    Toast.makeText(SignupActivity.this, "메일 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();

                else
                {
                    postFirebaseDatabase(true);
                    Toast.makeText(SignupActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    clearET();
                }
            }

        });
    }

    public void postFirebaseDatabase(boolean add) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {
            FirebaseAccountList accountList = new FirebaseAccountList(ID, PW, UID, name);
            postValues = accountList.toMap();
        }
        childUpdates.put("/account_list/" + UID, postValues);
        dbReference.updateChildren(childUpdates);
        verify_tv.setText("인증메일을 발송 중입니다.");
    }

    public void clearET() {
        edit_signup_id.setText("");
        edit_signup_pw.setText("");
        edit_pw_check.setText("");
        edit_signup_name.setText("");
        ID = "";
        PW="";
        UID="";
        name = "";
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SignupActivity.this, "회원가입이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
