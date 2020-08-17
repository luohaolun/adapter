package k.lhl.refresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import k.lhl.adapter.R;


public class DefaultFooterView extends BaseHeaderOrFooterView {

    private TextView mFooterTextView;
    private ProgressBar mFooterProgressBar;

    public DefaultFooterView(Context context, @NonNull ViewGroup root) {
        super(context, root, FOOTER);
    }

    @Override
    protected void onViewCreated(View view) {
        mFooterTextView = (TextView) view.findViewById(R.id.pull_to_load_text);
        mFooterProgressBar = (ProgressBar) view.findViewById(R.id.pull_to_load_progress);
    }

    @Override
    protected int onBindLayoutId() {
        return R.layout.default_footer;
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
        if (over) mFooterTextView.setText("已经到底了~");
        else mFooterTextView.setText("上拉加载更多");
        mFooterTextView.setVisibility(View.VISIBLE);
        mFooterProgressBar.setVisibility(View.GONE);
    }
}