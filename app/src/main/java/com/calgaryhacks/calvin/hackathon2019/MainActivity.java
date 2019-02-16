package com.calgaryhacks.calvin.hackathon2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().getReference().setValue("Board is green!");
    }
}
