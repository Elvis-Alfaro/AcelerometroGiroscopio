package com.apps.strangesound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView giroscopioTV, acelerometroTV;

    private SensorManager sensorManager;
    private Sensor sensorAcelerometro, sensorGiroscopio;
    private static MediaPlayer soundAcc;
    private static MediaPlayer soundGyro;

    private static final float SHAKE_THRESHOLD = 2.0f;
    private static final int SHAKE_WAIT_TIME_MS = 300;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 300;

    private long mShakeTime = 0;
    private long mRotationTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensorListener();
    }

    private void registerSensorListener() {
        sensorManager.registerListener(this, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGiroscopio, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListener() {
        sensorManager.registerListener(this, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGiroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        soundAcc.pause();
        soundGyro.pause();
    }

    private void initViews() {
        acelerometroTV = findViewById(R.id.acelerometro_tv);
        giroscopioTV = findViewById(R.id.giroscopio_tv);
    }

    private void initValues() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGiroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Instanciate the sound to use
        soundAcc = MediaPlayer.create(this, R.raw.latigazo);
        soundGyro = MediaPlayer.create(this, R.raw.resorte);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String acelerometerContent, gyroscopeContent;
        /*if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                acelerometerContent = "X: ? /n Y: ? /n Z: ?";
                acelerometroTV.setText(acelerometerContent);
            }
            else {
                gyroscopeContent = "X: ? /n Y: ? /n Z: ?";
                giroscopioTV.setText(gyroscopeContent);
            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                acelerometerContent = "X: "+event.values[0]+
                        " /n Y: "+event.values[1]+
                        " /n Z: "+event.values[2];
                acelerometroTV.setText(acelerometerContent);
                detectedShake(event);
            }
            else {
                gyroscopeContent = "X: "+event.values[0]+
                        " /n Y: "+event.values[1]+
                        " /n Z: "+event.values[2];
                giroscopioTV.setText(gyroscopeContent);
                detectedRotation(event);
            }
        }*/

        synchronized (this){
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    acelerometerContent = "X: "+event.values[0]+
                            "\n Y: "+event.values[1]+
                            "\n Z: "+event.values[2];
                    acelerometroTV.setText(acelerometerContent);
                    detectedShake(event);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroscopeContent = "X: "+event.values[0]+
                            "\n Y: "+event.values[1]+
                            "\n Z: "+event.values[2];
                    giroscopioTV.setText(gyroscopeContent);
                    detectedRotation(event);
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void detectedShake(SensorEvent event) {
        long now = System.currentTimeMillis();

        if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if (gForce > SHAKE_THRESHOLD) {
                soundAcc.start();
            }
        }
    }

    private void detectedRotation(SensorEvent event) {
        long now = System.currentTimeMillis();

        if ((now - mRotationTime) > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now;

            // Change background color if rate of rotation around any
            // axis and in any direction exceeds threshold;
            // otherwise, reset the color
            if (Math.abs(event.values[0]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[1]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[2]) > ROTATION_THRESHOLD) {
                soundGyro.start();
            }
        }
    }

}
