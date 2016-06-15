package com.yixia.pinnedheaderlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YangGuoShan on 16/6/15 17:07.
 * Describe:
 */
public class PinnedHeaderListView extends ListView {

    private Context context;
    private PinnedSectionLvAdapter adapter;
    private View currentPinnedHeader;
    private int mTranslateY;
    private boolean isPinnedHeaderShown;

    public PinnedHeaderListView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        isPinnedHeaderShown = false;
        setOnScrollListener(mOnScrollListener);
    }

    public void setData(ArrayList<ChooseCountryBean> datas) {
        if (adapter == null) {
            adapter = new PinnedSectionLvAdapter(context, datas);
            setAdapter(adapter);
        } else {
            adapter.setDatas(datas);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示顶部悬浮框
     */
    private synchronized void createPinnedHeader(int position) {

        View pinnedView = (TextView) adapter.getPinnedSectionView(position);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pinnedView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        }

        int heightMode = View.MeasureSpec.getMode(layoutParams.height);
        int heightSize = View.MeasureSpec.getSize(layoutParams.height);

        if (heightMode == View.MeasureSpec.UNSPECIFIED)
            heightMode = View.MeasureSpec.EXACTLY;

        int maxHeight = getHeight() - getListPaddingTop() - getListPaddingBottom();
        if (heightSize > maxHeight)
            heightSize = maxHeight;

        int ws = View.MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft() - getListPaddingRight(), View.MeasureSpec.EXACTLY);
        int hs = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        pinnedView.measure(ws, hs);
        pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(), pinnedView.getMeasuredHeight());

        currentPinnedHeader = pinnedView;
    }

    private String lastGroupName = "";
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                createPinnedHeader(view.getFirstVisiblePosition());
                invalidate();
            }
        }

        /**
         * 滚动时动态监测是否需要利用新顶部悬浮框顶替当前顶部悬浮框
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (adapter == null) return;
            // 屏幕中可以看到的顶部第一条
            ChooseCountryBean myData = adapter.getItem(firstVisibleItem);
            // 屏幕中可以看到的顶部第二条
            ChooseCountryBean nextData = adapter.getItem(firstVisibleItem + 1);

            // 对比第一二条数据
            if (!myData.getSort().equals(nextData.getSort())) {
                // 不同时即出现两个悬浮框互相顶替效果,
                // 则需要动态获取y轴偏移量,让顶部悬浮框在y轴上根据偏移量显示
                View childView = view.getChildAt(0);
                if (childView != null) {
                    mTranslateY = childView.getTop();
                    createPinnedHeader(firstVisibleItem); // 创建当前显示的悬浮框
                    postInvalidate();
                    System.out.println("ding ... " + mTranslateY);
                }
            } else {
                if ((currentPinnedHeader != null && isPinnedHeaderShown)) {
                    if (!myData.getSort().equals(lastGroupName)) {
                        createPinnedHeader(firstVisibleItem);
//                        Log.d("TAG", "create    merge " + firstVisibleItem);
                    } else {
//                        Log.d("TAG", "recycle  " + firstVisibleItem);
                    }
                    mTranslateY = 0;
                } else {
                }
            }
            lastGroupName = myData.getSort();
        }

    };

    /**
     * 核心方法 绘制顶部悬浮框
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (currentPinnedHeader != null) {
            View pinnedView = currentPinnedHeader;

            int pLeft = getListPaddingLeft();
            int pTop = getListPaddingTop();

            canvas.save();
            canvas.clipRect(pLeft, pTop, pLeft + pinnedView.getWidth(), pTop + pinnedView.getHeight());
            canvas.translate(pLeft, pTop + mTranslateY);
            drawChild(canvas, pinnedView, getDrawingTime());
            canvas.restore();

            isPinnedHeaderShown = true;
        }
    }

    /**
     * listview适配器,设置特殊item(本例中为蓝色背景的首字母栏)
     */
    public class PinnedSectionLvAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ChooseCountryBean> datas;
        public Map<String, Integer> maps;

        public PinnedSectionLvAdapter(Context context, ArrayList<ChooseCountryBean> datas) {
            this.context = context;
            this.datas = datas;
            sortLetter(datas);
        }

        public void setDatas(ArrayList<ChooseCountryBean> datas) {
            this.datas = datas;
        }

        /**
         * 获取需要顶部悬浮显示的view
         */
        public View getPinnedSectionView(int position) {
            ViewGroup view = (ViewGroup) getView(position, null, PinnedHeaderListView.this);
            View vAlpha = view.getChildAt(0);
            return vAlpha;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public ChooseCountryBean getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(context, R.layout.alpha_item, null);
                holder = new ViewHolder();
                holder.tvAlpha = (TextView) view.findViewById(R.id.alphaitem_tv_alpha);
                holder.tvContent = (TextView) view.findViewById(R.id.alphaitem_tv_content);
                holder.countryCode = (TextView) view.findViewById(R.id.countryCode);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }


            ChooseCountryBean myData = getItem(position);

            holder.tvAlpha.setText(myData.getSort());
            if (maps.get(myData.getSort()) == position) {
                holder.tvAlpha.setVisibility(View.VISIBLE);
            } else {
                holder.tvAlpha.setVisibility(View.GONE);
            }
            holder.tvAlpha.setTag(position);
            holder.countryCode.setText(datas.get(position).getCode());
            holder.tvContent.setText(datas.get(position).getCountry());

            return view;
        }

        private void sortLetter(ArrayList<ChooseCountryBean> datas) {
            Collections.sort(datas, new Comparator<ChooseCountryBean>() {
                @Override
                public int compare(ChooseCountryBean lhs, ChooseCountryBean rhs) {
                    if (rhs.getSort().equals("常用")) {
                        return 1;
                    }
                    return lhs.getSort().compareTo(rhs.getSort());
                }
            });

            maps = new HashMap<String, Integer>();
            for (int i = 0; i < datas.size(); i++) {
                if (!maps.containsKey(datas.get(i).getSort())) {
                    maps.put(datas.get(i).getSort(), i);
                }
            }
        }
    }

    class PinnedHeader {
        public View view;
        public int position;

        @Override
        public String toString() {
            return "PinnedHeader [view=" + view + ", position=" + position + "]";
        }
    }

    class ViewHolder {
        TextView tvAlpha, tvContent, countryCode;
    }
}