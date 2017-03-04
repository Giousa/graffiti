package com.noahark.moments.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.noahark.moments.R;
import com.noahark.moments.bean.ChatBean;
import com.noahark.moments.ui.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity{

    private Context mContext;

    private ListView mChatListView;
    private ListView mChatSelectListView;
    private EditText mChatSelectEditText;
    private TextView mChatSelectCancelBtn;

    private PtrClassicFrameLayout refreshLayout;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
    }

    public void initView()
    {
        mContext = this;

        mChatListView = (ListView)findViewById(R.id.chatlist);
        //����б�ͷ
        LinearLayout mChatListHeaderView = (LinearLayout) View.inflate(this,R.layout.listitem_chat_header,null);
        mChatListView.addHeaderView(mChatListHeaderView);


        mChatSelectListView = (ListView)findViewById(R.id.chatlist_select);

        mChatSelectEditText = (EditText)findViewById(R.id.edittext_chat_select);
        mChatSelectEditText.setOnFocusChangeListener(new SelectEditTextFocuser());
        mChatSelectEditText.addTextChangedListener(new SelectEditTextWatcher());

        mChatSelectCancelBtn = (TextView)findViewById(R.id.btn_chat_selectcancel);


        //��ȡ���ش�ŵģ�ͷ���ǳơ�ʱ�䡢���һ�������¼��������������������
        List<ChatBean> chatBeanList = new ArrayList<ChatBean>();

        //�û�����
        ChatBean chatBean = new ChatBean();
        chatBean.setAvatar("http://imgsrc.baidu.com/forum/w%3D580/sign=537a10b1b899a9013b355b3e2d940a58/00bacbef76094b36e0e5d748a3cc7cd98d109d33.jpg");
        chatBean.setNickname("Ҧ��");
        chatBean.setDate("2016/10/11");
        chatBean.setContent("�����ǵ�����������������ص�ʱ�����ȴ��Ҫ����");

        //����
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);
        chatBeanList.add(chatBean);

        //����
        ChatAdapter adapter = new ChatAdapter(this,chatBeanList);
        mChatListView.setAdapter(adapter);



        refreshLayout = (PtrClassicFrameLayout)findViewById(R.id.refresh_layout_chat);

//		//�����Զ�����
//		refreshLayout.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				refreshLayout.autoRefresh(true);
//			}
//		}, 150);

        //��������
        refreshLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        refreshLayout.refreshComplete();
                    }
                }, 1500);
            }
        });
    }

    public void initData()
    {

    }

    public void onCloseTabPageBtn(View view)
    {
        this.finish();
    }


    //��������EditText��ʱ����Ӧ����
    class SelectEditTextFocuser implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocused) {

            if(hasFocused)
            {
                mChatListView.setVisibility(View.INVISIBLE);
                mChatSelectListView.setVisibility(View.VISIBLE);
                mChatSelectCancelBtn.setVisibility(View.VISIBLE);
            }
        }
    }


    //����EditText�����ı�ʱ��Ӧ����
    class SelectEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence selection, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence selection, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    //���������ġ�X��
    public void onSelectClearBtn(View view)
    {
        mChatSelectEditText.setText(""); //���
    }

    //���������ġ�ȡ����
    public void onSelectCancelBtn(View view)
    {
        mChatSelectEditText.setText(""); //���
        mChatListView.setVisibility(View.VISIBLE);
        mChatSelectListView.setVisibility(View.INVISIBLE);
        mChatSelectCancelBtn.setVisibility(View.GONE);
        mChatSelectEditText.clearFocus();
    }

    public void onEnterCommentAreaBtn(View view)
    {
        Toast.makeText(this,"�������۽���",Toast.LENGTH_SHORT).show();
    }

    public void onEnterLikeAreaBtn(View view)
    {
        Toast.makeText(this,"������޽���",Toast.LENGTH_SHORT).show();
    }
}
