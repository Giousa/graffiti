
package com.noahark.moments.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.noahark.moments.R;
import com.noahark.moments.bean.FollowBean;
import com.noahark.moments.ui.fragment.FansFragment;
import com.noahark.moments.ui.fragment.FollowFragment;

public class RelationActivity extends FragmentActivity {

    private Context mContext;

    private ViewPager mRelationPager;
    private PagerAdapter mRelationPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);

        initView();
        initData();
    }

    public void initView()
    {
        mContext = this;

        mRelationPager = (ViewPager) findViewById(R.id.relation_pager);
        mRelationPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mRelationPager.setAdapter(mRelationPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mRelationPager);
    }

    public void initData()
    {

    }

    public void onCloseTabPageBtn(View view)
    {
        finish();
    }

    //�����ӹ�ע
    public void onAddFollowBtn(View view)
    {
        FollowFragment followFragment = (FollowFragment) mRelationPagerAdapter.instantiateItem(mRelationPager,0);

        FollowBean followBean1 = new FollowBean();
        followBean1.setAvatar("http://tupian.enterdesk.com/2014/mxy/11/5/4/10.jpg");
        followBean1.setNickname("�¾���");
        followFragment.addListItem(followBean1);
    }

//    //��������EditText��ʱ����Ӧ����
//    class SelectEditTextFocuser implements OnFocusChangeListener{
//
//        @Override
//        public void onFocusChange(View view, boolean hasFocused) {
//
//            if(hasFocused)
//            {
//                mCareListView.setVisibility(View.INVISIBLE);
//                mCareSelectListView.setVisibility(View.VISIBLE);
//                mCareSelectCancelBtn.setVisibility(View.VISIBLE);
//            }
//        }
//    }


//    //����EditText�����ı�ʱ��Ӧ����
//    class SelectEditTextWatcher implements TextWatcher{
//
//        @Override
//        public void beforeTextChanged(CharSequence selection, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence selection, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//        }
//    }


//    //���������ġ�X��
//    public void onSelectClearBtn(View view)
//    {
//        mCareSelectEditText.setText(""); //���
//    }
//
//    //���������ġ�ȡ����
//    public void onSelectCancelBtn(View view)
//    {
//        mCareSelectEditText.setText(""); //���
//        mCareListView.setVisibility(View.VISIBLE);
//        mCareSelectListView.setVisibility(View.INVISIBLE);
//        mCareSelectCancelBtn.setVisibility(View.GONE);
//        mCareSelectEditText.clearFocus();
//    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"��ע", "��˿"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0)
                return FollowFragment.newInstance(position);
            else
                return FansFragment.newInstance(position);
        }
    }

}

