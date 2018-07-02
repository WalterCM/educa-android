package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Course extends AppCompatActivity {
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
        JSONObject course = null;
        JSONObject professor = null;
        try {
            course = new JSONObject(mainData.getString("course"));
            professor = new JSONObject(mainData.getString("professor"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject c = course;
        final JSONObject p = professor;
        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.bamf1, "Informacion del curso", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING COURSE INFO");
                Intent i = new Intent(Course.this, CourseInfo.class);
                //i.putExtra("access", access);
                if (c != null) {
                    i.putExtra("course", c.toString());
                    i.putExtra("professor", p.toString());
                }
                startActivity(i);
            }
        }));

        list.add(new Card("drawable://" + R.drawable.bamf1, "Notas", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING COURSE GRADES");
                Intent i = new Intent(Course.this, Grades.class);
                i.putExtra("access", access);
                i.putExtra("classroom_id", classroom_id);
                if (c != null) {
                    try {
                        i.putExtra("course_id", c.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(i);
            }
        }));

        list.add(new Card("drawable://" + R.drawable.bamf1, "Notificaciones", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING NOTIFICATIONS");
                Intent i = new Intent(Course.this, Notifications.class);
                i.putExtra("access", access);
                i.putExtra("classroom_id", classroom_id);
                if (c != null) {
                    try {
                        i.putExtra("course_id", c.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(i);
            }
        }));

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }
}
