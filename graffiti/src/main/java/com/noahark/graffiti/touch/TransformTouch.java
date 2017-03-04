package com.noahark.graffiti.touch;

import android.graphics.Matrix;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.step.TransformpelStep;

import java.util.ListIterator;

//�任������
public class TransformTouch extends Touch {

	private static Matrix savedMatrix; //ѡ��ͼԪ���������
	private Matrix transMatrix; //�任���ӣ�ƽ�ơ����š���ת��
	private PointF downPoint; //���£��ƶ�����ָ�е�
	public  static PointF centerPoint; //���š���ת����
	private Pel savedPel; //�ػ�ͼԪ
	
	private static final float MIN_ZOOM=10; //��������	
	private static final float MAX_DY=70; //��������ת�л�����
	
	private static final int NONE = 0; // ƽ�Ʋ���
	private static final int DRAG = 1; // ƽ�Ʋ���
	private static final int ZOOM = 2; // ���Ų���
	private static final int ROTATE = 3; // ��ת����
	private static int mode = NONE; // ��ǰ��������
	
	private float oriDist; // ����ʱ��ָ�������ʱ�ľ���
	private float dx,dy; //ƽ��ƫ����
	
	private Step step=null;
	
	public TransformTouch()
	{
		super();
		
		savedMatrix=new Matrix();
		transMatrix=new Matrix();
		
		downPoint=new PointF();
		centerPoint=new PointF();
		
		savedPel=new Pel();
	}
	
	// ��һֻ��ָ����
	@Override
	public void down1()
	{
		// ��ȡdown�¼��ķ���λ��
		downPoint.set(curPoint);
	
		// �ж��Ƿ��ཻ
		Pel minDisPel=null;
		float minHorizontalDis=Float.MAX_VALUE;
		float minVerticalDis=Float.MAX_VALUE;
		
		ListIterator<Pel> pelIterator = pelList.listIterator(); // ��ȡpelList��Ӧ�ĵ�����ͷ���
		while (pelIterator.hasNext())
		{
			Pel pel = pelIterator.next();
			Rect rect=(pel.region).getBounds();
					
			float leftDis=Math.abs(rect.left-downPoint.x);
			float rightDis=Math.abs(rect.right-downPoint.x);
			float horizontalDis=leftDis+rightDis;
			
			float topDis=Math.abs(rect.top-downPoint.y);
			float bottomDis=Math.abs(rect.bottom-downPoint.y);
			float verticalDis=topDis+bottomDis;
			
			if(horizontalDis < minHorizontalDis || verticalDis < minVerticalDis)
			{
				if(leftDis + rightDis < rect.width()+5)
				{
					if(topDis + bottomDis < rect.height()+5)
					{
						minDisPel=pel;
						minHorizontalDis=leftDis+rightDis;
						minVerticalDis=topDis+bottomDis;	
					}			
				}
			}
		}
				
		// Բ����չ������Ƿ���ѡ���κ�ͼԪ
		if (minDisPel != null)
		{
			// �ö���ͼԪ
			CanvasView.setSelectedPel(selectedPel = minDisPel);
			
			//����ѡ��ͼԪ�����ĵ�
			centerPoint.set(calPelCenterPoint(selectedPel));

			// ��ȡѡ��ͼԪ�ĳ�ʼmatrix
			savedMatrix.set(calPelSavedMatrix(selectedPel));
			
			//����֪��Ϣ����ò���
			step=new TransformpelStep(selectedPel);//���øò����ӦͼԪ

			(savedPel.path).set(selectedPel.path); // ԭʼѡ��ͼԪ����λ�ü��䵽��ʱͼԪ��ȥ
			updateSavedBitmap();

			mode = DRAG;			
		}
		else //������ֵδѡ��
		{
			CanvasView.setSelectedPel(selectedPel = null); //ͬ��CanvasView�е�ǰѡ�е�ͼԪ
			updateSavedBitmap();
		}
	}
	
	// �ڶ�ֻ��ָ����
	@Override
	public void down2()
	{		
		oriDist = distance();
		if (oriDist > MIN_ZOOM && selectedPel != null) 
		{
			// ����С��50px����������
			takeOverSelectedPel();
			mode = ZOOM;
		}
	}
	
	// ��ָ�ƶ�
	@Override
	public void move()
	{
		// ��ȡmove�¼��ķ���λ��
		if (selectedPel != null)// ǰ����Ҫѡ����ͼԪ
		{			
			if (mode == DRAG)// ƽ�Ʋ���
			{				
				dx = curPoint.x - downPoint.x;//�������
				dy = curPoint.y - downPoint.y;					
				
				// ��ѡ��ͼԪʩ��ƽ�Ʊ任
				transMatrix.set(savedMatrix);
				transMatrix.postTranslate(dx, dy); // ������ƽ�Ʊ任����
				
				(selectedPel.path).set(savedPel.path);
				(selectedPel.path).transform(transMatrix); // ������ͼԪ
				(selectedPel.region).setPath(selectedPel.path, clipRegion); // ����ƽ�ƺ�·����������
			} 
			else if (mode == ZOOM) // ���Ų���
			{	
				float newDist = distance();					
				float dy=Math.abs(curPoint.y-secPoint.y);//��ָ�Ĵ�ֱ���				
				if(dy >= MAX_DY)//�ж��Ƿ���Ҫת��Ϊ��תģʽ
				{
					//����׼������
					mode=ROTATE;
					takeOverSelectedPel();
					(savedPel.path).set(selectedPel.path);
					downPoint.set(curPoint);
				}
				else if (newDist > MIN_ZOOM) 
				{
					//<100��Ȼ����������										
					float scale = newDist / oriDist;

					transMatrix.set(savedMatrix);
					transMatrix.postScale(scale, scale, centerPoint.x,centerPoint.y); // ���������ű任����

					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix); // ������ͼԪ
					(selectedPel.region).setPath(selectedPel.path, clipRegion); // ����ƽ�ƺ�·����������
				}
			} 
			else if (mode == ROTATE) // ��ת����
			{			
				float dy=Math.abs(curPoint.y-secPoint.y);//��ָ�Ĵ�ֱ���					
				if(dy < MAX_DY)//�ж��Ƿ���Ҫת��Ϊ����ģʽ
				{
					mode=ZOOM;
					takeOverSelectedPel();
					(savedPel.path).set(selectedPel.path);
					oriDist=distance();
				}
				else//>100��Ȼ��������ת
				{							
					transMatrix.set(savedMatrix);
					transMatrix.setRotate(degree(),centerPoint.x,centerPoint.y);

					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix); // ������ͼԪ
					(selectedPel.region).setPath(selectedPel.path, clipRegion); // ����ƽ�ƺ�·����������
				}
			}
		}
	}
	
	// ��ָ̧��
	@Override
	public void up()
	{
		//Ϊ�ж��Ƿ����ڡ�ѡ�У�����̧�������
		float disx=Math.abs(curPoint.x-downPoint.x);
		float disy=Math.abs(curPoint.y-downPoint.y);
		
		if((disx > 2f || disy >2f) && step != null) //�ƶ���������Ҫ�������2f
		{
			//�ö���ǰ��Ӧ����
			savedMatrix.set(transMatrix);
			step.setToUndoMatrix(transMatrix);//���ý��иôβ����ı任����
			undoStack.push(step);//���á�����ѹ��undoջ
				
			// �ö��˴β������������� 
			if (selectedPel != null)
				(savedPel.path).set(selectedPel.path); //��ʼλ��Ҳͬ������
		}

		mode = NONE;
	}
	
	/*
	 * �Զ��庯��
	 */
	
	// ��������������֮��ľ���
	private float distance() {
		float x = curPoint.x - secPoint.x;
		float y = curPoint.y - secPoint.y;
		return (float)Math.sqrt(x * x + y * y);
	}

	// ��ת�Ƕȵļ���
	private float degree() {
		// �������down��ʱ�ľ���
		float x=curPoint.x-downPoint.x;
		float y=curPoint.y-downPoint.y;
		
		float arc=(float)Math.sqrt(x * x + y * y);//����
		float radius=distance()/2;//�뾶

		float degrees=(arc/radius)*(180/3.14f);
		
		return degrees;
	}

	public static Matrix getSavedMatrix()
	{
		return savedMatrix;
	}
	
	public static PointF calPelCenterPoint(Pel selectedPel)
	{
		Rect boundRect=new Rect();
		selectedPel.region.getBounds(boundRect);

		return new PointF((boundRect.right+boundRect.left)/2,(boundRect.bottom+boundRect.top)/2);
	}
	
	public static Matrix calPelSavedMatrix(Pel selectedPel)
	{
		Matrix savedMatrix = new Matrix();
		PathMeasure pathMeasure = new PathMeasure(
				selectedPel.path, true);// ��Path��װ��PathMeasure�������ȡpath�ڵ�matrix��
		pathMeasure.getMatrix(pathMeasure.getLength(),
				savedMatrix, PathMeasure.POSITION_MATRIX_FLAG
						& PathMeasure.TANGENT_MATRIX_FLAG);
		
		return savedMatrix;
	}	
	
	public void takeOverSelectedPel() //���ֱ任��һ��Ҫ����������ͬ�任������ͼԪ����ƽ�Ƶ�ĳ�������������ţ������ŵ�ĳ������������ת��
	{	
		savedMatrix.set(transMatrix);//��ʼ�任����Ϊ�ղŵı任������
		centerPoint.set(calPelCenterPoint(selectedPel)); //���¼���ͼԪ���ĵ�
	}
}