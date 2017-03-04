package com.noahark.graffiti.ui.view;

import com.noahark.graffiti.ui.activity.DrawPictureActivity;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.touch.DrawPictureTouch;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawPictureView extends View
{
	private Bitmap savedBitmap;
	private DrawPictureTouch touch;
	private PointF textPoint;//��������
	private PointF centerPoint;//��������
	private int contentId;
	private Bitmap content=null;
	
	public DrawPictureView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);

		touch=new DrawPictureTouch();
		textPoint=new PointF();
		centerPoint=new PointF();
		savedBitmap=Bitmap.createScaledBitmap(CanvasView.savedBitmap,CanvasView.CANVAS_WIDTH,CanvasView.CANVAS_HEIGHT,true);
		
		textPoint.set(CanvasView.CANVAS_WIDTH/2.5f,CanvasView.CANVAS_HEIGHT/2.5f);
		centerPoint.set(textPoint);
	}
	
	//�����¼�
	public boolean onTouchEvent(MotionEvent event) 
	{
		touch.setCurPoint(new PointF(event.getX(0),event.getY(0)));
		
		//�ڶ�ֻ��ָ���꣨�����ڵڶ�ֻ��ָ��û����ʱ�����쳣��
		try{touch.setSecPoint(new PointF(event.getX(1),event.getY(1)));}
		catch(Exception e){touch.setSecPoint(new PointF(1,1));}
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
			case MotionEvent.ACTION_DOWN:// ��һֻ��ָ����
			{
				if(DrawPictureActivity.topToolbar.getVisibility() == View.VISIBLE)
				{
					DrawPictureActivity.closeTools();
					touch.dis=Float.MAX_VALUE;
				}

				touch.down1();
			}break;
			case MotionEvent.ACTION_POINTER_DOWN:// �ڶ�����ָ����
			{
				touch.down2();
			}break;
			case MotionEvent.ACTION_MOVE:
			{
				touch.move();
			}break;
			case MotionEvent.ACTION_UP:// ��һֻ��ָ̧��
			case MotionEvent.ACTION_POINTER_UP://�ڶ�ֻ��̧��
			{
				touch.up();
			}break;
		}
		invalidate();
	
		return true;
	}

	//�ػ�
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(savedBitmap, 0, 0, new Paint());// ������ͼԪ
		
		//��ֹ��û�еõ�ͼƬʱ�ĵ�һ��ˢ��
		if(content != null)
		{
			canvas.translate(touch.getDx(),touch.getDy());
			canvas.scale(touch.getScale(), touch.getScale(),centerPoint.x,centerPoint.y);
			canvas.rotate(touch.getDegree(),centerPoint.x,centerPoint.y);
			canvas.drawBitmap(content,textPoint.x, textPoint.y, null);
		}
	}
	
	public void setContentAndCenterPoint(int contentId)
	{	
		this.contentId=contentId;
		ensureBitmapRecycled(content);
		this.content=BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
				contentId);
		centerPoint.set(textPoint.x + content.getWidth()/2,textPoint.y+content.getHeight()/2);
	}
	
	public static void ensureBitmapRecycled(Bitmap bitmap) //ȷ������λͼ�Ѿ�����
	{
		if(bitmap != null && !bitmap.isRecycled())	
			bitmap.recycle();
	}
	
	public DrawPictureTouch getTouch()
	{
		return touch;
	}
	
	public PointF getTextPoint()
	{
		return textPoint;
	}
	
	public PointF getCenterPoint()
	{
		return centerPoint;
	}
	
	public int getContentId()
	{
		return contentId;
	}
	
	public Bitmap getContent()
	{
		return content;
	}
}
