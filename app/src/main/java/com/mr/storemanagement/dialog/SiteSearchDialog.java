package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SearchSiteAdapter;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.List;

public class SiteSearchDialog extends Dialog {

    private RecyclerView rvSite;

    private OnSiteSelectListener siteSelectListener;

    public void setSiteSelectListener(OnSiteSelectListener siteSelectListener) {
        this.siteSelectListener = siteSelectListener;
    }

    public SiteSearchDialog(@NonNull Context context, List<SiteBean> siteBeans) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_site_search_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        rvSite = findViewById(R.id.rv_site);
        SearchSiteAdapter siteAdapter = new SearchSiteAdapter(getContext(), siteBeans);
        rvSite.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSite.setAdapter(siteAdapter);

        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        siteAdapter.setItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (siteSelectListener != null && siteBeans != null) {
                    siteSelectListener.onSelect(siteBeans.get(position));
                }
                dismiss();
            }
        });

    }

    public interface OnSiteSelectListener {
        void onSelect(SiteBean siteBean);
    }
}
