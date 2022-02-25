package com.ray.prime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

public class SettingsActivity extends AppCompatActivity {
    EditText seed;
    EditText delay;
    EditText displayChar;
    EditText textSize;
    EditText textColour;
    Button   btnReset;
    Bundle defaultSettings;
    Bundle currentSettings;
    final BigInteger LIMIT = new BigInteger("2").pow(64).divide(BigInteger.TEN);

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

        defaultSettings = getIntent().getExtras().getBundle(Constants.DEFAULTS);
        currentSettings = getIntent().getExtras().getBundle(Constants.CURRENTS);

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
            //System.out.println("GOING BACK");
            Intent settings = new Intent(this, SettingsActivity.class);
            if(!isValidInput()) {
                Toast.makeText(getApplicationContext(), "Invalid settings restored to default values", Toast.LENGTH_SHORT).show();
            }
            settings.putExtras(currentSettings);
            setResult(RESULT_OK, settings);
            finish();
        }
        return true;
    }

    public boolean isValidInput() {
        getCurrentSettings();

        boolean isValid = true;

        //Validate Seed
        try {
            BigInteger value = new BigInteger(currentSettings.getString(Constants.SEED));
            if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(LIMIT) > 0) {
                currentSettings.putString(Constants.SEED, defaultSettings.getString(Constants.SEED));
                isValid = false;
            }
        } catch (Exception e) {
            currentSettings.putString(Constants.SEED, defaultSettings.getString(Constants.SEED));
            isValid = false;
        }

        //Validate Delay
        try {
            if (Integer.parseInt(currentSettings.getString(Constants.DELAY_VALUE)) < 1 || Integer.parseInt(currentSettings.getString(Constants.DELAY_VALUE)) > 2000) {
                currentSettings.putString(Constants.DELAY_VALUE, defaultSettings.getString(Constants.DELAY_VALUE));
                isValid = false;
            }
        } catch (Exception e) {
            currentSettings.putString(Constants.DELAY_VALUE, defaultSettings.getString(Constants.DELAY_VALUE));
            isValid = false;
        }

        //Validate Char
        if (currentSettings.getString(Constants.DISPLAY_CHAR).length() < 1 || Character.isWhitespace(currentSettings.getString(Constants.DISPLAY_CHAR).charAt(0))) {
            currentSettings.putString(Constants.DISPLAY_CHAR, defaultSettings.getString(Constants.DISPLAY_CHAR));
            isValid = false;
        }

        //Validate Text Size
        try {
            if (Float.parseFloat(currentSettings.getString(Constants.TEXT_SIZE)) < 1 || Float.parseFloat(currentSettings.getString(Constants.TEXT_SIZE)) > 100) {
                currentSettings.putString(Constants.TEXT_SIZE, defaultSettings.getString(Constants.TEXT_SIZE));
                isValid = false;
            }
        } catch (Exception e) {
            currentSettings.putString(Constants.TEXT_SIZE, defaultSettings.getString(Constants.TEXT_SIZE));
            isValid = false;
        }

        //Validate Text Colour
        try {
            Color.parseColor(currentSettings.getString(Constants.TEXT_CLR));
        } catch (Exception e) {
            currentSettings.putString(Constants.TEXT_CLR, defaultSettings.getString(Constants.TEXT_CLR));
            isValid = false;
        }

        return isValid;
    }

    public void getCurrentSettings() {
        currentSettings.putString(Constants.SEED, seed.getText().toString());
        currentSettings.putString(Constants.DELAY_VALUE, delay.getText().toString());
        currentSettings.putString(Constants.DISPLAY_CHAR, displayChar.getText().toString());
        currentSettings.putString(Constants.TEXT_SIZE, textSize.getText().toString());
        currentSettings.putString(Constants.TEXT_CLR, textColour.getText().toString());
    }

}