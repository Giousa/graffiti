package com.noahark.graffiti.step;

import android.graphics.Paint;

import com.noahark.graffiti.extra.Pel;

//���ͼԪ����
public class FillpelStep extends Step 
{
	private Paint oldPaint,newPaint;
	
	public FillpelStep(Pel pel,Paint oldPaint,Paint newPaint) 
	{
		super(pel);
		this.oldPaint=new Paint(oldPaint);
		this.newPaint=new Paint(newPaint);
	}
	
	@Override
	public void toUndoUpdate() //��д
	{
		(curPel.paint).set(newPaint);
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //��д
	{
		(curPel.paint).set(oldPaint);
		canvasVi.updateSavedBitmap();
	}
}
