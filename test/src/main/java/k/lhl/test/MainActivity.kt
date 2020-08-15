package k.lhl.test

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import k.lhl.adapter.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = mutableListOf<String>()
        for (i in 10..30) {
            data.add(i.toString())
        }

//        recyList.setHeader(MyHeaderView::class.java)

        recyList.setAdapter(RecyclerAdapter(data, R.layout.item_test) {
            tvNum.text = it
            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击" + position, Toast.LENGTH_SHORT).show() }
        }.setOnItemClickListener(500) {
            Toast.makeText(this@MainActivity, "点击    $position    $it", Toast.LENGTH_SHORT).show()
        }.setOnItemLongClickListener {
            Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
        })
//        recyList.setLayoutManager(LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL })

        recyList.setOnRefreshListener {
            recyList.postDelayed({
                recyList.onHeaderRefreshComplete()
            }, 3000)
        }

        recyList.setOnLoadListener {
            recyList.postDelayed({
                recyList.onFooterRefreshComplete()
            }, 3000)
        }

//
//
//        lvList.adapter = Adapter(data, R.layout.item_test) {
//            tvNum.text = it
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击" + position, Toast.LENGTH_SHORT).show() }
//        }

//        val data = mutableListOf<Pair<Int, String>>()
//        for (i in 10..30) {
//            if (i < 14 || i > 28)
//                data.add(Pair(0, i.toString()))
//            else
//                data.add(Pair(1, i.toString()))
//        }
//
//        recyList.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
//        recyList.adapter = MultiRecyclerAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) {
//            when (type) {
//                0 -> {
//                    tvNum.text = it
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击类型1     $position", Toast.LENGTH_SHORT).show() }
//                }
//                1 -> {
//                    tvNum.text = it
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击类型2      $position", Toast.LENGTH_SHORT).show() }
//                }
//            }
//        }.setOnItemClickListener {
//            Toast.makeText(this@MainActivity, "点击    $position     $it", Toast.LENGTH_SHORT).show()
//        }.setOnItemLongClickListener {
//            Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
//        }

//        recyList.adapter = MulRecyclerAdapter(data, 0, R.layout.item_test) {
//            tvNum.text = it
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击类型1     $position", Toast.LENGTH_SHORT).show() }
//        }.addItemType(1, R.layout.item_test_2) {
//            tvNum.text = it
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击类型2     $position", Toast.LENGTH_SHORT).show() }
//        }.setOnItemClickListener {
//            Toast.makeText(this@MainActivity, "点击    $position     $it", Toast.LENGTH_SHORT).show()
//        }.setOnItemLongClickListener {
//            Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
//        }


//        lvList.adapter = MultiAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) {
//            when (type) {
//                0 -> {
//                    tvNum.text = "$it  类型0"
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型0", Toast.LENGTH_SHORT).show() }
//                }
//                1 -> {
//                    tvNum.text = "$it  类型1"
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型1", Toast.LENGTH_SHORT).show() }
//                }
//            }
//        }

//        lvList.adapter = MulAdapter(data, 0, R.layout.item_test) {
//            tvNum.text = "$it  类型1"
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型1", Toast.LENGTH_SHORT).show() }
//        }.addItemType(1, R.layout.item_test_2) {
//            tvNum.text = "$it  类型2"
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型2", Toast.LENGTH_SHORT).show() }
//        }


    }
}
