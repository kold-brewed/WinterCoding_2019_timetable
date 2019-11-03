package com.koldbrew.timetable.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;

public class LectureItem implements Serializable {
    private String code;
    private String name;
    private String professor;
    private String location;
    private String startTime;
    private String endTime;
    private String[] dayofweek;
    private String details;
    private ArrayList<Memo> memos;

    private static JSONParser jsonParser = new JSONParser();

    public LectureItem(String name, String startTime, String endTime, String[] dayofweek,
                       String code, String professor, String location){
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayofweek = dayofweek;
        this.code = code;
        this.professor = professor;
        this.location = location;
        this.details = "";
        this.memos = new ArrayList<>();
    }

    public LectureItem(LectureItem item){
        this.name = item.getName();
        this.startTime = item.getStartTime();
        this.endTime = item.getEndTime();
        this.dayofweek = item.getDayofweek();
        this.code = item.getCode();
        this.professor = item.getProfessor();
        this.location = item.getLocation();
        this.details = item.getDetails();
        this.memos = item.getMemos();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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
        String str = startTime + " ~ " + endTime + " | ";
        for(int i = 0; i < dayofweek.length; i++){
            str += " (" + dayofweek[i] + ")";
        }
        return str;
    }

    public static LectureItem getItem(String _jsonArrayDataString) throws ParseException {
        LectureItem retDataList;

        JSONObject items = (JSONObject) jsonParser.parse(_jsonArrayDataString);
        final JSONArray jsonList = (JSONArray) items.get("Items");

        JSONObject item = (JSONObject) jsonList.get(0);
        JSONArray days = (JSONArray) jsonParser.parse(item.get("dayofweek").toString());
        String[] dayofweek = new String[days.size()];
        for(int i = 0; i < days.size(); i++){
            dayofweek[i] = days.get(i).toString();
        }

        retDataList = new LectureItem(
                item.get("lecture").toString(),
                item.get("start_time").toString(),
                item.get("end_time").toString(),
                dayofweek,
                item.get("code").toString(),
                item.get("professor").toString(),
                item.get("location").toString()
        );
        return retDataList;
    }

    public static ArrayList<LectureItem> getListified(String jsonArrayDataString) throws ParseException {
        ArrayList<LectureItem> retDataList = new ArrayList<>();

        JSONObject items = (JSONObject) jsonParser.parse(jsonArrayDataString);
        final JSONArray jsonList = (JSONArray) items.get("Items");

        for(int idx = 0; idx < jsonList.size(); idx++){
            JSONObject item = (JSONObject) jsonList.get(idx);
            JSONArray days = (JSONArray) jsonParser.parse(item.get("dayofweek").toString());
            String[] dayofweek = new String[days.size()];
            for(int i = 0; i < days.size(); i++){
                dayofweek[i] = days.get(i).toString();
            }
            retDataList.add(
                    new LectureItem(
                            item.get("lecture").toString(),
                            item.get("start_time").toString(),
                            item.get("end_time").toString(),
                            dayofweek,
                            item.get("code").toString(),
                            item.get("professor").toString(),
                            item.get("location").toString()
                    ));
        }
        return retDataList;
    }
}
