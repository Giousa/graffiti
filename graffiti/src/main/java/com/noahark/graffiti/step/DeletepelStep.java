package com.noahark.graffiti.step;

import com.noahark.graffiti.extra.Pel;

//ɾ��ͼԪ����
public class DeletepelStep extends DrawpelStep 
{
	public DeletepelStep(Pel pel) 
	{
		super(pel);
	}
	
	@Override
	public void toUndoUpdate() //��д
	{
		pelList.remove(location); //ɾ�������Ӧ����λ��ͼԪ
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //��д
	{	
		pelList.add(location,curPel); //����ͼԪ��������
		canvasVi.updateSavedBitmap();
	}
}
