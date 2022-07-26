package com.mr.storemanagement.helper;

import android.content.Context;

import com.mr.lib_base.network.SMException;
import com.mr.lib_base.network.listener.NetResultListener;
import com.mr.lib_base.util.ToastUtils;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.dialog.SiteSearchDialog;
import com.mr.storemanagement.presenter.GetSiteOutPresenter;
import com.mr.storemanagement.presenter.GetSitePutPresenter;
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

    private int mAction; //0:ru

    private List<SiteBean> mSiteBeans = new ArrayList<>();

//    public void setSiteClickListener(OnSiteEventListener siteClickListener) {
//        this.siteClickListener = siteClickListener;
//    }

    /**
     * @param context
     * @param action
     */
    public SiteChooseHelper(Context context, int action) {
        this.mContext = context;
        this.mAction = action;

        getSite();
    }

    //动作来源
    private void getSite() {
        GetSitePutPresenter presenter = new GetSitePutPresenter(null
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

        GetSiteOutPresenter outPresenter = new GetSiteOutPresenter(null
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

        GetSiteOutPresenter invPresenter = new GetSiteOutPresenter(null
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

        if (mAction == 0) {
            presenter.getSite();
        } else if (mAction == 1) {
            outPresenter.getSite();
        } else if (mAction == 2) {
            invPresenter.getSite();
        }
    }

    public void selectSite() {
        if (NullUtils.isNotEmpty(mSiteBeans)) {
            showSiteDialog();
        } else {
            getSite();
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
