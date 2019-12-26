package com.shresthagaurav.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DashBoard extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        textView = findViewById(R.id.userToken);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String token = bundle.getString("token");
            String username = bundle.getString("username");
            textView.setText("your username is "+username+"\n and your access token is "+token);
        }
    }
}
