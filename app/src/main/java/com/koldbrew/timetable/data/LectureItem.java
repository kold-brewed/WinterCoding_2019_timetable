package com.koldbrew.timetable.data;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LectureItem implements Serializable {
    private String code;
    private String name;
    private String professor;
    private String location;
    private String start_time;
    private String end_time;
    private String[] dayofweek;
    private String details;
    private ArrayList<Memo> memos;

    private static JSONParser jsonParser = new JSONParser();

    public LectureItem(String name, String start_time, String end_time, String[] dayofweek,
                       String code, String professor, String location){
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.dayofweek = dayofweek;
        this.code = code;
        this.professor = professor;
        this.location = location;
        this.details = "";
        this.memos = new ArrayList<>();
    }

    public LectureItem(LectureItem _item){
        this.name = _item.getName();
        this.start_time = _item.getStart_time();
        this.end_time = _item.getEnd_time();
        this.dayofweek = _item.getDayofweek();
        this.code = _item.getCode();
        this.professor = _item.getProfessor();
        this.location = _item.getlocation();
        this.details = _item.getDetails();
        this.memos = _item.getMemos();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String[] getDayofweek() {
        return dayofweek;
    }

    public void setDayofweek(String[] dayofweek) {
        this.dayofweek = dayofweek;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ArrayList<Memo> getMemos(){ return memos; }

    public void setMemos(ArrayList<Memo> memos) { this.memos = memos; }

    public String dateToString(){
        String str = start_time + " ~ " + end_time + " | ";
        for(int i = 0; i < dayofweek.length; i++){
            str += " (" + dayofweek[i] + ")";
        }
        return str;
    }

    public static LectureItem get_item(String _jsonArrayDataString) throws JSONException, ParseException {
        LectureItem _retDataList;

        JSONObject items = (JSONObject) jsonParser.parse(_jsonArrayDataString);
        final JSONArray _jsonList = (JSONArray) items.get("Items");

        JSONObject item = (JSONObject) _jsonList.get(0);
        JSONArray days = (JSONArray) jsonParser.parse(item.get("dayofweek").toString());
        String[] _dayofweek = new String[days.size()];
        for(int i = 0; i < days.size(); i++){
            _dayofweek[i] = days.get(i).toString();
        }

        _retDataList = new LectureItem(
                item.get("lecture").toString(),
                item.get("start_time").toString(),
                item.get("end_time").toString(),
                _dayofweek,
                item.get("code").toString(),
                item.get("professor").toString(),
                item.get("location").toString()
        );
        return _retDataList;
    }

    public static ArrayList<LectureItem> get_listified(String _jsonArrayDataString) throws JSONException, ParseException {
        ArrayList<LectureItem> _retDataList = new ArrayList<>();

        JSONObject items = (JSONObject) jsonParser.parse(_jsonArrayDataString);
        final JSONArray _jsonList = (JSONArray) items.get("Items");

        for(int idx = 0; idx < _jsonList.size(); idx++){
            JSONObject item = (JSONObject) _jsonList.get(idx);
            JSONArray days = (JSONArray) jsonParser.parse(item.get("dayofweek").toString());
            String[] _dayofweek = new String[days.size()];
            for(int i = 0; i < days.size(); i++){
                _dayofweek[i] = days.get(i).toString();
            }
            _retDataList.add(
                    new LectureItem(
                            item.get("lecture").toString(),
                            item.get("start_time").toString(),
                            item.get("end_time").toString(),
                            _dayofweek,
                            item.get("code").toString(),
                            item.get("professor").toString(),
                            item.get("location").toString()
                    ));
        }
        return _retDataList;
    }
}
