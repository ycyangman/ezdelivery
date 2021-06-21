# 환경구성

* aws configure

* EKS Cluster create
```
$ eksctl create cluster --name (Cluster-Name) --version 1.17 --nodegroup-name standard-workers --node-type t3.medium --nodes 3 --nodes-min 1 --nodes-max 3
$ eksctl create cluster --name user05ssb --version 1.17 --nodegroup-name standard-workers --node-type t3.medium --nodes 4 --nodes-min 1 --nodes-max 4


```

* EKS Cluster settings
```
$ aws eks --region ap-northeast-2 update-kubeconfig --name user05ssb
$ kubectl config current-context
$ kubectl get all
```

* ECR 인증
```
$ aws ecr get-login-password --region eu-central-1
docker login --username AWS -p <passwowrd> 740569282574.dkr.ecr.eu-central-1.amazonaws.com
```

* Metric Server 설치
```
$ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.3.6/components.yaml
$ kubectl get deployment metrics-server -n kube-system
```

* Kafka install (kubernetes/helm)
* •	Helm 3.x 설치(권장)

```
curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
chmod 700 get_helm.sh
./get_helm.sh

```
•	Helm 에게 권한을 부여하고 초기화
```
kubectl --namespace kube-system create sa tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller

```
* Kafka

```
helm repo add incubator https://charts.helm.sh/incubator 
helm repo update 
kubectl create ns kafka 
helm install my-kafka --namespace kafka incubator/kafka 
```

혹은
```
helm repo update
helm repo add bitnami https://charts.bitnami.com/bitnami
kubectl create ns kafka
helm install my-kafka bitnami/kafka --namespace kafka

kubectl get all -n kafka

```
Kafka 내부에 진입하여 메시지 확인하기(교재내용인데 잘 안됨)

```
kubectl run my-kafka-client --restart='Never' --image docker.io/bitnami/kafka:2.8.0-debian-10-r0 --namespace kafka --command -- sleep infinity
    kubectl exec --tty -i my-kafka-client --namespace kafka -- bash

    PRODUCER:
        kafka-console-producer.sh \
            
            --broker-list my-kafka-0.my-kafka-headless.kafka.svc.cluster.local:9092 \
            --topic test

    CONSUMER:
        kafka-console-consumer.sh \
            
            --bootstrap-server my-kafka.kafka.svc.cluster.local:9092 \
            --topic test \
            --from-beginning

```

* Istio 설치
```
$ curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.4.5 sh -
$ cd istio-1.4.5
$ export PATH=$PWD/bin:$PATH
$ for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done
$ kubectl apply -f install/kubernetes/istio-demo.yaml
$ kubectl get pod -n istio-system
```

* Kiali 설정
```
$ kubectl edit service/kiali -n istio-system

- type 변경 : ClusterIP -> LoadBalancer
- (접속주소) http://http://ac5885beaca174095bad6d5f5779a443-1156063200.ap-northeast-2.elb.amazonaws.com:20001/kiali
```

* Namespace 생성
```
$ kubectl create namespace ezdelivery
```

* Namespace istio enabled
```
$ kubectl label namespace ezdelivery istio-injection=enabled 

- (설정해제 : kubectl label namespace ezdelivery istio-injection=disabled --overwrite)
```

* siege deploy
```
cd ezdelivery/yaml
kubectl apply -f siege.yaml 
kubectl exec -it siege -n ezdelivery -- /bin/bash
apt-get update
apt-get install httpie
```

```
apiVersion: v1
kind: Pod
metadata:
  name: siege
  namespace: ezdelivery
spec:
  containers:
    - name: siege
      image: apexacme/siege-nginx
```
# Build & Deploy

* ECR image repository(아래 docker image  동일해야 함)
```
$ aws ecr create-repository --repository-name user08-ezdelivery-gateway --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-store --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-order --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-payment --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-mypage --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-alarm --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-delivery --region ap-northeast-2
$ aws ecr create-repository --repository-name user08-ezdelivery-commission --region ap-northeast-2
```

* image build & push
```
$ cd gateway
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-gateway:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-gateway:latest

$ cd ../store
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-store:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-store:latest

$ cd ../order
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-order:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-order:latest

$ cd ../payment
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-payment:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-payment:latest

$ cd ../mypage
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-mypage:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-mypage:latest

$ cd ../delivery
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-delivery:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-delivery:latest

$ cd ../alarm
$ mvn package
$ docker build -t 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-alarm:latest .
$ docker push 052937454741.dkr.ecr.ap-northeast-2.amazonaws.com/user08-ezdelivery-alarm:latest

```

* Deploy/service 
```
$ kubectl apply -f siege.yaml
$ kubectl apply -f configmap.yaml
$ kubectl apply -f gateway.yaml
$ kubectl apply -f store.yaml
$ kubectl apply -f order.yaml
$ kubectl apply -f payment.yaml
$ kubectl apply -f mypage.yaml
$ kubectl apply -f delivery.yaml
$ kubectl apply -f alarm.yaml

kubectl get svc -n ezdelivery
--------------------------------------------------------
NAME       TYPE           CLUSTER-IP       EXTERNAL-IP                                                                  PORT(S)          AGE
alarm      ClusterIP      10.100.203.217   <none>                                                                       8080/TCP         159m
delivery   ClusterIP      10.100.195.214   <none>                                                                       8080/TCP         74m
gateway    LoadBalancer   10.100.217.245   afaa345f3143649e4aa9fdf6a7196098-11992356.eu-central-1.elb.amazonaws.com     8080:32240/TCP   44s
mypage     ClusterIP      10.100.227.33    <none>                                                                       8080/TCP         38m
order      ClusterIP      10.100.105.118   <none>                                                                       8080/TCP         96m
payment    ClusterIP      10.100.170.105   <none>                                                                       8080/TCP         3h22m
store      LoadBalancer   10.100.72.169    a8246f41e86b64ea9932f056caa8c02c-1581443906.eu-central-1.elb.amazonaws.com   8080:31800/TCP   6h26m
```

* 삭제
````
kubectl delete -f store.yaml
kubectl delete -f gateway.yaml
kubectl delete -f payment.yaml
kubectl delete -f mypage.yaml
kubectl delete -f order.yaml
kubectl delete -f configmap.yaml

```


