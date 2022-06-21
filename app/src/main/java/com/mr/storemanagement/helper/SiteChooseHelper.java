package com.mr.storemanagement.helper;

import android.content.Context;

import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.SiteSearchDialog;
import com.mr.storemanagement.presenter.GetSitePresenter;
import com.mr.storemanagement.util.NullUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description: 站点选择助手
 */
public class SiteChooseHelper {

    public Context mContext;

    private SiteSearchDialog siteSearchDialog;

    private OnSiteEventListener siteClickListener;

    private boolean mIsOutStock;

    private List<SiteBean> mSiteBeans = new ArrayList<>();

    public void setSiteClickListener(OnSiteEventListener siteClickListener) {
        this.siteClickListener = siteClickListener;
    }

    public SiteChooseHelper(Context context, boolean isOutStock) {
        this.mContext = context;
        this.mIsOutStock = isOutStock;

        getSite(false);
    }

    private void getSite(boolean isClickGet) {
        GetSitePresenter presenter = new GetSitePresenter(null
                , new NetResultListener<List<SiteBean>>() {
            @Override
            public void loadSuccess(List<SiteBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    mSiteBeans.clear();
                    mSiteBeans.addAll(beans);
                    if (siteClickListener != null) {
                        siteClickListener.onFirst(beans.get(0));
                    }
                } else {
                    ToastUtils.show("站点信息为空");
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, null);

        GetSitePresenter outPresenter = new GetSitePresenter(null
                , new NetResultListener<List<SiteBean>>() {
            @Override
            public void loadSuccess(List<SiteBean> beans) {
                if (NullUtils.isNotEmpty(beans)) {
                    mSiteBeans.clear();
                    mSiteBeans.addAll(beans);
                    if (siteClickListener != null) {
                        siteClickListener.onFirst(beans.get(0));
                    }
                } else {
                    ToastUtils.show("站点信息为空");
                }
            }

            @Override
            public void loadFailure(SMException exception) {
                ToastUtils.show(exception.getErrorMsg());
            }
        }, null);

        if (mIsOutStock){
            outPresenter.getSite();
        }else {
            presenter.getSite();
        }
    }

    public void selectSite() {
        if (NullUtils.isNotEmpty(mSiteBeans)) {
            showSiteDialog();
        } else {
            getSite(true);
        }
    }

    private void showSiteDialog() {
        if (siteSearchDialog == null || !siteSearchDialog.isShowing()) {
            siteSearchDialog = new SiteSearchDialog(mContext, mSiteBeans);
            siteSearchDialog.setSiteSelectListener(new SiteSearchDialog.OnSiteSelectListener() {
                @Override
                public void onSelect(SiteBean siteBean) {
                    if (siteClickListener != null)
                        siteClickListener.onClick(siteBean);
                }
            });
            siteSearchDialog.show();
        }
    }

    public interface OnSiteEventListener {

        void onClick(SiteBean site);

        void onFirst(SiteBean site);

    }
}
