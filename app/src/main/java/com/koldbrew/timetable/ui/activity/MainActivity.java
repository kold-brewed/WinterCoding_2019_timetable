package com.koldbrew.timetable.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.koldbrew.timetable.ConnectionManager;
import com.koldbrew.timetable.R;
import com.koldbrew.timetable.data.LectureItem;
import com.koldbrew.timetable.data.Memo;
import com.koldbrew.timetable.ui.view.SmallMemoItemView;
import com.koldbrew.timetable.ui.view.TableItemView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final int ADD_NEW_LECTURE = 0;
    private final int ADD_MEMO = 1;

    private TextView[] dayOfWeek = new TextView[5];
    private TextView[] date = new TextView[5];
    private TextView textView_today;
    private TextView year_month;
    private Button prev_week;
    private Button next_week;
    private int weekOfYear;
    private int colorOfToday;

    private int[] bgColors = new int[5];
    private int[] textColors = new int[5];
    private RelativeLayout monLayout;
    private RelativeLayout tueLayout;
    private RelativeLayout wedLayout;
    private RelativeLayout thuLayout;
    private RelativeLayout friLayout;

    private ArrayList<LectureItem> lectures;
    private HashMap<Integer, LectureItem> tableItemHashMap = new HashMap<>();
    private HashMap<Integer, TableItemView> tableViewHashMap = new HashMap<>();
    private ConnectionManager cm = new ConnectionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 배경 색상 획득 */
        colorOfToday = getResources().getColor(R.color.colorAccent);
        bgColors[0] = getResources().getColor(R.color.light_green, getTheme());
        bgColors[1] = getResources().getColor(R.color.light_orange, getTheme());
        bgColors[2] = getResources().getColor(R.color.light_pink, getTheme());
        bgColors[3] = getResources().getColor(R.color.light_purple, getTheme());
        bgColors[4] = getResources().getColor(R.color.light_yellow, getTheme());

        textColors[0] = getResources().getColor(R.color.dark_green, getTheme());
        textColors[1] = getResources().getColor(R.color.dark_orange, getTheme());
        textColors[2] = getResources().getColor(R.color.dark_pink, getTheme());
        textColors[3] = getResources().getColor(R.color.dark_purple, getTheme());
        textColors[4] = getResources().getColor(R.color.dark_yellow, getTheme());

        /* 시간표 UI 연결 작업 */
        monLayout = findViewById(R.id.monColumn);
        tueLayout = findViewById(R.id.tueColumn);
        wedLayout = findViewById(R.id.wedColumn);
        thuLayout = findViewById(R.id.thuColumn);
        friLayout = findViewById(R.id.friColumn);

        /* 캘린더 UI 연결 작업 */
        dayOfWeek[0] = findViewById(R.id.day1);
        dayOfWeek[1] = findViewById(R.id.day2);
        dayOfWeek[2] = findViewById(R.id.day3);
        dayOfWeek[3] = findViewById(R.id.day4);
        dayOfWeek[4] = findViewById(R.id.day5);

        date[0] = findViewById(R.id.date1);
        date[1] = findViewById(R.id.date2);
        date[2] = findViewById(R.id.date3);
        date[3] = findViewById(R.id.date4);
        date[4] = findViewById(R.id.date5);

        next_week = findViewById(R.id.button_next);
        next_week.setText(">");
        next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekOfYear += 1;
                updateCalendar(weekOfYear);
            }
        });
        prev_week = findViewById(R.id.button_prev);
        prev_week.setText("<");
        prev_week.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                weekOfYear -= 1;
                updateCalendar(weekOfYear);
            }
        });
        year_month = findViewById(R.id.textView_year);
        textView_today = findViewById(R.id.textView_today);
        textView_today.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                weekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                updateCalendar(weekOfYear);
            }
        });
        weekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        updateCalendar(weekOfYear);



        /* 기존 유저 시간표 등록 */
        /* 서버에서 값 요청 */
        Thread _get = new Thread(){
            public void run(){
                try {
                    lectures = cm.get_user_timeline();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        _get.start();
        Toast.makeText(MainActivity.this, "TimeTable API & Memo API GET 요청.", Toast.LENGTH_LONG).show();
        try {
            _get.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("Main", "lecture size from server: " + lectures.size());
        for(LectureItem item: lectures){
            addViewNewLecture(item);
        }

    }

    public void mOnClick(View v){
        // 강의 검색 버튼 클릭
        Intent intent = new Intent(this, SearchLectureActivity.class);
        startActivityForResult(intent, ADD_NEW_LECTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case ADD_MEMO:
                    int viewId = (int) data.getIntExtra("viewCode", -1);
                    ArrayList<Memo> _changedItems = (ArrayList<Memo>) data.getSerializableExtra("memos");
                    tableItemHashMap.get(viewId).setMemos(_changedItems);
                    SmallMemoAdapter _adapter = new SmallMemoAdapter();
                    _adapter.setItems(_changedItems);
                    _adapter.setColorIdx(tableViewHashMap.get(viewId).getColor());
                    tableViewHashMap.get(viewId).setAdapter(_adapter);
                    break;
                case ADD_NEW_LECTURE:
                    final LectureItem item = (LectureItem) data.getSerializableExtra("selectLecture");
                    addViewNewLecture(item);
                    new Thread(){
                        @Override
                        public void run() {
                            cm.request_post_timeline(item);
                        }
                    }.start();
                    Toast.makeText(MainActivity.this,  "TimeTable API POST 요청.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private boolean addViewNewLecture(LectureItem item){
        // margin, height 찾기
        String[] start_time = item.getStart_time().split(":");
        int start_hour = Integer.parseInt(start_time[0]);
        int start_min = Integer.parseInt(start_time[1]);
        String[] end_time = item.getEnd_time().split(":");
        int end_hour = Integer.parseInt(end_time[0]);
        int end_min = Integer.parseInt(end_time[1]);
        int margin = 2*((start_hour-8)*60 + start_min);
        int height = 200;
        if(end_hour == start_hour){
            height = end_min-start_min;
        }
        else{
            if(end_min < start_min){
                height = (end_hour-start_hour-1)*60 + (end_min+60-start_min);
            }
            else{
                height = (end_hour-start_hour)*60 + (end_min-start_min);

            }
        }
        height *= 2;

        // 랜덤색상 적용
        Random r = new Random();
        int colorIdx = r.nextInt(5);

        SmallMemoAdapter smadapter = new SmallMemoAdapter();
        smadapter.setItems(item.getMemos());
        smadapter.setColorIdx(colorIdx);

        for(String day: item.getDayofweek()){
            TableItemView newView = new TableItemView(getApplicationContext());
            tableViewHashMap.put(newView.hashCode(), newView);
            tableItemHashMap.put(newView.hashCode(), item);
            newView.setTitle(item.getName());
            newView.setLocation(item.getlocation());
            newView.setColor(colorIdx);
            newView.setAdapter(smadapter);
            newView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(tableItemHashMap.containsKey(v.hashCode())){
                        Intent intent = new Intent(MainActivity.this, SearchDetailActivity.class);
                        intent.putExtra("option", "memo");
                        intent.putExtra("viewCode", v.hashCode());
                        intent.putExtra("lectureInfo", tableItemHashMap.get(v.hashCode()));
                        startActivityForResult(intent, ADD_MEMO);
                    }
                }
            });

            System.out.println("date of week: " + day);
            switch(day) {
                case "월":
                    monLayout.addView(newView);
                    newView.setMarginHeight((RelativeLayout.LayoutParams) newView.getLayoutParams(), margin, height);
                    break;
                case "화":
                    tueLayout.addView(newView);
                    newView.setMarginHeight((RelativeLayout.LayoutParams) newView.getLayoutParams(), margin, height);
                    break;
                case "수":
                    wedLayout.addView(newView);
                    newView.setMarginHeight((RelativeLayout.LayoutParams) newView.getLayoutParams(), margin, height);
                    break;
                case "목":
                    thuLayout.addView(newView);
                    newView.setMarginHeight((RelativeLayout.LayoutParams) newView.getLayoutParams(), margin, height);
                    break;
                case "금":
                    friLayout.addView(newView);
                    newView.setMarginHeight((RelativeLayout.LayoutParams) newView.getLayoutParams(), margin, height);
            }
        }
        return true;
    }

    private void updateCalendar(int _weekOfYear){
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, _weekOfYear);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("today: " + simpleDateFormat.format(cal.getTime()));

        int year = cal.get(Calendar.YEAR);
        int month = (cal.get(Calendar.MONTH)+1);

        /* 일주일 */
        String day = cal.get(Calendar.DAY_OF_MONTH) + "";
        date[0].setText(day);
        for(int i = 1; i < 5; i++){
            cal.add(Calendar.DAY_OF_MONTH, 1);
            day = cal.get(Calendar.DAY_OF_MONTH)+"";
            if(day.equals("1")){
                month += 1;
            }
            date[i].setText(day);
            date[i].setTypeface(null, Typeface.NORMAL);
            dayOfWeek[i].setTypeface(null, Typeface.NORMAL);

            /* 오늘은 색다르게 ~.~ */
            if(today.compareTo(cal) == 0){
                date[i].setTextColor(colorOfToday);
                date[i].setTypeface(null, Typeface.BOLD);
                dayOfWeek[i].setTextColor(colorOfToday);
                dayOfWeek[i].setTypeface(null, Typeface.BOLD);
            }
        }
        /* 연도 월 */
        String str = year + "년 " + month + "월";
        year_month.setText(str);
    }


    public class SmallMemoAdapter extends BaseAdapter {
        ArrayList<Memo> items = new ArrayList<>();
        int colorIdx;

        public void setItems(ArrayList<Memo> memos){
            items = memos;
            notifyDataSetChanged();
        }
        public void setColorIdx(int color){
            colorIdx = color;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SmallMemoItemView view = new SmallMemoItemView(getApplicationContext());
            final Memo item = items.get(position);
            view.setTitle(item.getTitle());
            view.setColor(colorIdx);
            return view;
        }
    }
}
