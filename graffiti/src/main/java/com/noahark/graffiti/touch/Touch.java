package com.noahark.graffiti.touch;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.Step;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Region;

//������
public class Touch
{
	protected static Stack<Step> undoStack=CanvasView.getUndoStack();//��ȡundo
	protected static Stack<Step> redoStack=CanvasView.getRedoStack();//��ȡredo
	protected static Region clipRegion=CanvasView.getClipRegion(); // �����ü�����
	protected static List<Pel> pelList=CanvasView.getPelList(); // ͼԪ����// ��Ļ���
	protected Pel selectedPel; // ��ǰѡ��ͼԪ
	protected Bitmap savedBitmap; // ��ǰ�ػ�λͼ	
	protected Canvas savedCanvas; //�ػ滭��
	
	public PointF curPoint; //��ǰ��һֻ��ָ�¼�����
	public PointF secPoint; //��ǰ�ڶ�ֻ��ָ�¼�����
	public static Step   step=null; //��ǰtouch�¼������Ժ�Ҫѹ��undoջ�Ĳ���
	public float dis;//��������������x��y�����ϵ�ƫ������
	protected PointF frontPoint1,frontPoint2;
	
	//���⴦����
	public boolean control=false; //�����������л�ʱ�ö�
	public PointF  beginPoint;//�����ʱ�ö�
	public boolean hasFinished=false;
	public static  Matrix  oriMatrix;//���ͼƬ�ĳ�ʼ����
	/*
	 * ��̳еķ���
	 */
	public Touch() 
	{
		selectedPel = CanvasView.getSelectedPel();
		
		savedCanvas=new Canvas();
		curPoint=new PointF();
		secPoint=new PointF();
		beginPoint=new PointF();
		frontPoint1=new PointF();
		frontPoint2=new PointF();
		dis=0;
	}

	// ��һֻ��ָ����
	public void down1()
	{
		frontPoint1.set(curPoint);
	}

	// �ڶ�ֻ��ָ����
	public void down2()
	{
		frontPoint2.set(secPoint);
	}

	// ��ָ�ƶ�
	public void move()
	{
		float dis1=Math.abs(curPoint.x-frontPoint1.x)+Math.abs(curPoint.y-frontPoint1.y);
		float dis2=0;

		if(secPoint != null)
		{
			dis2=Math.abs(secPoint.x-frontPoint2.x)+Math.abs(secPoint.y-frontPoint2.y);
			frontPoint2.set(secPoint);
		}
		dis+=dis1+dis2;

		frontPoint1.set(curPoint);
	}

	// ��ָ̧��
	public void up()
	{
		if(dis < 10f)
		{	
			dis=0;
			MainActivity.openTools();
			return;
		}
		dis=0;	
	}
	
	//�����ػ汳��λͼ�ã����ҽ���ѡ���ͼԪ�б仯��ʱ��ŵ��ã�
	protected void updateSavedBitmap()
	{
		//��������λͼ
		Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();
		CanvasView.ensureBitmapRecycled(savedBitmap);
		savedBitmap=backgroundBitmap.copy(Config.ARGB_8888, true);//�ɻ���������������λͼ
		savedCanvas.setBitmap(savedBitmap); //�뻭��������ϵ

		drawPels();

		CanvasView.setSavedBitmap(savedBitmap); // �ı�CanvasView�е�savedBitmap�������
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
				savedCanvas.drawBitmap(picture.createContent(),picture.getBeginPoint().x,picture.getBeginPoint().y, null);
				savedCanvas.restore();
			}
			else if(!pel.equals(selectedPel))//����ѡ�е�ͼԪ
				savedCanvas.drawPath(pel.path, pel.paint);
		}
	}
		
	public void setCurPoint(PointF point)
	{
		curPoint.set(point);
	}
	
	public void setSecPoint(PointF point)
	{
		secPoint.set(point);
	}
	
	public static Step getStep() //���ص�ǰ��������Ĳ���
	{
		return step;
	}
}
