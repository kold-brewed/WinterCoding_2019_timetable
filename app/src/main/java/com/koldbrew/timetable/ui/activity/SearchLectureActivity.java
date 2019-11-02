package com.koldbrew.timetable.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.koldbrew.timetable.ConnectionManager;
import com.koldbrew.timetable.R;
import com.koldbrew.timetable.data.LectureItem;
import com.koldbrew.timetable.ui.view.LectureItemView;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class SearchLectureActivity extends AppCompatActivity {
    ArrayList<LectureItem> lectureItems;
    ListView listView;
    EditText searchTab;
    LectureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lecture_list);

        lectureItems = new ArrayList<>();

        /* 서버에서 값 요청 */
        Thread _get = new Thread(){
            public void run(){
                ConnectionManager cm = new ConnectionManager();
                try {
                    lectureItems = cm.get_all_lectures();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        _get.start();
        try {
            _get.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* UI 작업들 */
        listView = findViewById(R.id.listView);
        adapter = new LectureAdapter();
        Log.d("Search", "parsing size=" + lectureItems.size());
        for(int i = 0; i < lectureItems.size(); i++) {
            adapter.addItem(lectureItems.get(i));
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LectureItem item = (LectureItem) adapter.getItem(position);
                /* 강의 상세 화면으로 연결 */
                Intent intent = new Intent(SearchLectureActivity.this, SearchDetailActivity.class);
                intent.putExtra("option", "lecture");
                intent.putExtra("lectureInfo", item);
                startActivityForResult(intent, 0);
            }
        });

        searchTab = findViewById(R.id.editText);
        searchTab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();
                if(filterText.length() > 0){
                    listView.setFilterText(filterText);
                }
                else{
                    listView.clearTextFilter();
                }
                ((LectureAdapter) listView.getAdapter()).getFilter().filter(filterText);
            }
        });
    }


    class LectureAdapter extends BaseAdapter implements Filterable {
        ArrayList<LectureItem> items = new ArrayList<LectureItem>();
        ArrayList<LectureItem> filteredItems = items;
        ListFilter listFilter;

        public void addItem(LectureItem item){
            items.add(item);
        }

        @Override
        public int getCount() {
            return filteredItems.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredItems.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LectureItemView view = new LectureItemView(getApplicationContext());
            LectureItem item = filteredItems.get(position);

            view.setName(item.getName());
            view.setDate(item.dateToString());
            view.setCode(item.getCode());
            view.setProfessor(item.getProfessor());
            view.setlocation(item.getlocation());

            return view;
        }

        @Override
        public Filter getFilter() {
            if(listFilter == null){
                listFilter = new ListFilter();
            }
            return listFilter;
        }

        private class ListFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if(charSequence == null || charSequence.length() == 0){
                    results.values = items;
                    results.count = items.size();
                }
                else{
                    ArrayList<LectureItem> itemList = new ArrayList<>();
                    for(LectureItem item: items){

                        if(item.getName().toUpperCase().contains(charSequence.toString().toUpperCase())){
                            Log.d("search", item.getName() +" vs " + charSequence);
                            itemList.add(item);
                        }
                    }
                    results.values = itemList;
                    results.count = itemList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (ArrayList<LectureItem>) results.values;

                if(results.count > 0){
                    notifyDataSetChanged();
                }
                else{
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                LectureItem item = (LectureItem) data.getSerializableExtra("selectLecture");
                Intent ret_intent = new Intent();
                ret_intent.putExtra("selectLecture", item);
                setResult(RESULT_OK, ret_intent);
                finish();
            }
        }

    }
}
