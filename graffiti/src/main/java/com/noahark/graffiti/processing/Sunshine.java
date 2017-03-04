package com.noahark.graffiti.processing;


import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//��ƬЧ��
public class Sunshine extends MergeHandler implements BitmapProcessor
{
	@Override
	public Bitmap createProcessedBitmap(Bitmap originalBitmap) 
	{
		Bitmap mergeBitmap = BitmapFactory.decodeResource(MainActivity.getContext().getResources(),
				R.drawable.pro_sunshine);
		Bitmap processedBitmap = merge(mergeBitmap, originalBitmap, 0, 0, encreaseLight(50),
				encreaseLight(80));
	    
	    return processedBitmap;
	}
}
