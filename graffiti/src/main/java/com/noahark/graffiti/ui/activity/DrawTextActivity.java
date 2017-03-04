 package com.noahark.graffiti.ui.activity;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.dialog.ColorpickerDialog;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.ui.view.DrawTextView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.extra.Picture;
import com.noahark.graffiti.extra.Text;
import com.noahark.graffiti.step.DrawpelStep;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.touch.DrawTextTouch;
import com.noahark.graffiti.touch.DrawTouch;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

public class DrawTextActivity extends Activity
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // ͼԪ����// ��Ļ���
	protected static Stack<Step> undoStack=CanvasView.getUndoStack();//��ȡundo
	protected Pel selectedPel; // ��ǰѡ��ͼԪ
	protected Bitmap savedBitmap; // ��ǰ�ػ�λͼ	
	protected Canvas savedCanvas; //�ػ滭��
	private String content="���";
	private ColorpickerDialog colorpickerDialog;
	public static DrawTextView drawTextVi=null;
	private static Context context=null;
	public static View topToolbar;
	private static View downToolbar;
	public static View[] allBtns;
	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
		demandContent();
	}
	
	//��ʼ�����
	public void initView()
	{
		setContentView(R.layout.activity_drawtext);
		drawTextVi=(DrawTextView)findViewById(R.id.drawtext_canvas);
		colorpickerDialog = new ColorpickerDialog(DrawTextActivity.this,R.style.GraffitiDialog);
		topToolbar=(View)findViewById(R.id.drawtext_toptoolbar);
		downToolbar=(View)findViewById(R.id.drawtext_downtoolbar);
		
		int[] btnIds=new int[]{R.id.drawtext_refuse,R.id.drawtext_sure,R.id.drawtext_content,R.id.drawtext_color};
		allBtns=new View[btnIds.length];
		for(int i=0;i<btnIds.length;i++)
			allBtns[i]=(View)findViewById(btnIds[i]);
	}
	
	//��ʼ������
	public void initData()
	{
		savedCanvas=new Canvas();
		context=DrawTextActivity.this;
	}
	
	//����
	public void onDrawtextBackBtn(View v)
	{
		finish();
	}
	
	//ȷ��
	public void onDrawtextOkBtn(View v)
	{
		//����ôε��ı�����,��װ��ͼԪ����
		DrawTextTouch touch = DrawTextView.touch;
		Text text=new Text(content,
				touch.getDx(),touch.getDy(),touch.getScale(),touch.getDegree(),
				new PointF(touch.getCenterPoint().x,touch.getCenterPoint().y),
				new PointF(touch.getTextPoint().x, touch.getTextPoint().y));
		Pel newPel = new Pel();
		newPel.text = text;
		
		//������ı�������
		(CanvasView.pelList).add(newPel);
		
		//��¼ջ����Ϣ
		undoStack.push(new DrawpelStep(newPel));//���á�����ѹ��undoջ
		
		//���»���
		updateSavedBitmap();
		
		//�����û
		finish();
	}
	
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
	
	public void onChangeContentBtn(View v)
	{
		demandContent();
	}
	
	public void onChangeColorBtn(View v)
	{
		colorpickerDialog.show();
		colorpickerDialog.picker.setOldCenterColor(DrawTouch.getCurPaint().getColor()); //��ȡ��ǰ���ʵ���ɫ��Ϊ���Բ��ɫ
	}	
	
	public void demandContent()	
	{
		final EditText editTxt=new EditText(DrawTextActivity.this);//��Ʒ�����Ʊ༭��
		editTxt.setText(content);
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
				drawTextVi.setContent(content=editTxt.getText().toString());
				drawTextVi.invalidate();
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}		
		}
		
		//ʵ����ȷ�϶Ի���
		Builder dialog=new AlertDialog.Builder(DrawTextActivity.this);
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setView(editTxt);
		dialog.setMessage("�������ı�");
		dialog.setPositiveButton("ȷ��", new okClick());
		dialog.setNegativeButton("ȡ��", new cancelClick());
		dialog.create();
		dialog.show();
	}
	
	//�رչ�����
	public static void closeTools()
	{			
		Animation downDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.downdisappear);  		
		Animation topDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.topdisappear);  			
		
		downToolbar.startAnimation(downDisappearAnim);
		topToolbar.startAnimation(topDisappearAnim);
		
		downToolbar.setVisibility(View.GONE);
		topToolbar.setVisibility(View.GONE);
		setToolsClickable(false);
	}
	
	//�򿪹�����
	public static void openTools()
	{				
		Animation downAppearAnim = AnimationUtils.loadAnimation(context, R.anim.downappear);  		
		Animation topAppearAnim = AnimationUtils.loadAnimation(context, R.anim.topappear);  			
		
		downToolbar.startAnimation(downAppearAnim);
		topToolbar.startAnimation(topAppearAnim);
		
		downToolbar.setVisibility(View.VISIBLE);
		topToolbar.setVisibility(View.VISIBLE);
		setToolsClickable(true);
	}
	
	public static void setToolsClickable(boolean bool)
	{	
		for(int i=0;i<allBtns.length;i++)
			allBtns[i].setClickable(bool);
	}
}
