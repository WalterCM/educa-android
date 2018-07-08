package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Classroom extends AppCompatActivity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        final String access = mainData.getString("access");
        final int classroom_id = mainData.getInt("classroom_id");

        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.arizona_dessert, "Horario", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING SCHEDULE");
            }
        }));

        list.add(new Card("drawable://" + R.drawable.bamf1, "Asistencia", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING ATTENDANCE");
                Intent i = new Intent(Classroom.this, Attendance.class);
                i.putExtra("access", access);
                i.putExtra("classroom_id", classroom_id);
                startActivity(i);
            }
        }));

        list.add(new Card("drawable://" + R.drawable.bamf1, "Cursos", new Runnable(){
            @Override
            public void run() {
                Log.e("RUNNABLE: ", "CALLING COURSES");
                Intent i = new Intent(Classroom.this, Courses.class);
                i.putExtra("access", access);
                i.putExtra("classroom_id", classroom_id);
                startActivity(i);
            }
        }));

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }
}
