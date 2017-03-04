package com.noahark.graffiti.touch;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.step.CrossfillStep;
import com.noahark.graffiti.step.Step;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;

public class CrossfillTouch extends Touch {
	private Point originPoint;
	private int fillColor;// ��ǰ���ɫ
	private int oldColor;// �����ʼ��ɫ
	private int initColor; // ʵ�ʳ�ʼ��ɫ
	private int curColor;
	private int[] pixels;
	private Stack<Point> pointStack;// Դ����ջ
	private Bitmap whiteBitmap;// ��ɫ�׵������Ϣ����
	private Bitmap backgroundBitmap;// ����ɫʱҪ��ͬʱ�ı�ı���ͼ
	private Canvas backgroundCanvas;
	private Bitmap copyOfBackgroundBitmap;

	private int MAX_WIDTH;
	private int MAX_HEIGHT;
	private CanvasView canvasVi;
	/***********************************************************/
	private Paint fillPaint; // ��仭��
	private int width, height; // λͼ���
	private List<ScanLine> scanLinesList; // ɨ��������
	private ProgressDialog progressDialog;
	private Thread fillThread; // ����߳�
	final Handler handler = new Handler() // �߳���Ϣ������
	{
		public void handleMessage(Message msg) {
			try {
				progressDialog.dismiss();
				undoStack.push(new CrossfillStep(null, initColor, fillColor,
						scanLinesList));// ���á�����ѹ��undoջ
				updateSavedBitmap();// �ػ�λͼ
				canvasVi.invalidate();

				super.handleMessage(msg);
			} catch (Exception e) {
			}
		}
	};

	public CrossfillTouch() {
		super();
		originPoint = new Point();
		pointStack = new Stack<Point>();// ���ض�ջ
		canvasVi = MainActivity.getCanvasView();
		backgroundCanvas = new Canvas();
		fillPaint = new Paint();
		fillPaint.setStrokeWidth(1);
	}

	public Bitmap createWhiteBitmap() {
		// ��������λͼ
		Bitmap bitmap = Bitmap.createBitmap(MAX_WIDTH, MAX_HEIGHT,
				Config.ARGB_8888);
		savedCanvas.setBitmap(bitmap);
		reprintFilledAreas(bitmap);

		ListIterator<Pel> pelIterator = pelList.listIterator();// ��ȡpelList��Ӧ�ĵ�����ͷ���
		while (pelIterator.hasNext()) {
			Pel pel = pelIterator.next();
			if (!pel.equals(selectedPel))
				savedCanvas.drawPath(pel.path, pel.paint);
		}

		return bitmap;
	}

	@Override
	public void down1() {
		/**
		 * ɨ������������㷨
		 */

		// ���µ�û�г�������
		MAX_WIDTH = CanvasView.CANVAS_WIDTH;
		MAX_HEIGHT = CanvasView.CANVAS_HEIGHT;
		if (curPoint.x < MAX_WIDTH && curPoint.x > 0 && curPoint.y < MAX_HEIGHT
				&& curPoint.y > 0) {
			// ���ȶԻ���������ʱ����
			progressDialog = new ProgressDialog(
					com.noahark.graffiti.ui.activity.MainActivity.getContext());
			progressDialog.setMessage("������ɫ�����Ե�...");
			progressDialog.show();

			// �����������������Ԫ��Ϊ���ֱ�ߵ���ʼ����㣩
			scanLinesList = new LinkedList<ScanLine>();

			fillThread = new Thread(new FillRunnable());
			fillThread.start();
		}
	}

	// �������̵߳�ʵ����
	class FillRunnable implements Runnable {
		public void run() {
			// ɨ�����������
			fill();
			handler.sendEmptyMessage(0);
		}
	}

	/**************************************************************************/
	public void fill() {
		/**
		 * �����ʱ����
		 */
		whiteBitmap = createWhiteBitmap();// ����ǰ�зǰ�ɫ�����Ļ���λͼת���ɰ�ɫ������
		backgroundBitmap = CanvasView.getBackgroundBitmap();// ��ȡ��ǰ����ͼƬ
		copyOfBackgroundBitmap = CanvasView.getCopyOfBackgroundBitmap();
		backgroundCanvas.setBitmap(backgroundBitmap);
		fillColor = DrawTouch.getCurPaint().getColor();// ��������ɫ
		oldColor = whiteBitmap.getPixel((int) curPoint.x, (int) curPoint.y);// �õ������ʼ��ɫ
		initColor = backgroundBitmap.getPixel((int) curPoint.x,
				(int) curPoint.y);
		if (initColor == copyOfBackgroundBitmap.getPixel((int) curPoint.x,
				(int) curPoint.y))
			initColor = Color.TRANSPARENT;

		// �㷨��ʼ��
		pointStack.clear();// ���Դ����ջ
		originPoint.set((int) curPoint.x, (int) curPoint.y);// �Ե�ǰdown��������Ϊ��ʼԴ����
		pointStack.push(originPoint);// ��ջ

		// ������仭����ɫ
		fillPaint.setColor(fillColor);

		width = whiteBitmap.getWidth();
		height = whiteBitmap.getHeight();
		pixels = new int[width * height];
		whiteBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		Point tmp;
		int x, y, XLeft, XRight, index;
		while (!pointStack.isEmpty()) {
			tmp = pointStack.pop();
			x = tmp.x;
			y = tmp.y;
			XLeft = x;
			XRight = x;

			while (x > 0
					&& (curColor = pixels[index = width * y + x]) == oldColor
					&& curColor != fillColor) {
				whiteBitmap.setPixel(x, y, fillColor);
				pixels[index] = fillColor;
				x--;
			}
			XLeft = x + 1;

			x = tmp.x + 1;
			while (x < width
					&& (curColor = pixels[index = width * y + x]) == oldColor
					&& curColor != fillColor) {
				whiteBitmap.setPixel(x, y, fillColor);
				pixels[index] = fillColor;
				x++;
			}
			XRight = x - 1;

			backgroundCanvas.drawLine(XLeft - 1, y, XRight + 2, y, fillPaint);
			
			ScanLine scanLine = new ScanLine();
			(scanLine.from).set(XLeft - 1, y);
			(scanLine.to).set(XRight + 2, y);
			scanLinesList.add(scanLine);

			if (y > 0) {
				findNewSeedInline(XLeft, XRight, y - 1, fillPaint);
			}
			if (y + 1 < height) {
				findNewSeedInline(XLeft, XRight, y + 1, fillPaint);
			}
		}
	}

	public void findNewSeedInline(int XLeft, int XRight, int y, Paint paint) {
		Point p;
		Boolean pflag;
		int x = XLeft + 1;
		while (x <= XRight) {
			pflag = false;

			while ((curColor = pixels[width * y + x]) == oldColor && x < XRight
					&& curColor != fillColor) {
				if (pflag == false) {
					pflag = true;
				}

				x++;
			}
			if (pflag == true) {
				if ((x == XRight)
						&& (curColor = pixels[width * y + x]) == oldColor
						&& curColor != fillColor) {
					p = new Point(x, y);
					pointStack.push(p);
				} else {
					p = new Point(x - 1, y);
					pointStack.push(p);
				}
				pflag = false;
			}

			// �������������ڲ�����Ч�㣨���������Ҷ����ϰ���������
			int xenter = x;
			while (pixels[width * y + x] != oldColor) {
				if (x >= XRight || x >= width) {
					break;
				}

				x++;
			}
			if (xenter == x) {
				x++;
			}
		}
	}

	public class ScanLine // ɨ������
	{
		public Point from; // ��ʼ��
		public Point to; // ��ֹ��

		ScanLine() {
			from = new Point();
			to = new Point();
		}
	}

	// ����������´�ӡ
	public static void reprintFilledAreas(Bitmap bitmap) {
		for (Step step : undoStack) {
			// ��Ϊ��䲽��
			if (step.getClass().getSimpleName().equals("CrossfillStep")) {
				((CrossfillStep) step).fillInWhiteBitmap(bitmap);
			}
		}
	}
}
