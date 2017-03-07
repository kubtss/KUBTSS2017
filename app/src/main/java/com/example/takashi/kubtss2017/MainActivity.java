package com.example.takashi.kubtss2017;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ReceivedDataAdapter mReceivedDataAdapter;
    private SensorAdapter mSensorAdapter;
    private MapSurfaceView mMapSurfaceView;
    private LoggerThread mLoggerThread;
    private double atmLapse, atmStandard;
    private String mapStr = "kyoto";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//スリープ抑制

        // WindowManagerのインスタンス取得
        WindowManager wm = getWindowManager();
        // Displayのインスタンス取得
        Display disp = wm.getDefaultDisplay();
        int width = disp.getWidth();
        int height = disp.getHeight();
        Log.i("Oncreate", "Oncreate!!" );
        if (Build.VERSION.SDK_INT >= 19) {
            Log.i("sd", "getExternalFilesDirを呼び出します");
            File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
            File extSdDir = extDirs[extDirs.length - 1];
            Logger.setExternalDir(extSdDir);
            Log.i("sd", "getExternalFilesDirが返すパス: " + extSdDir.getAbsolutePath());
        }else{
            Log.e("sd", "This SDK version is under 18.");
            finish();
        }

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Configuration config = getResources().getConfiguration();
        mSensorAdapter = new SensorAdapter(mSensorManager, mLocationManager, config);
        mReceivedDataAdapter = new ReceivedDataAdapter(getBaseContext());
        mMapSurfaceView = new MapSurfaceView(this,mSensorManager,mLocationManager,mSensorAdapter,mReceivedDataAdapter,width,height);

        //2:インスタンスをnew&start
        mLoggerThread = new LoggerThread(mSensorAdapter, mReceivedDataAdapter);
        mLoggerThread.start();

        setContentView(mMapSurfaceView);
    }
    @Override
    public void onResume() {
        super.onResume();

        //このアプリケーションのSharedPreferenceインスタンスを取得
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        this.mapStr = pref.getString("list_preference", "kyoto");
        mMapSurfaceView.changeMapPattern(mapStr);

        //スタート地点の変更
        if(mapStr.equals("kyoto")) {
            mSensorAdapter.setPlatformPoint(35.027578, 135.783206);//京都大学工学部電気総合館
        }else if(mapStr.equals("biwako")){
            mSensorAdapter.setPlatformPoint(35.294170, 136.254422);//platform
        }else if(mapStr.equals("kasaoka")){
            mSensorAdapter.setPlatformPoint(34.478106, 133.490225);//北端
        }

        String lapseStr = pref.getString(SettingPrefActivity.PREF_KEY_LAPSE, "0.12");
        String standardStr = pref.getString(SettingPrefActivity.PREF_KEY_STANDARD, "1013.25");
        atmLapse = Double.parseDouble(lapseStr);
        atmStandard = Double.parseDouble(standardStr);

        mMapSurfaceView.setPressureParam(atmStandard, atmLapse);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceivedDataAdapter.stop();
        mSensorAdapter.stopSensor();

        //3:スレッドストップ
        mLoggerThread.stopRunning();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(MainActivity.this, SettingPrefActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class LoggerThread extends Thread{
        SensorAdapter mSensorAdapter;
        ReceivedDataAdapter mReceivedDataAdapter;
        Handler handler = new Handler();


        private Logger mLogger;
        private boolean running = true;

        private double atmStandard, atmLapse;

        public LoggerThread(SensorAdapter mSensorAdapter, ReceivedDataAdapter mReceivedDataAdapter){
            this.mSensorAdapter = mSensorAdapter;
            this.mReceivedDataAdapter = mReceivedDataAdapter;

            mLogger = new Logger("ALL","androidTime,mbedTime,latitude,longitude,bearing,speed,altitude,straightDist,integralDist," +
                    "yaw,pitch,roll,airspeed,cadence,ultrasonic,atmpressure,elevator,rudder,trim,"+"ultsonicvoltage,cadencevoltage,servovoltage");
        }

        public void start(){
            new Thread(this).start();
        }

        public void stopRunning() {
            running = false;
        }

        @Override
        public void run(){
            while(running){
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mLogger.appendData(System.currentTimeMillis() + ","
                                + String.valueOf(mReceivedDataAdapter.getTime()) + ","
                                + String.valueOf(mSensorAdapter.getLatitude()) + ","
                                + String.valueOf(mSensorAdapter.getLongitude()) + ","
                                + String.valueOf(mSensorAdapter.getBearing()) + ","
                                + String.valueOf(mSensorAdapter.getSpeed()) + ","
                                + String.valueOf(mSensorAdapter.getAltitude()) + ","
                                + String.valueOf(mSensorAdapter.getStraightDistance()) + ","
                                + String.valueOf(mSensorAdapter.getIntegralDistance()) + ","
                                + String.valueOf(mSensorAdapter.getYaw()) + ","
                                + String.valueOf(mSensorAdapter.getPitch()) + ","
                                + String.valueOf(mSensorAdapter.getRoll()) + ","
                                + String.valueOf(mReceivedDataAdapter.getAirspeed()) + ","
                                + String.valueOf(mReceivedDataAdapter.getCadence()) + ","
                                + String.valueOf(mReceivedDataAdapter.getUltsonic()) + ","
                                + String.valueOf(mReceivedDataAdapter.getAtmpress()) + ","
                                + String.valueOf(mReceivedDataAdapter.getElevator()) + ","
                                + String.valueOf(mReceivedDataAdapter.getRudder()) + ","
                                + String.valueOf(mReceivedDataAdapter.getTrim()) + ","
                                + String.valueOf(mReceivedDataAdapter.getUltsonicVoltage()) + ","
                                + String.valueOf(mReceivedDataAdapter.getCadenceVoltage()) + ","
                                + String.valueOf(mReceivedDataAdapter.getServoVoltage())) ;

                        //sound.set(mSensorAdapter.getRoll(), 40, 60);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
