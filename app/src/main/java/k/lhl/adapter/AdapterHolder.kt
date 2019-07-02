package k.lhl.adapter

import android.view.View

/**
 * Author： luohaolun
 * Date： 2019/7/2
 */
class AdapterHolder<T>(val view: View,
                       var item: T,
                       var position: Int = 0,
                       var type: Int = 0
)

