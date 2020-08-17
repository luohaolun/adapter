package k.lhl.refresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import k.lhl.adapter.R;


public class DefaultHeaderView extends BaseHeaderOrFooterView {

    private ProgressBar progressBar;
    private ImageView image;
    private TextView tvTime;

    private float density;


    public DefaultHeaderView(Context context, @NonNull ViewGroup root) {
        super(context, root, HEADER);
        density = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onViewCreated(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        image = (ImageView) view.findViewById(R.id.image);
        tvTime = (TextView) view.findViewById(R.id.tvCurrentTime);
        tvTime.setText(getFormatDateString("MM/dd HH:mm"));
    }


    @Override
    protected int onBindLayoutId() {
        return R.layout.default_head;
    }

    /**
     * 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
     * 释放刷新的提示
     */
    @Override
    public void onPreRelease() {
//        text.setText("释放刷新数据");
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPrePull() {
//        text.setText("下拉刷新数据");
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 正在刷新
     */
    @Override
    public void onLoading() {
//        text.setText("正在刷新...");
        image.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新完成的回调
     */
    @Override
    public void onComplete(boolean over) {
//        text.setText("下拉刷新数据");
        tvTime.setText(getFormatDateString("MM-dd HH:mm"));
        progressBar.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
    }


    @Override
    public void onPercentage(float rate) {
        //这里设置的是根据下拉头显示的百分比进行一个头部图片动态缩放的效果
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = (int) (36 * density * rate);
        params.height = (int) (36 * density * rate);
        image.setLayoutParams(params);
    }
}