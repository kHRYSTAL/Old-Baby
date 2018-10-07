package com.oldbaby.article.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.oldbaby.article.model.IArticleDetailModel;
import com.oldbaby.article.view.IArticleDetailView;
import com.oldbaby.common.bean.Article;
import com.oldbaby.common.util.IFlySpeechConfigUtil;
import com.oldbaby.oblib.component.lifeprovider.PresenterEvent;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.gson.GsonHelper;

import java.util.List;

import rx.Subscriber;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleDetailPresenter extends BasePresenter<IArticleDetailModel, IArticleDetailView> {

    private static final String TAG = ArticleDetailPresenter.class.getSimpleName();

    // 语音合成对象
    private SpeechSynthesizer mSpeechSynthesizer;
    // 是否已经开始
    private boolean isStart = false;
    // 是否已经暂停
    private boolean isPause = false;

    private String articleId;
    private List<String> paragraphs;
    private int currentParagraphIndex;

    @Override
    public void bindView(@NonNull IArticleDetailView view) {
        super.bindView(view);
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(view.getViewContext(), mInitListener);
    }

    @Override
    public void unbindView() {
        super.unbindView();
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.stopSpeaking();
            // 退出时释放连接
            mSpeechSynthesizer.destroy();
        }
    }

    @Override
    protected void updateView() {
        super.updateView();
        if (setupDone() && !StringUtil.isNullOrEmpty(articleId)) {
            getArticleDetailFromInternet();
        }
    }

    // 页面onPause时暂停播放
    public void onPause() {
        if (mSpeechSynthesizer != null) {
            if (isStart) {
                mSpeechSynthesizer.pauseSpeaking();
                isPause = true;
                view().setPlayButtonText("开始");
            }
        }
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void onSpeechSynthesizerClick() {
        if (!isStart) { // 如果还没开始播放 点击则开始播放 按钮置为暂停
            paragraphs = view().getArticleDetailTexts();
            if (paragraphs == null || paragraphs.isEmpty())
                return;
            isStart = true;
            view().setPlayButtonText("暂停");
            // 执行语音合成 在合成回调complete是currentParagraphIndex自增1
            handleSpeak();
        } else {
            if (!isPause) { // 如果已经开始播放 点击则暂停播放
                isPause = true;
                mSpeechSynthesizer.pauseSpeaking();
                view().setPlayButtonText("开始");
            } else { // 如果已经暂停播放 点击则恢复播放
                isPause = false;
                mSpeechSynthesizer.resumeSpeaking();
                view().setPlayButtonText("暂停");
            }
        }
    }

    public void getArticleDetailFromInternet() {
        view().showProgressDlg();
        model().getArticleDetail(articleId)
                .subscribeOn(getSchedulerSubscribe())
                .observeOn(getSchedulerObserver())
                .compose(this.<Article>bindUntilEvent(PresenterEvent.UNBIND_VIEW))
                .subscribe(new Subscriber<Article>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view().hideProgressDlg();
                        MLog.e(TAG, "文章详情获取失败", e, e.getMessage());
                        view().hideArticleEmptyView();
                        view().showArticleErrorView();
                        paragraphs = null;
                        currentParagraphIndex = 0;
                        isPause = false;
                        isStart = false;
                        view().hidePlayButtonText();

                    }

                    @Override
                    public void onNext(Article article) {
                        isPause = false;
                        isStart = false;
                        view().hideProgressDlg();
                        view().hideArticleErrorView();
                        paragraphs = null;
                        currentParagraphIndex = 0;
                        MLog.e(TAG, "文章详情获取成功");
                        MLog.json(TAG, GsonHelper.GetCommonGson().toJson(article));
                        if (article.getArticle() == null || article.getArticle().isEmpty()) {
                            view().showArticleEmptyView();
                            view().hidePlayButtonText();
                        } else {
                            view().hideArticleEmptyView();
                            view().setPlayButtonText("开始");
                            view().setDataToView(article.getArticle());
                        }
                    }
                });
    }

    // 处理分段语音合成逻辑
    private void handleSpeak() {
        if (paragraphs != null && currentParagraphIndex <= paragraphs.size() - 1) {
            int code = mSpeechSynthesizer.startSpeaking(paragraphs.get(currentParagraphIndex), mSpeechListener);
            if (code != ErrorCode.SUCCESS) {
                view().showToast("语音合成失败,错误码: " + code);
            } else {
                currentParagraphIndex++;
            }
        } else {
            MLog.e(TAG, "全部播放完成");
        }
    }

    private SynthesizerListener mSpeechListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            MLog.e(TAG, "开始播放:" + (currentParagraphIndex - 1));
            view().startSpeak(currentParagraphIndex - 1);
        }

        @Override
        public void onSpeakPaused() {
            MLog.e(TAG, "暂停播放:" + currentParagraphIndex);
        }

        @Override
        public void onSpeakResumed() {
            MLog.e(TAG, "继续播放:" + currentParagraphIndex);
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 缓冲进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }


        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                MLog.e(TAG, "第 " + currentParagraphIndex + "段播放完成");
                view().endSpeak(currentParagraphIndex - 1);
                handleSpeak();
            } else {
                view().showToast(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            MLog.e(TAG, "SynthesizerListener onEvent >>>" + eventType);
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                MLog.d(TAG, "session id =" + sid);
            }
        }
    };

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            MLog.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                view().showToast("初始化失败,错误码：" + code);
                // TODO: 18/9/30 初始化失败处理
            } else {
                // 初始化成功，之后可以调用startSpeaking方法 配置语音合成参数
                IFlySpeechConfigUtil.config(mSpeechSynthesizer);
            }
        }
    };
}
