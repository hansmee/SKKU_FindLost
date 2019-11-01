package edu.skku.map.skku_findlost;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAccountList {
    public String ID;
    public String PW;
    public String UID;
    public String name;

    public FirebaseAccountList(){
    // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public FirebaseAccountList(String ID, String PW, String UID, String name) {
        this.ID=ID;
        this.PW=PW;
        this.UID=UID;
        this.name=name;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("PW", PW);
        result.put("UID", UID);
        result.put("name", name);
        return result;
    }
}
