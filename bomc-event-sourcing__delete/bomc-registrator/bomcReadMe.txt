Website with swagger:
______________________________________________
"http://192.168.99.100:30080/registrator-app/"


Build image:
______________________________________________
"docker build -t bomc/registrator-wf:v1.0.0 ."


Deploy to Kubernetes:
______________________________________________
kubectl apply -f ./delpoyment/registrator-wf.yml
kubectl apply -f ./delpoyment/registrator-wf-svc.yml

or

kubectl apply -f ./delpoyment
