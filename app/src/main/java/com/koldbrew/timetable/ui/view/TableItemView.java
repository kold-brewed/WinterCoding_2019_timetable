package com.koldbrew.timetable.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koldbrew.timetable.R;
import com.koldbrew.timetable.ui.activity.MainActivity;


public class TableItemView extends LinearLayout {
    TextView title;
    TextView location;
    ListView listView;
    int bgcolor[] = new int[5];
    int txtcolor[] = new int[5];
    int colorIdx;

    public TableItemView(Context context) {
        super(context);
        init(context);
    }

    public TableItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_table_item, this, true);

        title = findViewById(R.id.customView_lecture_title);
        location = findViewById(R.id.customView_location);
        listView = findViewById(R.id.customView_memo_list);

        bgcolor[0] = getResources().getColor(R.color.light_pink);
        bgcolor[1] = getResources().getColor(R.color.light_purple);
        bgcolor[2] = getResources().getColor(R.color.light_yellow);
        bgcolor[3] = getResources().getColor(R.color.light_orange);
        bgcolor[4] = getResources().getColor(R.color.light_green);

        txtcolor[0] = getResources().getColor(R.color.dark_pink);
        txtcolor[1] = getResources().getColor(R.color.dark_purple);
        txtcolor[2] = getResources().getColor(R.color.dark_yellow);
        txtcolor[3] = getResources().getColor(R.color.dark_orange);
        txtcolor[4] = getResources().getColor(R.color.dark_green);
    }

    public void setTitle(String _title){
        title.setText(_title);
    }

    public void setLocation(String _location){
        location.setText(_location);
    }

    public void setColor(int _colorIdx){
        colorIdx = _colorIdx;
        this.setBackgroundColor(bgcolor[_colorIdx]);
        title.setTextColor(txtcolor[_colorIdx]);
        title.setTypeface(null, Typeface.BOLD);
        location.setTextColor(txtcolor[_colorIdx]);
    }

    public int getColor(){
        return colorIdx;
    };

    public void setMarginHeight(RelativeLayout.LayoutParams params, int _margin, int _height){
        params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _height, getResources().getDisplayMetrics());
        params.topMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _margin, getResources().getDisplayMetrics());
        setLayoutParams(params);
    }

    public void addMemo(String _memoTitle){
        /* 리스트뷰에 메모제목 추가 */

    }

    public void setAdapter(MainActivity.SmallMemoAdapter adapter){
        listView.setAdapter(adapter);
    }

    public MainActivity.SmallMemoAdapter getAdapter(){
        return (MainActivity.SmallMemoAdapter) listView.getAdapter();
    }
}
