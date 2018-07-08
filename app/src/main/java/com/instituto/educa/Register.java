package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private TextView linkSignin;
    private EditText edtUsername, edtPassword1, edtPassword2, edtFirstName, edtLastName, edtEmail;
    private Spinner spinnerUserType;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.input_username);
        edtPassword1 = findViewById(R.id.input_password1);
        edtPassword2 = findViewById(R.id.input_password2);
        edtFirstName= findViewById(R.id.input_firstname);
        edtLastName= findViewById(R.id.input_lastname);
        edtEmail = findViewById(R.id.input_email);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        linkSignin = findViewById(R.id.link_signin);
        linkSignin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        spinnerUserType = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);
    }

    private void register() {
        Log.e("TAG: " , "REGISTER METHOD");
        final String username = String.valueOf(edtUsername.getText());
        final String password1 = String.valueOf(edtPassword1.getText());
        final String password2 = String.valueOf(edtPassword2.getText());
        final String firstName= String.valueOf(edtFirstName.getText());
        final String lastName= String.valueOf(edtLastName.getText());
        final String email= String.valueOf(edtEmail.getText());
        String userType = spinnerUserType.getSelectedItem().toString();
        if (userType.equalsIgnoreCase("Profesor")) {
            userType = "instructor";
        }

        Map<String, String> params = new HashMap();
        params.put("username", username);
        params.put("password1", password1);
        params.put("password2", password2);
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("user_type", userType);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
                APIContract.URL + "register/",
            parameters,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("REGISTER: ", response.toString());
                    try {
                        if (response.getBoolean("registered")) {
                            Intent i = new Intent(Register.this, Login.class);
                            startActivity(i);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //TODO: handle failure
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }
}
