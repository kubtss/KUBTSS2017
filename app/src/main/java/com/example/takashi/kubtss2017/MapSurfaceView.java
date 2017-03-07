package com.example.takashi.kubtss2017;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by takashi on 2016/03/01.
 */
public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorAdapter mSensorAdapter;
    private ReceivedDataAdapter mReceivedDataAdapter;

    private MeterDrawer mMeterDrawer = null;

    private SensorManager mSensorManager;
    private LocationManager mLocationManager;
    private boolean runflg = true;
    private boolean sleepflg = false;

    private Bitmap bmpKyoto, bmpBiwako, bmpKasaoka, bmpMap;// 表示する画像
    Rect srcKyoto0, srcKyoto1, srcKyoto2, srcKyoto3, srcKyoto4, dstKyoto;
    Rect srcBiwako0, srcBiwako1, srcBiwako2, srcBiwako3, srcBiwako4, dstBiwako; //切り取る画像のobject
    Rect[] srcBiwako;
    Rect srcKasaoka0, srcKasaoka1, srcKasaoka2, srcKasaoka3, srcKasaoka4, dstKasaoka;
    Rect srcMap0, srcMap1, srcMap2, srcMap3, srcMap4, dstMap;

    int screenwidth,screenheight;
    String mapStr;

    Paint paint;

    Handler handler;

    public double startLongi, startLati, endLongi, endLati;
    public int startScreenX, startScreenY, endScreenX, endScreenY;

    public double  endLongi2, endLati2;
    public int endScreenX2, endScreenY2;

    public int mapwidth, mapheight;
    public int maptop,mapbottom,mapleft,mapright;

    public double atmLapse, atmStandard;
    //コンストラクタ
    public MapSurfaceView(Context context, SensorManager mSensorManager, LocationManager mLocationManager,SensorAdapter mSensorAdapter,
                          ReceivedDataAdapter mReceivedDataAdapter,int width, int height) {
        super(context);


        srcBiwako = new Rect[16];//4倍拡大用の配列


        this.mLocationManager=mLocationManager;
        this.mSensorManager = mSensorManager;
        this.mSensorAdapter = mSensorAdapter;
        this.mReceivedDataAdapter = mReceivedDataAdapter;

        this.screenwidth = width;
        this.screenheight = height;

        getHolder().addCallback(this);

        bmpKyoto = BitmapFactory.decodeResource(getResources(), R.drawable.kyoto_701_409);
        srcKyoto0 = new Rect(0, 0, (int)Math.floor(bmpKyoto.getWidth() * 1.0),(int)Math.floor(bmpKyoto.getHeight() * 1.0));
        srcKyoto1 = new Rect((int)Math.floor(bmpKyoto.getWidth() / 2),(int)Math.floor(bmpKyoto.getHeight() / 2),
                (int)Math.floor(bmpKyoto.getWidth() * 1.0),(int)Math.floor(bmpKyoto.getHeight() * 1.0));
        srcKyoto2 = new Rect((int)Math.floor(bmpKyoto.getWidth() / 2), 0,
                (int)Math.floor(bmpKyoto.getWidth() * 1.0),(int)Math.floor(bmpKyoto.getHeight() / 2));
        srcKyoto3 = new Rect(0 ,(int)Math.floor(bmpKyoto.getHeight() / 2),
                (int)Math.floor(bmpKyoto.getWidth() / 2),(int)Math.floor(bmpKyoto.getHeight() * 1.0));
        srcKyoto4 = new Rect(0, 0, (int)Math.floor(bmpKyoto.getWidth() / 2),(int)Math.floor(bmpKyoto.getHeight() / 2));

        bmpBiwako = BitmapFactory.decodeResource(getResources(), R.drawable.biwa_circle_870_592);
        srcBiwako0 = new Rect(0, 0, (int)Math.floor(bmpBiwako.getWidth() * 1.0),(int)Math.floor(bmpBiwako.getHeight() * 1.0));
        srcBiwako1 = new Rect((int)Math.floor(bmpBiwako.getWidth() / 2),(int)Math.floor(bmpBiwako.getHeight() / 2),
                (int)Math.floor(bmpBiwako.getWidth() * 1.0),(int)Math.floor(bmpBiwako.getHeight() * 1.0));
        srcBiwako2 = new Rect((int)Math.floor(bmpBiwako.getWidth() / 2), 0,
                (int)Math.floor(bmpBiwako.getWidth() * 1.0),(int)Math.floor(bmpBiwako.getHeight() / 2));
        srcBiwako3 = new Rect(0 ,(int)Math.floor(bmpBiwako.getHeight() / 2),
                (int)Math.floor(bmpBiwako.getWidth() / 2),(int)Math.floor(bmpBiwako.getHeight() * 1.0));
        srcBiwako4 = new Rect(0, 0, (int)Math.floor(bmpBiwako.getWidth() / 2),(int)Math.floor(bmpBiwako.getHeight() / 2));

        bmpKasaoka = BitmapFactory.decodeResource(getResources(), R.drawable.kasaoka_819_545);
        srcKasaoka0 = new Rect(0, 0, (int)Math.floor(bmpKasaoka.getWidth() * 1.0),(int)Math.floor(bmpKasaoka.getHeight() * 1.0));
        srcKasaoka1 = new Rect((int)Math.floor(bmpKasaoka.getWidth() / 2),(int)Math.floor(bmpKasaoka.getHeight() / 2),
                (int)Math.floor(bmpKasaoka.getWidth() * 1.0),(int)Math.floor(bmpKasaoka.getHeight() * 1.0));
        srcKasaoka2 = new Rect((int)Math.floor(bmpKasaoka.getWidth() / 2), 0,
                (int)Math.floor(bmpKasaoka.getWidth() * 1.0),(int)Math.floor(bmpKasaoka.getHeight() / 2));
        srcKasaoka3 = new Rect(0 ,(int)Math.floor(bmpKasaoka.getHeight() / 2),
                (int)Math.floor(bmpKasaoka.getWidth() / 2),(int)Math.floor(bmpKasaoka.getHeight() * 1.0));
        srcKasaoka4 = new Rect(0, 0, (int)Math.floor(bmpKasaoka.getWidth() / 2),(int)Math.floor(bmpKasaoka.getHeight() / 2));


        int mBlockX=(int) Math.floor(bmpBiwako.getWidth() / 4);
        int mBlockY=(int) Math.floor(bmpBiwako.getHeight() / 4);

        srcBiwako[0] = new Rect(0, 0, mBlockX, mBlockY);
        srcBiwako[1] = new Rect(mBlockX, 0, mBlockX*2, mBlockY);
        srcBiwako[2] = new Rect(mBlockX*2, 0, mBlockX*3, mBlockY);
        srcBiwako[3] = new Rect(mBlockX*3, 0, mBlockX*4, mBlockY);

        srcBiwako[4] = new Rect(0, mBlockY, mBlockX, mBlockY*2);
        srcBiwako[5] = new Rect(mBlockX, mBlockY, mBlockX*2, mBlockY*2);
        srcBiwako[6] = new Rect(mBlockX*2, mBlockY, mBlockX*3, mBlockY*2);
        srcBiwako[7] = new Rect(mBlockX*3, mBlockY, mBlockX*4, mBlockY*2);

        srcBiwako[8] = new Rect(0, mBlockY*2, mBlockX, mBlockY*3);
        srcBiwako[9] = new Rect(mBlockX, mBlockY*2, mBlockX*2, mBlockY*3);
        srcBiwako[10] = new Rect(mBlockX*2, mBlockY*2, mBlockX*3, mBlockY*3);
        srcBiwako[11] = new Rect(mBlockX*3, mBlockY*2, mBlockX*4, mBlockY*3);

        srcBiwako[12] = new Rect(0, mBlockY*3, mBlockX, mBlockY*4);
        srcBiwako[13] = new Rect(mBlockX, mBlockY*3, mBlockX*2, mBlockY*4);
        srcBiwako[14] = new Rect(mBlockX*2, mBlockY*3, mBlockX*3, mBlockY*4);
        srcBiwako[15] = new Rect(mBlockX*3, mBlockY*3, mBlockX*4, mBlockY*4);

        this.paint = new Paint();

        this.handler = new Handler();
    }
    // SurfaceView生成時に呼び出される(初期画面の描画)
    public void surfaceCreated(SurfaceHolder holder) {

        //初期値をnullにしておいて一回だけ作動させる。
        if(mMeterDrawer == null){
            // スレッド生成および開始
            mMeterDrawer = new MeterDrawer(mSensorAdapter,mReceivedDataAdapter,handler);
            mMeterDrawer.start();
        }
        sleepflg = false;//trueなら描画しない。Activityが切り替わる時の措置
    }

    public void changeMapPattern(String mapStr){
        this.mapStr = mapStr;
        if(mapStr.equals("kyoto")){
            bmpMap = bmpKyoto;
            srcMap0 = srcKyoto0; srcMap1 = srcKyoto1; srcMap2 = srcKyoto2; srcMap3 = srcKyoto3; srcMap4 = srcKyoto4;
            dstMap = dstKyoto;
            this.startLongi = 135.780865;//時計台
            this.startLati = 35.025874;
            this.endLongi = 135.772465;//今出川、橋の手前
            this.endLati = 35.028922;

            this.startScreenX = 720;
            this.startScreenY = 330;
            this.endScreenX = 105;
            this.endScreenY = 60;

        }else if (mapStr.equals("biwako")){
            bmpMap = bmpBiwako;
            srcMap0 = srcBiwako0; srcMap1 = srcBiwako1; srcMap2 = srcBiwako2; srcMap3 = srcBiwako3; srcMap4 = srcBiwako4;
            dstMap = dstBiwako;
            this.startLongi = 136.254422;//platform
            this.startLati = 35.294170;
            this.endLongi = 136.063992;//OkiIsland
            this.endLati = 35.205572;

            this.endLongi2 = 136.143569;
            this.endLati2 = 35.422128;

            this.startScreenX = 658;
            this.startScreenY = 340;
            this.endScreenX = 340;
            this.endScreenY = 520;

            this.endScreenX2 = 473;
            this.endScreenY2 = 78;

        }else if (mapStr.equals("kasaoka")){
            bmpMap = bmpKasaoka;
            srcMap0 = srcKasaoka0; srcMap1 = srcKasaoka1; srcMap2 = srcKasaoka2; srcMap3 = srcKasaoka3; srcMap4 = srcKasaoka4;
            dstMap = dstKasaoka;
            this.startLongi = 133.490225;//北端
            this.startLati = 34.478106;
            this.endLongi = 133.487115;//南端03
            this.endLati = 34.472819;

            this.startScreenX = 465;
            this.startScreenY = 105;
            this.endScreenX = 280;
            this.endScreenY = 480;
        }

        this.mapwidth = bmpMap.getWidth();
        this.mapheight = bmpMap.getHeight();

        this.mapleft = 0; this.maptop = 0; this.mapright = screenwidth; this.mapbottom = screenwidth * mapheight / mapwidth;
        dstMap = new Rect(mapleft, maptop, mapright, mapbottom);

        Log.d(TAG,"mapchange done");

    }

    //基準気圧と逓減率
    public void setPressureParam(double atmStandard, double atmLapse) {
        this.atmStandard = atmStandard;
        this.atmLapse = atmLapse;
    }

    // SurfaceView変更時に呼び出される(画面の更新処理)
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // SurfaceView破棄時に呼び出される(画面の削除、削除後の処理)
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 処理がない場合も必要
        runflg = true;     //trueの間threadがrunする
        sleepflg = true;   //trueの間描画処理を行わない
    }

    class MeterDrawer extends Thread {
        SensorAdapter mSensorAdapter;
        ReceivedDataAdapter mReceivedDataAdapter;
        Handler handler ;//= new Handler();

        int i = 0;
        int m =0 , l=0;

        int cx,cy,r;
        double rad,deg;
        double min,max;
        float line,bar;

        double latitude,longitude;
        double roll,switching,yaw,pitch,ultsonic,atmpress,airspeed,cadence,elevater,rudder,straight,integral,GpsCnt;
        int time, selector;
        int trim;
        int platformX, platformY, goalX, goalY, goalX2, goalY2;
        int mass;
        int MapPattern;

        double x,X,y,Y;//拡大前後の点

        double CurrentPointX, CurrentPointY;
        double LatestCurrentPointX, LatestCurrentPointY;

        ArrayList<Double> arrayLatitude;
        ArrayList<Double> arrayLongitude;

        private int presenthour, presentminute, presentsecond, presenttotalsecond;

        public int starthour, startminute, startsecond, starttotalsecond, isTimeStarted;

        //コンストラクタ
        public MeterDrawer(SensorAdapter mSensorAdapter,ReceivedDataAdapter mReceivedDataAdapter,Handler handler) {
            this.mSensorAdapter = mSensorAdapter;
            this.mReceivedDataAdapter = mReceivedDataAdapter;
            this.handler = handler;

            arrayLatitude = new ArrayList<Double>();
            arrayLongitude = new ArrayList<Double>();

            this.isTimeStarted = 0;
        }

        //メインスレッド
        synchronized public void run() {
            while (runflg) {
                // ハンドルクラスによるUI描画
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mMeterDrawer.setValue(mSensorAdapter.getLatitude(), mSensorAdapter.getLongitude(),
                                mSensorAdapter.getYaw(), mSensorAdapter.getRoll(), mSensorAdapter.getPitch(),
                                mSensorAdapter.getRoll(), mReceivedDataAdapter.getUltsonic(),
                                mReceivedDataAdapter.getAtmpress(), mReceivedDataAdapter.getAirspeed(),
                                mReceivedDataAdapter.getCadence(), mReceivedDataAdapter.getElevator(),
                                mReceivedDataAdapter.getRudder(), mReceivedDataAdapter.getTrim(),
                                mReceivedDataAdapter.getTime(), mReceivedDataAdapter.getSelector(),
                                mSensorAdapter.getStraightDistance(), mSensorAdapter.getIntegralDistance(),
                                mSensorAdapter.getGpsCnt());

                        if(!sleepflg) {
                            doDraw(getHolder());
                        }

                    }
                });
                try {
                    Thread.sleep(500);//500
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setMap(Canvas canvas,int pattern){
            switch(pattern){
                case 0: canvas.drawBitmap(bmpMap, srcMap0, dstMap, paint); break;
                case 1: canvas.drawBitmap(bmpMap, srcMap1, dstMap, paint); break;
                case 2: canvas.drawBitmap(bmpMap, srcMap2, dstMap, paint); break;
                case 3: canvas.drawBitmap(bmpMap, srcMap3, dstMap, paint); break;
                case 4: canvas.drawBitmap(bmpMap, srcMap4, dstMap, paint); break;

            }

            for(int j=-16; j<0; j++) {
                if(j==pattern)
                    canvas.drawBitmap(bmpMap, srcBiwako[-j-1], dstMap, paint);
            }

        }

        private void doDraw(SurfaceHolder holder) {

            Canvas canvas = holder.lockCanvas(); //描画の開始

            Calendar cal = Calendar.getInstance(); //日時の取得
            this.presenthour = cal.get(Calendar.HOUR_OF_DAY);
            this.presentminute = cal.get(Calendar.MINUTE);
            this.presentsecond = cal.get(Calendar.SECOND);
            this.presenttotalsecond = presenthour * 60 * 60 + presentminute * 60 + presentsecond;

            canvas.drawColor(Color.WHITE);

            arrayLatitude.add(latitude);
            arrayLongitude.add(longitude);

            this.CurrentPointX = endScreenX + (arrayLongitude.get(arrayLongitude.size() - 1) - endLongi) / (startLongi -endLongi)*(startScreenX - endScreenX);//最新のポイント
            this.CurrentPointY = endScreenY + (arrayLatitude.get(arrayLatitude.size() - 1) - endLati) / (startLati - endLati) * (startScreenY - endScreenY);

            if(mapStr.equals("kyoto")){
                bmpMap = bmpKyoto;
            }else if(mapStr.equals("biwako")){
                bmpMap = bmpBiwako;
            }else if(mapStr.equals("kasaoka")){
                bmpMap = bmpKasaoka;
            }

            mapwidth = bmpMap.getWidth();
            mapheight = bmpMap.getHeight();
            mapleft = 0; maptop = 0; mapright = screenwidth; mapbottom = screenwidth * mapheight / mapwidth;

/*
            if(pitch > 60){
                selector=2;
            }else if(pitch>35){
                selector = 1;
            }else{
                selector=0;
            }
            }*/
            if(selector == 0){
                this.MapPattern = 0;
            }else if((mapleft + mapright) / 2 <= CurrentPointX && (maptop + mapbottom) / 2 <= CurrentPointY && selector == 1){
                this.MapPattern = 1;
            }else if((mapleft + mapright) / 2 <= CurrentPointX && (maptop + mapbottom) / 2 >= CurrentPointY && selector == 1){
                this.MapPattern = 2;
            }else if((mapleft + mapright) / 2 >= CurrentPointX && (maptop + mapbottom) / 2 <= CurrentPointY && selector == 1){
                this.MapPattern = 3;
            }else if((mapleft + mapright) / 2 >= CurrentPointX && (maptop + mapbottom) / 2 >= CurrentPointY && selector == 1) {
                this.MapPattern = 4;
            }

            double sBlockX=(mapleft + mapright) / 4;
            double sBlockY=(maptop + mapbottom) / 4;
            if(selector==2) {

                if(0 <= CurrentPointX && CurrentPointX<=sBlockX  && 0<=CurrentPointY && CurrentPointY<=sBlockY)
                    this.MapPattern = -1;
                if(sBlockX <= CurrentPointX && CurrentPointX<=sBlockX*2  && 0<=CurrentPointY && CurrentPointY<=sBlockY)
                    this.MapPattern = -2;
                if(sBlockX*2 <= CurrentPointX && CurrentPointX<=sBlockX*3  && 0<=CurrentPointY && CurrentPointY<=sBlockY)
                    this.MapPattern = -3;
                if(sBlockX*3 <= CurrentPointX && CurrentPointX<=sBlockX*4  && 0<=CurrentPointY && CurrentPointY<=sBlockY)
                    this.MapPattern = -4;

                if(0 <= CurrentPointX && CurrentPointX<=sBlockX  && sBlockY<=CurrentPointY && CurrentPointY<=sBlockY*2)
                    this.MapPattern = -5;
                if(sBlockX <= CurrentPointX && CurrentPointX<=sBlockX*2  && sBlockY<=CurrentPointY && CurrentPointY<=sBlockY*2)
                    this.MapPattern = -6;
                if(sBlockX*2 <= CurrentPointX && CurrentPointX<=sBlockX*3  && sBlockY<=CurrentPointY && CurrentPointY<=sBlockY*2)
                    this.MapPattern = -7;
                if(sBlockX*3 <= CurrentPointX && CurrentPointX<=sBlockX*4  && sBlockY<=CurrentPointY && CurrentPointY<=sBlockY*2)
                    this.MapPattern = -8;

                if(0 <= CurrentPointX && CurrentPointX<=sBlockX  && sBlockY*2<=CurrentPointY && CurrentPointY<=sBlockY*3)
                    this.MapPattern = -9;
                if(sBlockX <= CurrentPointX && CurrentPointX<=sBlockX*2  && sBlockY*2<=CurrentPointY && CurrentPointY<=sBlockY*3)
                    this.MapPattern = -10;
                if(sBlockX*2 <= CurrentPointX && CurrentPointX<=sBlockX*3  && sBlockY*2<=CurrentPointY && CurrentPointY<=sBlockY*3)
                    this.MapPattern = -11;
                if(sBlockX*3 <= CurrentPointX && CurrentPointX<=sBlockX*4  && sBlockY*2<=CurrentPointY && CurrentPointY<=sBlockY*3)
                    this.MapPattern = -12;

                if(0 <= CurrentPointX && CurrentPointX<=sBlockX  && sBlockY*3<=CurrentPointY && CurrentPointY<=sBlockY*4)
                    this.MapPattern = -13;
                if(sBlockX <= CurrentPointX && CurrentPointX<=sBlockX*2  && sBlockY*3<=CurrentPointY && CurrentPointY<=sBlockY*4)
                    this.MapPattern = -14;
                if(sBlockX*2 <= CurrentPointX && CurrentPointX<=sBlockX*3  && sBlockY*3<=CurrentPointY && CurrentPointY<=sBlockY*4)
                    this.MapPattern = -15;
                if(sBlockX*3 <= CurrentPointX && CurrentPointX<=sBlockX*4  && sBlockY*3<=CurrentPointY && CurrentPointY<=sBlockY*4) {
                    this.MapPattern = -16;
                }

            }

            mMeterDrawer.setMap(canvas,MapPattern);

            for (int i=0; i<=arrayLongitude.size() - 1; i++) {
                paint.setColor(Color.RED);
                this.x = endScreenX + (arrayLongitude.get(i) - endLongi) / (startLongi -endLongi)*(startScreenX - endScreenX);
                this.y = endScreenY + (arrayLatitude.get(i) - endLati) / (startLati - endLati) * (startScreenY - endScreenY);

                //座標の変換
                if(MapPattern == 0) {
                    this.X = x;
                    this.Y = y;
                    this.platformX = startScreenX;
                    this.platformY = startScreenY;
                    this.goalX = endScreenX;
                    this.goalY = endScreenY;

                    this.goalX2 = endScreenX2;
                    this.goalY2 = endScreenY2;

                    this.LatestCurrentPointX = CurrentPointX;
                    this.LatestCurrentPointY = CurrentPointY;

                }
                if(MapPattern == 1 || MapPattern == 2){
                    this.X = mMeterDrawer.Clamp((mapleft + mapright) / 2, mapright, x) * (mapright - mapleft) + mapleft;
                    this.platformX = (int) (mMeterDrawer.Clamp((mapleft + mapright) / 2, mapright, startScreenX) * (mapright - mapleft) + mapleft);
                    this.goalX = (int) (mMeterDrawer.Clamp((mapleft + mapright) / 2, mapright, endScreenX) * (mapright - mapleft) + mapleft);
                    this.goalX2 = (int) (mMeterDrawer.Clamp((mapleft + mapright) / 2, mapright, endScreenX2) * (mapright - mapleft) + mapleft);
                    this.LatestCurrentPointX = mMeterDrawer.Clamp((mapleft + mapright) / 2, mapright, CurrentPointX) * (mapright - mapleft) + mapleft;
                }
                if(MapPattern == 3 || MapPattern == 4){
                    this.X = mMeterDrawer.Clamp(mapleft, (mapleft + mapright) / 2, x) * (mapright - mapleft) + mapleft;
                    this.platformX = (int) (mMeterDrawer.Clamp(mapleft, (mapleft + mapright) / 2, startScreenX) * (mapright - mapleft) + mapleft);
                    this.goalX = (int) (mMeterDrawer.Clamp(mapleft, (mapleft + mapright) / 2, endScreenX) * (mapright - mapleft) + mapleft);
                    this.goalX2 = (int) (mMeterDrawer.Clamp(mapleft, (mapleft + mapright) / 2, endScreenX2) * (mapright - mapleft) + mapleft);
                    this.LatestCurrentPointX = mMeterDrawer.Clamp(mapleft, (mapleft + mapright) / 2, CurrentPointX) * (mapright - mapleft) + mapleft;
                }
                if(MapPattern == 1 || MapPattern == 3){
                    this.Y = mMeterDrawer.Clamp((maptop + mapbottom) / 2, mapbottom, y) * (mapbottom - maptop) + maptop;
                    this.platformY = (int) (mMeterDrawer.Clamp((maptop + mapbottom) / 2, mapbottom, startScreenY) * (mapbottom - maptop) + maptop);
                    this.goalY = (int) (mMeterDrawer.Clamp((maptop + mapbottom) / 2, mapbottom, endScreenY) * (mapbottom - maptop) + maptop);
                    this.goalY2 = (int) (mMeterDrawer.Clamp((maptop + mapbottom) / 2, mapbottom, endScreenY2) * (mapbottom - maptop) + maptop);
                    this.LatestCurrentPointY = mMeterDrawer.Clamp((maptop + mapbottom) / 2, mapbottom, CurrentPointY) * (mapbottom - maptop) + maptop;
                }
                if(MapPattern == 2 || MapPattern == 4) {
                    this.Y = mMeterDrawer.Clamp(maptop, (maptop + mapbottom) / 2, y) * (mapbottom - maptop) + maptop;
                    this.platformY = (int) (mMeterDrawer.Clamp(maptop, (maptop + mapbottom) / 2, startScreenY) * (mapbottom - maptop) + maptop);
                    this.goalY = (int) (mMeterDrawer.Clamp(maptop, (maptop + mapbottom) / 2, endScreenY) * (mapbottom - maptop) + maptop);
                    this.goalY2 = (int) (mMeterDrawer.Clamp(maptop, (maptop + mapbottom) / 2, endScreenY2) * (mapbottom - maptop) + maptop);
                    this.LatestCurrentPointY = mMeterDrawer.Clamp(maptop, (maptop + mapbottom) / 2, CurrentPointY) * (mapbottom - maptop) + maptop;
                }

                if(MapPattern < 0) {
                    //関数化しような
                    this.X = mapright*((x-sBlockX*((-MapPattern-1)%4))/sBlockX);
                    this.Y = mapbottom*((y-sBlockY*((-MapPattern-1)/4))/sBlockY);

                    this.LatestCurrentPointX = mapright*((CurrentPointX-sBlockX*((-MapPattern-1)%4))/sBlockX);
                    this.LatestCurrentPointY = mapbottom*((CurrentPointY-sBlockY*((-MapPattern-1)/4))/sBlockY);



                    this.platformX = (int)(mapright*((startScreenX-sBlockX*((-MapPattern-1)%4))/sBlockX));
                    this.platformY = (int)(mapbottom*((startScreenY-sBlockY*((-MapPattern-1)/4))/sBlockY));

                    this.goalX = (int)(mapright*((endScreenX-sBlockX*((-MapPattern-1)%4))/sBlockX));
                    this.goalY = (int)(mapbottom*((endScreenY-sBlockY*((-MapPattern-1)/4))/sBlockY));

                    this.goalX2 = (int)(mapright*((endScreenX2-sBlockX*((-MapPattern-1)%4))/sBlockX));
                    this.goalY2 = (int)(mapbottom*((endScreenY2-sBlockY*((-MapPattern-1)/4))/sBlockY));
                }
                if(Y<=mapbottom)//履歴店の画面外への飛び出しを防ぐ、下のみ
                    canvas.drawCircle((float) X, (float) Y, 2, paint);
                //
            }

            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
/*            if(mapleft<=goalX && goalX<=mapright && maptop<=goalY && goalY<=mapbottom){
                canvas.drawLine((float)LatestCurrentPointX, (float)LatestCurrentPointY, (float)goalX, (float)goalY, paint);
            }else{//一旦下に線が飛び出る時のみ考えるとする
                canvas.drawLine((float)LatestCurrentPointX, (float)LatestCurrentPointY,
                        (float)(goalX + (LatestCurrentPointX-goalX)*(goalY-mapbottom)/(goalY-LatestCurrentPointY)), (float)mapbottom, paint);
            }
*/
          //  if(mapleft<=goalX2 && goalX2<=mapright && maptop<=goalY2 && goalY2<=mapbottom){
//                canvas.drawLine((float)LatestCurrentPointX, (float)LatestCurrentPointY, (float)goalX2, (float)goalY2, paint);
            /*}else{//一旦下に線が飛び出る時のみ考えるとする
                canvas.drawLine((float)LatestCurrentPointX, (float)LatestCurrentPointY,
                        (float)(goalX2 + (LatestCurrentPointX-goalX2)*(goalY2-mapbottom)/(goalY2-LatestCurrentPointY)), (float)mapbottom, paint);
            }*/



            paint.setColor(Color.RED);
            canvas.drawCircle((float) platformX, (float) platformY,2, paint);
            canvas.drawCircle((float) goalX, (float) goalY, 2, paint);

            maptop = 0;
            mapbottom = maptop + screenwidth * mapheight / mapwidth;

            if(screenwidth <= screenheight - mapbottom) {
                mass = screenwidth / 3;
            }else{
                mass = (screenheight - mapbottom) / 3;
            }

            mMeterDrawer.DrawYawMeter(canvas, (int) LatestCurrentPointX, (int) LatestCurrentPointY);

            if(latitude==0 || longitude==0){
                mMeterDrawer.DrawYawMeter(canvas, startScreenX, startScreenY);
            }

            mMeterDrawer.DrawSpeedMeter(canvas, 0, mapbottom, mass * 2, mapbottom + mass * 2);
            mMeterDrawer.DrawRollPitchMeter(canvas, mass, mapbottom, mass * 2, mapbottom + mass);
            mMeterDrawer.DrawRpmMeter(canvas, mass * 2, mapbottom, mass * 4, mapbottom + mass * 2);
            mMeterDrawer.DrawUltMeter(canvas, 0, mapbottom + mass, mass * 2, mapbottom + mass * 3);
            mMeterDrawer.DrawAltMeter(canvas, mass, mapbottom + mass, mass * 3, mapbottom + mass * 3);
  //          mMeterDrawer.DrawRddMeter(canvas, 0, mapbottom + mass * 2, mass, mapbottom + mass * 3);
   //         mMeterDrawer.DrawElvMeter(canvas, mass * 5 / 2, mapbottom + mass * 3 / 2, mass * 7 / 2, mapbottom + mass * 5 / 2);

            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            paint.setTextSize(60);

         //   if(pitch > 60) { //time start の条件
                if(isTimeStarted == 0) {
                    Calendar startcal = Calendar.getInstance();
                    this.starthour = startcal.get(Calendar.HOUR_OF_DAY);
                    this.startminute = startcal.get(Calendar.MINUTE);
                    this.startsecond = startcal.get(Calendar.SECOND);
                    this.starttotalsecond = starthour * 60 * 60 + startminute * 60 + startsecond;
                }
                isTimeStarted = 1;
           // }
            if(isTimeStarted == 1) {
                canvas.drawText(String.valueOf((int) ((Math.floor((double) ((presenttotalsecond - starttotalsecond) / 3600))))) + ":" +
                        String.valueOf((int) (Math.floor((double) (((presenttotalsecond - starttotalsecond) % 3600) / 60)))) + ":" +
                        String.valueOf((int) (Math.floor((double) ((((presenttotalsecond - starttotalsecond) % 3600) % 60) % 60)))), 0, 100, paint);
            }

            canvas.drawText(String.valueOf( trim), 0, 150, paint);
            paint.setTextSize(50);
            canvas.drawText("straight=" + String.valueOf((int) straight) + "m", mass * 2 , 50, paint);
            canvas.drawText("total=" + String.valueOf((int) integral) + "m", mass*2, 100, paint);

           // canvas.drawText("pattern=" + String.valueOf((int) MapPattern), mass*2+50, 150, paint);


            if(time != 0 && time / 1000 % 800 == 0){
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            }

            paint.setStrokeWidth(5);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);

            if(latitude == 0 && longitude == 0) {
                canvas.drawText("No Gps", 0, 200, paint);
            }


            //画面上のcanvasの座標を見たいとき、方眼上になる。
/*
            paint.setStrokeWidth(1);
            while(m<=700) {
                paint.setColor(Color.WHITE);
                canvas.drawLine(0, m, canvas.getWidth(), m , paint);//起点座標、終点座標
                if(m % 100 == 0){

                    paint.setStrokeWidth(3);
                    paint.setColor(Color.RED);
                    canvas.drawLine(0, m, canvas.getWidth(), m , paint);
                }
                m += 10;
            }
            while(l<=1300) {
                paint.setColor(Color.WHITE);
                canvas.drawLine(l, 0, l, canvas.getHeight(), paint);//起点座標、終点座標
                if(l % 100 == 0){
                    paint.setColor(Color.RED);
                    canvas.drawLine(l, 0, l, canvas.getWidth(), paint);
                }
                l += 10;
            }
            m=0;l=0;*/
            // 描画のおわり
            holder.unlockCanvasAndPost(canvas);
        }
        public void DrawOval(Canvas canvas, float Left,float Top,float Right,float Bottom,float StartAngle,float SweepAngle,boolean UseCenter){

            paint.setStrokeWidth(5);
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);

            RectF oval1 = new RectF(Left, Top, Right, Bottom);
            canvas.drawArc(oval1, StartAngle, SweepAngle, UseCenter, paint);

        }


        public void setValue(double Latitude,double Longitude,double Yaw, double Roll,double Pitch ,double Switching,
                             double Ultsonic,double Atmpress, double Airspeed, double Cadence,double Elevater,double Rudder,
                             int Trim, int Time,int Selector,double Straight,double Integral,double GpsCnt) {
            this.latitude = Latitude;
            this.longitude = Longitude;
            this.yaw = Yaw;
            this.roll = Roll;
            this.pitch = Pitch;
            this.switching =Switching;
            this.ultsonic = Ultsonic;
            this.atmpress = Atmpress;
            this.airspeed = Airspeed;
            this.cadence = Cadence;
            this.elevater = Elevater;
            this.rudder = Rudder;
            this.trim = Trim;
            this.time = Time;
            this.selector = Selector;
            this.straight = Straight;
            this.integral = Integral;
            this.GpsCnt = GpsCnt;
        }

        public void DrawCircleScale(Canvas canvas,int cx,int cy,int r,double startdeg, double enddeg,double intervaldeg){
            paint.setStrokeWidth(5);
            paint.setColor(Color.BLACK);
            this.deg = 0;
            for(deg=startdeg; deg<=enddeg; deg = deg + intervaldeg) {
                this.rad = Math.toRadians(deg);
                canvas.drawLine(cx + (r - 10) * (float) Math.cos(rad), cy - (r - 10) * (float) Math.sin(rad),
                        cx + (r + 10) * (float) Math.cos(rad), cy - (r + 10) * (float) Math.sin(rad), paint);//角度の増え方は単位円
            }
        }

        public void DrawLineScale(Canvas canvas,float LineTop,float LineBottom,float LineX,float LineInterval ){
            paint.setStrokeWidth(5);
            paint.setColor(Color.BLACK);
            canvas.drawLine(LineX, LineTop, LineX, LineBottom, paint);

            this.line=0;
            for(line=LineTop; line<=LineBottom; line = line + LineInterval) {
                canvas.drawLine(LineX-10,line,LineX+10,line, paint);
            }
        }

        public void DrawArrow(Canvas canvas,int startX,int startY,int stopX,int stopY){
            //矢の長さを定義(r)
            int R;
            double Rad;

            R = (int)(Math.sqrt((double)((startX-stopX)*(startX-stopX)+(startY-stopY)*(startY-stopY))));

            Rad = Math.atan2((double)(-stopY+startY),(double)(stopX-startX));
            canvas.drawLine(startX, startY, stopX, stopY, paint);

            canvas.drawLine(stopX, stopY,
                    (float) (startX + R * Math.cos(Rad) - R / 8 * Math.cos(Rad + Math.toRadians(40))),
                    (float) (startY - R * Math.sin(Rad) + R / 8 * Math.sin(Rad + Math.toRadians(40))), paint);
            canvas.drawLine(stopX, stopY,
                    (float) (startX + R * Math.cos(Rad) - R / 8 * Math.cos(Rad - Math.toRadians(40))),
                    (float) (startY - R * Math.sin(Rad) + R / 8 * Math.sin(Rad - Math.toRadians(40))), paint);
        }

        public void DrawSpeedMeter(Canvas canvas,int Left,int Top,int Right,int Bottom){
            mMeterDrawer.DrawOval(canvas, Left, Top, Right, Bottom, 180, 90, true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;

            this.r = cx - Left + 5;

            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r,90,180,10);

            this.min = 0;
            this.max = 10;
            this.deg = 90 * mMeterDrawer.Clamp(min,max,airspeed);
            this.rad = Math.toRadians(this.deg);

            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);

            mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx - r * (float) Math.cos(rad)), (int) (cy - r * (float) Math.sin(rad)));
            paint.setStrokeWidth(4);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(airspeed), Left + r / 2, Top + r / 2, paint);
            canvas.drawText("air", Left + r / 2 - 50, Top + r / 2 + 50, paint);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(min), Left, Bottom - r, paint);
            canvas.drawText(String.valueOf(max), Right - r - 50, Top + 30, paint);
        }
        public void DrawRollPitchMeter(Canvas canvas,int Left,int Top,int Right,int Bottom){
            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;
            this.r = cx - Left + 5;
            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r, 40, 140, 10);

            double rollmin = -50;double rollmax = 50;
            double pitchmin =-10;double pitchmax = 10;

            this.rad = Math.toRadians(roll);
            if(rollmin <= roll && roll <= rollmax) {
                paint.setStrokeWidth(10);
                paint.setColor(Color.RED);
                canvas.drawCircle(cx + r * (float) Math.sin(rad), cy - r * (float) Math.cos(rad), 10, paint);
            }

            pitch -= 41;

            int linetop = cy-r/4;
            int linebottom = Bottom;
            this.bar = (float)((linetop + linebottom) / 2 + (linebottom - linetop) / 2 * mMeterDrawer.Clamp(pitchmin,pitchmax,pitch));//-41は補正
            paint.setStrokeWidth(5);
            this.line=0;
            for(line=linetop; line<=linebottom; line = line + (linebottom - linetop)/6) {
                if(line <= linetop + (linebottom - linetop)/6*2){
                    paint.setColor(Color.BLUE);
                }else if(line == linetop + (linebottom - linetop)/6*3){
                    paint.setColor(Color.BLACK);
                }else{
                    paint.setColor(Color.BLUE);
                }
                canvas.drawLine(Left+10,line,Right-10,line, paint);
            }
            if(pitchmin <= pitch && pitch <= pitchmax) {
                paint.setColor(Color.RED);
                canvas.drawLine(Left + 10, bar, Right - 10, bar, paint);
            }
        }

        public void DrawRpmMeter(Canvas canvas, int Left,int Top,int Right,int Bottom){
            mMeterDrawer.DrawOval(canvas, Left, Top, Right, Bottom, 180, 90, true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;

            this.r = cx - Left + 5;

            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r,90,180, 10);

            this.min = 0;
            this.max = 120;
            this.deg = 90 * mMeterDrawer.Clamp(min,max,cadence);
            this.rad = Math.toRadians(this.deg);

            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);

            mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx - r * (float) Math.cos(rad)), (int) (cy - r * (float) Math.sin(rad)));
            paint.setStrokeWidth(4);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(cadence), Left + r / 2, Top + r / 2, paint);
            canvas.drawText("rpm", Left + r / 2, Top + r / 2 + 50, paint);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(min), Left, Bottom - r, paint);
            canvas.drawText(String.valueOf(max), Right - r - 50, Top + 30, paint);
        }

        public void DrawRddMeter(Canvas canvas, int Left, int Top,int Right,int Bottom){
            mMeterDrawer.DrawOval(canvas,Left,Top, Right,Bottom,180,180,true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;
            this.r = cx - Left ;
            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r, 0, 180, 20);

            this.min = -90;
            this.max = 90;

            this.deg = 90 * mMeterDrawer.Clamp(min,max,rudder);
            this.rad = Math.toRadians(deg);
            rad = -rad;
            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);
            mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx + r * (float) Math.sin(rad)), (int) (cy - r * (float) Math.cos(rad)));
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(min), Left, Bottom - r, paint);
            canvas.drawText(String.valueOf(max), Right - 70, Bottom - r, paint);
            canvas.drawText(String.valueOf(rudder), Left + r - 50, Bottom - r - 50, paint);
        }

        public void DrawElvMeter(Canvas canvas,int Left,int Top,int Right,int Bottom){
            mMeterDrawer.DrawOval(canvas, Left, Top, Right, Bottom, 90, 180, true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;
            this.r = cx - Left ;
            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r, 90, 270, 20);

            this.min = -90;
            this.max = 90;

            this.deg = 90 * mMeterDrawer.Clamp(min,max,elevater);
            this.rad = Math.toRadians(deg);

            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);
            mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx - r * (float) Math.cos(rad)), (int) (cy - r * (float) Math.sin(rad)));

            paint.setStrokeWidth(4);
            paint.setTextSize(30);
            paint.setColor(Color.BLACK);
            canvas.drawText("Elv", cx - 100, cy, paint);
            canvas.drawText("Elv", cx - 100, cy, paint);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(max), cx - 50, Top + 50, paint);
            canvas.drawText(String.valueOf(min), cx - 50, Bottom - 50, paint);
            canvas.drawText(String.valueOf(elevater),  cx - 100, cy + 50, paint);
        }
        public void DrawUltMeter(Canvas canvas,int Left,int Top,int Right,int Bottom){

            mMeterDrawer.DrawOval(canvas, Left, Top, Right, Bottom, 180, 90, true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;

            this.r = cx - Left + 5;

            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r,90,180,10);

            this.min = 0;
            this.max = 5;

            ultsonic -= 140;

            this.deg = 90 * mMeterDrawer.Clamp(min, max,ultsonic/100);//-170は補正
            this.rad = Math.toRadians(this.deg);

            if(min<=ultsonic/100 && ultsonic/100<=max) {
                paint.setStrokeWidth(10);
                paint.setColor(Color.BLUE);

                mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx - r * (float) Math.cos(rad)), (int) (cy - r * (float) Math.sin(rad)));
            }
            paint.setStrokeWidth(4);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.format("%.2f", ultsonic / 100), Left + r / 2, Top + r / 2, paint);
            canvas.drawText("m", Left + r / 2, Top + r / 2 + 50, paint);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(min), Left, Bottom - r, paint);
            canvas.drawText(String.valueOf(max), Right - r - 50, Top + 30, paint);
        }
        public void DrawAltMeter(Canvas canvas,int Left,int Top,int Right,int Bottom){

            mMeterDrawer.DrawOval(canvas, Left, Top, Right, Bottom, 180, 90, true);

            this.cx = (Left + Right) / 2;
            this.cy = (Top + Bottom) / 2;

            this.r = cx - Left + 5;

            mMeterDrawer.DrawCircleScale(canvas, cx, cy, r,90,180,10);

            this.min = 0;
            this.max = 10;


            this.deg = 90 * mMeterDrawer.Clamp(min,max,(atmStandard-atmpress)/atmLapse);
            this.rad = Math.toRadians(this.deg);

            if(min<=((atmStandard-atmpress)/atmLapse)+10 && ((atmStandard-atmpress)/atmLapse)+10<=max) {
                paint.setStrokeWidth(10);
                paint.setColor(Color.BLUE);
                mMeterDrawer.DrawArrow(canvas, cx, cy, (int) (cx + r * (float) Math.sin(rad)), (int) (cy - r * (float) Math.cos(rad)));
            }
            paint.setStrokeWidth(4);
            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.format("%.2f", ((atmStandard - atmpress) / atmLapse)+10), Left + r / 2, Top + r / 2, paint);
            canvas.drawText("m", Left + r / 2, Top + r / 2 + 50, paint);
            paint.setTextSize(30);
            canvas.drawText(String.valueOf(min), Left, Bottom - r, paint);
            canvas.drawText(String.valueOf(atmpress), Left + r/2, Bottom - r, paint);
            canvas.drawText(String.valueOf(max), Right - r - 50, Top + 30, paint);
        }

        public void DrawYawMeter(Canvas canvas,int startX, int startY){
            paint.setStrokeWidth(10);
            paint.setColor(Color.BLUE);
            mMeterDrawer.DrawArrow(canvas, startX, startY, (int) (startX + 100 * Math.cos(Math.toRadians(yaw - 90))),
                    (int) (startY + 100 * Math.sin(Math.toRadians(yaw - 90))));
        }

        public double Clamp(double Min,double Max,double Value){
            if(Min < 0){
                return(-1 + (Value-Min)/(Max-Min)*2);//ー１～１に圧縮
            }else{
                return(0 + (Value-Min)/(Max-Min));//０～１に圧縮
            }
        }
    }



}