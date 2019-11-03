package com.koldbrew.timetable.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.koldbrew.timetable.R;

public class SmallMemoItemView extends LinearLayout {
    TextView title;
    int txtcolor[] = new int[5];

    public SmallMemoItemView(Context context) {
        super(context);
        init(context);
    }

    public SmallMemoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_small_memo_title, this, true);

        title = findViewById(R.id.textView_small_memo_title);

        txtcolor[0] = getResources().getColor(R.color.dark_pink);
        txtcolor[1] = getResources().getColor(R.color.dark_purple);
        txtcolor[2] = getResources().getColor(R.color.dark_yellow);
        txtcolor[3] = getResources().getColor(R.color.dark_orange);
        txtcolor[4] = getResources().getColor(R.color.dark_green);
    }

    public void setColor(int _colorIdx){
        title.setTextColor(txtcolor[_colorIdx]);
    }

    public void setTitle(String _title){
        title.setText(_title);
    }
}
