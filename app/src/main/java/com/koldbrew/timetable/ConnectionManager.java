package com.koldbrew.timetable;

import android.util.Log;

import com.koldbrew.timetable.data.LectureItem;
import com.koldbrew.timetable.data.Memo;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ConnectionManager {
    private static final String API_KEY = "QJuHAX8evMY24jvpHfHQ4pHGetlk5vn8FJbk70O6";
    private static final String TOKEN = "57f1e7abda340c857bdde39dabf177dc";
    private static final String API_SERVER_HOST = "https://k03c8j1o5a.execute-api.ap-northeast-2.amazonaws.com/v1";
    private static final String API_BASE_PATH = "/programmers";
    private static final String HttpMethodType_GET = "GET";
    private static final String HttpMethodType_POST = "POST";
    private static final String HttpMethodType_DELETE = "DELETE";
    private static final String TIMETABLE_PATH = "/timetable";
    private static final String TIMETABLE_QUERY_PATH = "/timetable?user_key="+TOKEN;
    private static final String LECTURE_PATH = "/lectures";
    private static final String LECTURE_QUERY_PATH = "/lectures?";
    private static final String MEMO_PATH = "/memo";
    private static final String MEMO_QUERY_PATH = "/memo?user_key="+TOKEN+"&code=";

    private static JSONParser jsonParser = new JSONParser();

    public ArrayList<LectureItem> getAllLectures() throws ParseException {
        return LectureItem.getListified(requestGet(LECTURE_PATH));
    }

    public LectureItem getLectureByCode(String _code) throws ParseException{
        return LectureItem.getItem(requestGet(LECTURE_QUERY_PATH + "code=" + _code));
    }

    private ArrayList<String> parseCode(String jsonDataString) throws ParseException {
        ArrayList<String> retDataList = new ArrayList<>();

        JSONObject items = (JSONObject) jsonParser.parse(jsonDataString);
        final JSONArray jsonList = (JSONArray) items.get("Items");

        for(int idx = 0; idx < jsonList.size(); idx++){
            JSONObject item = (JSONObject) jsonList.get(idx);
            String code = item.get("lecture_code").toString();
            retDataList.add(code);
        }

        return retDataList;
    }

    public ArrayList<Memo> getMemosByCode(String code) throws JSONException, ParseException {
        return Memo.getListified(requestGet(MEMO_QUERY_PATH + code));
    }

    public ArrayList<LectureItem> getUserTimeline() throws JSONException, ParseException {
        ArrayList<LectureItem> items = new ArrayList<>();

        ArrayList<String> codes = parseCode(requestGet(TIMETABLE_QUERY_PATH));
        for(String code:codes){
            LectureItem _element = new LectureItem(getLectureByCode(code));
            _element.setMemos(getMemosByCode(code));
            items.add(_element);
        }

        return items;
    }

    private String requestGet(final String apiPath){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + apiPath;
        Log.i("Connection", "request uri: " + requestUri);
        HttpURLConnection conn = null;
        InputStreamReader INPUT_READER = null;
        BufferedReader BUF_READER = null;

        try{
            final URL url= new URL(requestUri);
            conn = makeConnection(HttpMethodType_GET, url);
            conn.setRequestProperty("charset", "utf-8");
            conn.setAllowUserInteraction(false);

            final int responseCode = conn.getResponseCode();
            Log.i("Connection", String.format("Request[%s] to URI: %s", HttpMethodType_GET, url));
            Log.i("Connection", "Response Code: " + responseCode);
            if( responseCode == 200)
                /* Success */
                INPUT_READER = new InputStreamReader( conn.getInputStream());
            else
                INPUT_READER = new InputStreamReader( conn.getErrorStream());

            /* Read string data from input stream */
            BUF_READER = new BufferedReader( INPUT_READER);
            final StringBuffer BUFFER = new StringBuffer();
            String dataLine;
            while( (dataLine = BUF_READER.readLine()) != null) {
                BUFFER.append(dataLine);
            }
            Log.i("Connection", BUFFER.toString());
            return BUFFER.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            conn.disconnect();
        }
        return null;
    }

    public int requestPostMemo(Memo memo){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + MEMO_PATH;
        HttpURLConnection conn = null;
        int responseCode = -1;

        try {
            final URL url = new URL(requestUri);
            conn = makeConnection(HttpMethodType_POST, url);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            writeMemo(out, memo, HttpMethodType_POST);

            // check results
            responseCode = conn.getResponseCode();
            Log.i("Connection", String.format("Request[%s] to URI: %s", HttpMethodType_POST, url));
            Log.i("Connection", "Response Code: " + responseCode + "(" + conn.getResponseMessage() + ")");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return responseCode;
    }

    public void requestPostTimeline(LectureItem lecture){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + TIMETABLE_PATH;
        Log.i("Connection", requestUri);
        HttpURLConnection conn = null;

        try {
            final URL url = new URL(requestUri);
            conn = makeConnection(HttpMethodType_POST, url);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            writeLecture(out, lecture);

            // check results
            final int responseCode = conn.getResponseCode();
            Log.i("Connection", String.format("Request[%s] to URI: %s", HttpMethodType_POST, url));
            Log.i("Connection", "Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.i("Connection", readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public void requestDeleteTimeline(LectureItem lecture){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + TIMETABLE_PATH;
        Log.i("Connection", requestUri);
        HttpURLConnection conn = null;

        try {
            final URL url = new URL(requestUri);
            conn = makeConnection(HttpMethodType_DELETE, url);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            writeLecture(out, lecture);

            // check results
            final int responseCode = conn.getResponseCode();
            Log.i("Connection", String.format("Request[%s] to URI: %s", HttpMethodType_DELETE, url));
            Log.i("Connection", "Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.i("Connection", readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public void requestDeleteMemo(Memo memo){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + MEMO_PATH;
        Log.i("Connection", requestUri);
        HttpURLConnection conn = null;

        try {
            final URL url = new URL(requestUri);
            conn = makeConnection(HttpMethodType_DELETE, url);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            writeMemo(out, memo, HttpMethodType_DELETE);

            // check results
            final int responseCode = conn.getResponseCode();
            Log.i("Connection", String.format("Request[%s] to URI: %s", HttpMethodType_DELETE, url));
            Log.i("Connection", "Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.i("Connection", readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    private void writeMemo(OutputStream out, Memo memo, String httpMethodType) throws IOException {
        JSONObject postData = new JSONObject();
        postData.put("code", memo.getlectureCode());
        postData.put("user_key", TOKEN);
        postData.put("type", memo.getType());
        if(httpMethodType.equals(HttpMethodType_POST)){
            postData.put("title", memo.getTitle());
            postData.put("description", memo.getDescription());
            postData.put("date", memo.getDate());
        }
        String output = postData.toString();

        out.write(output.getBytes());
        out.flush();
    }

    private HttpURLConnection makeConnection(String type, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(type);
        conn.setRequestProperty("x-api-key", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }

    private void writeLecture(OutputStream out, LectureItem lecture) throws IOException {
        JSONObject postData = new JSONObject();
        postData.put("user_key", TOKEN);
        postData.put("code", lecture.getCode());
        String output = postData.toString();

        out.write(output.getBytes());
        out.flush();
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
