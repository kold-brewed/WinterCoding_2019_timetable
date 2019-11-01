package com.koldbrew.timetable;

import com.koldbrew.timetable.data.LectureItem;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ConnectionManager {
    private static final String API_KEY = "QJuHAX8evMY24jvpHfHQ4pHGetlk5vn8FJbk70O6";
    private static final String API_SERVER_HOST = "https://k03c8j1o5a.execute-api.ap-northeast-2.amazonaws.com/v1";
    private static final String API_BASE_PATH = "/programmers";
    private static final String HttpMethodType = "GET";
    private static final String TIMETABLE_PATH = "/timetable";
    private static final String TIMETABLE_QUERY_PATH = "/timetable?user_key=token_key_grepp";
    private static final String LECTURE_PATH = "/lectures";
    private static final String LECTURE_QUERY_PATH = "/lectures?";
    private static JSONParser jsonParser = new JSONParser();

    public ArrayList<LectureItem> get_all_lectures() throws JSONException, ParseException {
        return LectureItem.get_listified(request(LECTURE_PATH));
    }

    public LectureItem get_lecture_by_code(String _code) throws JSONException, ParseException{
        return LectureItem.get_item(request(LECTURE_QUERY_PATH + "code=" + _code));
    }

    private ArrayList<String> parse_code(String _jsonDataString) throws JSONException, ParseException {
        ArrayList<String> _retDataList = new ArrayList<>();

        JSONObject items = (JSONObject) jsonParser.parse(_jsonDataString);
        final JSONArray _jsonList = (JSONArray) items.get("Items");

        for(int idx = 0; idx < _jsonList.size(); idx++){
            JSONObject item = (JSONObject) _jsonList.get(idx);
            String code = item.get("lecture_code").toString();
            _retDataList.add(code);
        }

        return _retDataList;
    }

    public ArrayList<LectureItem> get_user_timeline() throws JSONException, ParseException {
        ArrayList<LectureItem> items = new ArrayList<>();

        ArrayList<String> codes = parse_code(request(TIMETABLE_QUERY_PATH));
        for(String code:codes){
            items.add(get_lecture_by_code(code));
        }

        return items;
    }

    private String request(final String apiPath){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + apiPath;
        System.out.println(requestUri);
        HttpURLConnection CONN;
        InputStreamReader INPUT_READER = null;
        BufferedReader BUF_READER = null;

        try{
            final URL url= new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType);
            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");
            CONN.setRequestProperty("charset", "utf-8");
            CONN.setAllowUserInteraction(false);

            final int responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType, url));
            System.out.println("Response Code: " + responseCode);
            if( responseCode == 200)
                /* Success */
                INPUT_READER = new InputStreamReader( CONN.getInputStream());
            else
                INPUT_READER = new InputStreamReader( CONN.getErrorStream());

            /* Read string data from input stream */
            BUF_READER = new BufferedReader( INPUT_READER);
            final StringBuffer BUFFER = new StringBuffer();
            String dataLine;
            while( (dataLine = BUF_READER.readLine()) != null) {
                BUFFER.append(dataLine);
            }
            System.out.println(BUFFER.toString());
            return BUFFER.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
