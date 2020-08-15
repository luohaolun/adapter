package k.lhl.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import k.lhl.adapter.R;


public class TicketFooterView extends BaseHeaderOrFooterView {

    private TextView mFooterTextView;
    private ProgressBar mFooterProgressBar;

    public TicketFooterView(Context context, @NonNull ViewGroup root) {
        super(context, root, FOOTER);
    }

    @Override
    protected void onViewCreated(View view) {
        mFooterTextView = (TextView) view.findViewById(R.id.pull_to_load_text);
        mFooterProgressBar = (ProgressBar) view.findViewById(R.id.pull_to_load_progress);
    }

    @Override
    protected int onBindLayoutId() {
        return R.layout.refresh_footer;
    }

    @Override
    public void onPreRelease() {
        mFooterTextView.setText("松开加载更多");
    }

    @Override
    public void onPrePull() {
        mFooterTextView.setText("上拉加载更多");
    }

    @Override
    public void onLoading() {
        mFooterTextView.setVisibility(View.GONE);
        mFooterProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(boolean over) {
        mFooterTextView.setText("上拉加载更多");
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPercentage(float rate) {
        //用不上的话可以不实现具体逻辑
    }
}