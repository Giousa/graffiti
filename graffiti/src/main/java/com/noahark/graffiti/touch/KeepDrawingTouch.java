package com.noahark.graffiti.touch;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.extra.Pel;

import android.R.drawable;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

public class KeepDrawingTouch extends Touch
{
	private PointF downPoint;
	public  static Pel newPel;
	
	public KeepDrawingTouch()
	{
		super();
		downPoint=new PointF();
	}
	
	// ��һֻ��ָ����
	@Override
	public void down1()
	{
		downPoint.set(curPoint);
		updateSavedBitmap();
		
		//�����ٴ�ȷ�϶Ի���
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{	
				(MainActivity.lastPoint).set(downPoint);
				MainActivity.registerKeepdrawingSensor(null);
				Toast.makeText(MainActivity.getContext(), "�ڶ��ֻ���ͼ�ɣ���һ������ֹͣŶ~", Toast.LENGTH_SHORT).show();
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				Toast.makeText(MainActivity.getContext(), "������ѡ�����", Toast.LENGTH_SHORT).show();
			}		
		}
		
		//ʵ����ȷ�϶Ի���
		Builder dialog=new AlertDialog.Builder(MainActivity.getContext());
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setMessage("��ȷ����("+Integer.toString((int)downPoint.x)+","+Integer.toString((int)downPoint.y)+")Ϊ��ʼ�㲢��ʼ������ͼ��");
		dialog.setPositiveButton("ȷ��", new okClick());
		dialog.setNegativeButton("ȡ��", new cancelClick());
		dialog.create();
		dialog.show();
	}
	
	@Override
	public void updateSavedBitmap()
	{
		super.updateSavedBitmap();
		
		//��ȡ��ʼ���־ͼƬ
		BitmapDrawable startFlag=(BitmapDrawable)MainActivity.getContext().getResources().getDrawable(R.drawable.img_startflag);
		savedCanvas.drawBitmap(startFlag.getBitmap(), downPoint.x, downPoint.y, null);	
	}
}
