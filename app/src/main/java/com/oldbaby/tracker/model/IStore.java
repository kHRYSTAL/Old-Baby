package com.oldbaby.tracker.model;

import com.oldbaby.tracker.bean.TrackerEvent;

import java.util.List;

public interface IStore {

    /**
     * 存储一个统计事件
     *
     * @param se
     */
    void log(TrackerEvent se);

    /**
     * 获取上传的事件
     *
     * @return
     */
    List<TrackerEvent> getUploadEvents(int maxUploads);

    /**
     * 获取当前日志总数
     */
    int getTrackerEventCounts();

    /**
     * 删除上传成功的事件
     *
     */
    void finishUpload(long maxId);
}