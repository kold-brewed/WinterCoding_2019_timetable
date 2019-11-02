package com.koldbrew.timetable;

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

    public ArrayList<LectureItem> get_all_lectures() throws JSONException, ParseException {
        return LectureItem.get_listified(request_get(LECTURE_PATH));
    }

    public LectureItem get_lecture_by_code(String _code) throws JSONException, ParseException{
        return LectureItem.get_item(request_get(LECTURE_QUERY_PATH + "code=" + _code));
    }

    public ArrayList<String> parse_code(String _jsonDataString) throws JSONException, ParseException {
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

    public ArrayList<Memo> get_memos_by_code(String _code) throws JSONException, ParseException {
        return Memo.get_listified(request_get(MEMO_QUERY_PATH + _code));
    }

    public ArrayList<LectureItem> get_user_timeline() throws JSONException, ParseException {
        ArrayList<LectureItem> items = new ArrayList<>();

        ArrayList<String> codes = parse_code(request_get(TIMETABLE_QUERY_PATH));
        for(String code:codes){
            LectureItem _element = new LectureItem(get_lecture_by_code(code));
            _element.setMemos(get_memos_by_code(code));
            items.add(_element);
        }

        return items;
    }

    private String request_get(final String apiPath){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + apiPath;
        System.out.println(requestUri);
        HttpURLConnection CONN;
        InputStreamReader INPUT_READER = null;
        BufferedReader BUF_READER = null;

        try{
            final URL url= new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType_GET);
            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");
            CONN.setRequestProperty("charset", "utf-8");
            CONN.setAllowUserInteraction(false);

            final int responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType_GET, url));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int request_post_memo(Memo memo){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + MEMO_PATH;
        HttpURLConnection CONN = null;
        int responseCode = -1;

        try {
            final URL url = new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType_POST);

            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");

            CONN.setDoOutput(true);
            CONN.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(CONN.getOutputStream());
            writeStream(out, memo, HttpMethodType_POST);

            // check results
            responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType_POST, url));
            System.out.println("Response Code: " + responseCode + "(" + CONN.getResponseMessage() + ")");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CONN.disconnect();
        }
        return responseCode;
    }

    public void request_post_timeline(LectureItem lecture){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + TIMETABLE_PATH;
        System.out.println(requestUri);
        HttpURLConnection CONN = null;

        try {
            final URL url = new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType_POST);

            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");

            CONN.setDoOutput(true);
            CONN.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(CONN.getOutputStream());
            writeStream(out, lecture);

            // check results
            final int responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType_POST, url));
            System.out.println("Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(CONN.getInputStream());
            System.out.println(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CONN.disconnect();
        }
    }

    public void request_delete_timeline(LectureItem lecture){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + TIMETABLE_PATH;
        System.out.println(requestUri);
        HttpURLConnection CONN = null;

        try {
            final URL url = new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType_DELETE);

            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");

            CONN.setDoOutput(true);
            CONN.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(CONN.getOutputStream());
            writeStream(out, lecture);

            // check results
            final int responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType_DELETE, url));
            System.out.println("Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(CONN.getInputStream());
            System.out.println(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CONN.disconnect();
        }
    }

    public void request_delete_memo(Memo memo){
        String requestUri = API_SERVER_HOST + API_BASE_PATH + MEMO_PATH;
        System.out.println(requestUri);
        HttpURLConnection CONN = null;

        try {
            final URL url = new URL(requestUri);
            CONN = (HttpURLConnection) url.openConnection();

            CONN.setRequestMethod( HttpMethodType_DELETE);

            CONN.setRequestProperty("x-api-key", API_KEY);
            CONN.setRequestProperty("Content-Type", "application/json");

            CONN.setDoOutput(true);
            CONN.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(CONN.getOutputStream());
            writeStream(out, memo, HttpMethodType_DELETE);

            // check results
            final int responseCode = CONN.getResponseCode();
            System.out.println(String.format("Request[%s] to URI: %s", HttpMethodType_DELETE, url));
            System.out.println("Response Code: " + responseCode);

            InputStream in = new BufferedInputStream(CONN.getInputStream());
            System.out.println(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CONN.disconnect();
        }
    }

    private void writeStream(OutputStream out, Memo memo, String _httpMethodType) throws IOException {
        JSONObject _postData = new JSONObject();
        _postData.put("code", memo.getLecture_code());
        _postData.put("user_key", TOKEN);
        _postData.put("type", memo.getType());
        if(_httpMethodType.equals(HttpMethodType_POST)){
            _postData.put("title", memo.getTitle());
            _postData.put("description", memo.getDescription());
            _postData.put("date", memo.getDate());
        }
        String output = _postData.toString();
        System.out.println("post output= " + output);

        out.write(output.getBytes());
        out.flush();
    }

    private void writeStream(OutputStream out, LectureItem lecture) throws IOException {
        JSONObject _postData = new JSONObject();
        _postData.put("user_key", TOKEN);
        _postData.put("code", lecture.getCode());
        String output = _postData.toString();
        System.out.println("post output= " + output);

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
