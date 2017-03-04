package com.noahark.graffiti.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.PeneffectView;
import com.noahark.graffiti.touch.DrawTouch;

//��ɫ��Ի���
public class PenDialog extends Dialog implements OnClickListener,OnSeekBarChangeListener 
{
	private Paint paint = DrawTouch.getCurPaint();//��ȡ��ǰ���ƻ���
	// �����ʴ���ؿؼ�
	private PeneffectView peneffectVi;
	private SeekBar penwidthSeekBar;
	private TextView penwidthTextVi;
	private Button cancelBtn;
	
	// ���ΰ�ť
	private Matrix matrix;
	private static Button curShapeBtn;
	private Button[] penshapeBtns = new Button[8];
	private int[] penshapeBtnsId = new int[] 
	{ R.id.btn_penshape1,R.id.btn_penshape2, R.id.btn_penshape3, R.id.btn_penshape4,
	  R.id.btn_penshape5,R.id.btn_penshape6, R.id.btn_penshape7, R.id.btn_penshape8};
	
	//��Ч��ť
	private static Button curEffectBtn;
	private Button[] peneffectBtns = new Button[4];
	private int[] peneffectBtnsId = new int[] 
	{ R.id.btn_peneffect1,R.id.btn_peneffect2, R.id.btn_peneffect3, R.id.btn_peneffect4};
	
	// ���캯��
	public PenDialog(Context context, int theme) {
		super(context, theme);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_pen);

		initView();
		initData();
	}

	// ��ʼ���������
	private void initView() {
		peneffectVi = (PeneffectView) findViewById(R.id.vi_peneffect);
		penwidthSeekBar = (SeekBar) findViewById(R.id.seekbar_penwidth);
		penwidthTextVi = (TextView) findViewById(R.id.textvi_penwidth);
		cancelBtn = (Button) findViewById(R.id.btn_pen_cancel);

		matrix=new Matrix();
		matrix.setSkew(2,2);
		curShapeBtn=(Button)findViewById(R.id.btn_penshape1);
		curEffectBtn=(Button)findViewById(R.id.btn_peneffect1);
		//12���ΰ�ť
		for (int i = 0; i < penshapeBtns.length; i++) 
		{
			penshapeBtns[i] = (Button) findViewById(penshapeBtnsId[i]);
		}
		//4��Ч��ť
		for(int i=0;i<peneffectBtns.length;i++)
		{
			peneffectBtns[i]=(Button) findViewById(peneffectBtnsId[i]);
		}
	}

	// ��ʼ������
	private void initData() 
	{
		// ���ü���
		penwidthSeekBar.setOnSeekBarChangeListener(this);
		for (int i = 0; i < penshapeBtns.length; i++) //���ΰ�ť
		{
			penshapeBtns[i].setOnClickListener(this);
		}
		for (int i = 0; i < peneffectBtns.length; i++) //��Ч��ť
		{
			peneffectBtns[i].setOnClickListener(this);
		}		
		cancelBtn.setOnClickListener(this);

		// �Ե�ǰ���ʷ���ʼ����Ч����
		int curWidth = (int) (DrawTouch.getCurPaint()).getStrokeWidth();
		penwidthSeekBar.setProgress(curWidth);
		penwidthTextVi.setText(Integer.toString(curWidth));

		peneffectVi.invalidate();
	}

	public void onClick(View v) 
	{
		switch (v.getId()) 
		{		
			/**
			 * 8�����ΰ�ť
			 */
			case R.id.btn_penshape1: //����
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new CornerPathEffect(10));
			}break;
			case R.id.btn_penshape2: //����1
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DashPathEffect(new float[] { 20,20}, 0.5f));
			}break;
			case R.id.btn_penshape3: //����2
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DashPathEffect(new float[] { 40, 20,10,20 }, 0.5f));
			}break;	
			case R.id.btn_penshape4: //����
			{
				updatePenshapeIcons(v);
				paint.setPathEffect(new DiscretePathEffect(5f, 9f));
			}break;
			case R.id.btn_penshape5: // ��Բ
			{
				updatePenshapeIcons(v);

				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addOval(new RectF(0, 0, width, width), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, width+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape6: //������
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addRect(new RectF(0, 0, width, width), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, width+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape7: //ë��
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addRect(new RectF(0, 0, width, width), Path.Direction.CCW);
				p.transform(matrix);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
			case R.id.btn_penshape8://��˱�
			{
				updatePenshapeIcons(v);
				
				float width=penwidthSeekBar.getProgress();
				Path p = new Path();
				p.addArc(new RectF(0, 0, width+4, width+4), -90, 90);
				p.addArc(new RectF(0, 0, width+4, width+4), 90, -90);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
	
			/**
			 * 4����Ч��ť
			 */
			case R.id.btn_peneffect1://��
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(null);
			}break;
			case R.id.btn_peneffect2:// ģ��
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(new BlurMaskFilter(8,BlurMaskFilter.Blur.NORMAL));
			}break;
			case R.id.btn_peneffect3:// �߿�
			{		
				updatePeneffectIcons(v);
				paint.setMaskFilter(new BlurMaskFilter(8,BlurMaskFilter.Blur.OUTER));
			}break;
			case R.id.btn_peneffect4://����
			{
				updatePeneffectIcons(v);
				paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1,1, 1 }, 0.4f, 6, 3.5f));
			}break;
	
			// ȡ����ť
			case R.id.btn_pen_cancel: 
			{
				this.dismiss();
			}break;
		}

		peneffectVi.invalidate();// ˢ����Ч����
	}

	// �϶���ʱ��ʱ�̸��´�ϸ�ı�
	public void onProgressChanged(SeekBar seekBar, int curWidth,boolean fromUser) 
	{
		// �Ƶ�0��ʱ���Զ�ת����1
		if (curWidth == 0)
		{
			seekBar.setProgress(1);
			curWidth=1;
		}

		penwidthTextVi.setText(Integer.toString(curWidth));//���´�ϸ�ı�

		//����PathDashPathEffect��ЧҪ���⴦��
		switch(curShapeBtn.getId())
		{
			case R.id.btn_penshape5: //��Բ
			{				
				Path p = new Path();
				p.addOval(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);		
				paint.setPathEffect(new PathDashPathEffect(p, curWidth+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape6://������
			{
				Path p = new Path();
				p.addRect(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);
				paint.setPathEffect(new PathDashPathEffect(p, curWidth+10, 0,PathDashPathEffect.Style.ROTATE));
			}break;
			case R.id.btn_penshape7://ë��
			{
				Path p = new Path();
				p.addRect(new RectF(0, 0, curWidth, curWidth), Path.Direction.CCW);
				p.transform(matrix);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
			case R.id.btn_penshape8://��˱�
			{
				Path p = new Path();
				p.addArc(new RectF(0, 0, curWidth+4, curWidth+4), -90, 90);
				p.addArc(new RectF(0, 0, curWidth+4, curWidth+4), 90, -90);
				paint.setPathEffect(new PathDashPathEffect(p, 2, 0,PathDashPathEffect.Style.TRANSLATE));
			}break;
		}
		
		paint.setStrokeWidth(curWidth);//�ı��ϸ
		peneffectVi.invalidate();// ����ʾ��view
	}

	public void onStartTrackingTouch(SeekBar seekBar) 
	{

	}

	// �ſ��϶����� �ػ���Чʾ������
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		
	}
	
	/**
	 * ������
	 */
	//�������������ͼ��
	public void updatePenshapeIcons(View v)
	{
		curShapeBtn.setTextColor(Color.parseColor("#ff666666"));
		curShapeBtn=(Button)v;
		curShapeBtn.setTextColor(Color.parseColor("#0099CC"));
	}
	
	//������Ч�����ͼ��
	public void updatePeneffectIcons(View v)
	{	
		curEffectBtn.setTextColor(Color.parseColor("#ff666666"));
		curEffectBtn=(Button)v;
		curEffectBtn.setTextColor(Color.parseColor("#0099CC"));
	}
}
