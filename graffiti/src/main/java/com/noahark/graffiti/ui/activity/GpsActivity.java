package com.noahark.graffiti.ui.activity;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.utils.GpsEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsActivity extends Activity
{
    private enum E_BUTTON_TYPE
    {
        LOC, COMPASS, FOLLOW
    }

    GpsEngine mGpsEngine;
    private E_BUTTON_TYPE mCurBtnType;

    // ·��
    private static Path path = new Path();
    // ��λ���
    LocationClient mLocClient;
    LocationData locData = null;
    public MyLocationListenner myListener = new MyLocationListenner();

    // ��λͼ��
    MyLocationOverlay myLocationOverlay = null;

    // ���������touch�¼���������̳У�ֱ��ʹ��MapView����
    MapView mMapView = null; // ��ͼView
    private MapController mMapController = null;

    // UI���
    OnCheckedChangeListener radioButtonListener = null;
    Button requestLocButton = null;
    boolean isRequest = false;// �Ƿ��ֶ���������λ
    boolean isFirstLoc = true;// �Ƿ��״ζ�λ

    // ����ͼ��
    private GraphicsOverlay graphicsOverlay;

    // �¾ɾ�γ��
    private double lastLong;
    private double lastLati;
    private double newLong;
    private double newLati;

    // ��ʱ�������޸��ٶ�bug
    private double tempLati;
    private double tempLong;

    private String softDir="/FreeGraffiti";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /**
         * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager. BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
         * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
         */

        if (GpsEngine.mBMapManager == null) {
            GpsEngine.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
             */

            GpsEngine.mBMapManager.init(new GpsEngine.MyGeneralListener());
        }

        setContentView(R.layout.relay_gps);
        requestLocButton = (Button) findViewById(R.id.btn_setlocation);
        mCurBtnType = E_BUTTON_TYPE.LOC;
        OnClickListener btnClickListener = new OnClickListener()
        {
            public void onClick(View v)
            {
                switch (mCurBtnType)
                {
                    case LOC:
                        // �ֶ���λ����
                        requestLocClick();
                        break;
                    case COMPASS:
                        myLocationOverlay.setLocationMode(LocationMode.NORMAL);
                        requestLocButton.setText("��λ");
                        mCurBtnType = E_BUTTON_TYPE.LOC;
                        break;
                    case FOLLOW:
                        myLocationOverlay.setLocationMode(LocationMode.COMPASS);
                        requestLocButton.setText("����");
                        mCurBtnType = E_BUTTON_TYPE.COMPASS;
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        // ��ͼ��ʼ��
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(false);

        graphicsOverlay = new GraphicsOverlay(mMapView);
        mMapView.getOverlays().add(graphicsOverlay);

        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        // ��λͼ���ʼ��
        myLocationOverlay = new MyLocationOverlay(mMapView);
        // ���ö�λ����
        myLocationOverlay.setData(locData);
        // ��Ӷ�λͼ��
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        // �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
        mMapView.refresh();

        /****************************************************************/
        // ��ͼ��ͼ����
        mMapView.regMapViewListener(mGpsEngine.mBMapManager, new MKMapViewListener() {

            @Override
            public void onClickMapPoi(MapPoi arg0) {
                // TODO �Զ����ɵķ������

            }

            @Override
            public void onGetCurrentMap(Bitmap bmp)
            {
                try
                {
                    // ˢ�±���
                    CanvasView canvasVi = MainActivity.getCanvasView();

                    Bitmap clipedBitmap=bmp.copy(Config.ARGB_8888, true);
                    CanvasView.ensureBitmapRecycled(bmp);

                    canvasVi.setBackgroundBitmap(clipedBitmap);
                    canvasVi.updateSavedBitmap();
                    Toast.makeText(MainActivity.getContext(), "�ѽ���ǰ��ͼ��Ϊ��������",Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    //��������쳣�޷�����ͼƬ��ֻ����
                    try
                    {
                        // ���浽SDcard
                        String mapPath = Environment.getExternalStorageDirectory().getPath() + softDir + "/" +getTimeName()+ ".jpg";
                        Bitmap bitmap = CanvasView.getSavedBitmap();
                        FileOutputStream fileOutputStream;
                        fileOutputStream = new FileOutputStream(mapPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
                        fileOutputStream.close();
                        Toast.makeText(MainActivity.getContext(), "��ͼ�ѱ���(" + mapPath + ")",Toast.LENGTH_SHORT).show();
                    }
                    catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onMapAnimationFinish()
            {
            }

            @Override
            public void onMapLoadFinish()
            {
            }

            @Override
            public void onMapMoveFinish()
            {
            }

        });
        /****************************************************************/
    }

    /**
     * �ֶ�����һ�ζ�λ����
     */
    public void requestLocClick()
    {
        isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(GpsActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT).show();
    }

    /**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
                return;

            // �õ����µľ�γ��
            newLati = locData.latitude = location.getLatitude();
            newLong = locData.longitude = location.getLongitude();

            // ����
            path.lineTo((float) newLati, (float) newLong);
            // �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
            locData.accuracy = location.getRadius();
            // �˴��������� locData�ķ�����Ϣ, �����λ SDK δ���ط�����Ϣ���û������Լ�ʵ�����̹�����ӷ�����Ϣ��
            locData.direction = location.getDerect();
            // ���¶�λ����
            myLocationOverlay.setData(locData);

            // �ڻ���ͼ���ϻ�������
            graphicsOverlay.setData(drawLine());

            // ����ͼ������ִ��ˢ�º���Ч
            mMapView.refresh();
            // ���ֶ�����������״ζ�λʱ���ƶ�����λ��
            if (isRequest || isFirstLoc)
            {
                // �ٶ��Դ��Ķ�̬�ƶ�
                mMapController.animateTo(new GeoPoint(
                        (int) (locData.latitude * 1e6),
                        (int) (locData.longitude * 1e6)));

                // �״ζ�λ�ľ�γ��
                tempLati = lastLong = locData.longitude;
                tempLong = lastLati = locData.latitude;
                // ����һ�����ƶ����״ζ�λ��
                path.moveTo((float) lastLati, (float) lastLong);

                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
                requestLocButton.setText("����");
                mCurBtnType = E_BUTTON_TYPE.FOLLOW;
            }
            // �״ζ�λ���
            // isFirstLoc = false;
        }

        private Graphic drawLine()
        {
            double mLat = lastLati;
            double mLon = lastLong;

            int lat = (int) (mLat * 1e6);
            int lon = (int) (mLon * 1e6);
            GeoPoint pt1 = new GeoPoint(lat, lon);

            if (isFirstLoc) {
                mLat = tempLati;
                mLon = tempLong;
                // �״ζ�λ���
                isFirstLoc = false;
            } else {
                mLat = newLati;
                mLon = newLong;
            }
            lat = (int) (mLat * 1e6);
            lon = (int) (mLon * 1e6);
            GeoPoint pt2 = new GeoPoint(lat, lon);

            // ������
            Geometry lineGeometry = new Geometry();
            // �趨���ߵ�����
            GeoPoint[] linePoints = new GeoPoint[2];
            linePoints[0] = pt1;
            linePoints[1] = pt2;
            lineGeometry.setPolyLine(linePoints);

            // ��ֵ���ֵ
            lastLong = newLong;
            lastLati = newLati;

            // �趨��ʽ
            Symbol lineSymbol = new Symbol();
            Symbol.Color lineColor = lineSymbol.new Color();
            lineColor.red = 139;
            lineColor.green = 0;
            lineColor.blue = 255;
            lineColor.alpha = 255;
            lineSymbol.setLineSymbol(lineColor, 10);
            // ����Graphic����
            Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
            return lineGraphic;
        }

        public void onReceivePoi(BDLocation poiLocation)
        {
            if (poiLocation == null)
            {
                return;
            }
        }
    }

    @Override
    protected void onPause()
    {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        // �˳�ʱ���ٶ�λ
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    public void onCancelBtn(View v)
    {
        class okClick implements DialogInterface.OnClickListener // ok
        {
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }

        class cancelClick implements DialogInterface.OnClickListener // cancel
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        }

        // ʵ����ȷ�϶Ի���
        Builder dialog = new AlertDialog.Builder(GpsActivity.this);
        dialog.setIcon(drawable.ic_dialog_info);
        dialog.setMessage("��ȷ��Ҫ�뿪��λ����ҳ�棿");
        dialog.setPositiveButton("ȷ��", new okClick());
        dialog.setNegativeButton("ȡ��", new cancelClick());
        dialog.create();
        dialog.show();
    }

    /****************************************************************/
    public void onClipScreenBtn(View v)
    {
        mMapView.getCurrentMap(); // ��ȡ��ǰ��ͼ,�ص���onGetCurrentMap()����
    }

    /****************************************************************/
    //��ȡ��ǰʱ��
    public static String getTimeName()
    {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }

}
