package com.oldbaby.tracker.bean;

import com.oldbaby.oblib.OrmDto;

public class TrackerEvent extends OrmDto {

    public TrackerEvent() {
    }

    /**
     * @param sessionId
     * @param level
     * @param type
     * @param name
     * @param time
     */
    public TrackerEvent(String sessionId, String level, String type, String name, Long time) {
        this.sessionId = sessionId;
        this.level = level;
        this.type = type;
        this.name = name;
        this.time = time;
    }

    public String sessionId;  // 会话id [必选，每次会话周期变更时重新生成，格式为ts_xxxxx(device_id)_xxxxx(时间戳，单位毫秒)，例如：ts_903f5573a5dd416fbb43aff072e773a4_1464937750246。会话周期：以客户端打开或切到前台，到应用被切到后台为一个会话周期]
    public String level;  // 事件等级 [必选，取值：page、event]
    public String type;   // 事件类型 [必选，取值：level:page = in、out；level:event = share、reg、login等等]
    public String name;   // 事件名称 [必选，事件发生时所在的页面名]
    public String alias;  // 事件别名 [可选，对事件名称起的可读性更好的别名]
    public Long time;     // 事件时间 [必选，事件发生的时间点，单位：毫秒]
    public String module; // 事件相关模块 [可选，用逗号分割的串，例如 "login,event,profile"]
    public Long duration; // 页面停留事件 [可选，仅当level:page && type:out时有值，单位：毫秒]
    public String relationId; // 关联Id [可选，仅在level:event && type='需要关联id的业务场景'时有值，例如type=share]
    public String userInfo; // 自定义参数 [可选，在某些需要自定义参数的场景中有值，以json方式提供]
    public String from;     // 页面来源[可选，仅当需要增加页面来源时添加]
}