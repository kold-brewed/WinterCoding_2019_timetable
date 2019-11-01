package com.koldbrew.timetable.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.koldbrew.timetable.R;
import com.koldbrew.timetable.data.LectureItem;
import com.koldbrew.timetable.data.Memo;

import java.util.ArrayList;

public class SearchDetailActivity extends AppCompatActivity {
    private LectureItem item;
    private ArrayList<Memo> memos = new ArrayList<>();
    private MemoAdapter memoAdapter;
    Button button;
    TextView name;
    TextView date;
    TextView code;
    TextView professor;
    TextView location;
    TextView details;
    TextView label_memo;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lecture_details);

        Intent intent = getIntent();
        item = (LectureItem) intent.getSerializableExtra("lectureInfo");
        String opt = intent.getStringExtra("option");

        button = findViewById(R.id.button_register_memo);
        if(opt.contains("lecture")){
            button.setText("강의 추가");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(SearchDetailActivity.this, "시간표에다가 이제 추가시켜야 함.", Toast.LENGTH_SHORT).show();
                    mOnClickAddLecture(v);
                }
            });
        }
        else {
            button.setText("메모 추가");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(SearchDetailActivity.this, "메모 추가시켜야 함.", Toast.LENGTH_SHORT).show();
                    mOnClickAddMemo(v);
                }
            });
        }

        name = findViewById(R.id.title_lecture_details);
        name.setText(item.getName());
        date = findViewById(R.id.lecture_time);
        date.setText(item.dateToString());
        code = findViewById(R.id.lecture_code);
        code.setText(item.getCode());

        professor = findViewById(R.id.lecture_professor);
        professor.setText(item.getProfessor());

        location = findViewById(R.id.lecture_location);
        location.setText(item.getlocation());

        details = findViewById(R.id.textView_lecture_details);
        details.setText("본 강의에서는 JSP를 이용한 웹 기반 프로그래밍 기초 및 응용기술에 대해 학습합니다." +
                " 특히 실습 위주의 수업으로 프로그래밍 스킬 향상 및 실 무 능력을 갖출 수 있도록 합니다");

        label_memo = findViewById(R.id.textView_register_memo);

        listView = findViewById(R.id.memo_list);
        memoAdapter = new MemoAdapter();
        Log.d("Search", "parsing size=" + memos.size());
        for(int i = 0; i < memos.size(); i++) {
            memoAdapter.addItem(memos.get(i));
        }
        listView.setAdapter(memoAdapter);

        if(memos.size() == 0){
            label_memo.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
    }

    public void mOnClickAddMemo(View v){
        Intent intent = new Intent(SearchDetailActivity.this, AddMemoActivity.class);
        intent.putExtra("code", item.getCode());
        startActivityForResult(intent, 0);
    }

    public void mOnClickAddLecture(View v){
        Intent intent = new Intent();
        intent.putExtra("selectLecture", item);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Memo memo = (Memo) data.getSerializableExtra("memo");
            memos.add(memo);
            memoAdapter.addItem(memo);
            label_memo.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    class MemoAdapter extends BaseAdapter {
        ArrayList<Memo> items = new ArrayList<Memo>();

        public void addItem(Memo item){
            items.add(item);
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            MemoItemView view = new MemoItemView(getApplicationContext());
            Memo item = items.get(position);

            view.setTitle(item.getTitle());

            return view;
        }

    }
}
