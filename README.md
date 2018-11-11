### 注意1：Master分支是开发分支、也是项目重构分支，Origin分支是原来项目全部代码

### 注意2：目前的版本不会再继续进行新功能开发，只会进行BUG修复，会拉一个分支基于jdk11和javafx11重构项目，重构后会减少内存占用50%左右，启动速度优化到1-3s启动

### 注意3：v1.1.0以后自己打包的，运行会报错误，因为增加了SSH通道、集群功能，需要将用户目录下的.redis_plus文件夹删掉，然后重新启动就会正常了

# RedisPlus

#### 项目介绍

RedisPlus是为Redis可视化管理开发的一款开源免费的桌面客户端软件，支持Windows 、Linux、Mac三大系统平台，RedisPlus提供更加高效、方便、快捷的使用体验，有着更加现代化的用户界面风格。该软件支持单机、集群模式连接，同时还支持SSH（单机、集群）通道连接。RedisPlus遵循GPL-3.0开源协议，禁止二次开发打包发布盈利，违反必究！RedisPlus致力于为大家提供一个高效的Redis可视化管理软件。

#### 下载地址

百度下载：https://pan.baidu.com/s/1ETwWnEj4rbsE1S3GlYHlWg


#### 版本说明

版本的命名规则以x.y.z形式

重大版本更新发布会更改x位主版本，例如v2.0.0 版本会增加集群、支持ssh通道连接、国际化等功能

一般解决了多个缺陷后，会发布一个小版本，更改y位，例如v1.1.0解决了1.0.0 的问题后发布的小版本

z位的更改不会发布版本，这是缺陷修复或者小需求增加的正常迭代


#### 版本规划

v2.0.0

1.SSH通道支持 （已支持）

2.集群连接支持 （已支持）

3.修复v1.1.0版本反馈问题


v3.0.0

1.使用jdk11+javafx11重构项目

2.使用zgc技术优化性能

3.修复v2.0.0反馈问题


3.0.0 详细更新日志：

<p>1.基于2.0.0版本重构，移除内置web容器，加强性能</p>

<p>2.支持jdk11，基于javafx11重构</p>

<p>3.优化80%的界面布局，支持大家反馈的适配各种分辨率机器</p>

<p>4.增加设置中心，陆续增加更多自定义项(目前支持主题颜色更改)</p>

<p>5.增加集群和单机模式下的数据新增功能</p>

<p>6.增加集群和单机模式下的数据还原、备份功能</p>

<p>7.增加集群和单机模式下的数据清空功能</p>

<p>8.重写单机模式信息模块功能，增加操作日志查看</p>

<p>9.重写集群模式信息模块功能，增加主从节点关系查看</p>

<p>10.重写单机模式数据项目监控模块，支持内存、CPU、键数量、流量实时监控</p>

<p>11.重写集群模式数据项目监控模块，支持内存、CPU、键数量、流量实时监控（连接的主节点，后续会增加查看多所有主节点）</p>

<p>12.修复2.0.0反映的redis4.0高版本集群连接失败问题</p>

<p>13.修复2.0.0反映的其他问题</p>


#### 软件交流

1.QQ群：857111033  点击链接加入群聊【RedisPlusj交流群】：https://jq.qq.com/?_wv=1027&k=5nFw9eg

2.issues专区：https://gitee.com/MaxBill/RedisPlus/issues
 

#### 技术选型

1.支持跨平台，使用java开发

2.使用javafx的桌面元素

3.使用derby内嵌数据库

4.内置服务使用springboot开发


#### 打包说明

1.linux平台：需要安装fakeroot库

2.windows：需要安装WiX Toolset、Inno Setup、microsoft .net framework库

#### 应用截图

<p><img alt="" src="https://oscimg.oschina.net/oscnet/f2c92653ff4674236ef682f2ae35b0ec859.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/5fbe6ddb858d8cbdde5d4103f7765777adf.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/b333e4e629f6707961c05be130f4f68ceb8.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/449f6798e64cc444bec4e898194fec33add.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/10571062419240bb9f26a979771674b42c0.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/a08fbfd6c4f5dcf39b7b333ce51751e5f1d.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/6ac1a8a4af9766e052fd2bbdecd29d71b08.jpg" /></p>

<p><img alt="" src="https://oscimg.oschina.net/oscnet/83d6371abe012ffab51333b05841ba23b16.jpg" /></p>