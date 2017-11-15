package com.archer.androidart.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.archer.androidart.notes.chapter02.aidl.BookManagerActivity;
import com.archer.androidart.notes.chapter02.binderpool.BinderPoolActivity;
import com.archer.androidart.notes.chapter02.messenger.MessengerActivity;
import com.archer.androidart.notes.chapter02.provider.ProviderActivity;
import com.archer.androidart.notes.chapter02.socket.TCPClientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button goToMessengerActivityBtn = (Button) findViewById(R.id.id_goToMessengerActivity);
        goToMessengerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MessengerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        Button goToBookManagerActivityBtn = (Button) findViewById(R.id.id_goToBookManagerActivity);
        goToBookManagerActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookManagerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        Button goToProviderActivityBtn = (Button) findViewById(R.id.id_goToProviderActivity);
        goToProviderActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProviderActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        Button goToTCPClientActivityBtn = (Button) findViewById(R.id.id_goToTCPClientActivity);
        goToTCPClientActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TCPClientActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        Button goToBinderPoolActivityBtn = (Button) findViewById(R.id.id_goToBinderPoolActivity);
        goToBinderPoolActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BinderPoolActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
