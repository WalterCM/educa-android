package com.instituto.educa;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Notification extends AppCompatActivity {
    private TextView authorView;
    private TextView subjectView;
    private TextView createdView;
    private TextView textView;

    //private Button btnDelete;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Bundle mainData = getIntent().getExtras();
        if (mainData == null)
        {
            // TODO: Do something here lmao
        }

        authorView = findViewById(R.id.author);
        subjectView = findViewById(R.id.subject);
        createdView = findViewById(R.id.created);
        textView = findViewById(R.id.text);
        //btnDelete = findViewById(R.id.btn_delete);

        authorView.append(": " + mainData.getString("author"));
        subjectView.append(": " + mainData.getString("subject"));
        createdView.append(": " + mainData.getString("date") + " Hora: " + mainData.getString("time"));
        textView.setText(mainData.getString("text"));

        /*btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotification();
            }
        });*/
    }

    public void deleteNotification() {

    }
}
