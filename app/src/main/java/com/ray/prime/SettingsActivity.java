package com.ray.prime;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    EditText seed;
    EditText delay;
    EditText displayChar;
    EditText textSize;
    EditText textColour;
    Button   btnReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seed = findViewById(R.id.editTextNumberSeedValue);
        delay = findViewById(R.id.editTextNumberDelay);
        displayChar = findViewById(R.id.editTextDisplayChar);
        textSize = findViewById(R.id.editTextDecimalCharSize);
        textColour = findViewById(R.id.editTextForeground);
        btnReset = findViewById(R.id.buttonReset);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Bundle currentSettings = getIntent().getExtras().getBundle(Constants.CURRENTS);

        seed.setText(currentSettings.getString(Constants.SEED));
        delay.setText(currentSettings.getString(Constants.DELAY_VALUE));
        displayChar.setText(currentSettings.getString(Constants.DISPLAY_CHAR));
        textSize.setText(currentSettings.getString(Constants.TEXT_SIZE));
        textColour.setText(currentSettings.getString(Constants.TEXT_CLR));

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Bundle defaults = getIntent().getExtras().getBundle(Constants.DEFAULTS);
                seed.setText(defaults.getString(Constants.SEED));
                delay.setText(defaults.getString(Constants.DELAY_VALUE));
                displayChar.setText(defaults.getString(Constants.DISPLAY_CHAR));
                textSize.setText(defaults.getString(Constants.TEXT_SIZE));
                textColour.setText(defaults.getString(Constants.TEXT_CLR));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            System.out.println("GOING BACK");
            Intent modifiedSettings = new Intent(this, SettingsActivity.class);
            Bundle extras = new Bundle();
            extras.putString(Constants.SEED, seed.getText().toString());
            extras.putString(Constants.DELAY_VALUE, delay.getText().toString());
            extras.putString(Constants.DISPLAY_CHAR, displayChar.getText().toString());
            extras.putString(Constants.TEXT_SIZE, textSize.getText().toString());
            extras.putString(Constants.TEXT_CLR, textColour.getText().toString());
            modifiedSettings.putExtras(extras);
            setResult(RESULT_OK, modifiedSettings);
            finish();
        }
        return true;
    }
}