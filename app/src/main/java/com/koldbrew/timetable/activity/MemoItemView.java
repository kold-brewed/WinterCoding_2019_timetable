package com.koldbrew.timetable.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.koldbrew.timetable.R;

public class MemoItemView extends ConstraintLayout {
    TextView title;
    ImageButton trash;

    public MemoItemView(Context context) {
        super(context);
        init(context);
    }

    public MemoItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_memo_item, this, true);

        title = findViewById(R.id.title_memo);
        trash = findViewById(R.id.trash_button);
    }

    public void setTitle(String _title){
        title.setText(_title);
    }
}
