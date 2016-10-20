package com.kaminari.multiremote;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class KeyboardActivity extends ActionBarActivity implements View.OnTouchListener {

    private Button keyA;

    private Handler keyAHandler;

    private Runnable keyAAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        init();
        assignListeners();
        defineRunnable();
    }

    private void init() {
        keyA = (Button) findViewById(R.id.keyA);
    }

    private void assignListeners() {
        keyA.setOnTouchListener(this);
    }

    private void defineRunnable() {
        keyAAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("65_press");
                keyAHandler.postDelayed(keyAAction, 100);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keyboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.keyA:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    keyAHandler = new Handler();
                    keyAHandler.postDelayed(keyAAction, 100);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    keyAHandler.removeCallbacks(keyAAction);
                    MainActivity.out.println("65_release");
                }
        }
        return false;
    }
}
