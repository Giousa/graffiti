package com.noahark.graffiti.step;

import java.util.List;

import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

//��ͼԪ����
public class DrawpelStep extends Step 
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // ͼԪ����
	protected int location; //ͼԪ��������λ��
	
	public DrawpelStep(Pel pel) //����
	{
		super(pel); //��д����
		location=pelList.indexOf(pel); //�ҵ���ͼԪ���������λ��
	}
	
	@Override
	public void toUndoUpdate() //��д
	{
		pelList.add(location,curPel); //����ͼԪ��������
		canvasVi.updateSavedBitmap();
	}
	
	@Override
	public void toRedoUpdate() //��д
	{
		pelList.remove(location); //ɾ�������Ӧ����λ��ͼԪ
		canvasVi.updateSavedBitmap();
	}
}
