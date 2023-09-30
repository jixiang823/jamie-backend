# Jamie API自动化测试框架
## 项目介绍
***Jamie***是一款基于`JMeter + Spring Boot + Vue Admin Template`的API自动化测试框架, 通过`Docker`一键安装部署. 框架具备提交脚本、手动/自动化执行测试脚本、生成测试报告、导出测试结果等功能.
![000.PNG](src%2Fmain%2Fresources%2FREADME%2F000.PNG)
## 开源地址
https://gitee.com/jixiang823/projects?tag=jamie
## 技术栈
*Docker | CentOS 7 | Java 8 | Springboot 2.7.4 | MySQL 5.7 | Vue 2 | JMeter 5.6.2*
![001.jpg](src%2Fmain%2Fresources%2FREADME%2F001.jpg)
## 安装项目
```log
# 1. 安装docker (过程略,自行百度)
https://www.docker.com/

# 2. 拉取镜像
docker pull registry.cn-hangzhou.aliyuncs.com/jix/jamie:20231001

# 3. 获取镜像ID
docker images

# 4. 启动镜像 (把$IMAGE_ID替换成实际镜像ID)
docker run --privileged=true --cap-add SYS_ADMIN -e container=docker --name="jamie" -p 30022:22 -p 30080:80 -p 33306:3306 -p 9123:9123 -d  $IMAGE_ID /usr/sbin/init

# 5. 获取容器ID
docker ps

# 6. 启动容器 (把$CONTAINER_ID替换成实际容器ID)
docker start $CONTAINER_ID

# 7. 访问项目
http://localhost:30080
```

## 本地连接容器环境
```log
# 1. ShellCraft(XShell)访问容器
Host: localhost
Port: 30022
Protocal: ssh
User Name: root
Password: 2020

# 2. Navicat访问容器内数据库
Host: localhost
Port: 33306
User Name: root
Password: 123456
```

## JMeter配置
容器环境无需配置, 这里是对本地测试环境做配置.
1. 安装JMeter (过程略,自行百度)
   https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.2.zip
2. 引入后端监听器
   将`jamie-report-backend-listener.jar`放置在`JMeter`的`/lib/ext`目录下.
3. 配置后端监听器
    * 新建`.jmx`脚本, `添加` -> `监听器` -> `后端监听器`, 选择 `JamieReportBackendListenerClient`
      ![002.png](src%2Fmain%2Fresources%2FREADME%2F002.png)
    * 填写相关参数
      ![003.png](src%2Fmain%2Fresources%2FREADME%2F003.png)
## 脚本规范
为提高脚本的可读性和可维护性, 请遵守:
* **独立运行**每个线程组.
* 一个**线程组**代表一个业务模块.
* 一个**事务控制器**代表一条测试用例.
* 每条测试用例可由一个或多个**HTTP请求**组成.
* 确保每条测试用例能够单独运行, 测试用例之间无依赖关系.
![004.png](src%2Fmain%2Fresources%2FREADME%2F004.png)
## 项目主要功能介绍
### 测试报告
本地执行JMeter脚本后, 可访问 [Jamie](http://localhost:30080/#/dashboard)查看测试报告. 每执行一个`.jmx`文件就会生成一份报告. 如果存在若干属于同一业务线的脚本, 请将脚本合并为一个`.jmx`文件.
![005.jpg](src%2Fmain%2Fresources%2FREADME%2F005.jpg)
**Dashboard报告页**呈现的信息主要包括:
* 饼图区域
   * 项目名称、执行环境、执行批次号、执行时间 (当前批次)
   * 测试用例总数、用例执行的成功率、失败率 (当前批次)
     ![006.png](src%2Fmain%2Fresources%2FREADME%2F006.png)
* 趋势图区域
   * 测试用例通过数的趋势 (最近的7个批次)
   * 测试用例失败数的趋势 (最近的7个批次)
   * 测试用例通过率的趋势 (最近的7个批次)
     ![007.png](src%2Fmain%2Fresources%2FREADME%2F007.png)
* 晴雨表区域
   * 执行通过的用例个数  (当前批次)、 执行通过的趋势 (最近的7个批次)
   * 执行失败的用例个数  (当前批次)、 执行失败的趋势(最近的7个批次)
   * 新增失败的用例个数 (上一批次某用例执行成功, 但这一批次执行失败)、 新增失败的趋势 (最近的7个批次)
   * 持续失败的用例个数 (上一批次某用例执行失败, 这一批次还是执行失败)、 持续失败的趋势 (最近的7个批次)
     ![008.png](src%2Fmain%2Fresources%2FREADME%2F008.png)
* 列表区域 (用例列表)
   * 业务名称、用例名称、用例的步骤数、用例的作者、用例的执行状态、用例的执行时间
     ![009.png](src%2Fmain%2Fresources%2FREADME%2F009.png)
* 列表区域 (用例详情)
   * API的请求信息
   * API的响应信息
   * API的断言结果
     ![010.png](src%2Fmain%2Fresources%2FREADME%2F010.png)
### 容器内运行脚本
#### 手动运行
在自动化运行脚本前, 建议手动运行一次, 以确保脚本能够正常执行.
1. 提交本地脚本到服务器.
   ![011.png](src%2Fmain%2Fresources%2FREADME%2F011.png)
2. 进入**手动运行**页面, 输入待执行脚本在容器内的绝对路径, 如`/root/scripts/demo.jmx`, 点击“运行”.
   ![012.png](src%2Fmain%2Fresources%2FREADME%2F012.png)
3. 观察**脚本日志**, 确认脚本运行结果符合预期.
   ![013.png](src%2Fmain%2Fresources%2FREADME%2F013.png)
4. 回到**Dashboard**页面查看测试报告.

#### 自动化运行
1. 进入**自动化配置**页面, 填写corn表达式, 如`0 0 1 * * ?`, 表示每天凌晨1点执行一次.
2. 将**启用定时任务**按钮切换为亮态, 表示开启定时任务.
3. 点击**提交**按钮, 完成自动化配置.
   ![014.png](src%2Fmain%2Fresources%2FREADME%2F014.png)
4. 若需要关闭自动化任务, 可将**启用定时任务**按钮切换为灰态, 再点击**提交**按钮即可.

### 测试结果
**测试结果页**记录所有历史测试数据, 主要功能有:
1. 支持关键词多条件模糊查询.
   ![015.jpg](src%2Fmain%2Fresources%2FREADME%2F015.jpg)
2. 点击测试结果页每行的数据, 可查看该条用例的具体执行情况.
   ![016.jpg](src%2Fmain%2Fresources%2FREADME%2F016.jpg)
3. 支持一键**导出**测试结果.
   ![017.png](src%2Fmain%2Fresources%2FREADME%2F017.png)
