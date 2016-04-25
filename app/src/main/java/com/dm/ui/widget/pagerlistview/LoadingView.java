package com.dm.ui.widget.pagerlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dm.dlib.R;


public class LoadingView extends LinearLayout {

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.lib_loading_view, this);
    }


}
