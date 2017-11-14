package com.archer.androidart.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.archer.androidart.notes.chapter02.aidl.BookManagerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button goToBookManagerActivityBtn = (Button) findViewById(R.id.id_goToBookManagerActivity);
        goToBookManagerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookManagerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
