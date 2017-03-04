package com.noahark.graffiti.utils;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * Created by chicken on 2016/11/18.
 */
public class GpsEngine {


    public static boolean m_bKeyRight = true;
    public static BMapManager mBMapManager = null;

    /**
     * �ٶȵ�ͼGps��������
     */
    public static void initGpsEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
    }

    // �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(), "���������������",
                        Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            //����ֵ��ʾkey��֤δͨ��
            if (iError != 0) {
                //��ȨKey����
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                        "�����������������Ƿ�����!", Toast.LENGTH_SHORT).show();
                m_bKeyRight = false;
            }
            else{
                m_bKeyRight = true;
                Toast.makeText(GraffitiApplication.getInstance().getApplicationContext(),
                        "key��֤�ɹ�", Toast.LENGTH_LONG).show();
            }
        }
    }

}
