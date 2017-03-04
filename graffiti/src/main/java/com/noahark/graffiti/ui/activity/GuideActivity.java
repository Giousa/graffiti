package com.noahark.graffiti.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.noahark.graffiti.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

@SuppressLint("WorldReadableFiles")
public class GuideActivity extends Activity {
	private SharedPreferences preferences; // ��¼�ڼ��ν����app���������棩
	// �������һ��
	private static final int TO_THE_END = 0;
	// �뿪���һ��
	private static final int LEAVE_FROM_END = 1;

	// ֻ�����������ɾ��ͼƬ����
	private int[] ids = { R.drawable.bg_guide1, R.drawable.bg_guide2,
			R.drawable.bg_guide3, R.drawable.bg_guide4, R.drawable.bg_guide5,
			R.drawable.bg_guide6};

	private List<View> guides = new ArrayList<View>();
	private ViewPager pager;
	private ImageView enter; // �������
	private ImageView curDot;
	private LinearLayout dotContain; // �洢�������
	private int offset; // λ����
	private int curPos = 0; // ��¼��ǰ��λ��

	private Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			Intent intent = new Intent();
			intent.setClass(GuideActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
		}
	};

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
		int count = preferences.getInt("count", 0);

		// �жϳ�����ڼ������У�����ǵ�һ����������ת������ҳ��
		if (count == 0) {
			setContentView(R.layout.activity_guide);
			init();
		} else {
			setContentView(R.layout.linlay_flash);
			mHandler.sendEmptyMessageDelayed(0, 1000);// 3����ת
		}
		Editor editor = preferences.edit();
		// ��������
		editor.putInt("count", ++count);
		// �ύ�޸�
		editor.commit();
	}

	private ImageView buildImageView(int id) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(id);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_XY);
		return iv;
	}

	// ���ܽ��ܽ���ĳ�ʼ��
	private void init() {
		this.getView();
		initDot();
		ImageView iv = null;
		guides.clear();
		for (int i = 0; i < ids.length; i++) {
			iv = buildImageView(ids[i]);
			guides.add(iv);
		}

		System.out.println("guild_size=" + guides.size());

		// ��curDot�����ڵ����β�ν�Ҫ�����ʱ�˷���������
		curDot.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						// ��ȡImageView�Ŀ��Ҳ���ǵ�ͼƬ�Ŀ��
						offset = curDot.getWidth();
						return true;
					}
				});

		final GuidePagerAdapter adapter = new GuidePagerAdapter(guides);
		// ViewPager�������������������������ʹ��ListViewʱ�õ�adapter
		pager.setAdapter(adapter);
		pager.clearAnimation();
		// ΪViewpager����¼������� OnPageChangeListener
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				int pos = position % ids.length;

				moveCursorTo(pos);

				if (pos == ids.length - 1) {// �����һ����
					handler.sendEmptyMessageDelayed(TO_THE_END, 500);

				} else if (curPos == ids.length - 1) {
					handler.sendEmptyMessageDelayed(LEAVE_FROM_END, 100);
				}
				curPos = pos;
				super.onPageSelected(position);
			}
		});

	}

	/**
	 * ��layout��ʵ����һЩView
	 */
	private void getView() {
		dotContain = (LinearLayout) this.findViewById(R.id.dot_contain);
		pager = (ViewPager) findViewById(R.id.contentPager);
		curDot = (ImageView) findViewById(R.id.cur_dot);
		enter = (ImageView) findViewById(R.id.enter);
		enter.setOnClickListener(new OnClickListener() {
			/*
			 * �ؼ�����������ť��ͽ��뵽������������٣���Ҳ�������
			 */
			public void onClick(View v) {
				// �������
				Intent intent = new Intent(); // �����
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();// ���ٸ�Activity
				overridePendingTransition(R.anim.push_in,R.anim.push_out);
			}
		});
	}

	/**
	 * ��ʼ���� ImageVIew
	 * 
	 * @return ����true˵����ʼ����ɹ�������ʵ����ʧ��
	 */
	private boolean initDot() {

		if (ids.length > 0) {
			ImageView dotView;
			for (int i = 0; i < ids.length; i++) {
				dotView = new ImageView(this);
				dotView.setImageResource(R.drawable.bg_dot_normal);
				dotView.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

				dotContain.addView(dotView);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ƶ�ָ�뵽���ڵ�λ�� ����
	 * 
	 * @param position
	 *            ָ�������ֵ
	 * */
	private void moveCursorTo(int position) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation tAnim = new TranslateAnimation(offset * curPos,
				offset * position, 0, 0);
		animationSet.addAnimation(tAnim);
		animationSet.setDuration(300);
		animationSet.setFillAfter(true);
		curDot.startAnimation(animationSet);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TO_THE_END)
				enter.setVisibility(View.VISIBLE);
			else if (msg.what == LEAVE_FROM_END)
				enter.setVisibility(View.GONE);
		}
	};

	// ViewPager ������
	class GuidePagerAdapter extends PagerAdapter {

		private List<View> views;

		public GuidePagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1 % views.size()));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			// ע������һ��Ҫ����һ����΢���ֵ,��Ȼ�������ͻ�������
			return views.size() * 20;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			Log.e("tag", "instantiateItem = " + arg1);
			((ViewPager) arg0).addView(views.get(arg1 % views.size()), 0);
			return views.get(arg1 % views.size());
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

}
