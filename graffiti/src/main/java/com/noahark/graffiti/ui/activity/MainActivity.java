package com.noahark.graffiti.ui.activity;
import android.R.drawable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.noahark.graffiti.R;
import com.noahark.graffiti.ui.dialog.ColorpickerDialog;
import com.noahark.graffiti.ui.dialog.PenDialog;
import com.noahark.graffiti.ui.fragment.GalleryFragment;
import com.noahark.graffiti.ui.fragment.HelpFragment;
import com.noahark.graffiti.ui.view.CanvasView;
import com.noahark.graffiti.extra.Pattern;
import com.noahark.graffiti.extra.Pel;
import com.noahark.graffiti.snow.RecordThread;
import com.noahark.graffiti.step.CopypelStep;
import com.noahark.graffiti.step.DeletepelStep;
import com.noahark.graffiti.step.DrawpelStep;
import com.noahark.graffiti.step.FillpelStep;
import com.noahark.graffiti.step.Step;
import com.noahark.graffiti.step.TransformpelStep;
import com.noahark.graffiti.touch.CrossfillTouch;
import com.noahark.graffiti.touch.DrawBesselTouch;
import com.noahark.graffiti.touch.DrawBrokenlineTouch;
import com.noahark.graffiti.touch.DrawFreehandTouch;
import com.noahark.graffiti.touch.DrawLineTouch;
import com.noahark.graffiti.touch.DrawOvalTouch;
import com.noahark.graffiti.touch.DrawPolygonTouch;
import com.noahark.graffiti.touch.DrawRectTouch;
import com.noahark.graffiti.touch.DrawTouch;
import com.noahark.graffiti.touch.KeepDrawingTouch;
import com.noahark.graffiti.touch.Touch;
import com.noahark.graffiti.touch.TransformTouch;
import com.noahark.moments.ui.activity.CommunityActivity;
import com.noahark.moments.ui.activity.LoginActivity;
import com.noahark.moments.ui.activity.MeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;


public class MainActivity extends FragmentActivity implements RecognizerDialogListener
{
	/*************************************************/
	private String softDir="/FreeGraffiti";
	public static Context context;
	public static int SCREEN_WIDTH,SCREEN_HEIGHT;
	/*************************************************/
	//�����еĸ�����Ƭ
	public  static final int MAIN_FRAGMENT=0;//������ҳ��
	private final int GALLERY_FRAGMENT=1;//����ҳ��
	private final int HELP_FRAGMENT=2;//�ʺ�ҳ��
	public  static int curFragmentFlag=MAIN_FRAGMENT;//��ǰҳ���־
	private Fragment curFragment; //��ǰҳ��
	/*************************************************/
	
	
	//�ڲ��㷨
	private List<Pel> pelList;
	private Pel selectedPel;
	private Stack<Step> undoStack;
	private static Stack<Step> redoStack;	
	private static CanvasView canvasVi;
	
	/*************************************************/
	//�˳�ϵͳ��
	private Builder dialog;
	private boolean hasExitAppDialog=false;
	/*************************************************/
	
	//�ؼ�;
	public static View[] allBtns;
	public  static View topToolbarSclVi;
	private static View downToolbarSclVi;
	private static Button undoBtn;
	private static Button redoBtn;
	private Button openPelbarBtn;
	private static View transbarLinlayout;
	
	private static PopupWindow pelbarPopwin;
	private static PopupWindow canvasbgbarPopwin;
	
	private static Button extendBtn;
	public static Button colorBtn;//��ɫ��ť������
	
	static public DrawerLayout drawerLayout;//������
	private ActionBarDrawerToggle drawerToggle;//����ʵ����
	
	//�Ի���
	private PenDialog penDialog;//��ɫ��Ի���
	private ColorpickerDialog colorpickerDialog;//��ɫ��Ի���
	
	//������
	public static Button curToolVi;//����������ǰѡ�еĹ���
	private static ImageView curPelVi;//ͼԪ������ǰѡ�е�ͼԪ
	private ImageView curCanvasbgVi,whiteCanvasbgVi;//����������ǰѡ�еı���
	/**************************************************************************************/
	//������
	public  static Thread keepdrawingThread;
	public  static PointF lastPoint=new PointF();
	public  static Pel   newPel;
	public  static SensorManager sensorManager;
	public  static SensorEventListener sensorEventListener;
	public  static SensorEventListener shakeSensorEventListener;
	public  static Sensor sensor;
	private int    responseCount=0;

	// ��������������TransformTouch�д���
	public static final int NOSENSOR = 0;// û�д�����
	public static final int ACCELEROMETER = 1;// ���ٶ�
	public static final int PROXIMITY =2; //�ƽ�
	public static final int ORIENTATION = 3;// ����
	public static int sensorMode=NOSENSOR;

	//ҡһҡ
	private Vibrator vibrator;
	private static boolean  sharkWaiting=false;
	
	//��������
	private static Matrix transMatrix=new Matrix(); //��ű任�����ӵ���ʱ����
	private static Matrix savedMatrix=new Matrix();
	private Pel    savedPel;
	private Step   step;
	private PointF centerPoint=new PointF();
	private String savedPath;
	/**************************************************************************************/
	//����
    private static final int REQUEST_CODE_NONE = 0;   
    private static final int REQUEST_CODE_GRAPH = 1;//����
    private static final int REQUEST_CODE_PICTURE = 2; //����
    private static final String IMAGE_UNSPECIFIED = "image/*";
	/**************************************************************************************/
    //����ʶ��    
    private RecognizerDialog isrDialog;
    private final String APP_ID="514fb8d7";
    private String said;
	/**************************************************************************************/
    //ѩ������
    private static RecordThread recordThread = null;
    /**************************************************************************************/
	//���ȶԻ����Ҫ���
	private ProgressDialog progressDialog;
	final Handler loadInBitmapHandler=new Handler() //����ͼƬ�߳���Ϣ������
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				progressDialog.dismiss();
				canvasVi.setBackgroundBitmap((Bitmap)msg.getData().getParcelable("loadedBitmap"));
	
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		} 
	};
	
	//ҡһҡ
    final Handler shakeHandler = new Handler() 
    {  
		public void handleMessage(Message msg)
		{	
			try
			{
				onClearBtn(null);
			    super.handleMessage(msg);
			}
			catch(Exception e){}
		}  
    }; 
    /**************************************************************************************/	
	protected void onCreate(Bundle savedInstanceState) 
	{			
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	
	/*
	 * �Զ����Ա����
	 */
	
	//��ʼ�����
	public void initView()
	{
		setContentView(R.layout.activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//����
//		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //�ر����ƻ���
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.btn_selectpel_normal,0x5555,0x5555)
        {
            public void onDrawerOpened(View drawerView)
            {
        	    ensurePelbarClosed();
                ensureCanvasbgbarClosed();
                setToolsClickable(false);
            }
			public void onDrawerClosed(View drawerView) 
			{
				if(curFragmentFlag == MAIN_FRAGMENT && topToolbarSclVi.getVisibility() == View.VISIBLE)
				{
					setToolsClickable(true);
				}
			}            
        };
        drawerLayout.setDrawerListener(drawerToggle);//Ϊ����ע�����ʵ�����ֹpopwindow��פ��
					
		//����id��������ԭʼ���
		canvasVi=(CanvasView)findViewById(R.id.vi_canvas);
		extendBtn=(Button)findViewById(R.id.btn_extend);
		openPelbarBtn=(Button)findViewById(R.id.btn_openpelbar);
		topToolbarSclVi=(View)findViewById(R.id.sclvi_toptoolbar);
		downToolbarSclVi=(View)findViewById(R.id.sclvi_downtoolbar);
		undoBtn=(Button)findViewById(R.id.btn_undo);
		redoBtn=(Button)findViewById(R.id.btn_redo);
		transbarLinlayout=(View)findViewById(R.id.linlay_transbar);
		colorBtn=(Button)findViewById(R.id.btn_color);
		int[] btnIds=new int[]{R.id.btn_openpelbar,R.id.btn_opentransbar,R.id.btn_opencrossfill,
				R.id.btn_opencanvasbgbar,R.id.btn_openprocessingbar,R.id.btn_opendrawtext,R.id.btn_opendrawpicture,
				R.id.btn_opendrawer,R.id.btn_color,R.id.btn_pen,R.id.btn_clear,R.id.btn_save,
				R.id.btn_undo,R.id.btn_redo};
		allBtns=new View[btnIds.length];
		for(int i=0;i<btnIds.length;i++)
			allBtns[i]=(View)findViewById(btnIds[i]);
				
		//���쵯��ʽ����
		//ͼԪ��\�任��\�����\������\�ɾ��
		View pelbarVi=this.getLayoutInflater().inflate(R.layout.popwin_pelbar, null);
		pelbarPopwin=new PopupWindow(pelbarVi,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		View canvasbgbarVi=this.getLayoutInflater().inflate(R.layout.popwin_canvasbgbar, null);
		canvasbgbarPopwin=new PopupWindow(canvasbgbarVi,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);			
	
		//����id��ʼ������ѡ�е����
		curToolVi=openPelbarBtn;//��ʼ��ѡ��ͼԪ��ť
		curPelVi=(ImageView)pelbarVi.findViewById(R.id.btn_freehand);//��ʼ��ѡ�������ֻ水ť
		curCanvasbgVi=whiteCanvasbgVi=(ImageView)canvasbgbarVi.findViewById(R.id.btn_canvasbg0);//��ʼ��ѡ�С���ֽ��������ť
		/**************************************************************************************/
		// �õ�����������
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorEventListener=new singleHandSensorEventListener();
		
		//ҡһҡ
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); 
		shakeSensorEventListener=new shakeSensorEventListener();
		sensorManager.registerListener(shakeSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		/**************************************************************************************/
		
		//�Ի���
		penDialog = new PenDialog(MainActivity.this,R.style.GraffitiDialog);
		colorpickerDialog = new ColorpickerDialog(MainActivity.this,R.style.GraffitiDialog);
		
		/**************************************************************************************/	
		//����ʶ��Ի���
		isrDialog = new RecognizerDialog(this,"appid="+APP_ID);
		/**************************************************************************************/	
	}
	
	//��ʼ������
	public void initData()
	{ 
        //��������ͼƬ���洢���ļ���  
        File file = new File(Environment.getExternalStorageDirectory().getPath()+softDir);
        if(!file.exists())
          file.mkdirs();

		//��ȡ��Ļ���
		WindowManager wm = this.getWindowManager();
		SCREEN_WIDTH=wm.getDefaultDisplay().getWidth();
		SCREEN_HEIGHT=wm.getDefaultDisplay().getHeight();
		
		context=MainActivity.this;
		/**************************************************************************************/	
		//���ݽṹ
		pelList = CanvasView.getPelList();
		undoStack=CanvasView.getUndoStack();
		redoStack=CanvasView.getRedoStack();
		/**************************************************************************************/	
		//����ʶ��
		said="";
		isrDialog.setEngine("sms",null,null);
		isrDialog.setListener(this);
		/**************************************************************************************/
		//ѩ������
		blowHandler= new BlowHandler();
		canvasVi.LoadSnowImage();
		canvasVi.SetView(SCREEN_WIDTH, SCREEN_HEIGHT);
		/**************************************************************************************/
		//���þ���
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0); 
	}
	
/**
* ��ť�¼�
*/	
	
	/**
	 * Open/Close���ְ�ť
	 */
	
	//�򿪳���
	public void onOpenDrawerBtn(View v)
	{
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
	
		drawerLayout.openDrawer(Gravity.LEFT);
	}

	//�򿪱༭������Ϣ����
	public void onEnterMeBtn(View v)
	{
		boolean hasLogined = true; //���ʷ���˼���Ƿ��ѵ�¼%%%%%%%%%%%%%%%%
		if(hasLogined) //�ѵ�¼
		{
			Intent intent = new Intent(); // �����
			intent.setClass(MainActivity.this, MeActivity.class);
			startActivity(intent);
		}
		else //δ��¼
		{
			Intent intent = new Intent(); // �����
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}


	//�򿪻�ͼ����
	public void onOpenCommunityBtn(View v)
	{
		boolean hasLogined = true; //���ʷ���˼���Ƿ��ѵ�¼%%%%%%%%%%%%%%%%
		if(hasLogined) //�ѵ�¼
		{
			Intent intent = new Intent(); // �����
			intent.setClass(MainActivity.this, CommunityActivity.class);
			startActivity(intent);
		}
		else //δ��¼
		{
			Intent intent = new Intent(); // �����
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}

	//�򿪷��ػ���
	public void onCloseDrawerBtn(View v)
	{
		if(curFragmentFlag != MAIN_FRAGMENT)
		{
			curFragmentFlag = MAIN_FRAGMENT;
			getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
			if(canvasVi.getVisibility() != View.VISIBLE)
				canvasVi.setVisibility(View.VISIBLE);
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
	}		
	
	//�����Ϸ���
	public void onOpenShareBtn(View v) throws IOException
	{
    	String tmpPath=Environment.getExternalStorageDirectory().getPath()+ "/tmp"+".jpg";	
		Bitmap bitmap=CanvasView.getSavedBitmap();
		
        FileOutputStream fileOutputStream = new FileOutputStream(tmpPath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
        Toast.makeText(MainActivity.this, "ͼƬ�ѱ���("+tmpPath+")", Toast.LENGTH_SHORT).show();
        
        //��ת������λ��
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_TEXT, "#�����Ե���Ϳѻ(Free Graffiti)#");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM,android.net.Uri.parse(tmpPath));
		startActivity(Intent.createChooser(intent, null));
	}
	
	//���ҵĻ���
	public void onOpenGalleryBtn(View v)
	{
		if(curFragmentFlag != GALLERY_FRAGMENT)
		{
			curFragmentFlag = GALLERY_FRAGMENT;
			curFragment = new GalleryFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, curFragment).commit();
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
	}
	
	//�򿪰���
	public void onOpenHelpBtn(View v)
	{
		if(curFragmentFlag != HELP_FRAGMENT)
		{
			curFragmentFlag = HELP_FRAGMENT;
			curFragment = new HelpFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, curFragment).commit();
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
		Toast.makeText(MainActivity.this, "���·���������Ŷ~", Toast.LENGTH_SHORT).show();
	}
	
	//�򿪹�����
	public void onOpenToolsBtn(View v)
	{
		ensurePelFinished();//ȷ��ͼ���Ѿ���ȫ����
		
		if(sensorMode != NOSENSOR) //ȡ��ע�ᴫ��������װ�ò���step����undoջ���ö��ôβ���ͼԪ
		{
			ensureSensorFinished();
		}
		else
		{
			openTools();
		}
	}
	
	//�رչ�����
	public static void closeTools()
	{
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
		clearRedoStack();//�������ջ
		
		if(curToolVi.getId() == R.id.btn_opentransbar
				||curToolVi.getId() == R.id.btn_opencrossfill
				||curPelVi.getId() == R.id.btn_keepdrawing)
			extendBtn.setVisibility(View.VISIBLE);
		else
			extendBtn.setVisibility(View.GONE);
		
		Animation downDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.downdisappear);  		
		Animation topDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.topdisappear);  			
		Animation leftDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.leftdisappear);  
		Animation rightDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.rightdisappear);  
		
		downToolbarSclVi.startAnimation(downDisappearAnim);
		topToolbarSclVi.startAnimation(topDisappearAnim);
		undoBtn.startAnimation(rightDisappearAnim);	
		redoBtn.startAnimation(leftDisappearAnim);
		
		downToolbarSclVi.setVisibility(View.GONE);
		topToolbarSclVi.setVisibility(View.GONE);
		undoBtn.setVisibility(View.GONE);
		redoBtn.setVisibility(View.GONE);
		
		setToolsClickable(false);
		
		//�޴�����ע��
		if(sensorMode == NOSENSOR)
		{
			if(curToolVi.getId() == R.id.btn_opentransbar) //��Ϊѡ��ģʽ
			{
				Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);  
				transbarLinlayout.setVisibility(View.VISIBLE); //��ʾ�任��
				transbarLinlayout.startAnimation(leftAppearAnim);
			}
			else if(curPelVi.getId() == R.id.btn_brokenline
					||curPelVi.getId() == R.id.btn_polygon)
			{
				extendBtn.setVisibility(View.VISIBLE);
				extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
			}
		}
	}
	
	//��ͼ����
	public void onOpenPelbarBtn(View v)
	{
		ensureCanvasbgbarClosed();
		updateToolbarIcons(v);//���¹�����ͼ����ʾ
		
		if(pelbarPopwin.isShowing())//�����������
			pelbarPopwin.dismiss();//�ر�	
		else
			pelbarPopwin.showAtLocation(downToolbarSclVi,Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//��������

		//����ҲҪע�ᵱǰѡ��ͼԪ��touch
		switch(curPelVi.getId())
		{
			case R.id.btn_rect:	     	CanvasView.setTouch(new DrawRectTouch());break;
			case R.id.btn_bessel:    	CanvasView.setTouch(new DrawBesselTouch());break;
			case R.id.btn_oval:      	CanvasView.setTouch(new DrawOvalTouch());break;
			case R.id.btn_polygon:   	CanvasView.setTouch(new DrawPolygonTouch());break;
			case R.id.btn_brokenline:	CanvasView.setTouch(new DrawBrokenlineTouch());break;
			case R.id.btn_line:      	CanvasView.setTouch(new DrawLineTouch());break;
			case R.id.btn_freehand:  	CanvasView.setTouch(new DrawFreehandTouch());break;
			case R.id.btn_keepdrawing:  CanvasView.setTouch(new KeepDrawingTouch());break;
		}
	}
	
	//�򿪹�����
	public static void openTools()
	{
		if(transbarLinlayout.getVisibility() == View.VISIBLE) //����任��Ϊ��״̬
			transbarLinlayout.setVisibility(View.GONE);//�ر�
				
		//�������¹������Ķ���
		Animation downAppearAnim = AnimationUtils.loadAnimation(context, R.anim.downappear);  		
		Animation topAppearAnim = AnimationUtils.loadAnimation(context, R.anim.topappear);  
		Animation leftAppearAnim = AnimationUtils.loadAnimation(context, R.anim.leftappear);	
		Animation rightAppearAnim = AnimationUtils.loadAnimation(context, R.anim.rightappear);	
		downToolbarSclVi.startAnimation(downAppearAnim);
		topToolbarSclVi.startAnimation(topAppearAnim);
		redoBtn.startAnimation(leftAppearAnim);	
		undoBtn.startAnimation(rightAppearAnim);
		downToolbarSclVi.setVisibility(View.VISIBLE);
		topToolbarSclVi.setVisibility(View.VISIBLE);
		undoBtn.setVisibility(View.VISIBLE);
		redoBtn.setVisibility(View.VISIBLE);
		setToolsClickable(true);
	}
	
	//�򿪱任��
	public void onOpenTransbarBtn(View v)
	{	
		ensurePelbarClosed();
		ensureCanvasbgbarClosed();
		updateToolbarIcons(v);
		closeTools();
		
		sensorManager.unregisterListener(sensorEventListener);// ȡ����һ����������ע��
		sensorMode = NOSENSOR;
		
		CanvasView.setTouch(new TransformTouch());
	}

	//�򿪱�����
	public void onOpenCanvasbgbarBtn(View v)
	{
		ensurePelbarClosed();
		if(canvasbgbarPopwin.isShowing())//�����������
			canvasbgbarPopwin.dismiss();//�ر�	
		else
			canvasbgbarPopwin.showAtLocation(downToolbarSclVi,Gravity.BOTTOM, 0, downToolbarSclVi.getHeight());//��������
	}

	//�������
	public void onOpenCrossfillBtn(View v)
	{
		updateToolbarIcons(v);
		CanvasView.setTouch(new CrossfillTouch());
	}
	
	//�л���ͼƬ����ҳ��
	public void onOpenProcessingbarBtn(View v)
	{
		Intent intent = new Intent(); // �����
		intent.setClass(MainActivity.this, ProcessingActivity.class);
		startActivity(intent);
	}

	public void onOpenDrawtextBtn(View v)
	{
		Intent intent = new Intent(); // �����
		intent.setClass(MainActivity.this, DrawTextActivity.class);
		startActivity(intent);
	}
	
	public void onOpenDrawpictureBtn(View v)
	{
		Intent intent = new Intent(); // �����
		intent.setClass(MainActivity.this, DrawPictureActivity.class);
		startActivity(intent);
	}
	/**
	 * ͼ����
	 */

	//�����Σ��ӣ�
	public void onRectBtn(View v)
	{
		updatePelbarIcons((ImageView)v);//�ӿ�ȥ�򡢸ı丸�˵�
		CanvasView.setTouch(new DrawRectTouch());
	}	
	
	//�����������ӣ�
	public void onBesselBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawBesselTouch());	
	}	
	
	//����Բ���ӣ�
	public void onOvalBtn(View v)
	{
		updatePelbarIcons((ImageView)v);			
		CanvasView.setTouch(new DrawOvalTouch());	
	}
	
	//��ֱ�ߣ��ӣ�
	public void onLineBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawLineTouch());
	}
	
	//�����ߣ��ӣ�
	public void onBrokenlineBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawBrokenlineTouch());
	}
	
	//�����ֻ棨�ӣ�
	public void onFreehandBtn(View v)
	{
		updatePelbarIcons((ImageView)v);		
		CanvasView.setTouch(new DrawFreehandTouch());
	}
	
	//������Σ��ӣ�
	public void onPolygonBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new DrawPolygonTouch());
	}
	
	//���ּ��ٶ���ͼ
	public void onKeepdrawingBtn(View v)
	{
		updatePelbarIcons((ImageView)v);	
		CanvasView.setTouch(new KeepDrawingTouch());
		Toast.makeText(this, "ѡ����ʼ�����������Ӧ��ͼ", Toast.LENGTH_SHORT).show();
	}
	
	//��˷�����ʶ���ӣ�
	public void onMicroBtn(View v)
	{
		closeTools();//�Զ��رչ����������ͼ		
		isrDialog.show();//��ʾ�����Ի���
		Toast.makeText(this, "��������ʶͼ���ˡ����䡢̫�������ӡ�С�ݡ��ʡ�Ц����ָ��", Toast.LENGTH_LONG).show();
	}
	
	//Gps��λ��ͼ���ӣ�
	public void onGpsBtn(View v)
	{
		closeTools();//�Զ��رչ����������ͼ
		Toast.makeText(this, "�����ڿ���ͨ�����ߵ�·������ͼŶ~", Toast.LENGTH_LONG).show();

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, GpsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_in,R.anim.push_out);
	}	
	
	/**
	 * �任��
	 */
	//����ͼԪ
	public void onCopypelBtn(View v)
	{		
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();
		if(selectedPel != null)//ѡ����ͼԪ���ܽ���ɾ������
		{		
			Pel pel=(Pel)(selectedPel).clone();//��ѡ��ͼԪΪģ�ͣ�����һ���¶���
			(pel.path).offset(10, 10);//ƫ��һ�������Ѻ�ʾ��
			(pel.region).setPath(pel.path, CanvasView.getClipRegion());
			
			(pelList).add(pel);
			undoStack.push(new CopypelStep(pel));//���á�����ѹ��undoջ
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();
		}
		else
		{
			Toast.makeText(MainActivity.this, "����ѡ��һ��ͼ�Σ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	//���ͼԪ
	public void onFillpelBtn(View v)
	{
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();	
		if(selectedPel != null)//ѡ����ͼԪ���ܽ���ɾ������
		{
			Paint oldPaint=new Paint(selectedPel.paint);//���þɻ��ʣ�undo�ã�

			(selectedPel.paint).set(DrawTouch.getCurPaint());//�Ե�ǰ���ʵ�ɫ̬��Ϊѡ�л��ʵ�
			if(selectedPel.closure == true)//���ͼ��
				(selectedPel.paint).setStyle(Paint.Style.FILL);//�������
			else
				(selectedPel.paint).setStyle(Paint.Style.STROKE);//���߿�
			
			Paint newPaint=new Paint(selectedPel.paint);////�����»��ʣ�undo�ã�
			undoStack.push(new FillpelStep(selectedPel,oldPaint,newPaint));//���á�����ѹ��undoջ
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();//�����ͼԪ����Ȼ���»��廭��
		}
		else
		{
			Toast.makeText(MainActivity.this, "����ѡ��һ��ͼ�Σ�", Toast.LENGTH_LONG).show();
		}
	}	
		
	//ɾ��ͼԪ
	public void onDeletepelBtn(View v)
	{		
		ensureSensorTransFinished();
		
		selectedPel = CanvasView.getSelectedPel();
		if(selectedPel != null)//ѡ����ͼԪ���ܽ���ɾ������
		{
			undoStack.push(new DeletepelStep(selectedPel));//���á�����ѹ��undoջ
			(pelList).remove(selectedPel);
			
			CanvasView.setSelectedPel(selectedPel=null);
			canvasVi.updateSavedBitmap();//ɾ����ͼԪ����Ȼ���»��廭��
		}
		else
		{
			Toast.makeText(MainActivity.this, "����ѡ��һ��ͼ�Σ�", Toast.LENGTH_LONG).show();
		}
	}
	

/**
 * ����ͼ��
 */
	
	//����ͼԪ�����ͼ��
	public void updatePelbarIcons(ImageView v)
	{
		//ȥ�򡢼ӿ�
		curPelVi.setImageDrawable(null);//�ϴ�ѡ�е�ͼԪȥ��	
		v.setImageResource(R.drawable.bg_highlight_frame);//�ı��Ӳ˵���ͼƬ���ӿ�
		curPelVi=v;//ת�ӵ�ǰѡ��

		//�޸ĸ��˵�ͼ��
		int fatherDrawableId=0;
		switch(v.getId())
		{
			case R.id.btn_bessel:	  fatherDrawableId=R.drawable.btn_bessel_pressed;break;
			case R.id.btn_brokenline: fatherDrawableId=R.drawable.btn_brokenline_pressed;break;
			case R.id.btn_freehand:   fatherDrawableId=R.drawable.btn_freehand_pressed;break;
			case R.id.btn_line:		  fatherDrawableId=R.drawable.btn_line_pressed;break;
			case R.id.btn_oval:		  fatherDrawableId=R.drawable.btn_oval_pressed;break;
			case R.id.btn_polygon:	  fatherDrawableId=R.drawable.btn_polygon_pressed;break;
			case R.id.btn_rect:		  fatherDrawableId=R.drawable.btn_rect_pressed;break;
			case R.id.btn_keepdrawing:fatherDrawableId=R.drawable.btn_keepdrawing_pressed;break;
		}
		
		final Drawable fatherDrawable = getResources().getDrawable(fatherDrawableId);
		curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, fatherDrawable , null, null);
	}
	
	//���»������������ͼ��
	public void updateCanvasbgAndIcons(ImageView v)
	{
		//ȥ�򡢼ӿ�
		curCanvasbgVi.setImageDrawable(null);//�ϴ�ѡ�е�ͼԪȥ��	
		v.setImageResource(R.drawable.bg_highlight_frame);//�ı��Ӳ˵���ͼƬ���ӿ�
		curCanvasbgVi=v;//ת�ӵ�ǰѡ��
		
		int backgroundDrawable=0;
		switch(v.getId())
		{
			case R.id.btn_canvasbg0:backgroundDrawable=R.drawable.bg_canvas0;break;
			case R.id.btn_canvasbg1:backgroundDrawable=R.drawable.bg_canvas1;break;
			case R.id.btn_canvasbg2:backgroundDrawable=R.drawable.bg_canvas2;break;
			case R.id.btn_canvasbg3:backgroundDrawable=R.drawable.bg_canvas3;break;
			case R.id.btn_canvasbg4:backgroundDrawable=R.drawable.bg_canvas4;break;
			case R.id.btn_canvasbg5:backgroundDrawable=R.drawable.bg_canvas5;break;
			case R.id.btn_canvasbg6:backgroundDrawable=R.drawable.bg_canvas6;break;
			case R.id.btn_canvasbg7:backgroundDrawable=R.drawable.bg_canvas7;break;
			default:return;
		
		}
		canvasVi.setBackgroundBitmap(backgroundDrawable);
	}
	
	//���¹��������ͼ��
	public void updateToolbarIcons(View v)
	{
		Button btn=(Button)v;
		//��ס�����(ɸѡͼƬ��Դ)
		int lastDrawableId=0;//�ϴ�ѡ�еİ�ť���ص�ͼƬ
		switch(curToolVi.getId())
		{
			case R.id.btn_openpelbar:
			{
				//֮ǰ��ѡ�����ĸ�ͼ�Σ������Ǹ�ͼ�ε���ɫ����Ϊ����
				switch(curPelVi.getId())
				{
					case R.id.btn_bessel:	  lastDrawableId=R.drawable.btn_bessel_normal;break;
					case R.id.btn_brokenline: lastDrawableId=R.drawable.btn_brokenline_normal;break;
					case R.id.btn_freehand:	  lastDrawableId=R.drawable.btn_freehand_normal;break;
					case R.id.btn_line:		  lastDrawableId=R.drawable.btn_line_normal;break;
					case R.id.btn_oval:		  lastDrawableId=R.drawable.btn_oval_normal;break;
					case R.id.btn_polygon:    lastDrawableId=R.drawable.btn_polygon_normal;break;
					case R.id.btn_rect:		  lastDrawableId=R.drawable.btn_rect_normal;break;
					case R.id.btn_keepdrawing:lastDrawableId=R.drawable.btn_keepdrawing_normal;break;
				}
			}break;//ͼԪ
			case R.id.btn_opentransbar:  lastDrawableId=R.drawable.btn_selectpel_normal;break;//ѡ��
			case R.id.btn_opencrossfill:lastDrawableId=R.drawable.btn_crossfill_normal;break;//���
		}		
			
		int nextDrawableId=0;//�ղŰ��µİ�ť��Ҫ��ɵ�ͼƬ
		switch(v.getId())
		{
			case R.id.btn_openpelbar:
			{
				//֮ǰ��ѡ�����ĸ�ͼ�Σ������Ǹ�ͼ�ε���ɫ����Ϊ����
				switch(curPelVi.getId())
				{
					case R.id.btn_bessel:	  nextDrawableId=R.drawable.btn_bessel_pressed;break;
					case R.id.btn_brokenline: nextDrawableId=R.drawable.btn_brokenline_pressed;break;
					case R.id.btn_freehand:	  nextDrawableId=R.drawable.btn_freehand_pressed;break;
					case R.id.btn_line:		  nextDrawableId=R.drawable.btn_line_pressed;break;
					case R.id.btn_oval:		  nextDrawableId=R.drawable.btn_oval_pressed;break;
					case R.id.btn_polygon:    nextDrawableId=R.drawable.btn_polygon_pressed;break;
					case R.id.btn_rect:		  nextDrawableId=R.drawable.btn_rect_pressed;break;
					case R.id.btn_keepdrawing:nextDrawableId=R.drawable.btn_keepdrawing_pressed;break;
				}
			}break;//ͼԪ
			case R.id.btn_opentransbar:  nextDrawableId=R.drawable.btn_selectpel_pressed;break;//ѡ��
			case R.id.btn_opencrossfill: nextDrawableId=R.drawable.btn_crossfill_pressed;break;//�������
		}
		
		final Drawable lastDrawable = getResources().getDrawable(lastDrawableId);
		curToolVi.setCompoundDrawablesWithIntrinsicBounds(null, lastDrawable , null, null);
		curToolVi.setTextColor(Color.WHITE);
		
		final Drawable nextDrawable = getResources().getDrawable(nextDrawableId);
		btn.setCompoundDrawablesWithIntrinsicBounds(null, nextDrawable , null, null);
		btn.setTextColor(Color.parseColor("#0099CC"));

		curToolVi=btn;//ת�ӵ�ǰѡ��
	}

/**
 * ȷ����ȷ�رպ����
 */
	//ȷ������ͼ�����ر�
	private static void ensurePelbarClosed()
	{
		if(pelbarPopwin.isShowing())//�����������
			pelbarPopwin.dismiss();//�ر�	
	}
	
	//ȷ�������������ر�
	private static void ensureCanvasbgbarClosed()
	{
		if(canvasbgbarPopwin.isShowing())
			canvasbgbarPopwin.dismiss();//�ر�
	}
	
	//ȷ��δ�����ͼԪ�ܹ������ö�
	private void ensurePelFinished()
	{	
		Touch touch=CanvasView.getTouch();
		String className=touch.getClass().getSimpleName();	
		selectedPel = CanvasView.getSelectedPel();
		
		if(selectedPel != null)
		{					
			//ʹ��Ϊ�ö�ͼԪ�Ĳ���(�����������ߡ������)
			if(className.equals("DrawBesselTouch"))
			{
				touch.control=true;
				touch.up();
			}
			else if(className.equals("DrawBrokenlineTouch"))
			{
				touch.hasFinished=true;
				touch.up();
			}
			else if(className.equals("DrawPolygonTouch"))
			{
				(touch.curPoint).set(touch.beginPoint);
				touch.up();
			}
			else //����ѡ��
			{
				CanvasView.setSelectedPel(null);//ʧȥ����
				canvasVi.updateSavedBitmap();//�ػ�λͼ
			}
		}
	}
	
	private void ensureSensorTransFinished()
	{
		//�Ƿ��Ǵ�����ƽ�ơ����š���ת;�е����
		if(sensorMode != NOSENSOR)
		{
			// ȡ����ǰ��������ע��
			sensorManager.unregisterListener(sensorEventListener);// ȡ����һ����������ע��
			sensorMode = NOSENSOR;
			
			//selectedPel��·����ɵ������ö�
			(selectedPel.region).setPath(selectedPel.path, CanvasView.getClipRegion());
			
			//undoջ����
			step.setToUndoMatrix(transMatrix);//���ý��иôβ����ı任����
			undoStack.push(step);//���á�����ѹ��undoջ
		}
	}
	
	private void ensureSensorFinished() //��֤�������ر�
	{
		if(curToolVi.getId() == R.id.btn_opentransbar) //�任ͼԪ
		{
			decideTranspel();
		}
		else //������ͼ
		{
			decideKeepdrawing();
		}

		if(recordThread != null)
		{
			recordThread.stopRecord(); //��ֹ��Ƶ��������
			recordThread=null;
		}
		
		sharkWaiting=false;//���¿���ҡһҡ����
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);//��ȥok��ť
	}
	
//	private void setToolsInClickable()//����򿪺��ֹ�������а�ť
//	{
//		((Button)findViewById(R.id.btn_openpelbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_opentransbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_opencrossfill)).setClickable(false);
//		((Button)findViewById(R.id.btn_opencanvasbgbar)).setClickable(false);
//		((Button)findViewById(R.id.btn_openprocessingbar)).setClickable(false);
//		
//		((Button)findViewById(R.id.btn_opendrawer)).setClickable(false);
//		((Button)findViewById(R.id.btn_color)).setClickable(false);
//		((Button)findViewById(R.id.btn_pen)).setClickable(false);
//		((Button)findViewById(R.id.btn_clear)).setClickable(false);
//		((Button)findViewById(R.id.btn_save)).setClickable(false);
//		
//		extendBtn.setClickable(false);
//	}
//	
//	private void setToolsClickable() //����رպ����������а�ť
//	{
//		((Button)findViewById(R.id.btn_openpelbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_opentransbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_opencrossfill)).setClickable(true);
//		((Button)findViewById(R.id.btn_opencanvasbgbar)).setClickable(true);
//		((Button)findViewById(R.id.btn_openprocessingbar)).setClickable(true);
//		
//		((Button)findViewById(R.id.btn_opendrawer)).setClickable(true);
//		((Button)findViewById(R.id.btn_color)).setClickable(true);
//		((Button)findViewById(R.id.btn_pen)).setClickable(true);
//		((Button)findViewById(R.id.btn_clear)).setClickable(true);
//		((Button)findViewById(R.id.btn_save)).setClickable(true);
//
//		extendBtn.setClickable(true);
//	}
	
	private void decideTranspel()
	{
		// ȡ����ǰ��������ע��
		sensorManager.unregisterListener(sensorEventListener);// ȡ����һ����������ע��
		sensorMode = NOSENSOR;
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
		
		//selectedPel��·����ɵ������ö�
		(selectedPel.region).setPath(selectedPel.path, CanvasView.getClipRegion());
		
		//undoջ����
		step.setToUndoMatrix(transMatrix);//���ý��иôβ����ı任����
		undoStack.push(step);//���á�����ѹ��undoջ
		
		//��Ļ
		CanvasView.setSelectedPel(null);//ʧȥ����
		canvasVi.updateSavedBitmap();//�ػ�λͼ
	}
	
	private void decideKeepdrawing()
	{
		// ȡ����ǰ��������ע��
		sensorManager.unregisterListener(sensorEventListener);// ȡ����һ����������ע��
		sensorMode = NOSENSOR;
		extendBtn.setBackgroundResource(R.drawable.btn_extend_normal);
		
		//�ö���ͼԪ��·�������򣬻���,����
		(newPel.region).setPath(newPel.path, CanvasView.getClipRegion());
		(newPel.paint).set(DrawTouch.getCurPaint());

		/**
		 * ���²���
		 */
		
		//1.���»��õ�ͼԪ����ͼԪ������
		pelList.add(newPel);
		
		//2.��װ�õ�ǰ���� �ڵĲ���
		undoStack.push(new DrawpelStep(newPel));//���á�����ѹ��undoջ

		//3.�����ػ�λͼ
		CanvasView.setSelectedPel(selectedPel = null);//�ղŻ���ͼԪʧȥ����
		canvasVi.updateSavedBitmap();//�ػ�λͼ	
	}
	
	//�������ջ
	public static void clearRedoStack()
	{
		if(!redoStack.empty())//redoջ����
			redoStack.clear();//���redoջ
	}
	
/**
 * ���������
 */	
	/**
	 *���ִ�����ģʽ
	 */
	
	/**
	 * ע����ٶȴ�����
	 */
	
	//ƽ��ͼԪ
	public void onTranslatepelBtn(View v)
	{
		registerTranspelSensor(v);
	}

	/**
	 * ע��ƽ�������
	 */
	//����ͼԪ
	public void onZoompelBtn(View v)
	{
		registerTranspelSensor(v);
	}
	
	/**
	 * ע�᷽�򴫸���
	 */
	//��תͼԪ
	public void onRotatepelBtn(View v)
	{
		registerTranspelSensor(v);
	}	
	
	/**
	 * ���ô�����
	 */
	//���ּ��ٶ���ͼ������
	public static void registerKeepdrawingSensor(View v)
	{
		sharkWaiting=true;//�ر�ҡһҡ������Ӧ
		
		newPel=new Pel();
		newPel.closure=true;
		lastPoint.set(CanvasView.getTouch().curPoint);
		(newPel.path).moveTo(lastPoint.x,lastPoint.y);
		
		sensorMode = ACCELEROMETER; //���ٶ�ע���־
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//����Ʈ���̺߳ͼ��ٶȴ���������
		registerSnowThread();
		sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_GAME);
		extendBtn.setBackgroundResource(R.drawable.selector_ok);
	}
	
	//�任ͼԪ������
	public void registerTranspelSensor(View v)
	{
		sharkWaiting=true;//�ر�ҡһҡ������Ӧ
		
		selectedPel = CanvasView.getSelectedPel();
		savedPel = new Pel();
		if(selectedPel != null) //ѡ����ͼԪ��ſ���ע�ᴫ����
		{
			//��ǰ�����ѡ��ͼԪ�ĳ�ʼ·������ʼ����
			(savedPel.path).set(selectedPel.path);
			savedMatrix.set(TransformTouch.calPelSavedMatrix(savedPel));
			
			//��������
			step=new TransformpelStep(selectedPel);//�ɳ�ʼͼԪ�����任�Ͳ���
			
			//���������
			sensorManager.unregisterListener(sensorEventListener);// ȡ����һ����������ע��
			//��v����ע���ĸ�������
			switch(v.getId())
			{
				case R.id.btn_translatepel:
				{			
					sensorMode = ACCELEROMETER;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

					Toast.makeText(this, "�ڶ��ֻ�", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "��һ������ֹͣ", Toast.LENGTH_SHORT).show();
				}break;
				case R.id.btn_zoompel:
				{
					sensorMode = PROXIMITY;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
					
					//�ٴ�ȷ����������
					centerPoint.set(TransformTouch.calPelCenterPoint(selectedPel));				
					
					Toast.makeText(this, "ǰ���ط�ת�ֻ�", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "��һ������ֹͣ", Toast.LENGTH_SHORT).show();
				}break;
				case R.id.btn_rotatepel:
				{
					sensorMode = ORIENTATION;
					sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

					//�ٴ�ȷ����������
					centerPoint.set(TransformTouch.calPelCenterPoint(selectedPel));
					
					Toast.makeText(this, "���Ҳ�ط�ת�ֻ�", Toast.LENGTH_SHORT).show();
					Toast.makeText(this, "��һ������ֹͣ", Toast.LENGTH_SHORT).show();
				}break;
			}	
			registerSnowThread();
			sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_GAME);
			extendBtn.setBackgroundResource(R.drawable.selector_ok); //�乴ͼ��
		}
		else
		{
			Toast.makeText(MainActivity.this, "����ѡ��һ��ͼ�Σ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	//ע��Ʈѩ�߳�
	private static void registerSnowThread()
	{
		if(recordThread == null) //Ʈѩ�߳�δ����
		{
			recordThread = new RecordThread(blowHandler, 1); // �����ť�������߳�
			recordThread.start();
			canvasVi.setStatus(false);
		}
	}
	
	//���ֲ���������������
	class singleHandSensorEventListener implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event) //�������仯
		{	
			if(curToolVi.getId() == R.id.btn_opentransbar) //�任ͼԪ
			{	
				if (sensorMode == ACCELEROMETER) //ƽ��
				{
					float dx = -50 * event.values[0];
					float dy =  90 * event.values[1];
					
					transMatrix.set(savedMatrix);
					transMatrix.postTranslate(dx,dy);
				}  
				else if (sensorMode == PROXIMITY) //����
				{
					float scale = -event.values[1]/30;
					
					transMatrix.set(savedMatrix);
					transMatrix.postScale(scale, scale,centerPoint.x,centerPoint.y);
				}
				else if (sensorMode == ORIENTATION) //��ת
				{
					float degree= event.values[2]*4;
			
					transMatrix.set(savedMatrix);
					transMatrix.setRotate(degree,centerPoint.x,centerPoint.y);	
				}

				if(selectedPel != null)
				{
					(selectedPel.path).set(savedPel.path);
					(selectedPel.path).transform(transMatrix);// ������ͼԪ
		
					canvasVi.invalidate();//ˢ��
				}
			}
			else if(curToolVi.getId() == R.id.btn_openpelbar)//���ּ��ٶ���ͼ
			{
				if(responseCount % 2 == 0)
				{	
					float dx = -event.values[0];
					float dy =  event.values[1];
					PointF nowPoint=new PointF(lastPoint.x+dx,lastPoint.y+dy);

					(newPel.path).quadTo(lastPoint.x,lastPoint.y, (lastPoint.x+nowPoint.x)/2, (lastPoint.y+nowPoint.y)/2);
					lastPoint.set(nowPoint);

					CanvasView.setSelectedPel(selectedPel = newPel);
					canvasVi.invalidate();//ˢ��
				}
				responseCount++;
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) //����
		{	
		}
	}
	
	//���ֲ���������������
	class shakeSensorEventListener implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event) //�������仯
		{	
			if(sharkWaiting == false)
			{
	           float[] values = event.values;  
	            float x = values[0]; // x�᷽����������ٶȣ�����Ϊ��   
	            float y = values[1]; // y�᷽����������ٶȣ���ǰΪ��   
	            float z = values[2]; // z�᷽����������ٶȣ�����Ϊ��   
	          
	            // һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��   
	            int medumValue = 19;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��   
	            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {  
	            	sharkWaiting=true;//���ڵȴ��û�ѡ���Ƿ����
	            	vibrator.vibrate(200);   
	                shakeHandler.sendEmptyMessage(0);  
	            } 
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) //����
		{	
		}
	}
	
/**
 * �м䲿�ְ�ť
 */
	//����
	public void onUndoBtn(View v)
	{
		if(!undoStack.empty())//�����ģʽ����ջ��Ϊ��
		{		
			Step step=undoStack.pop();//��undoջ����ջ��
			step.toRedoUpdate();//����ջ������ĸ��·���
			redoStack.push(step);//��ջ��ת�ƽ�redoջ
		}
	}	
	
	//����
	public void onRedoBtn(View v)
	{	
		if(!redoStack.empty())//�����ģʽ����ջ��Ϊ��
		{
			Step step=redoStack.pop(); //��redoջ����ջ��
			step.toUndoUpdate();//����ջ������ĸ��·���
			undoStack.push(step);//��ջ��ת�ƽ�undoջ
		}
	}	
	
	//���
	public void onClearBtn(View v)
	{
		//�����ٴ�ȷ�϶Ի���
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
				clearData();
				sharkWaiting=false;
			}		
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				sharkWaiting=false;
			}		
		}
		
		//ʵ����ȷ�϶Ի���
		Builder dialog=new AlertDialog.Builder(MainActivity.this);
		dialog.setIcon(drawable.ic_dialog_info);
		dialog.setMessage("��ȷ��Ҫ��գ�");
		dialog.setPositiveButton("ȷ��", new okClick());
		dialog.setNegativeButton("ȡ��", new cancelClick());
		dialog.create();
		dialog.show();
	}
	//����ڲ���������
	public void clearData()
	{
		pelList.clear();
		undoStack.clear();
		redoStack.clear();
		CanvasView.setSelectedPel(null);//����ѡ�е�ͼԪʧȥ����
		updateCanvasbgAndIcons(whiteCanvasbgVi);//��������ͼ�긴λ
		canvasVi.setBackgroundBitmap();//���������ɫ�ĵط�
	}
	//�ʴ�
	public void onPenBtn(View v)
	{
		penDialog.show();
	}
	
	//��ɫ��
	public void onColorBtn(View v)
	{
		colorpickerDialog.show();
		colorpickerDialog.picker.setOldCenterColor(DrawTouch.getCurPaint().getColor()); //��ȡ��ǰ���ʵ���ɫ��Ϊ���Բ��ɫ
	}
	
	//����
	public void onSaveBtn(final View v)
	{
		final EditText editTxt=new EditText(MainActivity.this);//��Ʒ�����Ʊ༭��
		//�����༭�Ի���
		class okClick implements DialogInterface.OnClickListener
		{
			public void onClick(DialogInterface dialog, int which) //ok
			{
		        try 
		        {
		        	savedPath=Environment.getExternalStorageDirectory().getPath()+softDir + "/"+editTxt.getText().toString()+".jpg";
		        	File file=new File(savedPath);
		        	
		        	if(!file.exists()) //�ļ�������
		        	{
		        		Bitmap bitmap=CanvasView.getSavedBitmap();
			            FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
			            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			            fileOutputStream.close();
			            
			            Toast.makeText(MainActivity.this, "ͼƬ�ѱ���("+savedPath+")", Toast.LENGTH_SHORT).show();
		        	}
		        	else //�ļ�����
		        	{   
			            //ѯ���û��Ƿ񸲸���ʾ��
			    		Builder dialog1=new AlertDialog.Builder(MainActivity.this);
			    		dialog1.setIcon(drawable.ic_dialog_info);
			    		dialog1.setMessage("�������Ѵ��ڣ��Ƿ񸲸ǣ�");
			    		dialog1.setPositiveButton("����", new OnClickListener()
			    		{
							public void onClick(DialogInterface dialog, int which) 
							{
								try 
								{
					        		Bitmap bitmap=CanvasView.getSavedBitmap();
						            FileOutputStream fileOutputStream = new FileOutputStream(savedPath);
						            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
						            try 
						            {
										fileOutputStream.close();
							            Toast.makeText(MainActivity.this, "ͼƬ�ѱ���("+savedPath+")", Toast.LENGTH_SHORT).show();
									} catch (IOException e) 
									{
										e.printStackTrace();
									}
								} 
								catch (FileNotFoundException e) 
								{
									e.printStackTrace();
								}
							}		
			    		});
			    		dialog1.setNegativeButton("ȡ��", new OnClickListener()
			    		{
							public void onClick(DialogInterface dialog, int which) 
							{
							}		
			    		});
			    		dialog1.create();
			    		dialog1.show();
		        	}
		        } 
		        catch (Exception e) 
		        {
		        	e.printStackTrace();
		        }
			}
		}		
		class cancelClick implements DialogInterface.OnClickListener //cancel
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}		
		}
		
		//ʵ����ȷ�϶Ի���
		Builder dialog=new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("��������Ʒȡ�����ְ�~");
		dialog.setView(editTxt);
		dialog.setPositiveButton("����", new okClick());
		dialog.setNegativeButton("ȡ��", new cancelClick());
		dialog.create();
		dialog.show();
/*******************************************************************************/		
	}	
	
	//������
	public void onCanvasbg0Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}	
	
	public void onCanvasbg1Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg2Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg3Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg4Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg5Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg6Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	public void onCanvasbg7Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
	}
	
	//ͼ��
	public void onCanvasbg8Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
		
        Intent intent = new Intent(Intent.ACTION_PICK, null);                
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);    
        startActivityForResult(intent, REQUEST_CODE_PICTURE);

	}
	
	//����
	public void onCanvasbg9Btn(View v)
	{
		updateCanvasbgAndIcons((ImageView)v);
		
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"))); 
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name());
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Configuration.ORIENTATION_LANDSCAPE);
        startActivityForResult(intent, REQUEST_CODE_GRAPH);

	}
	
	//��ͼ�⡢������Ϻ���Ҫִ�о���Ķ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) 
	{
		//�����ʼ��ʱ����
        if (resultCode == REQUEST_CODE_NONE)            
        	return;        
        
        // ��������ģʽ�ġ�ȷ��������ȡ����������������ť��       
        if (requestCode == REQUEST_CODE_GRAPH) 
        {   
    		//���ȶԻ���
    		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
    		progressDialog.setMessage("�������룬���Ե�...");
    		progressDialog.show();
    		
        	new Thread(new Runnable()
        	{
				public void run() 
				{
					try 
					{
						//��ȡͼƬ
						File file=new File(Environment.getExternalStorageDirectory()+ "/temp.jpg");
						FileInputStream fis = new FileInputStream(file);
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = false;
						opts.inSampleSize = 2;
						Bitmap photo = BitmapFactory.decodeStream(fis, null, opts);
									
						//����λͼ��Ϣ��ˢ��
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", photo);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
						
						//ɾ��ͼƬ
						file.delete();
					}
					catch (Exception e) //ͼƬ�������쳣
					{
						e.printStackTrace();
					} 
				}	
        	}).start();
        }        
        
        // ����ͼ��ģʽ�ġ�ѡ�񡱡�����������ť��       
        if (requestCode == REQUEST_CODE_PICTURE) 
        {    
    		//���ȶԻ���
    		progressDialog=new ProgressDialog(com.noahark.graffiti.ui.activity.MainActivity.getContext());
    		progressDialog.setMessage("�������룬���Ե�...");
    		progressDialog.show();
    		
        	new Thread(new Runnable()
        	{
				public void run() 
				{
					try 
					{
						//��ȡѡ���ͼƬ
						Uri uri=data.getData();
						File file=new File(uri.getPath());
						FileInputStream fis = new FileInputStream(file);
						Bitmap pic = BitmapFactory.decodeStream(fis);
						fis.close();
						
						//����λͼ��Ϣ��ˢ��
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", pic);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
					}
					catch (Exception e) //ͼƬ�������쳣
					{
						//��ȡѡ���ͼƬ·��
						Uri uri = data.getData();
						String[] filePathColumn = { MediaStore.Images.Media.DATA };
						Cursor cursor = getContentResolver().query(uri,
						filePathColumn, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						String picturePath = cursor.getString(columnIndex);
						cursor.close();
						
						Options op = new Options(); 
						op.inJustDecodeBounds = true; 
						Bitmap pic = BitmapFactory.decodeFile(picturePath, op); 
						int xScale = op.outWidth / CanvasView.getCanvasWidth(); 
						int yScale = op.outHeight / CanvasView.getCanvasHeight(); 
						op.inSampleSize = xScale > yScale ? xScale : yScale; 
						op.inJustDecodeBounds = false; 
						pic = BitmapFactory.decodeFile(picturePath, op); 	
						
						//����λͼ��Ϣ��ˢ��
						Bundle data=new Bundle();
						data.putParcelable("loadedBitmap", pic);
						Message msg=new Message();
						msg.setData(data);
						loadInBitmapHandler.sendMessage(msg);
					}
				}	
        	}).start();
        }  
		super.onActivityResult(requestCode, resultCode, data);
	}
	/*************************************************************************************/	
/**
 * ��������
 */
	/**
	 * get()
	 */
	public static int getSensorMode()
	{
		return sensorMode;
	}

	public static CanvasView getCanvasView()
	{
		return canvasVi;
	}
	
	public static Context getContext()
	{
		return context;
	}

	/**
	 * back�������˳�Ӧ��
	 */	
	public boolean onKeyDown(int keyCode, KeyEvent event) // ����View�İ����¼�
	{
		//���·���
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if(hasExitAppDialog == false) //û���˳��Ի���
			{
				//�����ٴ�ȷ�϶Ի���
				class okClick implements DialogInterface.OnClickListener
				{
					public void onClick(DialogInterface dialog, int which) //ok
					{
						android.os.Process.killProcess(android.os.Process.myPid());//ɱ������
						MainActivity.this.onDestroy();//�ݻٻ
						System.exit(0);//����ϵͳ
					}		
				}		
				class cancelClick implements DialogInterface.OnClickListener //cancel
				{
					public void onClick(DialogInterface dialog, int which) 
					{
					}		
				}
				
				//ʵ����ȷ�϶Ի���
				dialog=new AlertDialog.Builder(MainActivity.this);
				dialog.setIcon(drawable.ic_dialog_info);
				dialog.setMessage("��ȷ��Ҫ�˳���");
				dialog.setPositiveButton("ȷ��", new okClick());
				dialog.setNegativeButton("ȡ��", new cancelClick());
				dialog.create();
				dialog.show();
			}
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && sensorMode == NOSENSOR) //�����������Ӧ����
		{
			onUndoBtn(null);
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && sensorMode == NOSENSOR)//������С����Ӧ����
		{
			onRedoBtn(null);
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU) //�˵�������������
		{
			if(topToolbarSclVi.getVisibility() == View.VISIBLE)
				closeTools();
			else
				onOpenToolsBtn(null);
		}
		return super.onKeyDown(keyCode, event);//�����ϼ�ҳ���������onkeydown�¼�
	}
	/**********************************************************************************/
	/**
	 * ����ʶ��
	 */
	//һ��˵������
	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) 
	{
		said+=results.get(0).text;
	}
	
	//���¡�˵���ˡ���ť
	@Override
	public void onEnd(SpeechError error) 
	{			
		//ʶ���said����״����ͼ	
		Pattern pattern=new Pattern();
		if(!said.equals("") && said != null)
		{
			if(said.equals("�ˡ�"))
			{
				Pel recognizedPel=pattern.drawMan();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("���䡣"))
			{
				Pel recognizedPel=pattern.drawFlower();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("̫����"))
			{
				Pel recognizedPel=pattern.drawSun();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("���ӡ�"))
			{
				Pel recognizedPel=pattern.drawHouse();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("С�ݡ�"))
			{
				Pel recognizedPel=pattern.drawGrass();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("�ʡ�"))
			{
				Pel recognizedPel=pattern.drawHouse();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("Ц����"))
			{
				Pel recognizedPel=pattern.drawSmileFace();
				addRecognizedPel(recognizedPel);
			}
			else if(said.equals("ָ����"))
			{
				Pel recognizedPel=pattern.drawRing();
				addRecognizedPel(recognizedPel);
			}			
			else
			{
				Toast.makeText(this, "��Ǹ���ղ�û����������˼",Toast.LENGTH_SHORT).show();
			}

			//����ִ�
			said="";
		}
	}
	public void addRecognizedPel(Pel recognizedPel)
	{
		/**
		 * ��������
		 */
		//��װͼԪ
		(recognizedPel.region).setPath(recognizedPel.path, CanvasView.getClipRegion());
		(recognizedPel.paint).set(DrawTouch.getCurPaint());
		
		//��������
		pelList.add(recognizedPel);//��������		
		undoStack.push(new DrawpelStep(recognizedPel));//���á�����ѹ��undoջ
		
		//���»���
		CanvasView.setSelectedPel(selectedPel = null);//�ղŻ���ͼԪʧȥ����
		canvasVi.updateSavedBitmap();//�ػ�λͼ
	}
	/**********************************************************************************/
	public void onOpenTransChildren(View v)
	{
		View parentBtn=(View)findViewById(R.id.btn_opentranschildren);
		
		View deletepelBtn=(View)findViewById(R.id.btn_deletepel);
		View copypelBtn=(View)findViewById(R.id.btn_copypel);
		View fillpelBtn=(View)findViewById(R.id.btn_fillpel);
		View rotatepelBtn=(View)findViewById(R.id.btn_rotatepel);
		View zoompelBtn=(View)findViewById(R.id.btn_zoompel);
		View translatepelBtn=(View)findViewById(R.id.btn_translatepel);
		
		if(deletepelBtn.getVisibility() == View.GONE)
		{
			parentBtn.setBackgroundDrawable(null);
			parentBtn.setBackgroundResource(R.drawable.btn_arrow_close);
	
			deletepelBtn.setVisibility(View.VISIBLE);
			copypelBtn.setVisibility(View.VISIBLE);
			fillpelBtn.setVisibility(View.VISIBLE);			
			rotatepelBtn.setVisibility(View.VISIBLE);
			zoompelBtn.setVisibility(View.VISIBLE);
			translatepelBtn.setVisibility(View.VISIBLE);
		}
		else
		{
			parentBtn.setBackgroundDrawable(null);
			parentBtn.setBackgroundResource(R.drawable.btn_arrow_open);
			
			deletepelBtn.setVisibility(View.GONE);
			copypelBtn.setVisibility(View.GONE);
			fillpelBtn.setVisibility(View.GONE);	
			rotatepelBtn.setVisibility(View.GONE);
			zoompelBtn.setVisibility(View.GONE);
			translatepelBtn.setVisibility(View.GONE);
		}
	}
	
	public void onBackBtn(View v)
	{
		drawerLayout.openDrawer(Gravity.LEFT);
	}
	/**********************************************************************/
	/**
	 * ѩ������
	 */
	public static BlowHandler blowHandler;
	class BlowHandler extends Handler 
	{
		public void sleep(long delayMillis) 
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(2), delayMillis);
		}
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case 1:
					recordThread.stopRecord();
					update();
					break;
				case 2:
					if(canvasVi.getStatus()) //ѩ��δ����
					{
						canvasVi.invalidate();
						sleep(100);
					}
					else //ѩ��Ʈ��
					{
						if(curToolVi.getId() == R.id.btn_opentransbar)
						{
							decideTranspel();//�ö�ͼԪ
						}
						else
						{
							decideKeepdrawing();//�ö�������ͼ
						}
						
						sharkWaiting=false; //��������ҡһҡ����
						recordThread.stopRecord(); //��ֹ��Ƶ��������
						recordThread=null;
					}
					break;
				default:
					break;
			}
		}
	};
	//ѩ������
	public void update() 
	{
		canvasVi.addRandomSnow();
		canvasVi.setStatus(true);
		blowHandler.sleep(200);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	public static void setToolsClickable(boolean bool)
	{	
		for(int i=0;i<allBtns.length;i++)
			allBtns[i].setClickable(bool);
	}
}
