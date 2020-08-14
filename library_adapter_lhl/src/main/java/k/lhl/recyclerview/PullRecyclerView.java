package k.lhl.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView中的所有方法都可以在此类中设置，暴露出去以供调用
 */
public class PullRecyclerView extends PullBaseView<RecyclerView> {


    public PullRecyclerView(Context context) {
        this(context, null);
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected RecyclerView createRecyclerView(Context context, AttributeSet attrs) {
        /**
        * 这里返回一个RecyclerView，添加到LinearLayout中
        * 那么，如果你想使用ListView的话呢，那么你在这里返回就行了
        * 当然需要修改父类中的泛型相关的地方(包括列表所使用到的Adapter）
        */
        return new RecyclerView(context, attrs);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

}