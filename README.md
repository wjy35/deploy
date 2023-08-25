# EC2 Initial Setting

Package version
```text
sudo apt-get update
```

```text
sudo apt-get upgrade
```

Time zone
```text
sudo timedatectl set-timezone Asia/Seoul
```

### UFW 

pacakge를 이용한 ufw 설치
```text
sudo apt install ufw
```

ufw 를 활용한 방화벽 설정
```text
sudo ufw default deny incoming 
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow http 
sudo ufw allow https
```

### NginX

nginx 설치
```text
sudo apt-get install nginx
```

80번 port 상태 체크
```text
netstat -an | grep 80
```

nginx service 상태 확인
```text
systemctl start nginx
systemctl status nginx
```

```text
sudo service nginx stop 
sudo service nginx start 
sudo service nginx restart
sudo service nginx reload
```

nginx 삭제
```text
sudo apt-get -y remove --purge nginx nginx-full nginx-common
```


1. /etc/nginx/conf.d 로 이동

```text
cd /etc/nginx/conf.d 
```

2. .conf 파일 생성

```text
sudo vim default.conf
```

### Docker

```text
sudo apt-get -y install apt-transport-https ca-certificates curl gnupg-agent software-properties-common
```

```text
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add
```

```text
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
```

```text
sudo apt-get update && sudo apt-get install docker-ce docker-ce-cli containerd.io
```

```text
sudo usermod -aG docker ubuntu
sudo service docker restart
newgrp docker
```

### Docker Compose

```text
sudo apt install jq
```

```text
DCVERSION=$(curl --silent https://api.github.com/repos/docker/compose/releases/latest | jq .name -r)
```
```text
DCDESTINATION=/usr/bin/docker-compose
```
```text
sudo curl -L https://github.com/docker/compose/releases/download/${DCVERSION}/docker-compose-$(uname -s)-$(uname -m) -o $DCDESTINATION
```

```text
sudo chmod 755 $DCDESTINATION
```

```text
docker-compose -v
```

### Docker Network

```text
docker network create deploy
```

### Jenkins

```text
df -h 
sudo fallocate -l 8G /swapfile 
sudo chmod 600 /swapfile 
sudo mkswap /swapfile 
sudo swapon /swapfile 
free -h
```

```text
sudo docker pull jenkins/jenkins:lts
```

```text
sudo docker run -d \
  --env JENKINS_OPTS=--httpPort=9090 \
  --env JAVA_OPTS=-Xmx2g \
  -v /etc/localtime:/etc/localtime:ro \
  -e TZ=Asia/Seoul \
  -p 9090:9090 \
  -v /jenkins:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker-compose:/usr/bin/docker-compose \
  --name jenkins \
  -u root \
  jenkins/jenkins:lts
```

```text
upstream jenkins {
    keepalive 32; 
    server 127.0.0.1:9090; 
}

map $http_upgrade $connection_upgrade {
    default upgrade;
    '' close; 
}

server {
    listen          80;
    server_name     13.125.224.152;
    root            /var/run/jenkins/war/;
    access_log      /var/log/nginx/jenkins.access.log;
    error_log       /var/log/nginx/jenkins.error.log;
    ignore_invalid_headers off;
        
    location ~ "^/static/[0-9a-fA-F]{8}\/(.*)$" {
        rewrite "^/static/[0-9a-fA-F]{8}\/(.*)" /$1 last;
    }
        
    location /userContent {
        root /var/lib/jenkins/;
        if (!-f $request_filename){
            rewrite (.*) /$1 last;
            break;
        }
        sendfile on;
    }  
 
    location / {
        sendfile off;
        proxy_pass          http://jenkins;
        proxy_redirect      default;
        proxy_http_version  1.1;
        proxy_set_header    Connection	        $connection_upgrade;
        proxy_set_header    Upgrade             $http_upgrade;
        proxy_set_header    Host                $http_host;
        proxy_set_header    X-Real-IP           $remote_adr;
        proxy_set_header    X-Forwarded-For     $proxy_add_x_forwarded_for;
        proxy_set_header    X-Forwarded-Proto   $scheme;
        proxy_max_temp_file_size 0;

        client_max_body_size       10m;
        client_body_buffer_size    128k;
        proxy_connect_timeout	    90;
        proxy_send_timeout	        90;
        proxy_read_timeout	        90;
        proxy_buffering	            off;
        proxy_request_buffering 	off;
        proxy_set_header Connection “”;
    }
}
```

```text
sudo docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

