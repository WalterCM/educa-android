package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClassroomCreation extends AppCompatActivity {
    private String access;
    private Spinner courseSelector;
    private TextView roomTitle;
    private Button btnCreateClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_creation);

        courseSelector = findViewById(R.id.course_selector);
        roomTitle = findViewById(R.id.room);
        btnCreateClass = findViewById(R.id.btn_create_class);

        Bundle tokenData = getIntent().getExtras();
        if (tokenData == null) {
            // TODO: Do something here lmao
        }

        access = tokenData.getString("access");

        btnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClassroom();
            }
        });

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                APIContract.URL + "courses/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        try {
                            setCourses(new JSONArray(response));
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

    private void setCourses(JSONArray courses) throws JSONException {
        String[] options = new String[courses.length()];
        for (int i = 0; i < courses.length(); i++) {
            options[i] = courses.getJSONObject(i).getString("title");
        }
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, options);

        aa.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        courseSelector.setAdapter(aa);
    }

    private void createClassroom() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                APIContract.URL + "classrooms/create/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (new JSONObject(response).getBoolean("created")) {
                                // TODO: show a message that is created, madafaka
                                Intent i = new Intent(ClassroomCreation.this, Main.class);
                                i.putExtra("access", access);
                                startActivity(i);
                            } else {
                                // TODO: show a message that it already exists
                            }
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("room", String.valueOf(roomTitle.getText()));
                params.put("course", String.valueOf(courseSelector.getSelectedItem()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
