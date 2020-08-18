# AbsListView RecyclerView 通用适配器


### 说明

超简化代码，高阶函数参数简化为1，使用非常方便。多类型注意数据源的构造 data:List<Pair<Int,T>>

2.0版本合并log库： [https://github.com/luohaolun/log]()


### 添加依赖

> Project build.gradle

	allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
	}
	
> Module build.gradle

	dependencies {
	    implementation 'com.github.luohaolun:adapter:2.0'
	}


### BaseAdapter使用

> 通常

	val data = mutableListOf<String>()
	for (i in 0..10) {
	    data.add(i.toString())
	}
	
    lvList.adapter = Adapter(data, R.layout.item_test) {
        tvNum.text = it
    }



> 多类型Item


	val data = mutableListOf<Pair<Int, String>>()
	
	for (i in 0..20) {
		if (i < 4)
                data.add(Pair(0, i.toString()))
            else
                data.add(Pair(1, i.toString()))
        }
	
>> MultiAdapter

        lvList.adapter = MultiAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) {
            when (type) {
                0 -> {
                    tvNum.text = "$it  类型0"
                }
                1 -> {
                    tvNum.text = "$it  类型1"
                }
            }
        }

>> MulAdapter

        lvList.adapter = MulAdapter(data, 0, R.layout.item_test) {
            tvNum.text = "$it  类型1"
        }.addItemType(1, R.layout.item_test_2) {
            tvNum.text = "$it  类型2"
        }
	
	
	
	
	
### RecyclerView.Adapter使用


> 通常

	val data = mutableListOf<String>()
	for (i in 0..10) {
		data.add(i.toString())
	}

    recyList.adapter = RecyclerAdapter(data, R.layout.item_test) {
        tvNum.text = it
    }.setOnItemClickListener(500) { //点击间隔 500ms
        Toast.makeText(this@MainActivity, "点击    $position    $it", Toast.LENGTH_SHORT).show()
    }.setOnItemLongClickListener {
        Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
    }
	

> 多类型Item

	val data = mutableListOf<Pair<Int, String>>()
	
	for (i in 0..20) {
		if (i < 4)
                data.add(Pair(0, i.toString()))
            else
                data.add(Pair(1, i.toString()))
        }
	
>> MultiRecyclerAdapter

        recyList.adapter = MultiRecyclerAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) {
            when (type) {
                0 -> {
                    tvNum.text = it
                }
                1 -> {
                    tvNum.text = it
                }
            }
        }.setOnItemClickListener {
            Toast.makeText(this@MainActivity, "点击    $position     $it", Toast.LENGTH_SHORT).show()
        }.setOnItemLongClickListener {
            Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
        }
	
	
>> MulRecyclerAdapter


        recyList.adapter = MulRecyclerAdapter(data, 0, R.layout.item_test) {
            tvNum.text = it
        }.addItemType(1, R.layout.item_test_2) {
            tvNum.text = it
        }.setOnItemClickListener {
            Toast.makeText(this@MainActivity, "点击    $position     $it", Toast.LENGTH_SHORT).show()
        }.setOnItemLongClickListener {
            Toast.makeText(this@MainActivity, "长按    $position     $it", Toast.LENGTH_SHORT).show()
        }


### 下拉刷新和上拉加载RefreshLayout

>>使用

自带布局预览，RecyclerView可替换为任意View，如TextView、LinearLayout..


    <k.lhl.refresh.RefreshLayout
        android:id="@+id/refresh"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:headerClass="k.lhl.test.MyHeaderView"   
        app:footerClass="k.lhl.test.MyFooterView"   
        app:empty="@layout/default_empty_view"    // 没有数据时显示的空布局  refresh.showEmpty()
        app:enableFooter="true"
        app:enableHeader="true">
        <androidx.recyclerview.widget.RecyclerView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
            tools:itemCount="10"
            tools:listitem="@layout/item_test" />
    </k.lhl.refresh.RefreshLayout>
    


>>自定义头部或底部：

    1.继承BaseHeaderOrFooterView
    
    2.设置
    xml：app:headerClass/footerClass = "k.lhl.test.MyHeaderView"  // 完整包路径
    代码：refresh.setHeader(MyHeaderView::class.java)  // class

### PullRecyclerView

>>使用

继承自RefreshLayout，对RecyclerView的扩展

    <k.lhl.refresh.PullRecyclerView
        android:id="@+id/recyList"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:enableFooter="true"
        app:enableHeader="true"
        app:empty="@layout/default_empty_view"
        app:enableEmpty="true"                      // 数据源为空自动展示空布局视图
        app:footerClass="k.lhl.test.MyFooterView"
        app:headerClass="k.lhl.test.MyHeaderView"
        app:item="@layout/item_test"                // xml配置item后设置adapter可省略item布局
        app:itemDivider="@drawable/default_item_divider" />