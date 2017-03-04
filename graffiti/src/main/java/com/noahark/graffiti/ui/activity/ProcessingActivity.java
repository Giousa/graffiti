package com.noahark.graffiti.ui.activity;


import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.processing.BitmapProcessor;
import com.noahark.graffiti.processing.Colorful;
import com.noahark.graffiti.processing.DiPian;
import com.noahark.graffiti.processing.Flower;
import com.noahark.graffiti.processing.HeiBai;
import com.noahark.graffiti.processing.HuaiJiu;
import com.noahark.graffiti.processing.JiMu;
import com.noahark.graffiti.processing.LianHuanHua;
import com.noahark.graffiti.processing.Light;
import com.noahark.graffiti.processing.Nick;
import com.noahark.graffiti.processing.Oxpaper;
import com.noahark.graffiti.processing.Pencil;
import com.noahark.graffiti.processing.Rommantic;
import com.noahark.graffiti.processing.RuiHua;
import com.noahark.graffiti.processing.Star;
import com.noahark.graffiti.processing.SuMiao;
import com.noahark.graffiti.processing.Sunshine;
import com.noahark.graffiti.processing.XiangSu;
import com.noahark.graffiti.processing.YouHua;
import com.noahark.graffiti.processing.YuanTu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ProcessingActivity extends Activity
{
	private View previewVi;
	private Bitmap previewBitmap=null;
	private CanvasView canvasVi=MainActivity.getCanvasView();
	
	public ProgressDialog progressDialog;
	final Handler processBitmapHandler=new Handler() //ͼƬ�����߳���Ϣ������
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				progressDialog.dismiss();		
				
				CanvasView.ensureBitmapRecycled(previewBitmap);
				previewBitmap=(Bitmap)msg.getData().getParcelable("processedBitmap");
				Drawable previewDrawable=new BitmapDrawable(previewBitmap);
				previewVi.setBackgroundDrawable(previewDrawable);			
				
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		} 
	};
	
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	//��ʼ�����
	public void initView()
	{
		setContentView(R.layout.activity_processing);
		previewVi=(View)findViewById(R.id.vi_preview);
	}
	
	//��ʼ������
	public void initData()
	{
		Drawable backgroundDrawable=new BitmapDrawable(CanvasView.getCopyOfBackgroundBitmap());
		previewVi.setBackgroundDrawable(backgroundDrawable);
	}
	
	//ԭͼ
	public void onYuanTuStyleBtn(View v)
	{		
		Processing(v);
	}
	
	//����
	public void onHuaijiuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//�ڰ�
	public void onHeibaiStyleBtn(View v)
	{
		Processing(v);
	}
	
	//��Ƭ
	public void onDipianStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onFudiaoStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onBingdongStyleBtn(View v)
	{
		Processing(v);
	}
	
	//��
	public void onWuhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//��ľ
	public void onJimuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onRongzhuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//������
	public void onLianhuanhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onGaoliangStyleBtn(View v)
	{
		Processing(v);
	}
	
	//��
	public void onRuihuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onSumiaoStyleBtn(View v)
	{
		Processing(v);
	}
	
	//ģ��
	public void onMohuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//�ͻ�
	public void onYouhuaStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onXiangsuStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onFlowerStyleBtn(View v)
	{
		Processing(v);
	}
	
	//��Ǧ
	public void onColorfulStyleBtn(View v)
	{Log.v("v","asasdasd");
		Processing(v);
	}
	
	//��Ǧ
	public void onPencilStyleBtn(View v)
	{
		Processing(v);
	}
	
	//ţƤֽ
	public void onOxpaperStyleBtn(View v)
	{
		Processing(v);
	}
	
	//�̺�
	public void onNickStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onLightStyleBtn(View v)
	{
		Processing(v);
	}
	
	//����
	public void onRommanticStyleBtn(View v)
	{
		Processing(v);
	}
	
	//�ǿ�
	public void onStarStyleBtn(View v)
	{
		Processing(v);
	}
	
	//Ϧ��
	public void onSunshineStyleBtn(View v)
	{
		Processing(v);
	}
	
	//ͼƬ����������
	public void Processing(final View v)
	{
		//���ȶԻ���
		progressDialog=new ProgressDialog(this);
		progressDialog.setMessage("ͼƬ��Ⱦ�У����Ե�...");
		progressDialog.show();
		
		new Thread(new Runnable()
		{
			public void run() 
			{
				Bitmap originalBitmap=CanvasView.getOriginalBackgroundBitmap();
				BitmapProcessor processor = null;//ͼƬ����������
				
				//ɸѡ��ͬͼƬ�����㷨
				switch(v.getId())
				{
					case R.id.btn_style_yuantu://ԭͼ
					{
						processor=new YuanTu();
					}break;
					case R.id.btn_style_huaijiu://����
					{
						processor=new HuaiJiu();
					}break;
					case R.id.btn_style_heibai://�ڰ�
					{
						processor=new HeiBai();
					}break;
					case R.id.btn_style_dipian://��Ƭ
					{
						processor=new DiPian();
					}break;
					case R.id.btn_style_jimu://��ľ
					{
						processor=new JiMu();
					}break;
					case R.id.btn_style_lianhuanhua://������
					{
						processor=new LianHuanHua();
					}break;
					case R.id.btn_style_ruihua://��
					{
						processor=new RuiHua();
					}break;
					case R.id.btn_style_sumiao://����
					{
						processor=new SuMiao();
					}break;
					case R.id.btn_style_youhua://�ͻ�
					{
						processor=new YouHua();
					}break;
					case R.id.btn_style_xiangsu://����
					{
						processor=new XiangSu();
					}break;
					case R.id.btn_style_flower://����
					{
						processor=new Flower();	
					}break;
					case R.id.btn_style_colorful://��Ǧ
					{
						processor=new Colorful();
					}break;
					case R.id.btn_style_pencil://��Ǧ
					{
						processor=new Pencil();
					}break;
					case R.id.btn_style_oxpaper://ţƤֽ
					{
						processor=new Oxpaper();
					}break;
					case R.id.btn_style_nick://�̺�
					{
						processor=new Nick();
					}break;
					case R.id.btn_style_light://����
					{
						processor=new Light();
					}break;
					case R.id.btn_style_rommantic://����
					{
						processor=new Rommantic();
					}break;
					case R.id.btn_style_star://�ǿ�
					{
						processor=new Star();
					}break;
					case R.id.btn_style_sunshine://Ϧ��
					{
						processor=new Sunshine();
					}break;
					
				}		

				//ͼƬ�������Ϣ��׼�����ݸ��̴߳�����
				Bundle data=new Bundle();
				data.putParcelable("processedBitmap", processor.createProcessedBitmap(originalBitmap));
				Message msg=new Message();
				msg.setData(data);
				processBitmapHandler.sendMessage(msg);
			}					
		}).start();
	}
	
	//����
	public void onProcessingBackBtn(View v)
	{
		finish();
	}
	
	//ȷ��
	public void onProcessingOkBtn(View v)
	{
		if(previewBitmap != null) //˵�����й�ͼƬ����
		{
			canvasVi.setProcessedBitmap(previewBitmap);
			Toast.makeText(MainActivity.getContext(), "�Ѹ�������", Toast.LENGTH_SHORT).show();
		}
		
		finish();
	}
}
