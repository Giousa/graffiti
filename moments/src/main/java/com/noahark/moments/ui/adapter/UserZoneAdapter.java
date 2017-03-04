package com.noahark.moments.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.noahark.moments.R;
import com.noahark.moments.bean.UserZoneBean;
import com.rd.PageIndicatorView;
import com.stfalcon.frescoimageviewer.ImageViewer.Builder;
import com.stfalcon.frescoimageviewer.ImageViewer.OnImageChangeListener;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chicken on 2016/11/9.
 */
public class UserZoneAdapter extends BaseAdapter{

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<UserZoneBean> mUserZoneBeanList = null;

    private final int mListItemLayout = R.layout.listitem_userzone;
    private final int mListItemDayViewId = R.id.userzonelistitem_day;
    private final int mListItemMonthViewId = R.id.userzonelistitem_month;
    private final int mListItemSnapshotViewId = R.id.userzonelistitem_snapshot;
    private final int mListItemContentViewId = R.id.userzonelistitem_content;

    public UserZoneAdapter(Context context, List<UserZoneBean> userZoneBeanList)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUserZoneBeanList = userZoneBeanList;
    }

    @Override
    public int getCount() {
        return mUserZoneBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserZoneBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        //convertView�Ƿ����
        if(convertView == null) //������
        {
            //����
            convertView = mInflater.inflate(mListItemLayout,null);

            //װ��ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.dayTextView = (TextView) convertView.findViewById(mListItemDayViewId);
            viewHolder.monthTextView = (TextView) convertView.findViewById(mListItemMonthViewId);
            viewHolder.snapshotImageView = (SimpleDraweeView) convertView.findViewById(mListItemSnapshotViewId);
            viewHolder.contentTextView = (TextView)convertView.findViewById(mListItemContentViewId);
            convertView.setTag(viewHolder);
        }
        else
        {
            //��ȡ�����Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //˵˵��̬�ı�
        UserZoneBean userZoneBean = mUserZoneBeanList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        viewHolder.dayTextView.setText(Integer.toString(calendar.get(Calendar.DATE)));
        viewHolder.monthTextView.setText(Integer.toString(calendar.get(Calendar.MONTH) + 1) + "��");
        viewHolder.contentTextView.setText(userZoneBean.getContent());

        //˵˵����ͼƬ
        String pictureUri = userZoneBean.getPicture();
        if(pictureUri == null || pictureUri.isEmpty())
            viewHolder.snapshotImageView.setVisibility(View.GONE); //����
        else
        {
            viewHolder.snapshotImageView.setVisibility(View.VISIBLE);
            viewHolder.snapshotImageView.setImageURI(pictureUri);
        }

        return convertView;
    }


    static final class ViewHolder{

        private TextView dayTextView;
        private TextView monthTextView;
        private SimpleDraweeView snapshotImageView;
        private TextView contentTextView;
    }

    //�㿪ĳ��ͼƬ����Խ��뵱ǰ˵˵������ͼƬ�Ĳ鿴����
    class UserZoneImageViewOnClickListener implements OnClickListener{

        private Builder imageViewer;
        private int pictureTotalNum;

        UserZoneImageViewOnClickListener(Builder imageViewer, int pictureTotalNum)
        {
            this.imageViewer = imageViewer;
            this.pictureTotalNum = pictureTotalNum;
        }

        @Override
        public void onClick(View view) {

            //�������˵˵����ҳ
            Toast.makeText(mContext, "��ת������˵˵�������!", Toast.LENGTH_SHORT).show();

//            //��ȡ��ǰ��������ͼƬҳ��
//            int curPageIndex = Integer.parseInt((String)view.getTag());
//
//            //����ҳ��СԲ��
//            RelativeLayout overlayLayout = (RelativeLayout)View.inflate(mContext,R.layout.imgoverlay_circle,null);
//            PageIndicatorView pageIndicatorView = (PageIndicatorView) overlayLayout.findViewById(R.id.page_indicator);
//            pageIndicatorView.setCount(pictureTotalNum);
//            pageIndicatorView.setSelection(curPageIndex);
//
//            //��ҳʱ��Ӧ�Ķ���
//            OnImageChangeListener imageViewerTurnPageListener = new ImageViewerTurnPageListener(pageIndicatorView);
//
//            //��ʾϸ�²鿴����
//            imageViewer.setStartPosition(curPageIndex)
//                    .setOverlayView(overlayLayout)
//                    .setImageChangeListener(imageViewerTurnPageListener)
//                    .show();
        }
    }

    class ImageViewerTurnPageListener implements OnImageChangeListener{

        private PageIndicatorView pageIndicatorView;

        ImageViewerTurnPageListener(PageIndicatorView pageIndicatorView)
        {
            this.pageIndicatorView = pageIndicatorView;
        }

        @Override
        public void onImageChange(int curPageIndex) {

            pageIndicatorView.setSelection(curPageIndex);//����ҳ��СԲ��
        }
    }

    //���캬��pictureList��ͼƬ����鿴��ImageViewer
    private Builder createImageViewer(Context context, List<String> pictureList, GenericDraweeHierarchyBuilder hierarchyBuilder)
    {
        return new Builder(context, pictureList)
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder);
    }
}

