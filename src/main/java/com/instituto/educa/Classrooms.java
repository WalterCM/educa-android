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

public class Classrooms extends AppCompatActivity {
    private ListView mListView;
    private String access = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Bundle tokenData = getIntent().getExtras();
        if (tokenData == null)
        {
            // TODO: Do something here lmao
        }

        access = tokenData.getString("access");

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                APIContract.URL + "mine/",
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
            final JSONObject classroom = courses.getJSONObject(i);
            final int classroom_id = classroom.getInt("id");
            String room = classroom.getString("room");
            list.add(new Card("drawable://" + R.drawable.bamf1, room, new Runnable(){
                @Override
                public void run() {
                    Intent i = new Intent(Classrooms.this, Classroom.class);
                    i.putExtra("access", access);
                    i.putExtra("classroom_id", classroom_id);
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
