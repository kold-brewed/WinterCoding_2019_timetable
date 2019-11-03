package com.koldbrew.timetable.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.koldbrew.timetable.ConnectionManager;
import com.koldbrew.timetable.R;
import com.koldbrew.timetable.data.LectureItem;
import com.koldbrew.timetable.data.Memo;
import com.koldbrew.timetable.ui.view.MemoItemView;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class SearchDetailActivity extends AppCompatActivity {
    private LectureItem item;
    private ArrayList<Memo> memos = new ArrayList<>();
    private MemoAdapter memoAdapter;
    private ConnectionManager cm = new ConnectionManager();
    private int viewId;
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
        viewId = (int) intent.getIntExtra("viewCode", -1);
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

        memos = item.getMemos();

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
        final int[] responseCode = new int[1];

        if(resultCode == RESULT_OK){
            final Memo memo = (Memo) data.getSerializableExtra("memo");

            /* 서버로 메모 POST 요청 전송 */
            Thread _postTask = new Thread(new Thread() {
                public void run() {
                    responseCode[0] = cm.request_post_memo(memo);
                }
            });
            _postTask.start();
            try {
                _postTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(responseCode[0] == 200){
                memos.add(memo);
                memoAdapter.addItem(memo);
                label_memo.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Memo API POST 요청.", Toast.LENGTH_LONG).show();
            }
            else if(responseCode[0] == 409) {
                Toast.makeText(this, "메모 Type이 이미 존재합니다.", Toast.LENGTH_LONG).show();
            }
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
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            MemoItemView view = new MemoItemView(getApplicationContext());
            final Memo item = items.get(position);
            view.setTitle(item.getTitle());

            ImageButton delBtn = view.findViewById(R.id.trash_button);
            delBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    /* 리스트 삭제 후 갱신 */
                    items.remove(position);
                    memos.remove(position);
                    notifyDataSetChanged();
                    /* 서버로 DELETE 요청 전송 */
                    new Thread( new Thread(){
                        public void run(){
                            cm = new ConnectionManager();
                            cm.request_delete_memo(item);
                        }
                    }).start();
                    Toast.makeText(SearchDetailActivity.this, "Memo API DELETE 요청.", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* intent로 변경된 Memo리스트 전송 */
        Intent intent = new Intent();
        intent.putExtra("viewCode", viewId);
        intent.putExtra("memos", memos);
        setResult(RESULT_OK, intent);
        finish();
    }
}
