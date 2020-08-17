package k.lhl.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Scroller
import k.lhl.adapter.R
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.text.DecimalFormat


open class RefreshLayout : LinearLayout {
    private var isCanScrollAtRefreshing = true //刷新时是否可滑动
    private var isCanPullDown = true //是否可下拉
    private var isCanPullUp = true //是否可上拉
    private var isLoadMoreOver = false//下拉已经没有数据

    //记住上次落点的坐标
    private var mLastMotionY = 0

    //headerview-头布局
    private lateinit var mHeaderView: BaseHeaderOrFooterView

    //footerview-尾布局
    private lateinit var mFooterView: BaseHeaderOrFooterView

    //头状态
    private var mHeaderState = 0

    //尾状态
    private var mFooterState = 0

    //下拉状态
    private var mPullState = 0

    //刷新接口-提供下拉刷新+上拉加载的回调方法
    private var refreshListener: (() -> Unit)? = null
    private var loadListener: (() -> Unit)? = null
    private var mScroller: Scroller? = null

    private lateinit var mContentView: View


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }


    /**
     * init-初始化方法，为我们的RecyclerView做一些必要的初始化工作
     */
    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)
        val headerClassName = typedArray.getString(R.styleable.RefreshLayout_headerClass)
        mHeaderView = createHeaderOrFooterView(context, headerClassName, attrs) ?: DefaultHeaderView(context, this)
        val footerClassName = typedArray.getString(R.styleable.RefreshLayout_footerClass)
        mFooterView = createHeaderOrFooterView(context, footerClassName, attrs) ?: DefaultFooterView(context, this)
        isCanPullDown = typedArray.getBoolean(R.styleable.RefreshLayout_enableHeader, true)
        isCanPullUp = typedArray.getBoolean(R.styleable.RefreshLayout_enableFooter, true)
        typedArray.recycle()
        mScroller = Scroller(context)
        if (!isCanPullDown) mHeaderView.view.visibility = View.INVISIBLE
        if (!isCanPullUp) mFooterView.view.visibility = View.INVISIBLE
        orientation = VERTICAL
    }

    /**
     * 当view渲染完成后回调此方法，原先在此方法中初始化了尾布局，现在暂时废弃不用
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        mContentView = getContentView()
        removeAllViews()
        if (isInEditMode) {
            if (isCanPullDown) {
                mHeaderView.getParams().topMargin = 0
                addView(mHeaderView.view, 0, mHeaderView.getParams())
            }
            addView(mContentView, 1)
            if (isCanPullUp) {
                addView(mFooterView.view, 2, mFooterView.getParams())
            }
            mContentView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f)
            return
        }
        addView(mHeaderView.view, 0, mHeaderView.getParams())
        addView(mContentView, 1)
        addView(mFooterView.view, 2, mFooterView.getParams())
    }

    open fun getContentView(): View {
        mContentView = getChildAt(0) ?: LayoutInflater.from(context).inflate(R.layout.item_default, null)
        return mContentView
    }

    private fun createHeaderOrFooterView(context: Context, className: String?, attrs: AttributeSet): BaseHeaderOrFooterView? {
        var className: String? = className
        if (className != null) {
            className = className.trim { it <= ' ' }
            if (!className.isEmpty()) {
                return try {
                    val classLoader: ClassLoader = if (isInEditMode) {
                        // Stupid layoutlib cannot handle simple class loaders.
                        this.javaClass.classLoader
                    } else {
                        context.classLoader
                    }
                    val headerClass = Class.forName(className, false, classLoader)
                        .asSubclass(BaseHeaderOrFooterView::class.java)
                    var constructor: Constructor<out BaseHeaderOrFooterView>
                    try {
                        constructor = headerClass.getConstructor(Context::class.java, ViewGroup::class.java)
                    } catch (e: NoSuchMethodException) {
                        constructor = try {
                            headerClass.getConstructor()
                        } catch (e1: NoSuchMethodException) {
                            e1.initCause(e)
                            throw IllegalStateException(
                                attrs.positionDescription
                                        + ": Error creating LayoutManager " + className, e1
                            )
                        }
                    }
                    constructor.isAccessible = true
                    constructor.newInstance(context, this)
                } catch (e: ClassNotFoundException) {
                    throw IllegalStateException(
                        attrs.positionDescription
                                + ": Unable to find LayoutManager " + className, e
                    )
                } catch (e: InvocationTargetException) {
                    throw IllegalStateException(
                        attrs.positionDescription
                                + ": Could not instantiate the LayoutManager: " + className, e
                    )
                } catch (e: InstantiationException) {
                    throw IllegalStateException(
                        attrs.positionDescription
                                + ": Could not instantiate the LayoutManager: " + className, e
                    )
                } catch (e: IllegalAccessException) {
                    throw IllegalStateException(
                        attrs.positionDescription
                                + ": Cannot access non-public constructor " + className, e
                    )
                } catch (e: ClassCastException) {
                    throw IllegalStateException(
                        attrs.positionDescription
                                + ": Class is not a LayoutManager " + className, e
                    )
                }
            }
        }
        return null
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (!isCanScrollAtRefreshing) {
                if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 判断是否应该到了父View,即PullToRefreshView滑动
     */
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val y = e.rawY.toInt()
        val x = e.rawX.toInt()
        when (e.action) {
            MotionEvent.ACTION_DOWN ->                 // 首先拦截down事件,记录y坐标
                mLastMotionY = y
            MotionEvent.ACTION_MOVE -> {
                // deltaY > 0 是向下运动,< 0是向上运动
                val deltaY = y - mLastMotionY
                if (isRefreshViewScroll(deltaY)) {
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }
        return false
    }

    /*
     * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
     * false)PullBaseView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
     */
    private var isPointerUp = false //可以双指交替拉动
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.rawY.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN -> mLastMotionY = y
            MotionEvent.ACTION_POINTER_UP -> isPointerUp = true
            MotionEvent.ACTION_MOVE -> {
                if (isPointerUp) {
                    isPointerUp = false
                    mLastMotionY = y
                }
                val deltaY = y - mLastMotionY
                if (mPullState == PULL_DOWN_STATE) {
                    //头布局准备刷新
                    headerPrepareToRefresh(deltaY)
                } else if (mPullState == PULL_UP_STATE) {
                    //尾布局准备加载
                    footerPrepareToRefresh(deltaY)
                }
                mLastMotionY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //当我们的手指离开屏幕的时候，应该判断做什么处理
                val topMargin = headerTopMargin
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0 && isCanPullDown) {
                        // 开始刷新
                        headerRefreshing()
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderView.viewHeight, true)
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (isCanPullUp && Math.abs(topMargin) >= mHeaderView.viewHeight + mFooterView.viewHeight && !isLoadMoreOver) {
                        // 开始执行footer 刷新
                        footerRefreshing()
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderView.viewHeight, true)
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     *
     * @param deltaY , deltaY > 0 是向下运动,< 0是向上运动
     * @return
     */
    private fun isRefreshViewScroll(deltaY: Int): Boolean {
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            return false
        }
        if (deltaY >= -20 && deltaY <= 20) return false

        if (deltaY > 0 && !mContentView.canScrollVertically(-1)) {
            mPullState = PULL_DOWN_STATE
            return true
        }

        if (deltaY < 0 && !mContentView.canScrollVertically(1)) {
            mPullState = PULL_UP_STATE
            return true
        }

        return false
    }


    /**
     * header 准备刷新,手指移动过程,还没有释放
     *
     * @param deltaY ,手指滑动的距离
     */
    private fun headerPrepareToRefresh(deltaY: Int) {
        val newTopMargin = changingHeaderViewTopMargin(deltaY)
        // 当headerview的topMargin>=0时，说明已经完全显示出来了,修改header view的提示状态
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            //调用我们自定义头布局的释放刷新操作，具体代码在自定义headerview中实现
            mHeaderView.onPreRelease()
            mHeaderState = RELEASE_TO_REFRESH
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderView.viewHeight) { // 拖动时没有释放
            //调用我们自定义头布局的下拉刷新操作，具体代码在自定义headerview中实现
            mHeaderView.onPrePull()
            mHeaderState = PULL_TO_REFRESH
        }
    }

    /**
     * footer准备刷新,手指移动过程,还没有释放 移动footerview高度同样和移动header view
     * 高度是一样，都是通过修改headerview的topmargin的值来达到
     *
     * @param deltaY ,手指滑动的距离
     */
    private fun footerPrepareToRefresh(deltaY: Int) {
        val newTopMargin = changingHeaderViewTopMargin(deltaY)
        // 如果header view topMargin 的绝对值大于或等于header + footer 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (isLoadMoreOver) return
        if (Math.abs(newTopMargin) >= mHeaderView.viewHeight + mFooterView.viewHeight && mFooterState != RELEASE_TO_REFRESH) {
            //调用我们自定义尾布局的释放加载操作，具体代码在自定义footerview中实现
            mFooterView.onPreRelease()
            mFooterState = RELEASE_TO_REFRESH
        } else if (Math.abs(newTopMargin) < mHeaderView.viewHeight + mFooterView.viewHeight) {
            //调用我们自定义尾布局的上拉加载操作，具体代码在自定义footerview中实现
            mFooterView.onPrePull()
            mFooterState = PULL_TO_REFRESH
        }
    }

    /**
     * 修改Headerview topmargin的值
     *
     * @param deltaY
     * @description
     */
    private fun changingHeaderViewTopMargin(deltaY: Int): Int {
        val params = mHeaderView.view.layoutParams as LayoutParams
        val newTopMargin = params.topMargin + deltaY * 0.3f

        //此处要做的事情是通过布局返回一个百分比大小，供有的头布局动画使用
        if (mHeaderView.viewHeight + params.topMargin <= mHeaderView.viewHeight) {
            //如果我们头布局的高度是正值，params.topMargin是负值
            //当头布局从完全隐藏到刚好显示的过程是0～mHeaderView.getViewHeight()的过程
            //所以用它做分子，分母就是我们的头布局高度
            //计算并通过方法返回比例
            val format = DecimalFormat("0.00")
            val differ = mHeaderView.viewHeight + params.topMargin.toFloat()
            val total = mHeaderView.viewHeight.toFloat()
            val rate = differ / total
            mHeaderView.onPercentage(format.format(rate.toDouble()).toFloat())
        } else {
            //走到这儿说明我们的头布局已经被继续下拉，超过了本身的大小
            //所以返回1恒定值表示100%，不在继续增加
            mHeaderView.onPercentage(1.00f)
        }

        // 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
        // 表示如果是在上拉后一段距离,然后直接下拉
        if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderView.viewHeight) {
            //如果每次偏移量>0，说明是下拉刷新，并且params.topMargin绝对值小于等于头布局高度
            //说明头布局还未完全显示，直接返回
            return params.topMargin
        }
        // 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
        if (deltaY < 0 && mPullState == PULL_DOWN_STATE && params.topMargin < 0 && Math.abs(params.topMargin) >= mHeaderView.viewHeight) {
            return params.topMargin
        }
        params.topMargin = newTopMargin.toInt()
        mHeaderView.view.layoutParams = params
        invalidate()
        return params.topMargin
    }

    /**
     * header refreshing
     */
    fun headerRefreshing() {
        mHeaderState = REFRESHING
        //设置头布局完全显示
        setHeaderTopMargin(0, true)
        //通过自定义头布局回调方法，实行我们自定义的逻辑
        mHeaderView?.onLoading()
        refreshListener?.invoke()
        isLoadMoreOver = false
    }

    /**
     * footer refreshing
     */
    private fun footerRefreshing() {
        mFooterState = REFRESHING
        //将我们的头布局margin设为头布局+尾布局高度和，这样尾布局将完全显示
        val top = mHeaderView.viewHeight + mFooterView.viewHeight
        setHeaderTopMargin(-top, true)
        //通过自定义尾布局回调方法，实行我们自定义的逻辑
        mFooterView?.onLoading()
        loadListener?.invoke()
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     * @description
     */
    private var offset = 0
    private var curTop = 0
    private fun setHeaderTopMargin(topMargin: Int, isStart: Boolean) {
        curTop = mHeaderView.view.top
        offset = curTop - topMargin
        mScroller!!.startScroll(0, 0, 0, offset, 500)
        invalidate() //这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    override fun computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller!!.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            val params = mHeaderView.getParams()
            params.topMargin = curTop - mScroller!!.currY
            mHeaderView.view.layoutParams = params
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate()
        }
        super.computeScroll()
    }

    /**
     * header view 完成更新后恢复初始状态
     */
    fun onHeaderComplete(over: Boolean = false) {
        setHeaderTopMargin(-mHeaderView.viewHeight, false)
        //通过自定义头布局更新我们刷新完成后的头布局各控件的状态和显示
        mHeaderView?.onComplete(over)
        mHeaderState = PULL_TO_REFRESH
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    fun onFooterComplete(over: Boolean = false) {
        isLoadMoreOver = over
        setHeaderTopMargin(-mHeaderView.viewHeight, false)
        //通过自定义尾布局更新我们刷新完成后的尾布局各控件的状态和显示
        mFooterView.onComplete(over)
        mFooterState = PULL_TO_REFRESH
    }

    /**
     * 获取当前header view 的topMargin
     *
     * @description
     */
    private val headerTopMargin: Int
        private get() {
            val params = mHeaderView.view.layoutParams as LayoutParams
            return params.topMargin
        }

    /**
     * set headerRefreshListener
     * 设置下拉刷新接口
     *
     * @description
     */
    fun setOnRefreshListener(refreshListener: () -> Unit): RefreshLayout {
        this.refreshListener = refreshListener
        return this
    }

    /**
     * 设置上拉加载接口
     *
     * @description
     */
    fun setOnLoadListener(loadListener: () -> Unit): RefreshLayout {
        this.loadListener = loadListener
        return this
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */


    /**
     * 设置是否可以在刷新时滑动
     *
     * @param canScrollAtRereshing
     */
    fun setCanScrollAtRereshing(canScrollAtRereshing: Boolean) {
        isCanScrollAtRefreshing = canScrollAtRereshing
    }

    /**
     * 设置是否可上拉
     *
     * @param canPullUp
     */
    fun enableFooter(canPullUp: Boolean) {
        isCanPullUp = canPullUp
    }

    /**
     * 设置是否可下拉
     *
     * @param canPullDown
     */
    fun enableHeader(canPullDown: Boolean) {
        isCanPullDown = canPullDown
    }

    /**
     * 用于设置刷新列表头部显示样式
     *
     * @param headerClass
     * @return
     */
    fun setHeader(headerClass: Class<out BaseHeaderOrFooterView?>): RefreshLayout {
        try {
            val constructor =
                headerClass.getConstructor(Context::class.java, ViewGroup::class.java)
            constructor.isAccessible = true
            val view = constructor.newInstance(context, this)
            removeView(mHeaderView.view)
            mHeaderView = view!!
            if (!isCanPullDown) mHeaderView.view.visibility = View.INVISIBLE
            addView(mHeaderView.view, 0, mHeaderView.getParams())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 用于设置刷新列表底部显示样式
     *
     * @param footerClass
     * @return
     */
    fun setFooter(footerClass: Class<out BaseHeaderOrFooterView?>): RefreshLayout {
        try {
            val constructor =
                footerClass.getConstructor(Context::class.java, ViewGroup::class.java)
            constructor.isAccessible = true
            val view = constructor.newInstance(context, this)
            removeView(mFooterView.view)
            mFooterView = view!!
            if (!isCanPullUp) mFooterView.view.visibility = View.INVISIBLE
            addView(mFooterView.view, mFooterView.getParams())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }


    companion object {
        // pull state
        private const val PULL_UP_STATE = 0
        private const val PULL_DOWN_STATE = 1

        // refresh states
        private const val PULL_TO_REFRESH = 2
        private const val RELEASE_TO_REFRESH = 3
        private const val REFRESHING = 4
    }

}