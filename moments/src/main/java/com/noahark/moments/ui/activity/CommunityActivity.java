package com.noahark.moments.ui.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.noahark.moments.R;

import butterknife.BindView;

public class CommunityActivity extends TabActivity {

    private TabHost mTabHost;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    public void initView()
    {
        setContentView(R.layout.activity_community);

        //tabhost�������,����4����ǩҳ�棬�Ա����һ��ҳ������ת
        mTabHost = this.getTabHost();

        //����
        Intent intent = new Intent().setClass(this,ChatActivity.class);
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec("chat").setIndicator("chat").setContent(intent);
        mTabHost.addTab(tabSpec);

        //��ע
        intent = new Intent().setClass(this,RelationActivity.class);
        tabSpec = mTabHost.newTabSpec("relation").setIndicator("relation").setContent(intent);
        mTabHost.addTab(tabSpec);

        //��̬
        intent = new Intent().setClass(this,CircleActivity.class);
        tabSpec = mTabHost.newTabSpec("circle").setIndicator("circle").setContent(intent);
        mTabHost.addTab(tabSpec);

        //����
        intent = new Intent().setClass(this,FindActivity.class);
        tabSpec = mTabHost.newTabSpec("find").setIndicator("find").setContent(intent);
        mTabHost.addTab(tabSpec);

        //��ʼѡ��
        mTabHost.setCurrentTabByTag("chat");
    }

    public void initData()
    {

    }

    //��������ҳ
    public void onChatBtn(View view)
    {
        mTabHost.setCurrentTabByTag("chat");
    }

    //���뻭��ҳ����ϵ��
    public void onRelationBtn(View view)
    {
        mTabHost.setCurrentTabByTag("relation");
    }

    //���붯̬ҳ
    public void onCircleBtn(View view)
    {
        mTabHost.setCurrentTabByTag("circle");
    }

    //���뷢��ҳ
    public void onFindBtn(View view)
    {
        mTabHost.setCurrentTabByTag("find");
    }
}
