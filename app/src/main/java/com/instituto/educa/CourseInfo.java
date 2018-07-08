package com.instituto.educa;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseInfo extends AppCompatActivity {
    private TextView textView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        textView = findViewById(R.id.textView);
        JSONObject course = null;
        JSONObject professor = null;
        try {
            course = new JSONObject(mainData.getString("course"));
            professor = new JSONObject(mainData.getString("professor"));

            String info = "<b>Nombre del curso:</b> " + course.getString("title") + "<br>";
            info += "<b>Descripcion:</b> " + course.getString("overview") + "<br>";
            info += "<b>Profesor:</b> " + professor.getString("first_name") + " ";
            info += professor.getString("last_name");
            info += " (" + professor.getString("email") + ")" + "<br>";
            textView.setText(Html.fromHtml(info));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
