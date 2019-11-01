package edu.skku.map.skku_findlost;

import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class LostItem {
    public String l_title;
    public String l_photo;
    public String l_content;
    public String l_place;
    public String l_type;
    public String l_date;
    public String l_username;
    public String l_userID;
    public StorageReference l_photo1;

    public LostItem(){}

    public LostItem(String lost_titleET, String lost_image, String lost_contentET, String location, String keyword, String date, String name, String ID){
        this.l_title = lost_titleET;
        this.l_photo = lost_image;
        this.l_content = lost_contentET;
        this.l_place = location;
        this.l_type = keyword;
        this.l_date = date;
        this.l_username = name;
        this.l_userID = ID;
        //    this.l_photo1 = l_photo1;
    }

    public String getLost_titleET(){return l_title;}

    public String getLost_image(){return l_photo;}

    public String getLost_contentET(){return l_content;}

    public String getLocation(){return l_place;}

    public String getKeyword(){return l_type;}

    public String getDate(){return l_date;}

    public String getName(){return l_username;}

    public String getUserID(){return l_userID;}

    public StorageReference getL_photo1(){return l_photo1;}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("l_title", l_title);
        result.put("l_photo", l_photo);
        result.put("l_content", l_content);
        result.put("l_place", l_place);
        result.put("l_type", l_type);
        result.put("l_date", l_date);
        result.put("l_username", l_username);
        result.put("l_userID", l_userID);

        return result;
    }
}