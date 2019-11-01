package edu.skku.map.skku_findlost;

import java.util.HashMap;
import java.util.Map;

public class FoundItem {
    public String f_title;
    public String f_photo;
    public String f_content;
    public String f_place;
    public String f_type;
    public String f_date;
    public String f_username;
    public String f_userID;
    public String f_keeping_place;

    public FoundItem(){}

    public FoundItem(String found_titleET, String found_image, String found_contentET, String location, String keyword, String date, String name, String ID, String found_storeET){
        this.f_title = found_titleET;
        this.f_photo = found_image;
        this.f_content = found_contentET;
        this.f_place = location;
        this.f_type = keyword;
        this.f_date = date;
        this.f_username = name;
        this.f_userID = ID;
        this.f_keeping_place = found_storeET;
    }

    public String getFound_titleET(){return f_title;}

    public String getFound_image(){return f_photo;}

    public String getFound_contentET(){return f_content;}

    public String getLocation(){return f_place;}

    public String getKeyword(){return f_type;}

    public String getDate(){return f_date;}

    public String getName(){return f_username;}

    public String getUserID(){return f_userID;}

    public String getKeepingPlace(){return f_keeping_place;}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("f_title", f_title);
        result.put("f_photo", f_photo);
        result.put("f_content", f_content);
        result.put("f_place", f_place);
        result.put("f_type", f_type);
        result.put("f_date", f_date);
        result.put("f_username", f_username);
        result.put("f_userID", f_userID);
        result.put("f_keeping_place", f_keeping_place);
        return result;
    }
}