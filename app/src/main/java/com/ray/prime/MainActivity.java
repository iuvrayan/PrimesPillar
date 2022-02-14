package com.ray.prime;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    private BigInteger bufferCounter;

    private BigInteger displayCounter;

    private BigInteger lastAllStars;

    private int DELAY;

    private char SYMBOL;

    private String ALL_STARS;

    private TextView textView;

    private ActionBar actionBar;

    private boolean isPaused = false;

    private Bundle defaultSettings = new Bundle();
    private Bundle currentSettings;

    private final BigInteger[] UNITS = new BigInteger[]{
            new BigInteger("1"),
            new BigInteger("3"),
            new BigInteger("7"),
            new BigInteger("9")
    };

    private final BigInteger LIMIT = new BigInteger("2").pow(64).divide(BigInteger.TEN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Store Default Settings
        setDefaultSettings();

        //Set current settings to default settings
        currentSettings = defaultSettings;

        //Initialise variables based on currents
        init();

        final Handler handler = new Handler();

        class RunnableTextView implements Runnable {
            private final Handler handler;
            private final TextView textView;

            public RunnableTextView(Handler handler, TextView textView) {
                this.handler = handler;
                this.textView = textView;
            }

            @Override
            public void run() {
                System.out.println("IN RUN METHOD");
                if (!isPaused) {
                    StringBuilder sb = new StringBuilder(textView.getText());
                    sb.append(getNextPattern());
                    sb.delete(0, 5);
                    textView.setText(sb.toString());

                    displayCounter = displayCounter.add(BigInteger.ONE);

                    if (sb.substring(0, 5).equals(ALL_STARS)) {
                        lastAllStars = displayCounter;
                    }

                    if (actionBar != null) {
                        sb = new StringBuilder();
                        sb.append(lastAllStars);
                        sb.append(Constants.SEPARATOR);
                        sb.append(displayCounter);
                        actionBar.setTitle(sb.toString());
                    }
                }
                handler.postDelayed(this, DELAY);
            }
        }

        handler.post(new RunnableTextView(handler, textView));
    }

    public void setDefaultSettings() {
        defaultSettings.putString(Constants.SEED, BigInteger.ZERO.toString());
        defaultSettings.putString(Constants.DELAY_VALUE, "150");
        defaultSettings.putString(Constants.DISPLAY_CHAR, "*");
        defaultSettings.putString(Constants.TEXT_SIZE, "14");
        defaultSettings.putString(Constants.TEXT_CLR, "#FF7233");
    }

    // This method is used for initialising the textView state after changing the settings.
    public void init() {
        bufferCounter = new BigInteger(currentSettings.getString(Constants.SEED));
        System.out.println("BUFFER COUNTER: "+bufferCounter);
        displayCounter = bufferCounter;

        DELAY = Integer.valueOf(currentSettings.getString(Constants.DELAY_VALUE));

        SYMBOL = currentSettings.getString(Constants.DISPLAY_CHAR).charAt(0);
        ALL_STARS = new String(new char[]{SYMBOL, SYMBOL, SYMBOL, SYMBOL, '\n'});
        lastAllStars = BigInteger.ZERO;

        textView.setTextSize(Float.valueOf(currentSettings.getString(Constants.TEXT_SIZE)));
        textView.setTextColor(Color.parseColor(currentSettings.getString(Constants.TEXT_CLR)));

        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int height = displaySize.y;
        System.out.println("Height: "+height);

        int bufferLines = height / Float.valueOf(currentSettings.getString(Constants.TEXT_SIZE)).intValue();
        System.out.println("Buffer Lines: "+ bufferLines);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< bufferLines; i++) {
            String pattern = getNextPattern();
            System.out.println(pattern);
            sb.append(pattern);
        }

        textView.setText(sb.toString());

        if (sb.substring(0, 5).equals(ALL_STARS)) {
            lastAllStars = displayCounter;
        }

        if (actionBar != null) {
            sb = new StringBuilder();
            sb.append(lastAllStars);
            sb.append(Constants.SEPARATOR);
            sb.append(displayCounter);
            actionBar.setTitle(sb.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAffinity();
        } else if (item.getItemId() == R.id.settings_menu_item){
            System.out.println("SETTINGS CLICKED");
            isPaused = true;
            Intent currentSettings = new Intent(this, SettingsActivity.class);
            Bundle preferences = new Bundle();
            preferences.putBundle(Constants.DEFAULTS, defaultSettings);
            preferences.putBundle(Constants.CURRENTS, this.currentSettings);
            currentSettings.putExtras(preferences);
            System.out.println("CURRENT SETTINGS: "+currentSettings.getExtras());
            settingsLauncher.launch(currentSettings);
        }
        return true;
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    validateAndChangeSettings(result.getData().getExtras());
                    System.out.println(currentSettings);
                    init();
                    isPaused = false;
                }
            });

    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (!actionBar.isShowing()) {
                    actionBar.show();
                } else {
                    actionBar.hide();
                }
            }
        }
        return true;
    }

    public String getNextPattern() {
        StringBuilder sb = new StringBuilder();
        for (BigInteger u : UNITS) {
            BigInteger v = bufferCounter.multiply(BigInteger.TEN).add(u);
            if (v.isProbablePrime(Integer.MAX_VALUE)) {
                sb.append(SYMBOL);
            } else {
                sb.append('\u0020');
            }
        }
        sb.append('\n');

        bufferCounter = bufferCounter.add(BigInteger.ONE);
        if (bufferCounter.compareTo(LIMIT) > 0) {
            bufferCounter = BigInteger.ZERO;
        }

        return sb.toString();
    }

    public void validateAndChangeSettings(Bundle changes) {
        //Validate Seed
        try {
            BigInteger value = new BigInteger(changes.getString("SEED"));
            if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(LIMIT) > 0) {
                changes.putString(Constants.SEED, defaultSettings.getString(Constants.SEED));
            }
        } catch (Exception e) {
            changes.putString(Constants.SEED, defaultSettings.getString(Constants.SEED));
        }

        //Validate Delay
        try {
            if (Integer.valueOf(changes.getString(Constants.DELAY_VALUE)) < 1) {
                changes.putInt(Constants.DELAY_VALUE, defaultSettings.getInt(Constants.DELAY_VALUE));
            }
        } catch (Exception e) {
            changes.putInt(Constants.DELAY_VALUE, defaultSettings.getInt(Constants.DELAY_VALUE));
        }

        //Validate Char
        if (Character.isWhitespace(changes.getString(Constants.DISPLAY_CHAR).charAt(0))) {
            changes.putChar(Constants.DISPLAY_CHAR, defaultSettings.getChar(Constants.DISPLAY_CHAR));
        }

        //Validate Text Size
        try {
            if (Float.valueOf(changes.getString("TEXT_SIZE")) < 1) {
                changes.putString(Constants.TEXT_SIZE, defaultSettings.getString(Constants.TEXT_SIZE));
            }
        } catch (Exception e) {
            changes.putString(Constants.TEXT_SIZE, defaultSettings.getString(Constants.TEXT_SIZE));
        }

        //Validate Text Colour
        try {
            Color.parseColor(changes.getString(Constants.TEXT_CLR));
        } catch (Exception e) {
            changes.putString(Constants.TEXT_CLR, defaultSettings.getString(Constants.TEXT_CLR));
        }

        currentSettings = changes;
    }
}