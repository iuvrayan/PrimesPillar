package com.ray.prime;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

//import org.apache.commons.math3.primes.Primes;

public class MainActivity extends AppCompatActivity {

    private int bufferCounter;

    private int displayCounter;

    private int lastAllStars;

    private int DELAY;

    private char SYMBOL;

    private String ALL_STARS;

    private TextView textView;

    private ActionBar actionBar;

    private boolean isPaused = false;

    private final Bundle defaultSettings = new Bundle();
    private Bundle currentSettings;

    private final int[] UNITS = new int[]{1, 3, 7, 9};

    final int LIMIT = Integer.MAX_VALUE / 10;

    final int range = Integer.MAX_VALUE;
    final int sqrt_limit = (int)Math.sqrt(range);
    final int qurt_limit = (int)Math.sqrt(sqrt_limit);
    boolean[] primes = new boolean[sqrt_limit + 1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sieve();

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
                if (!isPaused) {
                    StringBuilder sb = new StringBuilder(textView.getText());

                    sb.delete(0, 5);
                    updateDisplayCounter();
                    if (sb.substring(0, 5).equals(ALL_STARS)) {
                        lastAllStars = displayCounter;
                    }

                    sb.append(getPattern(bufferCounter));
                    updateBufferCounter();

                    textView.setText(sb.toString());

                    updateActionBar();
                }
                handler.postDelayed(this, DELAY);
            }

        }

        handler.post(new RunnableTextView(handler, textView));
    }

    public void setDefaultSettings() {
        defaultSettings.putString(Constants.SEED, "0");
        defaultSettings.putString(Constants.DELAY_VALUE, "150");
        defaultSettings.putString(Constants.DISPLAY_CHAR, "*");
        defaultSettings.putString(Constants.TEXT_SIZE, "14");
        defaultSettings.putString(Constants.TEXT_CLR, "#FF7233");
    }

    // This method is used for initialising the textView state after changing the settings.
    public void init() {
        bufferCounter = Integer.parseInt(currentSettings.getString(Constants.SEED));
        displayCounter = bufferCounter;

        DELAY = Integer.parseInt(currentSettings.getString(Constants.DELAY_VALUE));

        SYMBOL = currentSettings.getString(Constants.DISPLAY_CHAR).charAt(0);
        ALL_STARS = new String(new char[]{SYMBOL, SYMBOL, SYMBOL, SYMBOL, '\n'});
        lastAllStars = 0;

        textView.setTextSize(Float.parseFloat(currentSettings.getString(Constants.TEXT_SIZE)));
        textView.setTextColor(Color.parseColor(currentSettings.getString(Constants.TEXT_CLR)));

        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int height = displaySize.y;
        //System.out.println("Height: "+height);

        int bufferLines = height / Float.valueOf(currentSettings.getString(Constants.TEXT_SIZE)).intValue();
        if (bufferLines == 0) {
            bufferLines = 10;
        }
        //System.out.println("Buffer Lines: "+ bufferLines);

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<bufferLines; i++) {
            sb.append(getPattern(bufferCounter));
            updateBufferCounter();
        }

        if (sb.substring(0, 5).equals(ALL_STARS)) {
            lastAllStars = displayCounter;
        }

        textView.setText(sb.toString());
        updateActionBar();
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
        } else if (item.getItemId() == R.id.help_menu_item){
            Intent helpIntent = new Intent(this, HelpActivity.class);
            startActivity(helpIntent);
        } else if (item.getItemId() == R.id.settings_menu_item){
            isPaused = true;
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            Bundle preferences = new Bundle();
            preferences.putBundle(Constants.DEFAULTS, defaultSettings);
            currentSettings.putString(Constants.SEED, Integer.toString(displayCounter));
            preferences.putBundle(Constants.CURRENTS, this.currentSettings);
            settingsIntent.putExtras(preferences);
            settingsLauncher.launch(settingsIntent);
        } else if (item.getItemId() == R.id.pause_resume_menu_item) {
            isPaused = !isPaused;
            if(isPaused) {
                item.setTitle("Resume");
            } else {
                item.setTitle("Pause");
            }
        }
        return true;
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //validateAndChangeSettings(result.getData().getExtras());
                    if (result.getData() != null) {
                        currentSettings = result.getData().getExtras();
                    }
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private String getPattern(int i) {
        StringBuilder sb = new StringBuilder();

        for (int u : UNITS) {
            int v = i * 10 + u;

            if (v > 0 && isPrime(v)) {
                sb.append(SYMBOL);
            } else {
                sb.append('\u0020');
            }
        }

        sb.append('\n');

        return sb.toString();
    }

    private void updateActionBar() {
        if (actionBar != null) {
            StringBuilder ab = new StringBuilder();
            ab.append(lastAllStars);
            ab.append(Constants.SEPARATOR);
            ab.append(displayCounter);
            actionBar.setTitle(ab.toString());
        }
    }

    private void updateDisplayCounter() {
        displayCounter++;
        if (displayCounter > LIMIT) {
            //bufferCounter = 0;
            displayCounter = 0;
        }
    }

    private void updateBufferCounter() {
        bufferCounter++;
        if (bufferCounter > LIMIT) {
            bufferCounter = 0;
            //displayCounter = 0;
        }
    }

    private boolean isPrime(int n) {
        if (n <= sqrt_limit) {
            return primes[n];
        }

        int v = (int)Math.sqrt(n);

        for (int i=2; i<=v; i++) {
            if (primes[i] && n%i == 0) {
                return false;
            }
        }

        return true;
    }

    private void sieve() {
        for(int i=2; i<primes.length; i++) {
            primes[i] = true;
        }

        for (int i = 2; i<= qurt_limit; i++) {
            if (primes[i]) {
                for (int j = i*i; j <= sqrt_limit; j += i) {
                    primes[j] = false;
                }
            }
        }
    }
}