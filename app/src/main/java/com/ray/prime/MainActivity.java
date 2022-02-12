package com.ray.prime;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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

    private BigInteger counter = BigInteger.ZERO;

    private final BigInteger[] UNITS = new BigInteger[]{
            new BigInteger("1"),
            new BigInteger("3"),
            new BigInteger("7"),
            new BigInteger("9")
    };

    private final BigInteger LIMIT = new BigInteger("1844674407370955161");

    private final String COMPLETE = "****\n";

    private final int DELAY = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowTitleEnabled(false);

        TextView textView = findViewById(R.id.textView);

        init(textView, actionBar);

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
                StringBuilder sb = new StringBuilder(textView.getText());
                if (sb.substring(0,5).equals(COMPLETE)) {
                    actionBar.setTitle(counter.toString());
                }
                sb.append(getNextPattern());
                textView.setText(sb.substring(5));
                handler.postDelayed(this, DELAY);
            }
        }

        handler.post(new RunnableTextView(handler, textView));
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

            Intent currentSettings = new Intent(this, SettingsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("COLOUR", "BLACK");
            currentSettings.putExtras(extras);
            System.out.println("CURRENT SETTINGS: "+currentSettings.getExtras());

            //startActivityForResult(intent, MY_REQUEST_CODE);



            settingsLauncher.launch(currentSettings);
        }
        return true;
    }

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent modifiedSettings = result.getData();
                    System.out.println("MODIFIED SETTINGS: "+modifiedSettings.getExtras().get("COLOUR"));
                }
            });

    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            ActionBar actionBar = getSupportActionBar();
            if (!actionBar.isShowing()) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
        return true;
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent settings) {
        super.onActivityResult(requestCode, resultCode, settings);
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("BACK TO MAIN ACTIVITY");
        }
    }
    */
    public void init(TextView textView, ActionBar actionBar) {
        //Random random = new Random();
        //counter = new BigInteger(String.valueOf(random.nextInt(100000)));
        //System.out.println("Counter: "+counter);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        System.out.println("Height: "+height);
        //int height = getWindowManager().getCurrentWindowMetrics().getBounds().height();

        int lines = height / 10;
        System.out.println("Lines: "+lines);

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<lines; i++) {
            sb.append(getNextPattern());
        }
        textView.setText(sb.toString());
    }

    public String getNextPattern() {
        counter = counter.add(BigInteger.ONE);

        if (counter.compareTo(LIMIT) > 0) {
            counter = BigInteger.ZERO;
        }

        StringBuilder sb = new StringBuilder();
        for (BigInteger u : UNITS) {
            BigInteger v = counter.multiply(BigInteger.TEN).add(u);
            if (v.isProbablePrime(Integer.MAX_VALUE)) {
                sb.append('*');
            } else {
                sb.append('\u0020');
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}