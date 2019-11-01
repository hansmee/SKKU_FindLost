package edu.skku.map.skku_findlost;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class ListLostActivity extends AppCompatActivity {

    String currentID;
    String currentName;
    ListView listView;
    ArrayList<LostItem> losts;
    LostAdapter lostAdapter;
    Spinner location;
    Spinner keyword;
    String loc;
    String keyw;
    String image;
    Button start_date;
    Button end_date;
    ImageButton sort;
    int sYear, sMonth, sDay;
    int eYear, eMonth, eDay;
    boolean bloc, bkey, bdate;
    boolean NoResult;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_lost);
        Intent intent = getIntent();
        currentID = intent.getStringExtra("currentID");
        currentName = intent.getStringExtra("currentName");

        listView = (ListView)findViewById(R.id.lost_listview);
        losts = new ArrayList<LostItem>();
        bloc = false;
        bkey = false;
        bdate = false;

        mPostReference = FirebaseDatabase.getInstance().getReference();

        getFirebaseDatabase();

        lostAdapter = new LostAdapter(this, losts);

        location = (Spinner)findViewById(R.id.location);
        keyword = (Spinner)findViewById(R.id.keyword);

        start_date = (Button)findViewById(R.id.get_startdate);
        start_date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateStartDate();
            }
        });

        end_date = (Button)findViewById(R.id.get_enddate);
        end_date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateEndDate();
            }
        });

        listView.setAdapter(lostAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostLostActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("l_title", losts.get(position).l_title);
                intent.putExtra("l_username", losts.get(position).l_username);
                intent.putExtra("l_userID", losts.get(position).l_userID);
                intent.putExtra("l_content", losts.get(position).l_content);
                intent.putExtra("l_place", losts.get(position).l_place);
                intent.putExtra("l_type", losts.get(position).l_type);
                intent.putExtra("l_date",losts.get(position).l_date);
                intent.putExtra("l_photo", losts.get(position).l_photo);
                startActivity(intent);
            }
        });

        sort = (ImageButton)findViewById(R.id.sort);
        sort.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                loc = location.getSelectedItem().toString();
                keyw = keyword.getSelectedItem().toString();
                if(loc.equals("") || loc.equals("장소 선택")){
                    bloc = false;
                }else{
                    bloc = true;
                }

                if(keyw.equals("") || keyw.equals("종류 선택")){
                    bkey = false;
                }else{
                    bkey = true;
                }

                if(sYear == 0 || eYear == 0){ // 날짜 하나라도 선택 안하면
                    bdate = false;
                    if(sYear != 0){
                        Toast.makeText(ListLostActivity.this, "끝 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }else if(eYear != 0){
                        Toast.makeText(ListLostActivity.this, "시작 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }
                }else if(sYear > eYear){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth > eMonth){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth == eMonth && sDay > eDay){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    bdate = true;
                }

                getFirebaseDatabase();
            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int lc = location.getSelectedItemPosition();
        int ky = keyword.getSelectedItemPosition();
        String sd = start_date.getText().toString();
        String ed = end_date.getText().toString();

        setContentView(R.layout.activity_list_lost);

        listView = (ListView)findViewById(R.id.lost_listview);
        location = (Spinner)findViewById(R.id.location);
        keyword = (Spinner)findViewById(R.id.keyword);
        start_date = (Button)findViewById(R.id.get_startdate);
        end_date = (Button)findViewById(R.id.get_enddate);
        sort = (ImageButton)findViewById(R.id.sort);
        losts = new ArrayList<LostItem>();
        mPostReference = FirebaseDatabase.getInstance().getReference();

        start_date.setText(sd);
        start_date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateStartDate();
            }
        });
        end_date.setText(ed);
        end_date.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateEndDate();
            }
        });
        location.setSelection(lc);
        keyword.setSelection(ky);

        getFirebaseDatabase();

        lostAdapter = new LostAdapter(this, losts);
        listView.setAdapter(lostAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostLostActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("l_title", losts.get(position).l_title);
                intent.putExtra("l_username", losts.get(position).l_username);
                intent.putExtra("l_userID", losts.get(position).l_userID);
                intent.putExtra("l_content", losts.get(position).l_content);
                intent.putExtra("l_place", losts.get(position).l_place);
                intent.putExtra("l_type", losts.get(position).l_type);
                intent.putExtra("l_date",losts.get(position).l_date);
                intent.putExtra("l_photo", losts.get(position).l_photo);
                startActivity(intent);
            }
        });

        sort.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                loc = location.getSelectedItem().toString();
                keyw = keyword.getSelectedItem().toString();
                if(loc.equals("") || loc.equals("장소 선택")){
                    bloc = false;
                }else{
                    bloc = true;
                }

                if(keyw.equals("") || keyw.equals("종류 선택")){
                    bkey = false;
                }else{
                    bkey = true;
                }

                if(sYear == 0 || eYear == 0){ // 날짜 하나라도 선택 안하면
                    bdate = false;
                    if(sYear != 0){
                        Toast.makeText(ListLostActivity.this, "끝 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }else if(eYear != 0){
                        Toast.makeText(ListLostActivity.this, "시작 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }
                }else if(sYear > eYear){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth > eMonth){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth == eMonth && sDay > eDay){
                    bdate = false;
                    Toast.makeText(ListLostActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    bdate = true;
                }

                getFirebaseDatabase();
            }
        });
    }


    public void updateStartDate(){
        Calendar cal = new GregorianCalendar();
        sYear = cal.get(Calendar.YEAR);
        sMonth = cal.get(Calendar.MONTH);
        sDay = cal.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, slistener, sYear, sMonth, sDay).show();
    }

    public void updateEndDate(){
        Calendar cal = new GregorianCalendar();
        eYear = cal.get(Calendar.YEAR);
        eMonth = cal.get(Calendar.MONTH);
        eDay = cal.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, elistener, eYear, eMonth, eDay).show();
    }

    private DatePickerDialog.OnDateSetListener slistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;
            start_date.setText(sYear + "." + (sMonth + 1) + "." + sDay);
        }
    };

    private DatePickerDialog.OnDateSetListener elistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            eYear = year;
            eMonth = monthOfYear;
            eDay = dayOfMonth;
            end_date.setText(eYear + "." + (eMonth + 1) + "." + eDay);
        }
    };

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                losts.clear();
                NoResult = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    LostItem get = postSnapshot.getValue(LostItem.class);
                    String[] info = {get.l_title, get.l_photo, get.l_content, get.l_place, get.l_type, get.l_date, get.l_username, get.l_userID};
                    String tmp = info[5].replaceAll("[^0-9 ]", "");

                    String[] datestring = tmp.split(" ");
                    int date = Integer.parseInt(datestring[0])*10000 + Integer.parseInt(datestring[1])*100 + Integer.parseInt(datestring[2]);
                    int sdate = sYear*10000 + (sMonth+1)*100 + sDay;
                    int edate = eYear*10000 + (eMonth+1)*100 + eDay;
                    boolean bLoc = false, bKey = false, bDate = false;

                    final FirebaseStorage storage = FirebaseStorage.getInstance();
                    long size;

                    StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("lost_image/");

                    String fileName = info[1];

                    StorageReference spaceRef = storageRef.child(fileName);

                    LostItem result = new LostItem(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7]);

                    if(bloc) {
                        if (loc.equals(info[3])) bLoc = true;
                    }
                    if(bkey){
                        if(keyw.equals(info[4])) bKey = true;
                    }
                    if(bdate) {
                        if (date >= sdate && date <= edate) bDate = true;
                    }

                    if(bloc && bkey && bdate){
                        if(bLoc && bKey && bDate){
                            losts.add(result);
                            NoResult = false;
                        }
                    }else if(bloc && bkey) {
                        if(bLoc && bKey){
                            losts.add(result);
                            NoResult =false;
                        }
                    }else if(bloc && bdate) {
                        if (bLoc && bDate){
                            losts.add(result);
                            NoResult =false;
                        }
                    }else if(bkey && bdate){
                        if(bKey && bDate){
                            losts.add(result);
                            NoResult =false;
                        }
                    }else if(bloc){
                        if(bLoc) {
                            losts.add(result);
                            NoResult = false;
                        }
                    }else if(bkey) {
                        if (bKey){
                            losts.add(result);
                            NoResult =false;
                        }
                    }else if(bdate){
                        if(bDate){
                            losts.add(result);
                            NoResult =false;
                        }
                    } else {
                        losts.add(result);
                        NoResult =false;
                    }
                }

                Collections.reverse(losts);

                lostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("lost_list").addValueEventListener(postListener);
    }
}