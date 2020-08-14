package k.lhl.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

import k.lhl.adapter.R;

public abstract class PullBaseView<T extends RecyclerView> extends LinearLayout {
    //PullBaseView是继承LinearLayout，所以我们的头尾布局和RecycleView都是通过addView方法添加的
    protected T mRecyclerView;
    private boolean isCanScrollAtRereshing = false;//刷新时是否可滑动
    private boolean isCanPullDown = true;//是否可下拉
    private boolean isCanPullUp = true;//是否可上拉
    // pull state
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;
    // refresh states
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;

    //记住上次落点的坐标
    private int mLastMotionY;
    //headerview-头布局
    private BaseHeaderOrFooterView mHeaderView;
    //footerview-尾布局
    private BaseHeaderOrFooterView mFooterView;

    //头状态
    private int mHeaderState;
    //尾状态
    private int mFooterState;
    //下拉状态
    private int mPullState;

    //刷新接口-提供下拉刷新+上拉加载的回调方法
    private OnRefreshListener refreshListener;

    private Scroller mScroller;

    public PullBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullBaseView(Context context) {
        super(context);
    }

    /**
     * init-初始化方法，为我们的RecyclerView做一些必要的初始化工作
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullBaseView);

        String headerClassName = typedArray.getString(R.styleable.PullBaseView_headerClass);
        mHeaderView = createHeaderOrFooterView(context, headerClassName, attrs);
        if (mHeaderView == null)
            mHeaderView = new TicketHeaderView(context, this);

        String footerClassName = typedArray.getString(R.styleable.PullBaseView_footerClass);
        mFooterView = createHeaderOrFooterView(context, footerClassName, attrs);
        if (mFooterView == null)
            mFooterView = new TicketFooterView(context, this);

        isCanPullDown = typedArray.getBoolean(R.styleable.PullBaseView_enableHeader, false);
        isCanPullUp = typedArray.getBoolean(R.styleable.PullBaseView_enableFooter, false);

        typedArray.recycle();

        mScroller = new Scroller(context);
        //通过回调方法获得一个RecyclerView对象
        mRecyclerView = createRecyclerView(context, attrs);
        //设置RecyclerView全屏显示
        mRecyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //这里仅仅添加了一个RecyclerView只是做占位使用，在我们具体设置头布局的时候
        //会清空LinearLayout中所有的View，重新添加头布局，然后添加RecyclerView

        if (!isCanPullDown) mHeaderView.getView().setVisibility(View.INVISIBLE);
        if (!isCanPullUp) mFooterView.getView().setVisibility(View.INVISIBLE);

        addView(mHeaderView.getView(), mHeaderView.getParams());
        addView(mRecyclerView);
        addView(mFooterView.getView(), mFooterView.getParams());

    }

    private BaseHeaderOrFooterView createHeaderOrFooterView(Context context, String className, AttributeSet attrs) {
        if (className != null) {
            className = className.trim();
            if (!className.isEmpty()) {
                try {
                    ClassLoader classLoader;
                    if (isInEditMode()) {
                        // Stupid layoutlib cannot handle simple class loaders.
                        classLoader = this.getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends BaseHeaderOrFooterView> headerClass =
                            Class.forName(className, false, classLoader)
                                    .asSubclass(BaseHeaderOrFooterView.class);
                    Constructor<? extends BaseHeaderOrFooterView> constructor;
                    Object[] constructorArgs = null;
                    try {
                        constructor = headerClass
                                .getConstructor(new Class<?>[]{Context.class, ViewGroup.class});
                        constructorArgs = new Object[]{context, this};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = headerClass.getConstructor();
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription()
                                    + ": Error creating LayoutManager " + className, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    return constructor.newInstance(constructorArgs);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Unable to find LayoutManager " + className, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (InstantiationException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Cannot access non-public constructor " + className, e);
                } catch (ClassCastException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Class is not a LayoutManager " + className, e);
                }
            }
        }
        return null;
    }

    /**
     * 当view渲染完成后回调此方法，原先在此方法中初始化了尾布局，现在暂时废弃不用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //刷新时禁止滑动
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isCanScrollAtRereshing) {
                    if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
                        return true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否应该到了父View,即PullToRefreshView滑动
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                if (isRefreshViewScroll(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }


    /*
     * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
     * false)PullBaseView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
     */
    private boolean isPointerUp = false;//可以双指交替拉动

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY(0);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isPointerUp = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPointerUp) {
                    isPointerUp = false;
                    mLastMotionY = y;
                }
                int deltaY = y - mLastMotionY;
                if (mPullState == PULL_DOWN_STATE) {
                    //头布局准备刷新
                    headerPrepareToRefresh(deltaY);
                } else if (mPullState == PULL_UP_STATE) {
                    //尾布局准备加载
                    footerPrepareToRefresh(deltaY);
                }
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //当我们的手指离开屏幕的时候，应该判断做什么处理
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0 && isCanPullDown) {
                        // 开始刷新
                        headerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderView.getViewHeight(), true);
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (isCanPullUp && Math.abs(topMargin) >= mHeaderView.getViewHeight() + mFooterView.getViewHeight()) {
                        // 开始执行footer 刷新
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderView.getViewHeight(), true);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     *
     * @param deltaY , deltaY > 0 是向下运动,< 0是向上运动
     * @return
     */
    private boolean isRefreshViewScroll(int deltaY) {
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            return false;
        }
        if (deltaY >= -20 && deltaY <= 20)
            return false;

        if (mRecyclerView != null) {
            // 子view(ListView or GridView)滑动到最顶端
            if (deltaY > 0) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    // 如果mRecyclerView中没有数据,不拦截
                    return false;
                }
                if (isScrollTop() && child.getTop() == 0) {
                    //如果滑动到了顶端，要拦截事件交由自己处理
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mRecyclerView.getPaddingTop();
                if (isScrollTop() && Math.abs(top - padding) <= 8) {// 这里之前用3可以判断,但现在不行,还没找到原因
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
            } else if (deltaY < 0) {
                View lastChild = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1);
                if (lastChild == null) {
                    // 如果mRecyclerView中没有数据,不拦截
                    return false;
                }
                // 最后一个子view的Bottom小于父View的高度说明mRecyclerView的数据没有填满父view,
                // 等于父View的高度说明mRecyclerView已经滑动到最后
                if (lastChild.getBottom() <= getHeight() && isScrollBottom()) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断mRecyclerView是否滑动到顶部
     *
     * @return
     */
    boolean isScrollTop() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断mRecyclerView是否滑动到底部
     *
     * @return
     */
    boolean isScrollBottom() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (linearLayoutManager.findLastVisibleItemPosition() == (mRecyclerView.getAdapter().getItemCount() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * header 准备刷新,手指移动过程,还没有释放
     *
     * @param deltaY ,手指滑动的距离
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 当headerview的topMargin>=0时，说明已经完全显示出来了,修改header view的提示状态
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            //调用我们自定义头布局的释放刷新操作，具体代码在自定义headerview中实现
            mHeaderView.releaseToRefreshOrLoad();
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderView.getViewHeight()) {// 拖动时没有释放
            //调用我们自定义头布局的下拉刷新操作，具体代码在自定义headerview中实现
            mHeaderView.pullToRefreshOrLoad();
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer准备刷新,手指移动过程,还没有释放 移动footerview高度同样和移动header view
     * 高度是一样，都是通过修改headerview的topmargin的值来达到
     *
     * @param deltaY ,手指滑动的距离
     */
    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 如果header view topMargin 的绝对值大于或等于header + footer 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (Math.abs(newTopMargin) >= (mHeaderView.getViewHeight() + mFooterView.getViewHeight()) && mFooterState != RELEASE_TO_REFRESH) {
            //调用我们自定义尾布局的释放加载操作，具体代码在自定义footerview中实现
            mFooterView.releaseToRefreshOrLoad();
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (mHeaderView.getViewHeight() + mFooterView.getViewHeight())) {
            //调用我们自定义尾布局的上拉加载操作，具体代码在自定义footerview中实现
            mFooterView.pullToRefreshOrLoad();
            mFooterState = PULL_TO_REFRESH;
        }
    }

    /**
     * 修改Headerview topmargin的值
     *
     * @param deltaY
     * @description
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getView().getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.3f;

        //此处要做的事情是通过布局返回一个百分比大小，供有的头布局动画使用
        if ((mHeaderView.getViewHeight() + params.topMargin) <= mHeaderView.getViewHeight()) {
            //如果我们头布局的高度是正值，params.topMargin是负值
            //当头布局从完全隐藏到刚好显示的过程是0～mHeaderView.getViewHeight()的过程
            //所以用它做分子，分母就是我们的头布局高度
            //计算并通过方法返回比例
            DecimalFormat format = new DecimalFormat("0.00");
            float differ = mHeaderView.getViewHeight() + params.topMargin;
            float total = mHeaderView.getViewHeight();
            float rate = differ / total;
            mHeaderView.getPercentage(Float.parseFloat(format.format(rate)));
        } else {
            //走到这儿说明我们的头布局已经被继续下拉，超过了本身的大小
            //所以返回1恒定值表示100%，不在继续增加
            mHeaderView.getPercentage(1.00f);
        }

        // 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
        // 表示如果是在上拉后一段距离,然后直接下拉
        if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderView.getViewHeight()) {
            //如果每次偏移量>0，说明是下拉刷新，并且params.topMargin绝对值小于等于头布局高度
            //说明头布局还未完全显示，直接返回
            return params.topMargin;
        }
        // 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
        if (deltaY < 0 && mPullState == PULL_DOWN_STATE && params.topMargin < 0 && Math.abs(params.topMargin) >= mHeaderView.getViewHeight()) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderView.getView().setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * header refreshing
     */
    public void headerRefreshing() {
        mHeaderState = REFRESHING;
        //设置头布局完全显示
        setHeaderTopMargin(0, true);
        //通过自定义头布局回调方法，实行我们自定义的逻辑
        mHeaderView.isRefreshingOrLoading();
        if (refreshListener != null) {
            //接口回调，用于处理网络
            refreshListener.onPullToRefresh(this);
        }
    }

    /**
     * footer refreshing
     */
    private void footerRefreshing() {
        mFooterState = REFRESHING;
        //将我们的头布局margin设为头布局+尾布局高度和，这样尾布局将完全显示
        int top = mHeaderView.getViewHeight() + mFooterView.getViewHeight();
        setHeaderTopMargin(-top, true);
        //通过自定义尾布局回调方法，实行我们自定义的逻辑
        mFooterView.isRefreshingOrLoading();
        if (refreshListener != null) {
            //接口回调，用于网络处理
            refreshListener.onPullToLoadMore(this);
        }
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     * @description
     */
    private int offset;
    private int curTop;

    private void setHeaderTopMargin(final int topMargin, boolean isStart) {
        curTop = mHeaderView.getView().getTop();
        offset = curTop - topMargin;
        mScroller.startScroll(0, 0, 0, offset, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            LayoutParams params = (LayoutParams) mHeaderView.getParams();
            params.topMargin = curTop - mScroller.getCurrY();
            mHeaderView.getView().setLayoutParams(params);
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * header view 完成更新后恢复初始状态
     */
    public void onHeaderRefreshComplete() {
        setHeaderTopMargin(-mHeaderView.getViewHeight(), false);
        //通过自定义头布局更新我们刷新完成后的头布局各控件的状态和显示
        mHeaderView.refreshOrLoadComplete();
        mHeaderState = PULL_TO_REFRESH;
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    public void onFooterRefreshComplete() {
        setHeaderTopMargin(-mHeaderView.getViewHeight(), false);
        //通过自定义尾布局更新我们刷新完成后的尾布局各控件的状态和显示
        mFooterView.refreshOrLoadComplete();
        mFooterState = PULL_TO_REFRESH;
        if (mRecyclerView != null) {
            //加载完后列表停留在最后一项
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        }
    }

    /**
     * 获取当前header view 的topMargin
     *
     * @description
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getView().getLayoutParams();
        return params.topMargin;
    }


    /**
     * set headerRefreshListener
     * 设置我们的接口
     *
     * @description
     */
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */
    public interface OnRefreshListener {
        //下拉刷新的回调方法
        void onPullToRefresh(PullBaseView view);

        //上拉加载的回调方法
        void onPullToLoadMore(PullBaseView view);
    }

    /**
     * 设置是否可以在刷新时滑动
     *
     * @param canScrollAtRereshing
     */
    public void setCanScrollAtRereshing(boolean canScrollAtRereshing) {
        isCanScrollAtRereshing = canScrollAtRereshing;
    }

    /**
     * 设置是否可上拉
     *
     * @param canPullUp
     */
    public void enableFooter(boolean canPullUp) {
        isCanPullUp = canPullUp;
    }

    /**
     * 设置是否可下拉
     *
     * @param canPullDown
     */
    public void enableHeader(boolean canPullDown) {
        isCanPullDown = canPullDown;
    }

    protected abstract T createRecyclerView(Context context, AttributeSet attrs);

    /**
     * 用于设置刷新列表头部显示样式
     *
     * @param headerClass
     * @return
     */
    public PullBaseView setHeader(Class<? extends BaseHeaderOrFooterView> headerClass) {
        try {
            Constructor<? extends BaseHeaderOrFooterView> constructor = headerClass.getConstructor(new Class<?>[]{Context.class, ViewGroup.class});
            constructor.setAccessible(true);
            BaseHeaderOrFooterView view = constructor.newInstance(getContext(), this);
            removeView(this.mHeaderView.getView());
            this.mHeaderView = view;
            if (!isCanPullDown) this.mHeaderView.getView().setVisibility(View.INVISIBLE);
            addView(mHeaderView.getView(), 0, mHeaderView.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 用于设置刷新列表底部显示样式
     *
     * @param footerClass
     * @return
     */
    public PullBaseView setFooter(Class<? extends BaseHeaderOrFooterView> footerClass) {
        try {
            Constructor<? extends BaseHeaderOrFooterView> constructor = footerClass.getConstructor(new Class<?>[]{Context.class, ViewGroup.class});
            constructor.setAccessible(true);
            BaseHeaderOrFooterView view = constructor.newInstance(getContext(), this);
            removeView(this.mFooterView.getView());
            this.mFooterView = view;
            if (!isCanPullUp) this.mFooterView.getView().setVisibility(View.INVISIBLE);
            addView(mFooterView.getView(), mFooterView.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}

