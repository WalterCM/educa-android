package com.instituto.educa;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.util.HashMap;
import java.util.Map;

public class Attendance extends AppCompatActivity {
    private TableLayout tableLayout;
    private String access;
    private int classroom_id;
    private CheckBox[] attendanceCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        access = mainData.getString("access");
        classroom_id = mainData.getInt("classroom_id");

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                APIContract.URL + "classrooms/" + classroom_id + "/attendance/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        try {
                            createTables(new JSONObject(response));
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
    private void createTables(JSONObject attendanceObject) throws JSONException {
        tableLayout = findViewById(R.id.tableLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(lp);

        boolean isProfessor = attendanceObject.getBoolean("is_professor");

        TextView name = new TextView(this);
        name.setText("Nombres");
        name.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        TextView attended = new TextView(this);
        attended.setText("Asistidas");
        attended.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        TextView percentage = new TextView(this);
        percentage.setText("Porcentaje");
        percentage.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        headerRow.addView(name);
        headerRow.addView(attended);
        headerRow.addView(percentage);
        if (isProfessor) {
            TextView newAttendance = new TextView(this);
            newAttendance.setText("Nueva asistencia");
            newAttendance.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            headerRow.addView(newAttendance);
        }
        tableLayout.addView(headerRow);

        /*ScrollView scrollView = new ScrollView(this);
        tableLayout.addView(scrollView);*/

        JSONArray students = attendanceObject.getJSONArray("students");

        attendanceCheckboxes = new CheckBox[students.length()];
        for (int i = 0; i < students.length(); i++) {
            TableRow studentRow = new TableRow(this);
            studentRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(studentRow);

            JSONObject student = students.getJSONObject(i);
            TextView studentName = new TextView(this);
            studentName.setText(student.getString("first_name") + " " + student.getString("last_name"));
            studentName.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            TextView studentAttendance = new TextView(this);
            studentAttendance.setText(student.getString("classes_attended") + "/" + attendanceObject.getString("classes_done"));
            studentAttendance.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            TextView studentPercentage = new TextView(this);
            studentPercentage.setText(student.getString("percentage"));
            studentPercentage.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentName);
            studentRow.addView(studentAttendance);
            studentRow.addView(studentPercentage);

            if (isProfessor) {
                attendanceCheckboxes[i] = new CheckBox(this);
                attendanceCheckboxes[i].setId(student.getInt("id"));
                attendanceCheckboxes[i].setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

                attendanceCheckboxes[i].setChecked(true);
                studentRow.addView(attendanceCheckboxes[i]);
            }
        }

        if (isProfessor) {
            Button submit = new Button(this);
            submit.setText("Guardar asistencia");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitAttendance();
                }
            });
            tableLayout.addView(submit);
        }
    }

    private void submitAttendance() {
        StringRequest stringRequest= new StringRequest(
                Request.Method.POST,
                APIContract.URL + "classrooms/" + classroom_id + "/attendance/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        // TODO: show a message about submition
                        tableLayout = findViewById(R.id.tableLayout);
                        tableLayout.removeAllViews();
                        try {
                            createTables(new JSONObject(response));
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
                JSONArray students = new JSONArray();
                for (int i = 0; i < attendanceCheckboxes.length; i++) {
                    JSONObject attendance = new JSONObject();
                    try {
                        attendance.put("id", attendanceCheckboxes[i].getId());
                        attendance.put("is_attended", attendanceCheckboxes[i].isChecked());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    students.put(attendance);
                }
                params.put("students", students.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
