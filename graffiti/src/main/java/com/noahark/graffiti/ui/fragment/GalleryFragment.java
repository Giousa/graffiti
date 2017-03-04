package com.noahark.graffiti.ui.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.activity.MainActivity;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.ui.view.GalleryView;

import android.R.drawable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class GalleryFragment extends Fragment implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private String softDir="/FreeGraffiti";
	private List<String> ImageList;// sd���е�ͼƬ·��
	private int screenWidth;
	private int screenHeight;
	public static Bitmap bmp;
	private ImageAdapter adapter;
	GalleryView g;
	private int NUM = 100;// �������������ͼƬ����
	private String[] path = new String[NUM];
	ImageView iv;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery, container, false);

		// ��Ļ���****************
		screenWidth = (int) (MainActivity.SCREEN_WIDTH/1.5); // ��Ļ�����أ��磺480px��Ҫ��MainAcivity���浥������
		screenHeight = (int) (MainActivity.SCREEN_HEIGHT/1.5); // ��Ļ�ߣ����أ��磺800p��

		/**
		 * ȡ��SD���ϵ�ͼƬ�ļ�,����ͼƬ����·�����浽ImageList��
		 */
		ImageList = getInSDPhoto();

		/**
		 * ��ȡ�õ�·������ImageListת�������鲢����list�� List�����е�toArray()�����������ڼ���������ת����
		 */
		g = (GalleryView) view.findViewById(R.id.mygallery);
		g.setFocusable(true);
		adapter = new ImageAdapter(view.getContext(), ImageList);
		if (adapter.getCount() != 0) { // ��ͼƬ����ǿ�ʱ����������
			g.setAdapter(adapter);
		}

		return view;
	}

	private List<String> getInSDPhoto() {

		/**
		 * �趨ͼƬ����·��
		 */
		List<String> it = new ArrayList<String>();
		String photosPath=Environment.getExternalStorageDirectory().getPath()+softDir + "/";
		
		File f = new File(photosPath);
		File[] files = f.listFiles();

		/**
		 * �������ļ�����ArrayList��,����ط���Ļ����ļ�·��
		 */
		for (int i = 0; i < files.length; i++) 
		{
			File file = files[i];
			path[i] = file.getPath();
			if (getAllImage(path[i]))
				it.add(path[i]);
		}
		if(files.length == 0)
			Toast.makeText(MainActivity.getContext(),"��������ʱ��û��������ƷŶ",Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(MainActivity.getContext(), "����ͼƬ���Է��������Ŷ~", Toast.LENGTH_SHORT).show();
		
		return it;
	}

	private boolean getAllImage(String fName) {
		boolean re;

		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* ����չ�������;���MimeType */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}

		return re;
	}

	// ������
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		public ImageAdapter(Context context, List<String> list) {
			mContext = context;
			lis = list;

			TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// �ö����styleable�����ܹ�����ʹ��
			a.recycle();
		}

		public int getCount() {
			return lis.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		/* ��д�ķ���getView,������View���� */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			iv = new ImageView(mContext);
			// ʹ��ͼƬ�������벢����ͼƬ
			bmp = BitmapFactory.decodeFile(lis.get(position).toString());
			iv.setImageBitmap(bmp);

			/**
			 * �趨ImageView��� ������ScaleType�г�FIT_XY���������ѡ�Ч��ͼ�ֱ�����£�
			 */
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			// �����趨Layout�Ŀ��
			iv.setLayoutParams(new Gallery.LayoutParams(4 * screenWidth / 5,
					4 * screenHeight / 5));
			// �趨Gallery����ͼ
			iv.setBackgroundResource(mGalleryItemBackground);
			// ����imageView����,���ȵ�item��������
			g.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int selection, long arg3) {
					
					//�����Ի������û�ѡ���� ����or ����ͼƬ or ȡ��
					class shareClick implements DialogInterface.OnClickListener
					{
						public void onClick(DialogInterface dialog, int which) //����
						{
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("image/*");
							intent.putExtra(Intent.EXTRA_TEXT, "#�����Ե���Ϳѻ(Free Graffiti)#");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(Intent.EXTRA_STREAM,android.net.Uri.parse(path[selection]));
							startActivity(Intent.createChooser(intent, null));
						}		
					}	
					class inputClick implements DialogInterface.OnClickListener //����ͼƬ
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							Bitmap inputBitmap=null;
							try 
							{
								File file=new File(path[selection]);
								FileInputStream fis;
								fis = new FileInputStream(file);
								Bitmap bmp = BitmapFactory.decodeStream(fis);
								inputBitmap=bmp.copy(Config.ARGB_8888, true);
								CanvasView.ensureBitmapRecycled(bmp);
								fis.close();
							} 
							catch (FileNotFoundException e) 
							{
								e.printStackTrace();
							} catch (IOException e) {
								// TODO �Զ����ɵ� catch ��
								e.printStackTrace();
							}

							//ˢ�±���
							CanvasView canvasVi=MainActivity.getCanvasView();
							canvasVi.setBackgroundBitmap(inputBitmap);
							
							Toast.makeText(MainActivity.getContext(),"�ѽ���ͼƬ�ɹ����뵽����",Toast.LENGTH_SHORT).show();
						}		
					}
					class cancelClick implements DialogInterface.OnClickListener //ȡ��
					{
						public void onClick(DialogInterface dialog, int which) 
						{
						}		
					}
					
					//ʵ����ȷ�϶Ի���
					Builder dialog=new AlertDialog.Builder(MainActivity.getContext());
					dialog.setIcon(drawable.ic_dialog_info);
					dialog.setMessage("��Ҫ�Ը�ͼƬ����ʲô������");
					dialog.setPositiveButton("����", new shareClick());
					dialog.setNeutralButton("����", new inputClick());
					dialog.setNegativeButton("ȡ��", new cancelClick());
					dialog.create();
					dialog.show();
			
					return false;
				}
			});

			return iv;

		}
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
