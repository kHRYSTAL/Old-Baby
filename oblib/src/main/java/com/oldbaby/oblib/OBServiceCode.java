package com.oldbaby.oblib;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class OBServiceCode {
    /**
     * 通用返回码
     */
    public static final int COMM_OK = 200; // ok
    public static final int COMM_FAIL_SERVER = 100; // 系统异常
    public static final int COMM_FAIL_PARAMS = 101; // 参数异常

    public static final int COMM_TKN_OVERDUE = 102; // token过期或未登陆
    public static final int COMM_TKN_INVALID = 103; // 无效的token

    /**
     * 用户信息相关返回码
     */
    public static final int USER_LOGIN_FAIL_PASS = 301; // 登录错误：密码错误
    public static final int USER_LOGIN_FIAL_NOUSER = 302; // 登录错误：无此用户
    public static final int USER_LOGIN_FIAL_UIDNULL = 309; // 登录错误：返回的用户UID为空
    public static final int USER_INSERT_FAIL_EXIST = 303; // 插入用户信息失败：当前账号已存在
    public static final int USER_FAIL_LOGINTYPE = 304; // 登录类型错误

    /**
     * 查询数据结果
     */
    public static final int USER_SEARCH_NO_RESULT = 401; // 未检索到数据

    /**
     * 安装客户端更新
     */
    public static final int VERSION_RECOMMEND = 701; // 已经是最新版本
    public static final int VERSION_NON_EXIST = 704; // 未检测到安装包
}
