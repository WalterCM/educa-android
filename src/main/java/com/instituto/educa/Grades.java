package com.instituto.educa;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class Grades extends AppCompatActivity {
    private TableLayout tableLayout;
    private String access;
    private int classroom_id;
    private int course_id;
    private String url;
    private Map<String, HashMap<String, EditText>> gradesEditBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        access = mainData.getString("access");
        classroom_id = mainData.getInt("classroom_id");
        course_id = mainData.getInt("course_id");

        url = APIContract.URL + "classrooms/" + classroom_id + "/courses/"+ course_id + "/grades/";

        StringRequest stringRequest= new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        try {
                            createDetailTable(new JSONObject(response));
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
    private void createDetailTable(final JSONObject gradesObject) throws JSONException {
        tableLayout = findViewById(R.id.tableLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(lp);

        boolean isProfessor = gradesObject.getBoolean("is_professor");
        String[] textViewsStrings = {"Nombres", "PC1", "PC2", "PC3", "PC4", "Parcial", "Final"};
        TableRow headerRow = new TableRow(this);
        for (int i = 0; i < textViewsStrings.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(textViewsStrings[i]);
            textView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

            headerRow.addView(textView);
        }
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        tableLayout.addView(headerRow);

        JSONArray students = gradesObject.getJSONArray("students");

        gradesEditBoxes = new HashMap<String, HashMap<String, EditText>>();
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
            studentRow.addView(studentName);

            Map<String, EditText> gradesMap = new HashMap<>();
            for (int j = 1; j <= 4; j++) {
                TextView studentPC;
                if (isProfessor) {
                    studentPC = new EditText(this);
                    studentPC.setInputType(InputType.TYPE_CLASS_NUMBER);
                    gradesMap.put("pc" + j, (EditText) studentPC);
                } else {
                    studentPC = new TextView(this);
                }

                studentPC.setText(student.getString("pc" + j));
                studentPC.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                studentRow.addView(studentPC);
            }

            TextView studentMidterm;
            TextView studentFinal;
            if (isProfessor) {
                studentMidterm = new EditText(this);
                studentMidterm.setInputType(InputType.TYPE_CLASS_NUMBER);
                gradesMap.put("midterm", (EditText) studentMidterm);
                studentFinal = new EditText(this);
                studentFinal.setInputType(InputType.TYPE_CLASS_NUMBER);
                gradesMap.put("final", (EditText) studentFinal);
            } else {
                studentMidterm = new TextView(this);studentMidterm = new TextView(this);
                studentFinal = new TextView(this);
            }

            studentMidterm.setText(student.getString("midterm"));
            studentMidterm.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentMidterm);

            studentFinal.setText(student.getString("final"));
            studentFinal.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentFinal);

            gradesEditBoxes.put(student.getString("id"), (HashMap<String, EditText>) gradesMap);
        }

        Button seeAverage = new Button(this);
        seeAverage.setText("Ver promedios");
        seeAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                try {
                    createAverageTable(gradesObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tableLayout.addView(seeAverage);

        if (isProfessor) {
            Button submit = new Button(this);
            submit.setText("Guardar Notas");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitGrades();
                }
            });
            tableLayout.addView(submit);
        }
    }

    @SuppressLint("SetTextI18n")
    private void createAverageTable(final JSONObject gradesObject) throws JSONException {
        tableLayout = findViewById(R.id.tableLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(lp);

        //boolean isProfessor = gradesObject.getBoolean("is_professor");
        String[] textViewsStrings = {"Nombres", "Promedio PC", "Parcial", "Final", "Promedio Final"};
        TableRow headerRow = new TableRow(this);
        for (int i = 0; i < textViewsStrings.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(textViewsStrings[i]);
            textView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

            headerRow.addView(textView);
        }
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        tableLayout.addView(headerRow);

        JSONArray students = gradesObject.getJSONArray("students");

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
            studentRow.addView(studentName);

            TextView studentPCAverage = new TextView(this);
            studentPCAverage.setText(student.getString("pc_average"));
            studentPCAverage.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentPCAverage);

            TextView studentMidterm = new TextView(this);
            studentMidterm.setText(student.getString("midterm"));
            studentMidterm.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentMidterm);

            TextView studentFinal = new TextView(this);
            studentFinal.setText(student.getString("final"));
            studentFinal.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentFinal);

            TextView studentFinalAverage = new TextView(this);
            studentFinalAverage.setText(student.getString("grade_average"));
            studentFinalAverage.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            studentRow.addView(studentFinalAverage);
        }

        Button seeDetail = new Button(this);
        seeDetail.setText("Ver detalles");
        seeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                try {
                    createDetailTable(gradesObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tableLayout.addView(seeDetail);
    }

    private void submitGrades() {
        StringRequest stringRequest= new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REST RESPONSE: ", response);
                        // TODO: show message to show that it was saved
                        tableLayout = findViewById(R.id.tableLayout);
                        tableLayout.removeAllViews();
                        try {
                            createDetailTable(new JSONObject(response));
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
                for (String id : gradesEditBoxes.keySet()) {
                    JSONObject grades = new JSONObject();
                    try {
                        grades.put("id", id);
                        Map<String, EditText> gradeMap = gradesEditBoxes.get(id);
                        for (String key : gradeMap.keySet()) {
                            grades.put(key, gradeMap.get(key).getText());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    students.put(grades);
                }
                params.put("students", students.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
