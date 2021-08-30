# Classic Cipher Generation

## Backgournd  
As part of learning kubernetes, publish a Web Service that uses kubernetes to deepen my understanding of kubernetes.  
About motivation of a Web Service, I knew the classic ciphers as we encountered in "[The Code Book](https://www.amazon.co.jp/%E6%9A%97%E5%8F%B7%E8%A7%A3%E8%AA%AD%E3%80%88%E4%B8%8A%E3%80%89-%E6%96%B0%E6%BD%AE%E6%96%87%E5%BA%AB-%E3%82%B5%E3%82%A4%E3%83%A2%E3%83%B3-%E3%82%B7%E3%83%B3/dp/410215972X/ref=pd_bxgy_img_1/356-6066896-2580021?pd_rd_w=XYv2b&pf_rd_p=d8f6e0ab-48ef-4eca-99d5-60d97e927468&pf_rd_r=S91MP0VGJFW9R4473B3K&pd_rd_r=3392c553-c9d4-49ca-b357-63f449c0c50e&pd_rd_wg=XUmeV&pd_rd_i=410215972X&psc=1)", and I came to be interesting in the coding of the cipher's algorithm, I built a simple encrypt / decript service.  

## Technology  
### `Front-end`
> html  
> css

### `Back-end`
> Java  
> groovy  
> Grails  

### `Database`
> MySQL  
> Redis

### `Infrastrucuture`
> Docker  
> Docker Toolbox  
> Docker Compose  
> Minikubne  
> nginx

## Common Command List  
** Note: There are three ways to run this applications. However the configurations like endpoint, port etc are a little different for each way.  So below command steps doesn't guruantee to start application successfully. Please see it as  just reference purpose.  


### Command List - k8s  
Deploy applications to k8s  
#### Redis
Docker  
> docker pull redis  

k8s  
> kubectl apply -f Deploy/Redis/redis-deployment.yaml  
> kubectl apply -f Deploy/Redis/redis-service.yaml  

#### MySql
Docker  
> pushd ./Data/mysql  
> docker image build -t kurakuda/ccg:mysql5.7 -f ./Dockerfile .  
> popd  

k8s
> kubectl apply -f Deploy/MySql/mysql-statefulset.yaml  
> kubectl apply -f Deploy/MySql/mysql-service.yaml  
> kubectl logs ccg-mysql -c ccg-mysql  
> kubectl exec -it <Pod> -- mysql -u user -p  

#### App
Docker  
> pushd ./App/classic-cipher-generation-app  
> docker image build -t kurakuda/ccg:app-grails -f ./Dockerfile .  
> popd  

k8s
> kubectl apply -f Deploy/App/app-deployment.yaml  
> kubectl apply -f Deploy/App/app-service.yaml  

#### Web
Docker  
> pushd ./Web  
> docker image build -t kurakuda/ccg:web-nginx -f ./Dockerfile .  
> popd  

k8s  
> kubectl create configmap nginx-conf --from-file=Web/deployment/nginx.conf  
> kubectl create configmap server-conf --from-file=Web/deployment/server.conf  
> kubectl apply -f Deploy/Web/web-deployment.yaml  
> kubectl apply -f Deploy/Web/web-service.yaml  

#### LB
k8s  
> kubectl apply -f Deploy/LB/nodeport.yaml  

### Command List - Docker  
Run Applications by using docker command  
Prerequisite  
Build images by command  for k8s  

#### Redis
Docker  
> docker run -d -p 6379:6379 --name ccg-redis redis:latest  

#### MySql
Docker  
> docker run -d -p 3306:3306 -e MYSQL_DATABASE=classic_cipher_generation_db -e MYSQL_USER=user -e MYSQL_PASSWORD=password -e MYSQL_ROOT_PASSWORD=rootpassword --name ccg-mysql kurakuda/ccg:mysql5.7    

#### App
Docker  
> docker run -d -v :/app -e DB_HOST=db -p 8080:8080 -e DB_NAME=ccg-db -e DB_PORT=3306 -e DB_USERNAME=user -e DB_PASSWORD=password --name ccg-app kurakuda/ccg:app-grails  

#### Web
Docker  
> docker run -d -p 80:80 -v :/usr/share/nginx/html --name ccg-web kurakuda/ccg:web-nginx  

### Command List - Docker Compose  
Run Applications by using Docker Compose  

- Start / stop the applications  
> docker-compose up -d --build  
> docker-compose down

- Enter the container
> docker exec -it classic_cipher_generation_web bash  
> docker exec -it classic_cipher_generation_app bash  
> docker exec -it classic_cipher_generation_db bash  
> mysql -u user -ppassword classic_cipher_generation_db  
> docker exec -it classic_cipher_generation_redis bash  
> redis-cli  

- Check the container log
> docker logs classic_cipher_generation_app  
> docker logs classic_cipher_generation_db  
