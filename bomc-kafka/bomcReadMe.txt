Website with swagger:
______________________________________________
"http://192.168.99.100:30080/kafka-poc/"


Build image:
______________________________________________
"docker build -t bomc/kafka-wf ."


Deploy to Kubernetes:
______________________________________________
kubectl apply -f ./delpoyment/kafka-wf.yml
kubectl apply -f ./delpoyment/kafka-wf-svc.yml

or

kubectl apply -f ./delpoyment
