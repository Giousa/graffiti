package com.noahark.graffiti.ui.dialog;


import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.DrawTextActivity;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.DrawTextView;
import com.noahark.graffiti.touch.DrawTouch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//��ɫ��Ի���
public class ColorpickerDialog extends Dialog implements OnClickListener
{
	//��ɫ��ؼ����
	public  ColorPicker picker;
	private OpacityBar opacityBar;
	private SaturationBar saturationBar;
	private ValueBar valueBar;
	
	private Button okBtn;
		
	public ColorpickerDialog(Context context,int theme) 
	{
		super(context,theme);
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		this.setContentView(R.layout.dialog_colorpicker);
		
		initView();
		initData();
	}
	
	//��ʼ���������
	private void initView()
	{
		//�ҵ�ʵ������
		picker = (ColorPicker) findViewById(R.id.colorpicker_picker);
		opacityBar = (OpacityBar) findViewById(R.id.colorpicker_opacitybar);
		saturationBar = (SaturationBar) findViewById(R.id.colorpicker_saturationbar);
		valueBar = (ValueBar) findViewById(R.id.colorpicker_valuebar);
		
		//��ť����
		okBtn=(Button)findViewById(R.id.btn_colorpicker_ok);
	}
	
	//��ʼ������
	private void initData()
	{
		//ʹ����ȡɫ�����϶���������ϵ
		picker.addOpacityBar(opacityBar);
		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);	
		picker.setColor(Color.parseColor("#ff298ecb"));//��ʼ��Ϊ��ɫ
		
		okBtn.setOnClickListener(this);
	}
	
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_colorpicker_ok:
			{
				int curColor=picker.getColor();
				DrawTouch.getCurPaint().setColor(curColor);//���õ�ǰ��ɫ
				(MainActivity.colorBtn).setTextColor(curColor);//�ı���ɫ����

				if(DrawTextActivity.drawTextVi != null)//���ڱ༭���ֽ���򿪵�
				{	
					(DrawTextView.drawTextPaint).setColor(curColor);
					(DrawTextActivity.drawTextVi).invalidate();
				}
				
				this.dismiss();//�رնԻ���
			}break;
		}
	}
}
