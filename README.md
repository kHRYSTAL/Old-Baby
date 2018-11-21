<center>
<img src="https://ws1.sinaimg.cn/large/a856b58cly1fxfllkrt8tj20go0godgu.jpg" width="25%" height="25%" />
</center>


> 该版本为个人项目, 服务端数据暂不开源, 账户功能已阉割, 其他功能完整 可用于从零到一进行Android项目开发学习与功能实现
 数据来源均为爬取 侵删

### 功能点

* 重新封装`pulltorefresh`并修复一些bug
* 根据个人习惯从头封装 MVP模式 基于`Fragment`开发 抽取module, 可配合`pulltorefresh`或其他刷新库用于其他项目
* 资讯详情页支持手势放大缩小 根据科大讯飞语音合成功能回调锚定到指定位置
* 使用[Android-skin-support](https://github.com/ximsfei/Android-skin-support) 支持夜间模式
* 使用[JiaoZiVideoPlayer](https://github.com/lipangit/JiaoZiVideoPlayer) 支持视频播放
* 使用[mmkv](https://github.com/Tencent/MMKV) 支持轻量级缓存
* 使用[科大讯飞sdk](https://www.xfyun.cn/services/online_tts) 支持语音合成功能

### TODO List

* 简易的uri跳转功能 需要支持h5, push, 客户端内部跳转, 支持拦截器, 支持携带参数
* 使用由jsbrige封装的hybrid框架[threesome](https://github.com/kHRYSTAL/threesome) 实现一些native与h5之间的交互
* 实现简易埋点统计功能




```
Copyright (C) 2018 kHRYSTAL

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```