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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportFoundActivity extends AppCompatActivity {


    ImageView found_image;
    Button get_image;
    Button found_enter;
    Uri filepath;
    String f_photo;
    Button date;
    int mYear, mMonth, mDay;

    EditText found_titleET;
    EditText found_contentET;
    EditText found_keeping_placeET;
    Spinner location;
    Spinner keyword;
    String f_date;
    String f_content;
    String f_title;
    String f_place;
    String f_type;
    String f_username;
    String f_userID;
    String f_keeping_place;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_found);

        f_date = "";

        found_titleET = findViewById(R.id.found_titleET);
        found_contentET = findViewById(R.id.found_contentET);
        location = findViewById(R.id.location);
        keyword = findViewById(R.id.keyword);
        found_keeping_placeET = findViewById(R.id.store_ET);

        found_image = (ImageView)findViewById(R.id.found_image);
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
                f_date =mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일";
            }
        });

        mPostReference = FirebaseDatabase.getInstance().getReference();

        found_enter = (Button)findViewById(R.id.found_enter);
        found_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f_content = found_contentET.getText().toString();
                f_title = found_titleET.getText().toString();
                f_place = location.getSelectedItem().toString();
                f_type = keyword.getSelectedItem().toString();
                f_keeping_place = found_keeping_placeET.getText().toString();
                Intent intent1= getIntent();
                f_username = intent1.getStringExtra("currentName");
                f_userID = intent1.getStringExtra("currentID");
                f_photo = uploadFile(f_title, mYear+"",(mMonth+1)+"",mDay+"");
                if(f_title.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_photo == "no file"){
                    Toast.makeText(ReportFoundActivity.this, "사진을 첨부해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_content.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_place.equals("장소 선택")){
                    Toast.makeText(ReportFoundActivity.this, "장소를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_type.equals("종류 선택")){
                    Toast.makeText(ReportFoundActivity.this, "종류를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_keeping_place.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "보관 장소를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_date.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "날짜를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabese(true);
                    Toast.makeText(ReportFoundActivity.this, "분실 신고가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    f_date = "";
                    Intent intent = new Intent(ReportFoundActivity.this, MenuActivity.class);
                    intent.putExtra("currentName", f_username);
                    intent.putExtra("currentID", f_userID);
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
        String t = found_titleET.getText().toString();
        String c = found_contentET.getText().toString();
        String p = found_keeping_placeET.getText().toString();

        setContentView(R.layout.activity_report_found);

        found_titleET = findViewById(R.id.found_titleET);
        found_contentET = findViewById(R.id.found_contentET);
        location = findViewById(R.id.location);
        keyword = findViewById(R.id.keyword);
        found_keeping_placeET = findViewById(R.id.store_ET);
        date = (Button)findViewById(R.id.get_date);
        found_image = (ImageView)findViewById(R.id.found_image);
        get_image = (Button)findViewById(R.id.get_image);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        found_enter = (Button)findViewById(R.id.found_enter);

        found_titleET.setText(t);
        found_contentET.setText(c);
        found_keeping_placeET.setText(p);
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
                f_date =mYear + "년 " + (mMonth + 1) + "월 " + mDay + "일";
            }
        });

        if(filepath!=null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                found_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        found_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f_content = found_contentET.getText().toString();
                f_title = found_titleET.getText().toString();
                f_place = location.getSelectedItem().toString();
                f_type = keyword.getSelectedItem().toString();
                f_keeping_place = found_keeping_placeET.getText().toString();
                Intent intent1= getIntent();
                f_username = intent1.getStringExtra("currentName");
                f_userID = intent1.getStringExtra("currentID");
                f_photo = uploadFile(f_title, mYear+"",(mMonth+1)+"",mDay+"");
                if(f_title.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_photo == "no file"){
                    Toast.makeText(ReportFoundActivity.this, "사진을 첨부해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_content.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_place.equals("장소 선택")){
                    Toast.makeText(ReportFoundActivity.this, "장소를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_type.equals("종류 선택")){
                    Toast.makeText(ReportFoundActivity.this, "종류를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_keeping_place.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "보관 장소를 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(f_date.equals("")){
                    Toast.makeText(ReportFoundActivity.this, "날짜를 선택해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabese(true);
                    Toast.makeText(ReportFoundActivity.this, "분실 신고가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    f_date = "";
                    Intent intent = new Intent(ReportFoundActivity.this, MenuActivity.class);
                    intent.putExtra("currentName", f_username);
                    intent.putExtra("currentID", f_userID);
                    startActivity(intent);
                }
            }
        });

    }

    public  void postFirebaseDatabese(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> foundValues = null;
        if(add){
            FoundItem found = new FoundItem(f_title,f_photo,f_content,f_place,f_type,f_date, f_username, f_userID, f_keeping_place);
            foundValues = found.toMap();
        }
        childUpdates.put("/found_list/"+NameByCurrentTime()+"/", foundValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void updateDate(){
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
                found_image.setImageBitmap(bitmap);
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

            StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("found_image/" + photoname);

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