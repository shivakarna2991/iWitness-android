package com.iwitness.androidapp.libraries.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.iwitness.androidapp.controllers.adapters.VideoListAdapter;
import com.perpcast.lib.foundation.FwiJson;

public class VideoListView extends ListView implements OnScrollListener {

    private View footer;
    private boolean isLoading;
    private EndlessListener listener;
    private VideoListAdapter adapter;

    public VideoListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);
    }

    public VideoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public VideoListView(Context context) {
        super(context);
        this.setOnScrollListener(this);
    }

    public void setListener(EndlessListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null)
            return ;

        if (getAdapter().getCount() == 0)
            return ;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            // It is time to add new data. We call the listener
            this.addFooterView(footer);
            isLoading = true;
            listener.loadData();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public void setLoadingView(int resId) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer =  inflater.inflate(resId, null);
        this.addFooterView(footer);

    }

    public void requestData() {
        isLoading = true;
        listener.loadData();
    }

    public void setAdapter(VideoListAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        adapter.notifyDataSetChanged();
        this.removeFooterView(footer);
    }


    public void removeLoadingView() {
        this.removeFooterView(footer);
    }

    public void addNewData(FwiJson events) {
        adapter.addAll(events);
        isLoading = false;
    }

    public void reset() {
        adapter.reset();
        isLoading = false;
    }

    public EndlessListener getListener() {
        return listener;
    }


    public static interface EndlessListener {
        public void loadData() ;
    }

}