package com.noahark.graffiti.processing;

import android.graphics.Bitmap;

//ͼƬ����ӿ�
public interface BitmapProcessor 
{
	abstract Bitmap createProcessedBitmap(Bitmap originalBitmap);
}
