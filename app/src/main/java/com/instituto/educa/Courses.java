package com.instituto.educa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//import com.nostra13.universalimageloader.core.ImageLoader;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Courses extends AppCompatActivity {
    private ListView mListView;
    private String access = "";
    private int classroom_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        access = mainData.getString("access");
        classroom_id = mainData.getInt("classroom_id");

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                APIContract.URL + "classrooms/" + classroom_id + "/courses/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        try {
                            createCards(new JSONArray(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST ERROR RESPONSE: ", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Bearer " + access);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void createCards(JSONArray courses) throws JSONException {
        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        if (courses.length() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("No esta matriculado en ningun curso");
            emptyMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LinearLayout layout = findViewById(R.id.linearLayout);
            layout.addView(emptyMessage);
        }

        for (int i = 0; i < courses.length(); i++)
        {
            final JSONObject obj = courses.getJSONObject(i);
            final JSONObject course = obj.getJSONObject("course");
            final JSONObject professor = obj.getJSONObject("professor");
            String title = course.getString("title");
            list.add(new Card(APIEduca.getUrl(course.getString("image")), title, new Runnable(){
                @Override
                public void run() {
                    Intent i = new Intent(Courses.this, Course.class);
                    i.putExtra("access", access);
                    i.putExtra("classroom_id", classroom_id);
                    i.putExtra("course", course.toString());
                    i.putExtra("professor", professor.toString());
                    startActivity(i);
                }
            }));
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        //ImageLoader.getInstance().destroy();
    }
}
