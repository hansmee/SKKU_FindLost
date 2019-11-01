package edu.skku.map.skku_findlost;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportLostActivity extends AppCompatActivity {

    Button date;
    ImageView lost_image;
    Button get_image;
    Button lost_enter;
    EditText lost_titleET;
    EditText lost_contentET;
    Spinner location;
    Spinner keyword;
    Uri filepath;
    String l_photo;
    String l_date;
    String l_content;
    String l_title;
    String l_place;
    String l_type;
    String l_username;
    String l_userID;
    int mYear, mMonth, mDay;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost);

        l_date = "";

        lost_titleET = findViewById(R.id.lost_titleET);
        lost_contentET = findViewById(R.id.lost_contentET);
        location = findViewById(R.id.location);
        keyword = findViewById(R.id.keyword);

        lost_image = (ImageView)findViewById(R.id.lost_image);
        get_image = (Button)findViewById(R.id.get_image);
        get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        // 오늘 날짜 가져 오기
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        date = (Button)findViewById(R.id.get_date);
        date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateDate();
                l_date =mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일";
            }
        });

        mPostReference = FirebaseDatabase.getInstance().getReference();

        lost_enter = (Button)findViewById(R.id.lost_enter);
        lost_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l_content = lost_contentET.getText().toString();
                l_title = lost_titleET.getText().toString();
                l_place = location.getSelectedItem().toString();
                l_type = keyword.getSelectedItem().toString();
                l_photo = uploadFile(l_title, mYear+"",(mMonth+1)+"",mDay+"");
                Intent intent1= getIntent();
                l_username = intent1.getStringExtra("currentName");
                l_userID = intent1.getStringExtra("currentID");
                if(l_title.equals("")){
                    Toast.makeText(ReportLostActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_photo == "no file"){
                    Toast.makeText(ReportLostActivity.this, "사진을 첨부해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_content.equals("")){
                    Toast.makeText(ReportLostActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_place.equals("장소 선택")){
                    Toast.makeText(ReportLostActivity.this, "장소를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_type.equals("종류 선택")){
                    Toast.makeText(ReportLostActivity.this, "종류를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_date.equals("")){
                    Toast.makeText(ReportLostActivity.this, "날짜를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabese(true);
                    Toast.makeText(ReportLostActivity.this, "분실 신고가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    l_date = "";
                    Intent intent = new Intent(ReportLostActivity.this, MenuActivity.class);
                    intent.putExtra("currentName", l_username);
                    intent.putExtra("currentID", l_userID);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int lc = location.getSelectedItemPosition();
        int ky = keyword.getSelectedItemPosition();
        String d = date.getText().toString();
        String t = lost_titleET.getText().toString();
        String c = lost_contentET.getText().toString();

        setContentView(R.layout.activity_report_lost);

        lost_titleET = findViewById(R.id.lost_titleET);
        lost_contentET = findViewById(R.id.lost_contentET);
        location = findViewById(R.id.location);
        keyword = findViewById(R.id.keyword);
        date = (Button)findViewById(R.id.get_date);
        lost_enter = (Button)findViewById(R.id.lost_enter);
        lost_image = (ImageView)findViewById(R.id.lost_image);
        get_image = (Button)findViewById(R.id.get_image);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        lost_titleET.setText(t);
        lost_contentET.setText(c);
        location.setSelection(lc);
        keyword.setSelection(ky);
        date.setText(d);

        get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateDate();
                l_date =mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일";
            }
        });

        if(filepath!=null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                lost_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lost_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l_content = lost_contentET.getText().toString();
                l_title = lost_titleET.getText().toString();
                l_place = location.getSelectedItem().toString();
                l_type = keyword.getSelectedItem().toString();
                l_photo = uploadFile(l_title, mYear+"",(mMonth+1)+"",mDay+"");
                Intent intent1= getIntent();
                l_username = intent1.getStringExtra("currentName");
                l_userID = intent1.getStringExtra("currentID");
                if(l_title.equals("")){
                    Toast.makeText(ReportLostActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_photo == "no file"){
                    Toast.makeText(ReportLostActivity.this, "사진을 첨부해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_content.equals("")){
                    Toast.makeText(ReportLostActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_place.equals("장소 선택")){
                    Toast.makeText(ReportLostActivity.this, "장소를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_type.equals("종류 선택")){
                    Toast.makeText(ReportLostActivity.this, "종류를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(l_date.equals("")){
                    Toast.makeText(ReportLostActivity.this, "날짜를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabese(true);
                    Toast.makeText(ReportLostActivity.this, "분실 신고가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    l_date = "";
                    Intent intent = new Intent(ReportLostActivity.this, MenuActivity.class);
                    intent.putExtra("currentName", l_username);
                    intent.putExtra("currentID", l_userID);
                    startActivity(intent);
                }
            }
        });

    }

    public  void postFirebaseDatabese(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> lostValues = null;
        if(add){
            LostItem lost = new LostItem(l_title,l_photo,l_content,l_place,l_type,l_date,l_username,l_userID);
            lostValues = lost.toMap();
        }
        childUpdates.put("/lost_list/"+NameByCurrentTime()+"/", lostValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void updateDate() {
        new DatePickerDialog(this, listener, mYear, mMonth, mDay).show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            date.setText(mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filepath = data.getData();
            Log.d("Main Activity", "uri:" + String.valueOf(filepath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                lost_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String uploadFile(String title, String year, String month, String  date) {
        String photoname;
        if (filepath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            photoname = NameByCurrentTime()+"."+title;

            StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("lost_image/" + photoname);

            storageRef.putFile(filepath);
        }
        else{
            photoname = "no file";
        }

        return photoname;
    }

    public String NameByCurrentTime() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd-HH-mm-ss-SSS", Locale.KOREA);
        Date date = new Date();
        String CurrentTime = formatter.format(date);
        return CurrentTime;
    }

}