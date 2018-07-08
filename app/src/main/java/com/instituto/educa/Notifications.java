package com.instituto.educa;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends AppCompatActivity {
    private String access;
    private int classroom_id;
    private int course_id;
    private String url;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        access = mainData.getString("access");
        classroom_id = mainData.getInt("classroom_id");
        course_id = mainData.getInt("course_id");

        url = APIContract.URL + "classrooms/" + classroom_id + "/courses/" + course_id + "/notifications/";

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        try {
                            showNotifications(new JSONObject(response));
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

    private void showNotifications(JSONObject notificationResponse) throws JSONException {
        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card(APIEduca.getUrl("drawable://" + R.drawable.redact), "Redactar", new Runnable(){
            @Override
            public void run() {
                Log.e("TAG: ", "Clicking on a notification");
                Intent i = new Intent(Notifications.this, CreateNotification.class);
                i.putExtra("access", access);
                i.putExtra("classroom_id", classroom_id);
                i.putExtra("course_id", course_id);
                startActivity(i);
            }
        }));

        JSONArray notifications = notificationResponse.getJSONArray("notifications");

        if (notifications.length() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("No tiene notificaciones");
            emptyMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LinearLayout layout = findViewById(R.id.linearLayout);
            layout.addView(emptyMessage);
        }

        for (int i = 0; i < notifications.length(); i++)
        {
            final JSONObject notification = notifications.getJSONObject(i);
            final String author = notification.getString("author");
            final String subject = notification.getString("subject");
            final String text = notification.getString("text");

            String created = notification.getString("created");
            int indexOfT = created.indexOf("T");
            final String date = created.substring(0, indexOfT);
            final String time = created.substring(indexOfT + 1, indexOfT + 1 + 8);

            String title = subject;
            int max = 10;
            if (title.length() > max) {
                title = title.substring(0, max);
                title += "...";
            }
            title += " (" + notification.getString("author") +  ")" + " - " + date + " - " + time;
            list.add(new Card(APIEduca.getUrl("drawable://" + R.drawable.mail), title, new Runnable(){
                @Override
                public void run() {
                    Intent i = new Intent(Notifications.this, Notification.class);
                    i.putExtra("author", author);
                    i.putExtra("subject", subject);
                    i.putExtra("text", text);
                    i.putExtra("date", date);
                    i.putExtra("time", time);
                    startActivity(i);
                }
            }));
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.card_layout_main, list);
        mListView.setAdapter(adapter);
    }
}
