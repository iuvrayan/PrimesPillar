package com.ray.prime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnStart = findViewById(R.id.buttonStart);
        Intent mainIntent = new Intent(this, MainActivity.class);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(mainIntent);
            }
        });

    }
}