package com.kaminari.multiremote;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MouseActivity extends ActionBarActivity implements
        View.OnClickListener, View.OnTouchListener, SensorEventListener,
        GestureDetector.OnGestureListener {

    private Button leftMouseButton;
    private Button rightMouseButton;
    private Button middleMouseButton;
    private TextView trackPad;

    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean sensorSupported = false;

    private GestureDetectorCompat gesture;

    private float initX = 0;
    private float initY = 0;
    private float disX = 0;
    private float disY = 0;

    private static final String TAG = "MultiRemote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        initViews(); // Initialize the views
        assignListeners(); // Assign listeners to each view
    }

    private void initViews() {
        leftMouseButton = (Button) findViewById(R.id.leftMouseButton);
        rightMouseButton = (Button) findViewById(R.id.rightMouseButton);
        middleMouseButton = (Button) findViewById(R.id.middleMouseButton);

        trackPad = (TextView) findViewById(R.id.trackPad);

        gesture = new GestureDetectorCompat(this, this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorSupported = true;
        } else {
            Toast.makeText(this, "Accelerometer is not supported by your device!!",
                    Toast.LENGTH_LONG).show();
            sensorSupported = false;
        }
    }

    private void assignListeners() {
        leftMouseButton.setOnClickListener(this); // Left Mouse Button Pressed
        rightMouseButton.setOnClickListener(this); // Right Mouse Button Pressed
        middleMouseButton.setOnClickListener(this); // Middle Mouse Button Pressed

        middleMouseButton.setOnTouchListener(this); // Middle Mouse Button Scrolling

        trackPad.setOnTouchListener(this); // Track Pad Touched
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mouse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.showTrackPad:
                if(item.isChecked()) {
                    trackPad.setVisibility(View.GONE);
                    if(sensorSupported) {
                        sensorManager.registerListener(this, sensor,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        initX = 0;
                        initY = 0;
                        disX = 0;
                        disY = 0;
                    }
                    item.setChecked(false);
                } else {
                    trackPad.setVisibility(View.VISIBLE);
                    sensorManager.unregisterListener(this);
                    item.setChecked(true);
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        if(MainActivity.isConnected && MainActivity.out != null) {
            switch (v.getId()) {
                case R.id.leftMouseButton:
                    Log.d(TAG, "Left Mouse Button");
                    MainActivity.out.println("left_click"); // Send left_click to server
                    break;
                case R.id.rightMouseButton:
                    Log.d(TAG, "Right Mouse Button");
                    MainActivity.out.println("right_click"); // Send right_click to server
                    break;
                case R.id.middleMouseButton:
                    Log.d(TAG, "Middle Mouse Button");
                    MainActivity.out.println("middle_click"); // Send middle_click to server
                    break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MainActivity.isConnected && MainActivity.out != null) {
            switch (v.getId()) {
                case R.id.trackPad:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Current x and y positions
                            initX = event.getX();
                            initY = event.getY();
                            Log.d(TAG, initX + "," + initY);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // Displacements to new x and y positions
                            disX = event.getX() - initX;
                            disY = event.getY() - initY;
                            Log.d(TAG, disX + "," + disY);

                            // Updates the position of initX and initY
                            initX = event.getX();
                            initY = event.getY();

                            if (disX != 0 || disY != 0)
                                MainActivity.out.println(disX + "," + disY); // Send mouse
                                                                             // movements to the
                                                                             // server
                    }
                    break;
                case R.id.middleMouseButton:
                    gesture.onTouchEvent(event);
            }
        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        disX = initX - event.values[0];
        disY = initY - event.values[1];

        Log.d(TAG, disX + "," + disY);

        initX = disX;
        initY = disY;

        if (disX != 0 || disY != 0)
            MainActivity.out.println(disX + "," + disY); // Send mouse movements to the server
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "Middle Mouse Button");
        MainActivity.out.println("middle_click"); // Send middle_click to server
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        MainActivity.out.println("scroll:" + -1 * distanceY);
        Log.d(TAG, "scroll:" + -1 * distanceY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
