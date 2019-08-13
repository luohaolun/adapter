# AbsListView RecyclerView 通用适配器


### 说明

超简化代码，高阶函数参数简化为1，使用非常方便。多类型注意数据源的构造 data:List<Pair<Int,T>>


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
	        implementation 'com.github.luohaolun:adapter:1.4'
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
