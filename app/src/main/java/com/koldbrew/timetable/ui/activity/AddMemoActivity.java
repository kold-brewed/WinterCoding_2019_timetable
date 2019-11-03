package com.koldbrew.timetable.ui.activity;

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
    EditText editTitle;
    EditText editContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_memo);

        editTitle = findViewById(R.id.editText_memo_title);
        editContent = findViewById(R.id.editText_memo_contents);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");

    }

    public void mOnClick(View view) {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        if (title.length() == 0 || content.length() == 0){
            Toast.makeText(this, "메모 내용을 입력하세요", Toast.LENGTH_LONG).show();
        }
        else{
            Memo memo = new Memo(
                    code,
                    "STUDY",
                    title, content,
                    new SimpleDateFormat ( "yyyy-MM-dd").format(new Date()));

            Intent intent = new Intent();
            intent.putExtra("memo", memo);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
