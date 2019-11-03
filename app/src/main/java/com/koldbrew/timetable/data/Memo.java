package com.koldbrew.timetable.data;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;

public class Memo implements Serializable {
    private String lectureCode;
    private String type;
    private String title;
    private String description;
    private String date;
    private static JSONParser jsonParser = new JSONParser();


    public Memo(String lectureCode, String type, String title, String description, String date){
        this.lectureCode = lectureCode;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
    }
    public Memo(Memo _item){
        this.lectureCode = _item.getlectureCode();
        this.type = _item.getDate();
        this.title = _item.getTitle();
        this.description = _item.getDescription();
        this.date = _item.getDate();
    }

    public String getlectureCode() {
        return lectureCode;
    }

    public void setlectureCode(String lectureCode) {
        this.lectureCode = lectureCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static ArrayList<Memo> getListified(String _jsonArrayDataString) throws JSONException, ParseException {
        ArrayList<Memo> _retDataList = new ArrayList<>();

        JSONObject items = (JSONObject) jsonParser.parse(_jsonArrayDataString);
        final JSONArray _jsonList = (JSONArray) items.get("Items");

        for (int idx = 0; idx < _jsonList.size(); idx++) {
            JSONObject item = (JSONObject) _jsonList.get(idx);
            _retDataList.add(
                    new Memo(
                            item.get("lecture_code").toString(),
                            item.get("type").toString(),
                            item.get("title").toString(),
                            item.get("description").toString(),
                            item.get("date").toString()
                    ));
        }
        return _retDataList;
    }
}
