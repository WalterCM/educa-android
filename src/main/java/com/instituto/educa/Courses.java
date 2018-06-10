package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

//import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Courses extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        final String access = mainData.getString("access");
        JSONArray courses = null;
        try {
            courses = new JSONArray(mainData.getString("courses"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mListView = findViewById(R.id.listView);

        ArrayList<Card> list = new ArrayList<>();


        for (int i = 0; i < courses.length(); i++)
        {
            JSONObject course = null;
            try {
                course = courses.getJSONObject(i);
                list.add(new Card("drawable://" + R.drawable.bamf1, course.getString("title"), new Runnable(){
                    @Override
                    public void run() {
                        Intent i = new Intent(Courses.this, Course.class);
                        //i.putExtra("access", access);
                        startActivity(i);
                    }
                }));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        //ImageLoader.getInstance().destroy();
    }
}
