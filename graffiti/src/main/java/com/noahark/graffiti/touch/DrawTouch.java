package com.noahark.graffiti.touch;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.step.DrawpelStep;


//��ͼԪ������
public class DrawTouch extends Touch 
{
	
	protected PointF downPoint;
	protected PointF movePoint;
	protected Pel    newPel;
	protected static Paint paint;
	
	static 
	{
		// ��һ�����󴴽�ʱ�����ʼ����
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.parseColor("#ff298ecb"));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND); 
		paint.setStrokeWidth(1);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}
		
	protected DrawTouch()
	{
		super();
	
		downPoint=new PointF();
		movePoint=new PointF();
	}
	
	@Override
	public void down1()
	{
		super.down1();
		downPoint.set(curPoint);
	}	
	
	public void move()
	{
		super.move();
	}
	@Override
	public void up()
	{	
		//������Ļ����
		if(isNeedToOpenTools() == false)
		{
			//�ö���ͼԪ��·�������򣬻���,����
			(newPel.region).setPath(newPel.path, clipRegion);
			(newPel.paint).set(paint);
	
			/**
			 * ���²���
			 */
			
			//1.���»��õ�ͼԪ����ͼԪ������
			pelList.add(newPel);
			
			//2.��װ�õ�ǰ���� �ڵĲ���
			undoStack.push(new DrawpelStep(newPel));//���á�����ѹ��undoջ
	
			//3.�����ػ�λͼ
			CanvasView.setSelectedPel(selectedPel = null);//�ղŻ���ͼԪʧȥ����
			updateSavedBitmap();//�ػ�λͼ	
		}
	}
	
	public boolean isNeedToOpenTools()
	{
		if(dis < 10f)
		{	
			dis=0;
			MainActivity.openTools();
			control=true;
			
			return true;
		}
		else
		{
			dis=0;
			return false;
		}
	}
	
	//��õ�ǰ����
	public static Paint getCurPaint()
	{
		return paint;
	}
}
