package com.instituto.educa;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {
    private ListView mListView;
    private final String URL = "http://192.168.1.6:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle tokenData = getIntent().getExtras();
        if (tokenData == null)
        {
            // TODO: Do something here lmao
        }

        final String access = tokenData.getString("access");
        Log.e("AUTH: ", access);

        mListView = findViewById(R.id.listView);

        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.arizona_dessert, "Arizona Dessert", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING SCHEDULE");
            }
        }));

        list.add(new Card("drawable://" + R.drawable.bamf1, "Bamf", new Runnable(){
            @Override
            public void run(){
                //Courses
                Log.e("RUNNABLE: ", "CALLING COURSES");
                StringRequest stringRequest= new StringRequest(
                        Request.Method.GET,
                        URL + "courses/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("REST RESPONSE: ", response);

                                Intent i = new Intent(Main.this, Courses.class);
                                i.putExtra("access", access);
                                i.putExtra("courses", response);
                                startActivity(i);
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

                RequestQueue requestQueue = Volley.newRequestQueue(Main.this);
                requestQueue.add(stringRequest);
            }
        }));

        list.add(new Card("drawable://" + R.drawable.colorado_mountains, "Colorado Mountains", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING ASSITANCE");
            }
        }));

        list.add(new Card("drawable://" + R.drawable.cork, "Cork", new Runnable(){
            @Override
            public void run(){
                Log.e("RUNNABLE: ", "CALLING NOTIFICATION");
            }
        }));

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        //ImageLoader.getInstance().destroy();
    }
}
