IDEA使用sonarLint
### IDEA如何安装SonarLint插件
1. 打开 Idea
2. 点击【File】
3. 点击【Settings】
4. 点击【Plugins】
5. 在搜索栏中输入“sonarlint”关键字
6. 点击【Install】进行安装
7. 重启Idea

### IDEA如何连接Sonar服务器
1. 打开 Idea
2. 点击【File】→【Settings】
3. 选择【SonarLint General Settings】
4. 点击【+】
5. ConfigurationName：请输入连接名，如SonarQube
6. Choosea Connection Type：sonarqube
7. SonarQubeURL：http://xxxx
8. AuthenticationType：Login/Password
    •Login：请输入sonar系统登录用户名
    •Password：请输入sonar系统登录密码
9. 点击【Next】，提示连接成功

### IDEA中SonarLint如何进行代码扫描
#### 获取云端报告
1. 点击【SonarLint】
2. 点击【Report】
#### 扫描整个工程
1. 打开 IDEA
2. 右击项目名称，如mengniu-mn-gongxiang-service
3. 点击【SonarLint】
4. 点击【AnalyzeAll Files with SonarLint】
5. 弹出确认窗口，点击【Proceed】继续
6. 【SonarLint Analysis】窗口会显示扫描进度
#### 扫描单个文件
打开单个文件，sonarlint会自动进行sonar扫描