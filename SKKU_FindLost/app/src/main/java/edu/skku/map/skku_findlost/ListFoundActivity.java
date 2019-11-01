package edu.skku.map.skku_findlost;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class ListFoundActivity extends AppCompatActivity {

    String currentID;
    String currentName;
    ListView listView;
    ArrayList<FoundItem> founds;
    FoundAdapter foundAdapter;
    Spinner location;
    Spinner keyword;
    String loc;
    String keyw;
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
        setContentView(R.layout.activity_list_found);
        Intent intent = getIntent();
        currentID = intent.getStringExtra("currentID");
        currentName = intent.getStringExtra("currentName");

        listView = (ListView)findViewById(R.id.found_listview);
        founds = new ArrayList<FoundItem>();

        mPostReference = FirebaseDatabase.getInstance().getReference();

        getFirebaseDatabase();

        foundAdapter = new FoundAdapter(this, founds);

        listView.setAdapter(foundAdapter);

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

        listView.setAdapter(foundAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostFoundActivity.class);
                intent.putExtra("currentID", currentID);
                intent.putExtra("currentName", currentName);
                intent.putExtra("f_title", founds.get(position).f_title);
                intent.putExtra("f_username", founds.get(position).f_username);
                intent.putExtra("f_userID", founds.get(position).f_userID);
                intent.putExtra("f_content", founds.get(position).f_content);
                intent.putExtra("f_place", founds.get(position).f_place);
                intent.putExtra("f_type", founds.get(position).f_type);
                intent.putExtra("f_date",founds.get(position).f_date);
                intent.putExtra("f_keeping_place", founds.get(position).f_keeping_place);
                intent.putExtra("f_photo", founds.get(position).f_photo);
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
                        Toast.makeText(ListFoundActivity.this, "끝 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }else if(eYear != 0){
                        Toast.makeText(ListFoundActivity.this, "시작 날짜를 고르세요.", Toast.LENGTH_SHORT).show();
                    }
                }else if(sYear > eYear){
                    bdate = false;
                    Toast.makeText(ListFoundActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth > eMonth){
                    bdate = false;
                    Toast.makeText(ListFoundActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
                }else if(sYear == eYear && sMonth == eMonth && sDay > eDay){
                    bdate = false;
                    Toast.makeText(ListFoundActivity.this, "불가능한 기간입니다.", Toast.LENGTH_SHORT).show();
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
                founds.clear();
                NoResult = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FoundItem get = postSnapshot.getValue(FoundItem.class);
                    String[] info = {get.f_title, get.f_photo, get.f_content, get.f_place, get.f_type, get.f_date, get.f_username, get.f_userID, get.f_keeping_place};

                    String tmp = info[5].replaceAll("[^0-9 ]", "");
                    //Toast.makeText(ListLostActivity.this, tmp, Toast.LENGTH_SHORT).show();
                    String[] datestring = tmp.split(" ");
                    int date = Integer.parseInt(datestring[0])*10000 + Integer.parseInt(datestring[1])*100 + Integer.parseInt(datestring[2]);
                    int sdate = sYear*10000 + (sMonth+1)*100 + sDay;
                    int edate = eYear*10000 + (eMonth+1)*100 + eDay;
                    boolean bLoc = false, bKey = false, bDate = false;

                    FoundItem result = new FoundItem(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7], info[8]);

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
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bloc && bkey) {
                        if(bLoc && bKey){
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bloc && bdate) {
                        if (bLoc && bDate){
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bkey && bdate){
                        if(bKey && bDate){
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bloc){
                        if(bLoc){
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bkey) {
                        if (bKey){
                            founds.add(result);
                            NoResult = false;
                        }
                    }else if(bdate){
                        if(bDate){
                            founds.add(result);
                            NoResult = false;
                        }
                    } else {
                        founds.add(result);
                        NoResult = false;
                    }
                }

                Collections.reverse(founds);

                foundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("found_list").addValueEventListener(postListener);
    }
}