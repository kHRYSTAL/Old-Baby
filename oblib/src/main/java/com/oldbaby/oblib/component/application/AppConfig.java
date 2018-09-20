package com.oldbaby.oblib.component.application;

/**
 * usage: app配置目录与接口
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class AppConfig {
    /**
     * 获取缓存根目录
     *
     * @return
     */
    private String rootDir;
    /**
     * 文件上传服务的url
     *
     * @return
     */
    private String uploadBaseUrl;
    /**
     * 自定义的schema，用于应用中native代码的URI使用，以及webview中自定义链接的使用
     *
     * @return
     */
    private String schema;
    /**
     * 应用的运行环境配置
     *
     * @return
     */
    private int envType;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getUploadBaseUrl() {
        return uploadBaseUrl;
    }

    public void setUploadBaseUrl(String uploadUrl) {
        this.uploadBaseUrl = uploadUrl;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
