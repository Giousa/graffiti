package com.noahark.graffiti.touch;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

import android.graphics.PointF;
import android.util.Log;


public class DrawBesselTouch extends DrawTouch {
	private PointF  beginPoint,endPoint;
	
	public DrawBesselTouch()
	{
		super();
		
		beginPoint=new PointF();
		endPoint=new PointF();	
	}
	
	@Override
	public void down1()
	{
		super.down1();
		
		if (control == false) //���������߲�����������ͼԪ�Ŀ���
		{
			beginPoint.set(downPoint); //��¼���
			newPel=new Pel();
		} 
	}	
	
	@Override
	public void move()
	{
		super.move();
		
		movePoint.set(curPoint);
		
		(newPel.path).reset();
		if (control == false) //�����챴�������߲���
		{	
			(newPel.path).moveTo(beginPoint.x, beginPoint.y);
			(newPel.path).cubicTo(beginPoint.x, beginPoint.y, beginPoint.x, beginPoint.y,movePoint.x, movePoint.y);		
		}
		else 
		{
			(newPel.path).moveTo(beginPoint.x, beginPoint.y);
			(newPel.path).cubicTo(beginPoint.x, beginPoint.y, movePoint.x, movePoint.y, endPoint.x, endPoint.y);			
		}
		
		CanvasView.setSelectedPel(selectedPel = newPel);
	}
	
	@Override
	public void up()
	{
		ifNeedToOpenTools();
		
		PointF upPoint=new PointF();
		upPoint.set(curPoint);
		
		if (control == false) //�����챴�������߲������¼��ŵ�
		{	
			endPoint.set(upPoint);//��¼��ŵ�
			control = true;			
		} 
		else 
		{		
			newPel.closure=false;
			super.up(); //�����ö�
			
			control = false;
		}
	}
	
	public void ifNeedToOpenTools()
	{
		if(dis < 10f)
		{	Log.v("v","ttttttttttttttttttttt");
			dis=0;
			MainActivity.openTools();
			control=true;
			return;
		}
	}
}
