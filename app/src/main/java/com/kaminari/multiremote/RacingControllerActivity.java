package com.kaminari.multiremote;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


public class RacingControllerActivity extends ActionBarActivity implements
        View.OnTouchListener, View.OnClickListener, SensorEventListener {

    private Button race, brake, nos, eBrake, shiftUp, shiftDown, extraUp, extraRight,
            extraDown, extraLeft;
    private CheckBox autoRace;

    private Handler raceHandler, brakeHandler, nosHandler, eBrakeHandler, shiftUpHandler,
            shiftDownHandler, extraUpHandler, extraRightHandler, extraLeftHandler,
            extraDownHandler;

    private Runnable raceAction, brakeAction, nosAction, eBrakeAction, shiftUpAction,
            shiftDownAction, extraUpAction, extraDownAction, extraLeftAction, extraRightAction;

    private boolean isBraking = false;
    private boolean sensorSupported = false;
    private boolean turningLeft = false;
    private boolean turningRight = false;

    private SensorManager sensorManager;
    private Sensor accSensor;
    private WindowManager windowManager;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racing_controller);

        init();
        assignListeners();
        defineRunnable();
    }

    private void init() {
        race = (Button) findViewById(R.id.buttonRace);
        brake = (Button) findViewById(R.id.buttonBrake);
        nos = (Button) findViewById(R.id.buttonNOS);
        eBrake = (Button) findViewById(R.id.buttonEBrake);
        shiftUp = (Button) findViewById(R.id.buttonShiftUp);
        shiftDown = (Button) findViewById(R.id.buttonShiftDown);
        extraUp = (Button) findViewById(R.id.buttonExtraUp);
        extraRight = (Button) findViewById(R.id.buttonExtraRight);
        extraDown = (Button) findViewById(R.id.buttonExtraDown);
        extraLeft = (Button) findViewById(R.id.buttonExtraLeft);

        autoRace = (CheckBox) findViewById(R.id.autoRace);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorSupported = true;
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Toast.makeText(this, "Accelerometer is not supported by your device!!",
                    Toast.LENGTH_LONG).show();
            sensorSupported = false;
        }

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    private void assignListeners() {
        if(MainActivity.isConnected) {
            race.setOnTouchListener(this);
            brake.setOnTouchListener(this);
            nos.setOnTouchListener(this);
            eBrake.setOnTouchListener(this);
            shiftUp.setOnTouchListener(this);
            shiftDown.setOnTouchListener(this);
            extraUp.setOnTouchListener(this);
            extraRight.setOnTouchListener(this);
            extraDown.setOnTouchListener(this);
            extraLeft.setOnTouchListener(this);

            autoRace.setOnClickListener(this);

            if(sensorSupported)
                sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void defineRunnable() {
        raceAction = new Runnable() {
            @Override
            public void run() {
                if(!isBraking)
                    MainActivity.out.println("65_press");
                raceHandler.postDelayed(raceAction, 5);
            }
        };

        brakeAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("90_press");
                brakeHandler.postDelayed(brakeAction, 5);
            }
        };

        nosAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("16_press");
                nosHandler.postDelayed(nosAction, 5);
            }
        };

        eBrakeAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("32_press");
                eBrakeHandler.postDelayed(eBrakeAction, 5);
            }
        };

        shiftUpAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("87_press");
                shiftUpHandler.postDelayed(shiftUpAction, 5);
            }
        };

        shiftDownAction = new Runnable() {
            @Override
            public void run() {
                MainActivity.out.println("83_press");
                shiftDownHandler.postDelayed(shiftDownAction, 5);
            }
        };

        extraUpAction = new Runnable() {
            @Override
            public void run() {

            }
        };

        extraRightAction = new Runnable() {
            @Override
            public void run() {

            }
        };

        extraDownAction = new Runnable() {
            @Override
            public void run() {

            }
        };

        extraLeftAction = new Runnable() {
            @Override
            public void run() {

            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_racing_controller, menu);
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
            case R.id.buttonRace:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    raceHandler = new Handler();
                    raceHandler.postDelayed(raceAction, 5);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    raceHandler.removeCallbacks(raceAction);
                    MainActivity.out.println("65_release");
                }
                break;
            case R.id.buttonBrake:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    brakeHandler = new Handler();
                    brakeHandler.postDelayed(brakeAction, 5);
                    isBraking = true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    brakeHandler.removeCallbacks(brakeAction);
                    MainActivity.out.println("90_release");
                    isBraking = false;
                }
                break;
            case R.id.buttonNOS:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    nosHandler = new Handler();
                    nosHandler.postDelayed(nosAction, 5);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    nosHandler.removeCallbacks(nosAction);
                    MainActivity.out.println("16_release");
                }
                break;
            case R.id.buttonEBrake:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    eBrakeHandler = new Handler();
                    eBrakeHandler.postDelayed(eBrakeAction, 5);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    eBrakeHandler.removeCallbacks(eBrakeAction);
                    MainActivity.out.println("32_release");
                }
                break;
            case R.id.buttonShiftUp:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    shiftUpHandler = new Handler();
                    shiftUpHandler.postDelayed(shiftUpAction, 5);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    shiftUpHandler.removeCallbacks(shiftUpAction);
                    MainActivity.out.println("87_release");
                }
                break;
            case R.id.buttonShiftDown:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    shiftDownHandler = new Handler();
                    shiftDownHandler.postDelayed(shiftDownAction, 5);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    shiftDownHandler.removeCallbacks(shiftDownAction);
                    MainActivity.out.println("83_release");
                }
                break;
            case R.id.buttonExtraUp:
                break;
            case R.id.buttonExtraRight:
                break;
            case R.id.buttonExtraDown:
                break;
            case R.id.buttonExtraLeft:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.autoRace:
                if(autoRace.isChecked()) {
                    if(!isBraking) {
                        raceHandler = new Handler();
                        raceHandler.postDelayed(raceAction, 5);
                    }
                } else {
                    raceHandler.removeCallbacks(raceAction);
                    MainActivity.out.println("65_release");
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        float y = 0;

        switch (display.getRotation()) {
            case 1:
                y = event.values[1];
                break;
            case 3:
                y = -event.values[1];
                break;
        }

        if(y < -2) {
            MainActivity.out.println("37_press");
            turningLeft = true;
        } else if(y > 2) {
            MainActivity.out.println("39_press");
            turningRight = true;
        } else {
            if(turningLeft) {
                turningLeft = false;
                MainActivity.out.println("37_release");
            } else if(turningRight) {
                turningRight = false;
                MainActivity.out.println("39_release");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
