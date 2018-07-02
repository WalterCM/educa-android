package com.instituto.educa;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Enrollment extends AppCompatActivity {
    private ListView mListView;
    private String access = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        Bundle tokenData = getIntent().getExtras();
        if (tokenData == null)
        {
            // TODO: Do something here lmao
        }

        access = tokenData.getString("access");

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                APIContract.URL + "notmine/",
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
    private void createCards(JSONArray classrooms) {
        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        if (classrooms.length() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("No hay salones para matricularse o ya se encuentra matriculado");
            emptyMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LinearLayout layout = findViewById(R.id.linearLayout);
            layout.addView(emptyMessage);
        }

        for (int i = 0; i < classrooms.length(); i++)
        {
            JSONObject classroom = null;
            try {
                classroom = classrooms.getJSONObject(i);
                final JSONObject c = classroom;
                final String pk = classroom.getString("id");
                final String room = classroom.getString("room");
                list.add(new Card("drawable://" + R.drawable.cork, room, new Runnable(){
                    @Override
                    public void run() {
                        confirmEnrollment(pk, room);
                    }
                }));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }

    private void confirmEnrollment(final String pk, String room) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        enroll(pk);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // DO NOTHING
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Enrollment.this);
        builder.setMessage("Esta seguro que desea matricularse en el aula " + room + "?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void enroll(String pk) {
        Log.e("ENrOLL: ", "RECEIVED PK: " + pk);
        StringRequest stringRequest= new StringRequest(
                Request.Method.POST,
                APIContract.URL + "classrooms/" + pk + "/enroll/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);

                        Intent i = new Intent(Enrollment.this, Main.class);
                        i.putExtra("access", access);
                        startActivity(i);
                        finish();

                        try {
                            JSONObject enrollResponse = new JSONObject(response);
                            if (enrollResponse.getBoolean("enrolled")) {
                                // TODO: just enrolled message
                            } else if (enrollResponse.getInt("code") == 1){
                                // TODO: already enrolled message
                            } else {
                                // TODO: Show an error
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
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
