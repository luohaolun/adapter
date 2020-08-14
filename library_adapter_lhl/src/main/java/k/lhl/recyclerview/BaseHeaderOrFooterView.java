package k.lhl.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 所有HeaderView或者是FooterView的基类
 * 因为需要强制我们的子类实现某些方法，所以这里用的是抽象类
 */
public abstract class BaseHeaderOrFooterView {

    private Context mContext;
    private View mView;
    protected int mViewHeight;
    protected LinearLayout.LayoutParams params;
    public static final int HEADER = 0;
    public static final int FOOTER = 1;
    private int type;

    public BaseHeaderOrFooterView(Context context, @NonNull ViewGroup root, int type) {
        this(context, root, type,false);
    }

    public BaseHeaderOrFooterView(Context context, @NonNull ViewGroup root, int type, boolean attachToRoot) {
        mContext = context;
        this.type = type;
        createView(context, root, attachToRoot);
    }

    private void createView(Context context, ViewGroup root, boolean attachToRoot) {
        mView = LayoutInflater.from(context).inflate(onBindLayoutId(), root, attachToRoot);
        measureView(mView);
        mViewHeight = mView.getMeasuredHeight();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mViewHeight);
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        if (type == HEADER)
            params.topMargin = -(mViewHeight);
        onViewCreated(mView);
    }

    public final String getFormatDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    protected abstract void onViewCreated(View view);

    protected abstract int onBindLayoutId();

    protected LinearLayout.LayoutParams getParams() {
        return params;
    }

    protected int getViewHeight() {
        return mViewHeight;
    }

    /**
     * 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
     * 释放刷新/加载更多
     */
    public abstract void releaseToRefreshOrLoad();

    /**
     * 下拉刷新/上拉加载
     */
    public abstract void pullToRefreshOrLoad();

    /**
     * 执行正在刷新/加载的操作
     *
     * @return
     */
    public abstract void isRefreshingOrLoading();

    /**
     * 刷新/加载完成的操作
     *
     * @return
     */
    public abstract void refreshOrLoadComplete();

    /**
     * 这里提供一个方法能够获取下拉头显示的百分比，供动画效果使用
     */
    public abstract void getPercentage(float rate);

    public View getView() {
        return mView;
    }

    protected Context getContext() {
        return mContext;
    }

    public void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
}