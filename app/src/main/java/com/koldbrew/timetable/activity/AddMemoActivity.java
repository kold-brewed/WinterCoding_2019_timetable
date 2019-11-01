package com.koldbrew.timetable.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.koldbrew.timetable.R;
import com.koldbrew.timetable.data.Memo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMemoActivity extends AppCompatActivity {
    private String code;
    EditText edit_title;
    EditText edit_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_memo);

        edit_title = findViewById(R.id.editText_memo_title);
        edit_content = findViewById(R.id.editText_memo_contents);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");

    }

    public void mOnClick(View view) {
        String _title = edit_title.getText().toString();
        String _content = edit_content.getText().toString();
        if (_title.length() == 0 || _content.length() == 0){
            Toast.makeText(this, "메모 내용을 입력하세요", Toast.LENGTH_LONG).show();
        }
        else{
            Memo memo = new Memo(
                    code,
                    "default",
                    _title, _content,
                    new SimpleDateFormat ( "yyyy-MM-dd").format(new Date()));

            Intent intent = new Intent();
            intent.putExtra("memo", memo);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
