package com.instituto.educa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class Main extends AppCompatActivity {
    private ListView mListView;
    private String access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle tokenData = getIntent().getExtras();
        if (tokenData == null)
        {
            // TODO: Do something here lmao
        }

        access = tokenData.getString("access");

        createCards();
    }

    private void createCards() {
        mListView = findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.bamf1, "Salones", new Runnable(){
            @Override
            public void run(){
                //Courses
                Log.e("RUNNABLE: ", "CALLING Classrooms");
                Intent i = new Intent(Main.this, Classrooms.class);
                i.putExtra("access", access);
                startActivity(i);
            }
        }));

        if (APIEduca.isProfessor(access)) {
            list.add(new Card("drawable://" + R.drawable.cork, "Crear salon", new Runnable(){
                @Override
                public void run(){
                    //Courses
                    Log.e("RUNNABLE: ", "CALLING CLASSROOM CREATION");
                    Intent i = new Intent(Main.this, ClassroomCreation.class);
                    i.putExtra("access", access);
                    startActivity(i);
                }
            }));
        } else {
            list.add(new Card("drawable://" + R.drawable.cork, "Matricula", new Runnable(){
                @Override
                public void run(){
                    //Courses
                    Log.e("RUNNABLE: ", "CALLING ENROLLMENT");
                    Intent i = new Intent(Main.this, Enrollment.class);
                    i.putExtra("access", access);
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
