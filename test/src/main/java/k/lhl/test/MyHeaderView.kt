package k.lhl.test

import android.content.Context
import android.view.View
import android.view.ViewGroup
import k.lhl.recyclerview.BaseHeaderOrFooterView

/**
 * Author:  LuoHaoLun
 * Email :  506503279@qq.com
 * Date  :  2020/8/14
 */
class MyHeaderView(context: Context, parent: ViewGroup) : BaseHeaderOrFooterView(context, parent, HEADER) {
    override fun isRefreshingOrLoading() {
    }

    override fun releaseToRefreshOrLoad() {
    }

    override fun onViewCreated(view: View?) {
    }

    override fun onBindLayoutId(): Int {
        return R.layout.header
    }

    override fun refreshOrLoadComplete() {
    }

    override fun getPercentage(rate: Float) {
    }

    override fun pullToRefreshOrLoad() {
    }
}