package com.noahark.graffiti.step;

import java.util.List;

import android.graphics.Matrix;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;

//������
public class Step 
{
	protected static List<Pel> pelList=CanvasView.getPelList(); // ͼԪ����
	protected static CanvasView canvasVi=MainActivity.getCanvasView(); //֪ͨ�ػ���
	protected Pel    curPel;//�������undo��ͼԪ
	
	public Step(Pel pel) //����
	{
		this.curPel=pel;
	}
	
	public void toUndoUpdate() //��undoջʱ��List��ͼԪ�ĸ��£����าд��
	{
	}
	
	public void toRedoUpdate()//��redoջʱ��List��ͼԪ�ķ��ڣ����าд��
	{		
	}
	
	public void setToUndoMatrix(Matrix matrix)
	{
	}
}
