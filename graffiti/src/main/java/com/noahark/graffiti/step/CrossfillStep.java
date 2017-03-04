package com.noahark.graffiti.step;

import java.util.List;
import java.util.ListIterator;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.touch.CrossfillTouch.ScanLine;

public class CrossfillStep extends Step 
{	
	private int   initColor; //��ʼɫ����Ϊ��ɫ����undo��ʱ��ָ��ɱ���ɫ����Ϊ�ǰ�ɫ����undo��ʱ��ָ��ɸ�ɫ��
	private int   fillColor; //���ɫ
	private List<ScanLine> scanLinesList; //ɨ��������
	
	//���ȶԻ����Ҫ���
	private ProgressDialog progressDialog;
	private Thread fillThread;  //����߳�
	final Handler handler=new Handler() //�߳���Ϣ������
	{
		public void handleMessage(Message msg)
		{		
			progressDialog.dismiss();
			canvasVi.updateSavedBitmap();//�ػ�λͼ
			
		    super.handleMessage(msg);
		} 
	};
	
	public CrossfillStep(Pel pel,int initC,int fillC,List<ScanLine> scanLL) 
	{
		super(pel);
		initColor=initC;
		fillColor=fillC;
		scanLinesList=scanLL;
	}
	
	@Override
	public void toUndoUpdate() //�������������าд��
	{
		//���ȶԻ���������ʱ����
		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
		progressDialog.setMessage("�������������Ե�...");
		progressDialog.show();
		
		fillThread=new Thread(new toUndoFillRunnable());
		fillThread.start();
	}
	
	@Override
	public void toRedoUpdate()//�������������าд��
	{			
		//���ȶԻ���������ʱ����
		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
		progressDialog.setMessage("���ڳ��������Ե�...");
		progressDialog.show();
		
		fillThread=new Thread(new toRedoFillRunnable());
		fillThread.start();
	}
	
	/***********************************************************************/
	/**
	 * �߳����
	 */
	//��������߳�
	class toUndoFillRunnable implements Runnable
	{
		public void run() 
		{	
			/**
			 * �����ʱ����
			 */
			Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();//��ȡ��ǰ����ͼƬ
			
			//�������������ɫ
			Canvas backgroundCanvas=new Canvas();//���컭��
			backgroundCanvas.setBitmap(backgroundBitmap);
			Paint paint=new Paint();
			paint.setColor(fillColor);
			
			ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// ��ȡpelList��Ӧ�ĵ�����ͷ���
			while (scanlineIterator.hasNext()) 
			{
				ScanLine scanLine = scanlineIterator.next();
				backgroundCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
			}
			
			handler.sendEmptyMessage(0); 
		}	
	}
	
	//��������߳�
	class toRedoFillRunnable implements Runnable
	{
		public void run() 
		{	
			/**
			 * �����ʱ����
			 */
			//ɨ�����������
			Bitmap backgroundBitmap=CanvasView.getBackgroundBitmap();//��ȡ��ǰ����ͼƬ
			
			//������仹ԭɫ
			ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// ��ȡpelList��Ӧ�ĵ�����ͷ���
			if(initColor == Color.TRANSPARENT) //����ɫ���
			{
				Bitmap copyOfBackgroundBitmap=CanvasView.getCopyOfBackgroundBitmap();//��ȡ��ǰ����ͼƬ
				while (scanlineIterator.hasNext()) 
				{
					ScanLine scanLine = scanlineIterator.next();
					
					for(int x=scanLine.from.x,y=scanLine.from.y;x < scanLine.to.x;x++)
					{
						if(x < copyOfBackgroundBitmap.getWidth())
							backgroundBitmap.setPixel(x, y, copyOfBackgroundBitmap.getPixel(x, y));
					}
				}
			}
			else //����һ�ε���ɫ���
			{
				Canvas backgroundCanvas=new Canvas();//���컭��
				backgroundCanvas.setBitmap(backgroundBitmap);
				Paint paint=new Paint();
				paint.setColor(initColor);
				while (scanlineIterator.hasNext()) 
				{
					ScanLine scanLine = scanlineIterator.next();
					backgroundCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
				}
			}
			
			handler.sendEmptyMessage(0); 
		}	
	}
	
	//��䵽�׵�λͼ��
	public void fillInWhiteBitmap(Bitmap bitmap)
	{		
		//�������������ɫ
		Canvas whiteCanvas=new Canvas();//���컭��
		whiteCanvas.setBitmap(bitmap);
		Paint paint=new Paint();
		paint.setColor(fillColor);
		
		ListIterator<ScanLine> scanlineIterator = scanLinesList.listIterator();// ��ȡpelList��Ӧ�ĵ�����ͷ���
		while (scanlineIterator.hasNext()) 
		{
			ScanLine scanLine = scanlineIterator.next();
			whiteCanvas.drawLine(scanLine.from.x, scanLine.from.y, scanLine.to.x, scanLine.to.y, paint);
		}
	}
}
