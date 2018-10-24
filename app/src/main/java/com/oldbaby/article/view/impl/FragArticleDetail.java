package com.oldbaby.article.view.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.oldbaby.R;
import com.oldbaby.article.model.impl.ArticleDetailModel;
import com.oldbaby.article.presenter.ArticleDetailPresenter;
import com.oldbaby.article.view.IArticleDetailView;
import com.oldbaby.common.adapter.ArticleImageAdapter;
import com.oldbaby.common.base.CommonFragActivity;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.common.util.SpiderHeader;
import com.oldbaby.common.util.statusbar.ImmersionBar;
import com.oldbaby.common.view.articleplayer.ArticleSpeakPlayer;
import com.oldbaby.common.view.articleplayer.SpeakMediaPlayerAdapter;
import com.oldbaby.common.view.zoompage.InsideHeaderLayout;
import com.oldbaby.common.view.zoompage.OutsideDownFrameLayout;
import com.oldbaby.common.view.zoompage.PinchZoomPage;
import com.oldbaby.oblib.image.viewer.FreeImageViewer;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.FragBaseMvps;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.view.EmptyView;
import com.oldbaby.oblib.view.NetErrorView;
import com.oldbaby.oblib.view.dialog.PromptDlgAttr;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgTwoBtnListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZUtils;

/**
 * usage: 文章详情页
 * author: kHRYSTAL
 * create time: 18/9/30
 * update time:
 * email: 723526676@qq.com
 */
public class FragArticleDetail extends FragBaseMvps implements IArticleDetailView {

    private static final String TAG = FragArticleDetail.class.getSimpleName();
    private static final String TAG_DIALOG_PROMPT_SETTING = "tag_dialog_prompt_setting";
    private static final String TAG_DIALOG_PROMPT_RATIONALE = "tag_dialog_prompt_rationale";
    private static final String PAGE_NAME = "ArticleDetail";

    private static final String INK_ARTICLE_ID = "ink_article_id";

    @BindView(R.id.zoomPage)
    PinchZoomPage zoomPage;
    @BindView(R.id.errorView)
    NetErrorView errorView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.insideLayout)
    InsideHeaderLayout insideLayout;
    @BindView(R.id.rlClose)
    RelativeLayout rlClose;
    @BindView(R.id.rlInsideLayout)
    RelativeLayout rlInsideLayout;
    @BindView(R.id.llheader)
    LinearLayout llheader;
    @BindView(R.id.llContainer)
    LinearLayout llContainer;
    @BindView(R.id.downFrameLayout)
    OutsideDownFrameLayout downFrameLayout;
    @BindView(R.id.rlBottomLayout)
    RelativeLayout rlBottomLayout;
    @BindView(R.id.pageContainer)
    FrameLayout pageContainer;
    @BindView(R.id.articleSpeakPlayer)
    ArticleSpeakPlayer speakPlayer;
    @BindView(R.id.tvArticleTitle)
    TextView tvArticleTitle;
    @BindView(R.id.tvArticleSource)
    TextView tvArticleSource;
    @BindView(R.id.tvArticleTime)
    TextView tvArticleTime;

    private ArticleDetailPresenter presenter;

    public static void invoke(Context context, String articleId) {
        if (articleId == null)
            return;
        CommonFragActivity.CommonFragParams param = new CommonFragActivity.CommonFragParams();
        param.clsFrag = FragArticleDetail.class;
        param.noTitle = true;
        Intent intent = CommonFragActivity.createIntent(context, param);
        intent.putExtra(INK_ARTICLE_ID, articleId);
        context.startActivity(intent);
    }

    @Override
    protected Map<String, BasePresenter> createPresenters() {
        Map<String, BasePresenter> map = new HashMap<>();
        presenter = new ArticleDetailPresenter();
        presenter.setArticleId(getActivity().getIntent().getStringExtra(INK_ARTICLE_ID));

        presenter.setModel(new ArticleDetailModel());
        map.put(ArticleDetailPresenter.class.getSimpleName(), presenter);
        return map;
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_article_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews(rootView);
        //
        ImmersionBar.with(this).fullScreen(true).init();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    private void initViews(View rootView) {
        //region 初始化空布局与展示样式
        errorView.setBtnReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getArticleDetailFromInternet();
            }
        });
        emptyView.setImgRes(com.oldbaby.oblib.R.drawable.img_load_empty_def);
        emptyView.setPrompt("暂无内容");
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        //endregion
        // 设置文章内容的点击事件
        zoomPage.setOnPageItemClickListener(new PinchZoomPage.OnPageItemClickListener() {
            @Override
            public void onTextClick(TextView textView, int textPosition) {

            }

            @Override
            public void onImageClick(ImageView imageView, int imagePosition, List<String> allImageUrls) {
                presenter.onImageClick(imagePosition);
            }

            @Override
            public void onViewClick(View view) {

            }
        });
        // 设置内外布局联动效果
        configCouplingEffects();
        // 设置播放器点击事件监听
        speakPlayer.setMediaPlayerAdapter(new SpeakMediaPlayerAdapter() {
            @Override
            public void changeTextToSmall() {
                if (zoomPage != null)
                    zoomPage.toSmallTextSize();
            }

            @Override
            public void changeTextToBig() {
                if (zoomPage != null)
                    zoomPage.toBigTextSize();
            }

            @Override
            public void goBack() {
                // 关闭页面
                FragArticleDetail.this.finishSelf();
            }

            @Override
            public void onPlayClick() {
                // 点击播放按钮
                // 需要录音授权
                AndPermission.with(getActivity()).runtime()
                        .permission(Permission.RECORD_AUDIO)
                        .rationale(mRationale)
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                if (presenter != null)
                                    presenter.onSpeechSynthesizerClick();
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> permissions) {

                                if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                                    // 这些权限被用户总是拒绝。
                                    PromptDlgAttr promptDlgAttr = new PromptDlgAttr();
                                    promptDlgAttr.title = "无法继续运行";
                                    promptDlgAttr.subTitle = "请去设置页进行授权";
                                    promptDlgAttr.cancelable = false;
                                    promptDlgAttr.showClose = false;

                                    promptDlgAttr.btnText = "立即授权";
                                    promptDlgAttr.btnBgResId = R.drawable.sel_btn_sc_bg;
                                    showPromptDlg(TAG_DIALOG_PROMPT_SETTING, promptDlgAttr, new PromptDlgListener() {
                                        @Override
                                        public void onPromptClicked(Context context, String tag, Object arg) {
                                            hidePromptDlg(TAG_DIALOG_PROMPT_SETTING);
                                            AndPermission.with(getActivity())
                                                    .runtime()
                                                    .setting()
                                                    .onComeback(new Setting.Action() {
                                                        @Override
                                                        public void onAction() {
                                                            // 用户从设置回来了。
                                                        }
                                                    }).start();
                                        }
                                    });
                                }
                            }
                        }).start();
            }
        });
    }

    private void configCouplingEffects() {
        insideLayout.setOutsideLayout(downFrameLayout);
        insideLayout.setScrollView(zoomPage);
        downFrameLayout.setInsideLayout(insideLayout);
        downFrameLayout.setAnimViews(rlBottomLayout);
        zoomPage.setOutsideLayout(downFrameLayout);
        zoomPage.bindContainer(llContainer); // 绑定内容布局
    }

    //形象照展示时，底部关闭按钮 内部布局容器点击收起内页
    @OnClick({R.id.rlClose, R.id.rlInsideLayout})
    public void onCloseInside() {
        insideLayout.moveToTop();
    }

    @Override
    public void showArticleErrorView() {
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        pageContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideArticleErrorView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        pageContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showArticleEmptyView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        pageContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideArticleEmptyView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        pageContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public List<String> getArticleDetailTexts() {
        List<String> textsFromItems = null;
        if (zoomPage == null)
            return textsFromItems;
        textsFromItems = zoomPage.getTextsFromItems();
        return textsFromItems;
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    @Override
    public void setDataToView(List<PageItem> items) {
        zoomPage.setContent(items);
        pageContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPlayButtonType(int type) {
        if (speakPlayer != null)
            speakPlayer.setPlayType(type);
    }

    @Override
    public void hidePlayer() {
        if (speakPlayer != null)
            speakPlayer.setVisibility(View.GONE);
    }

    @Override
    public void showPlayer() {
        if (speakPlayer != null)
            speakPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void startSpeak(int pos) {
        if (zoomPage.getTextViewItems() != null && !zoomPage.getTextViewItems().isEmpty()) {
            TextView textView = zoomPage.getTextViewItems().get(pos);
            textView.setTextColor(getResources().getColor(R.color.color_ac));
            int location = textView.getTop();

            // when extend NestedScrollView need add this method, else smoothScrollTo will has default fling distance
            zoomPage.fling(0);
            zoomPage.smoothScrollTo(0, location);
        }
    }

    @Override
    public void endSpeak(int pos) {
        if (zoomPage.getTextViewItems() != null && !zoomPage.getTextViewItems().isEmpty()) {
            TextView textView = zoomPage.getTextViewItems().get(pos);
            textView.setTextColor(getResources().getColor(R.color.txt_black));
        }
    }

    @Override
    public void setReferer(String referer) {
        zoomPage.setImageReferer(referer);
    }

    @Override
    public void watchImage(int imagePosition, HashMap<String, String> header) {
        ArticleImageAdapter articleImageAdapter = new ArticleImageAdapter(zoomPage.getImageUrlsFromItems());
        FreeImageViewer.invoke(getActivity(), articleImageAdapter,
                imagePosition, articleImageAdapter.count(),
                -1, 0,
                FreeImageViewer.TYPE_SHOW_NUMBER, header);
    }

    @Override
    public void setArticleHeader(String title, String source, String pbTime) {
        if (!StringUtil.isNullOrEmpty(title))
            tvArticleTitle.setText(title);
        if (!StringUtil.isNullOrEmpty(source))
            tvArticleSource.setText(source);
        if (!StringUtil.isNullOrEmpty(pbTime))
            tvArticleTime.setText(pbTime);
    }

    @Override
    public void setCover(String refer, String url) {
        // 如果形象照为空 ScrollView 滑动到顶部后不允许下拉 否则允许下拉
        if (StringUtil.isNullOrEmpty(url)) {
            downFrameLayout.setEnableDragDown(false);
            return;
        } else {
            downFrameLayout.setEnableDragDown(true);
        }
        if (StringUtil.isNullOrEmpty(refer))
            Glide.with(getContext()).load(url).into(ivCover);
        else {
            Headers headers = SpiderHeader.getInstance().addRefer(refer).build();
            Glide.with(getContext()).load(new GlideUrl(url, headers)).into(ivCover);
        }
    }

    @Override
    public void keepScreenOn() {
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void clearScreenOn() {
        JZUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private Rationale mRationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            // 这里使用一个Dialog询问用户是否继续授权。
            PromptDlgAttr promptDlgAttr = new PromptDlgAttr();
            promptDlgAttr.title = "确定取消授权?";
            promptDlgAttr.subTitle = "没有该功能程序将无法使用";
            promptDlgAttr.cancelable = false;
            promptDlgAttr.showClose = false;

            promptDlgAttr.isTwoBtn = true;
            promptDlgAttr.rightBtnText = "重新授权";
            promptDlgAttr.rightBtnBgResId = R.drawable.sel_btn_sc_bg;
            promptDlgAttr.rightBtnTextColorId = R.color.white;

            promptDlgAttr.leftBtnText = "取消授权";
            promptDlgAttr.leftBtnBgResId = R.drawable.sel_btn_sc_bg;
            promptDlgAttr.leftBtnTextColorId = R.color.white;

            showPromptDlg(TAG_DIALOG_PROMPT_RATIONALE, promptDlgAttr, null, new PromptDlgTwoBtnListener() {
                @Override
                public void onPromptLeftClicked(Context context, String tag, Object arg) {
                    hidePromptDlg(TAG_DIALOG_PROMPT_RATIONALE);
                    executor.cancel();
                }

                @Override
                public void onPromptRightClicked(Context context, String tag, Object arg) {
                    hidePromptDlg(TAG_DIALOG_PROMPT_RATIONALE);
                    executor.execute();
                }
            });
        }
    };
}
