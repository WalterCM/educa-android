package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class CreateNotification extends AppCompatActivity {
    private String access;
    private int classroom_id, course_id;
    private EditText edtSubject, edtText;
    private Button btnCreateNotificacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        access = mainData.getString("access");
        classroom_id = mainData.getInt("classroom_id");
        course_id = mainData.getInt("course_id");

        edtSubject = findViewById(R.id.input_subject);
        edtText = findViewById(R.id.input_text);
        btnCreateNotificacion = findViewById(R.id.btn_create_notification);

        btnCreateNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificacion();
            }
        });
    }

    public void createNotificacion() {
        StringRequest stringRequest= new StringRequest(
                Request.Method.POST,
                APIContract.URL + "classrooms/" + classroom_id + "/courses/" + course_id + "/notifications/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        // TODO: show message to show that it was saved
                        Intent i = new Intent(CreateNotification.this, Notifications.class);
                        i.putExtra("access", access);
                        i.putExtra("classroom_id", classroom_id);
                        i.putExtra("course_id", course_id);
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("subject", edtSubject.getText().toString());
                params.put("text", edtText.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
