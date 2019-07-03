package k.lhl.adapter

import android.view.View

/**
 * Author： luohaolun
 * Date： 2019/7/3
 */

val View.position: Int
    get() = getTag(R.id.adapterPosition) as Int? ?: 0

val View.type: Int
    get() = getTag(R.id.adapterType) as Int? ?: 0