package com.noahark.graffiti.step;

import android.graphics.Matrix;
import android.graphics.Region;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

//�任ͼԪ����
public class TransformpelStep extends Step 
{
	private Matrix toUndoMatrix;//�任ǰ��matrix
	private static Region clipRegion=CanvasView.getClipRegion();
	private Pel    savedPel;
	
	public TransformpelStep(Pel pel) //����
	{
		super(pel);//��д����
		toUndoMatrix=new Matrix();
		savedPel=curPel.clone();
	}
	
	@Override
	public void toUndoUpdate() //��д
	{
		(curPel.path).transform(toUndoMatrix);
		(curPel.region).setPath(curPel.path, clipRegion);

		CanvasView.setSelectedPel(null);
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //��д
	{
		(curPel.path).set(savedPel.path);
		(curPel.region).setPath(curPel.path, clipRegion);
		
		CanvasView.setSelectedPel(null);
		canvasVi.updateSavedBitmap();
	}
	
	/*
	 * set()����
	 */
	public void setToUndoMatrix(Matrix matrix)
	{
		toUndoMatrix.set(matrix);
	}
}
