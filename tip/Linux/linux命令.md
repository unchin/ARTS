netstat -ntlp

查看端口和进程





   server {
        listen       3234;
        server_name  subject-lib-dev;
        root /home/gitlab-runner/builds/whE8DY1Z/0/group_dev/standard-lib-front/dist; #定义服务器的默认网站根目录位置
        index index.html; #定义index页面
        error_page    404         /index.html; #将404错误页面重定向到index.html可以解决history模式访问不到页面问题
        location ^~ /api/{
            proxy_pass http://127.0.0.1:1234;
        }