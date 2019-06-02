# _______________________________________________________
# To compile and build the docker image use the 'run_mvn_docker_build.sh'.
# In git-bash:
./run_mvn_docker_build.sh 

# _______________________________________________________
minikube start --extra-config=apiserver.Admission.PluginNames="Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,GenericAdmissionWebhook,ResourceQuota"

# _______________________________________________________
# Nodeport
#
# involved files
bomc-app_namespace.yml
kuberistio-wf-rc.yml
kuberistio-wf-svc-nodeport.yml

# _______________________________________________________
# Read the ip from minikube
minikube ip

# _______________________________________________________
# Run curl command; -v = verbose; port = nodeport, defined in ./deployment/kuberistio-wf-svc-nodeport.yml;
# bomc-kuberistio = the contextroot, defined in jboss-web.xml or archive name.
curl -v 192.168.99.100:30082/bomc-kuberistio/rest/version/current-version

# _______________________________________________________
# Ingress
# -------------------------------------------------------

# Damit Ingress l�uft braucht es folgende Dateien './deployment':
- bomc-app_namespace.yml
- kuberistio-ingress.yml
- kuberistio-wf-depl.yml
- kuberistio-wf-svc.yml

Das entscheidende ist die Konfiguration in der 'kuberistio-ingress.yml'-Datei.
Das ganze kann in zwei Modi bterieben werden. Einmal mit mit konfiguriertem Host-Namen und einmal eben ohne.

# _______________________________________________ 
1. Ohne Hostname, in 'kuberistio.ingress.yml':
# -----------------------------------------------

...

spec:
  rules:
   # ____________________________________
   # Ohne Hostname
   #- host: bomc.ingress.org
   # ------------------------------------
   - http:
      paths:
      - path: /bomc-kuberistio
        backend:
          serviceName: kuberistio-svc
          servicePort: 80

...

Der Aufruf erfolgt �ber kubctl:

$ curl -v --location --insecure http://192.168.99.100:80/bomc-kuberistio/rest/version/current-version

# _______________________________________________ 
2. Mit Hostnamen in 'kuberistio.ingress.yml':
# -----------------------------------------------

ohne, dass host datei ge�ndert wurde.
curl -L �resolve bomc.ingress.org:80:192.168.99.100 http://bomc.ingress.org/bomc-kuberistio/rest/version/current-version

Der Hostname wird unter 'C:\Windows\System32\drivers\etc\hosts' hinzugef�gt.
- NOTE: der Editor muss als Administrator ge�ffnet werden. Windows -> im Startmen� "Editor" eingeben -> 'Als Administrator' ausw�hlen und Datei �ffnen.

Inhalt: '192.168.99.100 bomc.ingress.org'

...

spec:
  rules:
   # ____________________________________
   # Mit Hostname
   # ------------------------------------
   - host: bomc.ingress.org
     http:
      paths:
      - path: /bomc-kuberistio
        backend:
          serviceName: kuberistio-svc
          servicePort: 80

...

Der Aufruf erfolgt �ber kubctl:

$ curl -v http://bomc.ingress.org/bomc-order/rest/version/current-version


# ________________________________________________
# Install istio
# ------------------------------------------------

# Next to extend the Kubernetes API with Custom Resource Definitions (CRDs) that istio provides with.
$ kubectl apply -f install/kubernetes/helm/istio/templates/crds.yaml

# Install istio without TLS authentication
$ kubectl apply -f install/kubernetes/istio-demo.yaml

# Istio adds sidecar proxies to the services, and this is done without modifying the actual code of the application. 
# If automatic istio-sidecar-injector is enabled. Label a namespace with istio-injection=enabled and 
# when the application is deployed on this namespace the pods themselves will have specialized Envoy containers along 
# with the containers for the core application. 
$ kubectl label namespace bomc-app istio-injection=enabled

# ________________________________________________
# Einige Befehle
# ------------------------------------------------
kubectl delete -f ./deployment/
kubectl get pods --all-namespaces
minikube ip
kubectl apply -f ./deployment/
kubectl describe ing --namespace=bomc-app
kubectl get ing --namespace=bomc-app

minikube service list

# Lunch kubernetes dashboard
minikube dashboard

# Check dashboard URL
minikube dashboard � url
Ssh into minikube
minikube ssh

# Run kubectl commands against minikube
kubectl config use-context minikube
kubectl config current-context
kubectl get po -n kube-system
kubectl get po � all-namespaces
kubectl get all � all-namespaces
kubectl api-versions | grep rbac
kubectl version
kubectl cluster-info
kubectl api-versions

# Test minikube kubernetes deployment
kubectl run hello-minikube � image=k8s.gcr.io/echoserver:1.4 � port=8080 
kubectl expose deployment hello-minikube � type=NodePort
kubectl get services
kubectl get deploy
kubectl get pod

# Find IP
minikube ip

# Find mapped port
kubectl describe service hello-minikube
kubectl get svc hello-minikube


curl http://$IP:$PORT


# Remove testing deployment
kubectl delete services hello-minikube
kubectl delete deployment hello-minikube

# _______________________________________________
eval $(minikube docker-env)

# Note: Later, when you no longer wish to use the Minikube host, you can undo this change by running:
eval $(minikube docker-env -u)

# _______________________________________________
docker info

# https://nickjanetakis.com/blog/docker-tip-31-how-to-remove-dangling-docker-images
docker rmi -f $(docker images -f "dangling=true" -q) 


http://192.168.99.100:31380/bomc-order/swagger-ui/

