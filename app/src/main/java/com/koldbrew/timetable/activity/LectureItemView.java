package com.koldbrew.timetable.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koldbrew.timetable.R;

public class LectureItemView extends LinearLayout {
    TextView name;
    TextView date;
    TextView code;
    TextView professor;
    TextView location;

    public LectureItemView(Context context){
        super(context);
        init(context);
    }

    public LectureItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_lecture_item, this, true);

        name = (TextView) findViewById(R.id.textView_name);
        date = (TextView) findViewById(R.id.textView_date_contents);
        code = (TextView) findViewById(R.id.textView_code_contents);
        professor = (TextView) findViewById(R.id.textView_professor_contents);
        location = (TextView) findViewById(R.id.textView_location_contents);
    }

    public void setName(String _name){
        name.setText(_name);
    }

    public void setDate(String _date){
        date.setText(_date);
    }

    public void setCode(String _code){
        code.setText(_code);
    }

    public void setProfessor(String _professor){
        professor.setText(_professor);
    }

    public void setlocation(String _location){
        location.setText(_location);
    }

}
