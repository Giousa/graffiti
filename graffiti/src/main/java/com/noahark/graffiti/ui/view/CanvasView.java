package com.noahark.graffiti.ui.view;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import com.noahark.graffiti.snow.Snow;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.touch.CrossfillTouch;
import com.noahark.graffiti.touch.DrawFreehandTouch;
import com.noahark.graffiti.touch.DrawTouch;
import com.noahark.graffiti.touch.Touch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class CanvasView extends View 
{
	// ͼԪƽ������

	private float phase;// �������ʣ��任��λ�ã�
	public static Paint animPelPaint;// ����Ч������
	public static Paint drawPelPaint;// �����õĻ���
	private Paint drawTextPaint;
	private Paint drawPicturePaint;

	public static int CANVAS_WIDTH;//������
	public static int CANVAS_HEIGHT;//������

	public static Stack<Step> undoStack;//undoջ
	public static Stack<Step> redoStack;//redoջ	
	
	public static List<Pel> pelList;// ͼԪ����
	public static Region clipRegion; // �����ü�����
	public static Pel selectedPel = null; // ��ǰ��ѡ�е�ͼԪ
	public static Bitmap savedBitmap; // �ػ�λͼ
	private Canvas savedCanvas; //�ػ滭��
	public static Bitmap backgroundBitmap;
	public static Bitmap copyOfBackgroundBitmap;//ԭͼƬ��������ջ�ԭʱ��
	public static Bitmap originalBackgroundBitmap;

	public static Touch touch;//��������
	/*******************************************************************************/	
	//ѩ������
	
	int MAX_SNOW_COUNT = 50;
	// ѩ��ͼƬ
	Bitmap bitmap_snows = null;
	// ����
	private final Paint mPaint = new Paint();
	// �漴������
	private static final Random random = new Random();
	// ѩ����λ��
	private Snow[] snows = new Snow[MAX_SNOW_COUNT];
	// ��Ļ�ĸ߶ȺͿ��
	int view_height = 0;
	int view_width = 0;
	int MAX_SPEED = 25;
	boolean draw = false;
	private int gravity = 1  ;
	/*******************************************************************************/		
	Canvas cacheCanvas;
	public CanvasView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	
		//��ʼ���������Ϊ��Ļ���
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		CANVAS_WIDTH = wm.getDefaultDisplay().getWidth();
		CANVAS_HEIGHT = wm.getDefaultDisplay().getHeight();

		undoStack=new Stack<Step>();//��ʼ��undo redoջ
		redoStack=new Stack<Step>();
		pelList = new LinkedList<Pel>(); // ͼԪ������
		savedCanvas = new Canvas();
		
		clipRegion = new Region(); //��ȡ�����ü�����
		touch=new DrawFreehandTouch();//��ʼ��Ϊ�����ֻ����
		drawPelPaint=DrawFreehandTouch.getCurPaint();
		animPelPaint=new Paint(drawPelPaint);
		drawTextPaint=new Paint();
		drawTextPaint.setColor(DrawTouch.getCurPaint().getColor());
		drawTextPaint.setTextSize(50);
		drawPicturePaint=new Paint();
		
		initBitmap();
		updateSavedBitmap();
	}
	
	public void initBitmap()
	{
		clipRegion.set(new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT));
		BitmapDrawable backgroundDrawable=(BitmapDrawable)this.getResources().getDrawable(R.drawable.bg_canvas0);
		Bitmap scaledBitmap=Bitmap.createScaledBitmap(backgroundDrawable.getBitmap(),CANVAS_WIDTH, CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=scaledBitmap.copy(Config.ARGB_8888, true);
		ensureBitmapRecycled(scaledBitmap);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		cacheCanvas=new Canvas();
		cacheCanvas.setBitmap(backgroundBitmap);
	}
	//�����¼�
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(MainActivity.getSensorMode() == MainActivity.NOSENSOR
		&& MainActivity.curFragmentFlag == MainActivity.MAIN_FRAGMENT
		&& !MainActivity.drawerLayout.isDrawerOpen(Gravity.LEFT)) //�Ǵ�����ģʽ����Ӧ��Ļ
		{
			//��һֻ��ָ����
			touch.setCurPoint(new PointF(event.getX(0),event.getY(0)));
			
			//�ڶ�ֻ��ָ���꣨�����ڵڶ�ֻ��ָ��û����ʱ�����쳣��
			try{touch.setSecPoint(new PointF(event.getX(1),event.getY(1)));}
			catch(Exception e){touch.setSecPoint(new PointF(1,1));}
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) 
			{
				case MotionEvent.ACTION_DOWN:// ��һֻ��ָ����
					{
						if(MainActivity.topToolbarSclVi.getVisibility() == View.VISIBLE)
						{
							MainActivity.closeTools();
							touch.dis=Float.MAX_VALUE;
						}
							
						touch.down1();
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:// �ڶ�����ָ����
					touch.down2();
					break;
				case MotionEvent.ACTION_MOVE:
					touch.move();
					break;
				case MotionEvent.ACTION_UP:// ��һֻ��ָ̧��
				case MotionEvent.ACTION_POINTER_UP://�ڶ�ֻ��̧��
					touch.up();
					break;
			}
			invalidate();
		}
		
		return true;
	}

	//�ػ�
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(savedBitmap, 0, 0, new Paint());// ������ͼԪ
		if (selectedPel != null) 
		{
			if(touch.getClass().getSimpleName().equals("TransformTouch")) //ѡ��״̬�Ų�����̬����Ч��
			{
				setAnimPaint();
				canvas.drawPath(selectedPel.path, animPelPaint);
				invalidate();// ���ʶ���Ч��
			}
			else //��ͼ״̬��������̬����Ч��
			{
				canvas.drawPath(selectedPel.path, drawPelPaint);
			}
		}
//		else
//		{
//			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.brush); 
//			
//			
//			canvas.setBitmap(savedBitmap);
//			canvas.drawBitmap(bitmap, touch.curPoint.x, touch.curPoint.y, DrawTouch.getCurPaint());
//		}
		
		/**********************************************************************/
		//ѩ������
		if(draw)
		{
			int outOfBoundCount = 0;
			for (int i = 0; i < MAX_SNOW_COUNT; i += 1) 
			{
				//�ж��Ƿ�����ʾ����
				if (snows[i].coordinate.x > view_width || snows[i].coordinate.y > view_height) 
				{
					outOfBoundCount++;
					//������е�ѩ����������ʾ�����ˣ����´ξͲ���Ҫ�ٻ���ѩ����
					if(outOfBoundCount >= MAX_SNOW_COUNT)
					{
						setStatus(false);
					}
					continue;
				}
				//Ϊѩ������������
				snows[i].speed +=gravity;
				// ѩ��������ٶ�
				snows[i].coordinate.y += snows[i].speed;
				//ѩ��Ʈ����Ч��
	
				canvas.drawBitmap(bitmap_snows, ((float) snows[i].coordinate.x),
						((float) snows[i].coordinate.y), mPaint);
			}
		}
		/**********************************************************************/
	}

	/*
	 * �Զ����Ա����
	 */
	public void updateSavedBitmap() //�����ػ汳��λͼ�ã����ҽ���ѡ���ͼԪ�б仯��ʱ��ŵ��ã�
	{
		//��������λͼ
		ensureBitmapRecycled(savedBitmap);
		savedBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);//�ɻ���������������λͼ
		savedCanvas.setBitmap(savedBitmap);

		//����selectedPel�������ͼԪ
		drawPels();
		
		invalidate();
	}
	
	public void drawPels()
	{
		ListIterator<Pel> pelIterator = pelList.listIterator();// ��ȡpelList��Ӧ�ĵ�����ͷ���
		while (pelIterator.hasNext()) 
		{
			Pel pel = pelIterator.next();
			
			//�����ı�ͼԪ
			if(pel.text != null)
			{
				Text text = pel.text;
				savedCanvas.save();
				savedCanvas.translate(text.getTransDx(),text.getTransDy());
				savedCanvas.scale(text.getScale(),text.getScale(), text.getCenterPoint().x,text.getCenterPoint().y);
				savedCanvas.rotate(text.getDegree(),text.getCenterPoint().x,text.getCenterPoint().y);
				savedCanvas.drawText(text.getContent(),text.getBeginPoint().x,text.getBeginPoint().y, text.getPaint());
				savedCanvas.restore();
			}
			else if(pel.picture != null)
			{
				Picture picture = pel.picture;
				savedCanvas.save();
				savedCanvas.translate(picture.getTransDx(),picture.getTransDy());
				savedCanvas.scale(picture.getScale(),picture.getScale(), picture.getCenterPoint().x,picture.getCenterPoint().y);
				savedCanvas.rotate(picture.getDegree(),picture.getCenterPoint().x,picture.getCenterPoint().y);
				savedCanvas.drawBitmap(picture.createContent(),picture.getBeginPoint().x,picture.getBeginPoint().y, drawPicturePaint);
				savedCanvas.restore();
			}
			else if(!pel.equals(selectedPel))//����ѡ�е�ͼԪ
				savedCanvas.drawPath(pel.path, pel.paint);
		}
	}
	
	// �������ʸ���
	private void setAnimPaint() 
	{
		phase++; // ����λ

		Path p = new Path();
		p.addRect(new RectF(0, 0, 6, 3), Path.Direction.CCW); // ·����Ԫ�Ǿ��Σ�Ҳ����Ϊ��Բ��
		PathDashPathEffect effect = new PathDashPathEffect(p, 12, phase, // ����·��Ч��
				PathDashPathEffect.Style.ROTATE);
		animPelPaint.setColor(Color.BLACK);
		animPelPaint.setPathEffect(effect);
	}
	
	/**
	 * get()����:��ȡCanvasView��ָ����Ա
	 */
	public static int getCanvasWidth()
	{
		return CANVAS_WIDTH;
	}
	
	public static int getCanvasHeight()
	{
		return CANVAS_HEIGHT;
	}	

	public static Region getClipRegion()
	{
		return clipRegion;
	}
	
	public static List<Pel> getPelList()
	{
		return pelList;
	}
	
	public static Pel getSelectedPel()
	{
		return selectedPel;
	}	
	
	public static Bitmap getSavedBitmap()
	{
		return savedBitmap;
	}

	public static Bitmap getBackgroundBitmap()
	{
		return backgroundBitmap;
	}
	
	public static Bitmap getCopyOfBackgroundBitmap()
	{
		return copyOfBackgroundBitmap;
	}
	
	public static Bitmap getOriginalBackgroundBitmap()
	{
		return originalBackgroundBitmap;
	}
	
	public static Touch getTouch()
	{
		return touch;
	}
	
	public static Stack<Step> getUndoStack()
	{
		return undoStack;
	}	

	public static Stack<Step> getRedoStack()
	{
		return redoStack;
	}	
	/*
	 * set()����:����CanvasView��ָ����Ա
	 */	
	public static void setSelectedPel(Pel pel)
	{
		selectedPel=pel;
	}	
	
	public static void setSavedBitmap(Bitmap bitmap)
	{
		savedBitmap=bitmap;
	}
	
	public void setBackgroundBitmap(int id) //�����ṩѡ��ı���ͼƬ������
	{
		BitmapDrawable backgroundDrawable=(BitmapDrawable)this.getResources().getDrawable(id);
		Bitmap offeredBitmap=backgroundDrawable.getBitmap();
		
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(offeredBitmap,CANVAS_WIDTH, CANVAS_HEIGHT,true);
		
		
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//����������´�ӡ
		updateSavedBitmap();
	}	
	
	public void setBackgroundBitmap(Bitmap photo)//��ͼ������յõ��ı���ͼƬ������
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(photo,CANVAS_WIDTH,CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		ensureBitmapRecycled(originalBackgroundBitmap);
		originalBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//����������´�ӡ
		updateSavedBitmap();
	}
	
	public void setProcessedBitmap(Bitmap imgPro)//���ô�����ͼƬ��Ϊ����
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=Bitmap.createScaledBitmap(imgPro,CANVAS_WIDTH,CANVAS_HEIGHT,true);
		
		ensureBitmapRecycled(copyOfBackgroundBitmap);
		copyOfBackgroundBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//����������´�ӡ
		updateSavedBitmap();
	}
	
	public void setBackgroundBitmap() //��ջ���ʱ��֮ǰ����ĸ���������Ϊ�ػ棨ȥ����䣩
	{
		ensureBitmapRecycled(backgroundBitmap);
		backgroundBitmap=copyOfBackgroundBitmap.copy(Config.ARGB_8888, true);
		
		CrossfillTouch.reprintFilledAreas(backgroundBitmap);//����������´�ӡ
		updateSavedBitmap();
	}
	
	public static void setTouch(Touch childTouch)
	{
		touch=childTouch;
	}
	
	public static void setCanvasSize(int width,int height)
	{
		CANVAS_WIDTH=width;
		CANVAS_HEIGHT=height;
	}
	
	public static void ensureBitmapRecycled(Bitmap bitmap) //ȷ������λͼ�Ѿ�����
	{
		if(bitmap != null && !bitmap.isRecycled())	
			bitmap.recycle();
	}
/*******************************************************************************/	
	/**
	 * ѩ������
	 */
	/**
	 * ������Ůɢ���Ļ�ͼƬ���ڴ���
	 * 
	 */
	public void LoadSnowImage() {
		Resources r = this.getContext().getResources();
		bitmap_snows = ((BitmapDrawable) r.getDrawable(R.drawable.snow))
				.getBitmap();
	}

	/**
	 * ���õ�ǰ�����ʵ�ʸ߶ȺͿ��
	 * 
	 */
	public void SetView(int height, int width) {
		view_height = height;
		view_width = width;

	}
	
	public void setStatus(boolean draw)
	{
		this.draw = draw;
	}
	
	public boolean getStatus()
	{
		return this.draw;
	}

	/**
	 * ��������ɻ����λ��
	 * 
	 */
	public void addRandomSnow() {
		for(int i =0; i< MAX_SNOW_COUNT;i++){
			snows[i] = new Snow(random.nextInt(view_width), view_height,-(random.nextInt(MAX_SPEED)));
		}
	}
}