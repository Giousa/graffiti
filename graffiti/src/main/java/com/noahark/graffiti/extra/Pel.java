package com.noahark.graffiti.extra;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

public class Pel{//ͼԪ��
	public Path path; //·��
	public Region region;//����
	public Paint paint;//����
	public Text text;//�ı�
	public Picture picture;//�廭
	public boolean closure;//�Ƿ���
	
	//���죨ʵ��ʹ��ʱӦ�ð�Pel�����Pel(path region paint name)����ʽ���βξ����ⲿ���Ѿ�������˵ģ�
	public Pel()
	{
		path=new Path();
		region=new Region();
		paint=new Paint();
		text=null;
		picture=null;
	}
	
	//���
	public Pel clone()
	{	
		Pel pel=new Pel();
		(pel.path).set(path);
		(pel.region).set(region);
		(pel.paint).set(paint);
		pel.closure=closure;
		
		return pel;	
	}
}
