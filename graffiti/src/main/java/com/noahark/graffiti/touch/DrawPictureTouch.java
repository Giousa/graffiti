package com.noahark.graffiti.touch;

import android.graphics.PointF;

import com.noahark.graffiti.ui.activity.DrawPictureActivity;

public class DrawPictureTouch extends Touch
{
	
	private final float MIN_ZOOM=10; //��������	
	private final float MAX_DY=70; //��������ת�л�����
	private final int NONE = 0; // ƽ�Ʋ���
	private final int DRAG = 1; // ƽ�Ʋ���
	private final int ZOOM = 2; // ���Ų���
	private final int ROTATE = 3; // ��ת����
	private int mode = NONE; // ��ǰ��������
	
	private float dx,dy,oridx,oridy; //ƽ��ƫ����
	private float scale,oriscale; // ����ʱ��ָ�������ʱ�ľ���
	private float degree,oridegree;//��ת��
	
	private PointF downPoint;
	private float oriDist; // ����ʱ��ָ�������ʱ�ľ���
	
	
	public DrawPictureTouch()
	{
		downPoint=new PointF();
		
		dx=dy=oridx=oridy=0;
		scale=oriscale=1;
		degree=oridegree=0;
	}
	
	// ��һֻ��ָ����
	@Override
	public void down1()
	{
		super.down1();
		// ��ȡdown�¼��ķ���λ��
		downPoint.set(curPoint);
		mode = DRAG;
	}
	
	// �ڶ�ֻ��ָ����
	@Override
	public void down2()
	{
		oriDist = distance();
		if (oriDist > MIN_ZOOM) 
		{
			// ����С��50px����������
			mode = ZOOM;
		}
	}
	
	// ��ָ�ƶ�
	@Override
	public void move()
	{		
		super.move();
		if (mode == DRAG)// ƽ�Ʋ���
		{			
			dx = oridx + (curPoint.x - downPoint.x);//�������
			dy = oridy + (curPoint.y - downPoint.y);					
		} 
		else if (mode == ZOOM) // ���Ų���
		{	
			float newDist = distance();					
			float dy=Math.abs(curPoint.y-secPoint.y);//��ָ�Ĵ�ֱ���				
			if(dy >= MAX_DY)//�ж��Ƿ���Ҫת��Ϊ��תģʽ
			{
				//����׼������
				mode=ROTATE;
				downPoint.set(curPoint);
			}
			else if (newDist > MIN_ZOOM) 
			{
				//<100��Ȼ����������										
				scale = oriscale*(newDist / oriDist);
			}
		} 
		else if (mode == ROTATE) // ��ת����
		{			
			float dy=Math.abs(curPoint.y-secPoint.y);//��ָ�Ĵ�ֱ���					
			if(dy < MAX_DY)//�ж��Ƿ���Ҫת��Ϊ����ģʽ
			{
				mode=ZOOM;
				oriDist=distance();
			}
			else//>100��Ȼ��������ת
			{							
				degree=(oridegree % 360) + degree();
			}
		}
	}
	
	// ��ָ̧��
	@Override
	public void up()
	{
		if(dis < 10f)
		{	
			dis=0;
			DrawPictureActivity.openTools();
			return;
		}
		dis=0;
		
		//�ı����ֵ�����
		oridx=dx;oridy=dy;
		oriscale=scale;
		oridegree=degree;
		mode = NONE;
	}
	
	/*
	 * �Զ��庯��
	 */
	
	// ��������������֮��ľ���
	private float distance() 
	{
		float x = curPoint.x - secPoint.x;
		float y = curPoint.y - secPoint.y;
		return (float)Math.sqrt(x * x + y * y);
	}

	// ��ת�Ƕȵļ���
	private float degree() 
	{
		// �������down��ʱ�ľ���
		float x=curPoint.x-downPoint.x;
		float y=curPoint.y-downPoint.y;
		
		float arc=(float)Math.sqrt(x * x + y * y);//����
		float radius=distance()/2;//�뾶

		float degrees=(arc/radius)*(180/3.14f);
				
		return degrees;
	}
	
	public float getDx()
	{
		return dx;
	}
	
	public float getDy()
	{
		return dy;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public float getDegree()
	{
		return degree;
	}
	
	public void clear()
	{
		dx=dy=oridx=oridy=0;
		scale=oriscale=1;
		degree=oridegree=0;
	}
}
