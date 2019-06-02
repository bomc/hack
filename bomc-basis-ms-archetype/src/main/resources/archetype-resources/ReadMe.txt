# _______________________________________________________
# To compile and build the docker image use the 'run_mvn_docker_build.sh'.
# In git-bash:
./run_mvn_docker_build.sh 

# _______________________________________________________
minikube start --extra-config=apiserver.Admission.PluginNames="Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,GenericAdmissionWebhook,ResourceQuota"
minikube start --kubernetes-version v0.34.0

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
# Enable ingress.
minikube addons enable ingress

# Damit Ingress läuft braucht es folgende Dateien './deployment':
- bomc-app_namespace.yml
- kuberistio-ingress.yml
- kuberistio-wf-depl.yml
- kuberistio-wf-svc.yml

Das entscheidende ist die Konfiguration in der 'kuberistio-ingress.yml'-Datei.
Das ganze kann in zwei Modi bterieben werden. Einmal mit mit konfiguriertem Host-Namen und einmal eben ohne.______

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

Der Aufruf erfolgt über kubctl:

$ curl -v --location --insecure http://192.168.99.100:80/bomc-kuberistio/rest/version/current-version

# _______________________________________________ 
2. Mit Hostnamen in 'kuberistio.ingress.yml':
# -----------------------------------------------

ohne, dass host datei geändert wurde.
curl -L –resolve bomc.ingress.org:80:192.168.99.100 http://bomc.ingress.org/bomc-kuberistio/rest/version/current-version

Der Hostname wird unter 'C:\Windows\System32\drivers\etc\hosts' hinzugefügt.
- NOTE: der Editor muss als Administrator geöffnet werden. Windows -> im Startmenü "Editor" eingeben -> 'Als Administrator' auswählen und Datei öffnen.

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

Der Aufruf erfolgt über kubctl:

$ curl -v http://bomc.ingress.org/${artifactId}/rest/version/current-version


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
# Install kiali
# ------------------------------------------------
bash <(curl -L http://git.io/getLatestKialiKubernetes)

check if kiali pod is running
kubectl get pods -n istio-system

create secrets for kiali

apiVersion: v1
kind: Secret
metadata:
  name: kiali
  namespace: istio-system
  labels:
    app: kiali
type: Opaque
data:
  username: YWRtaW4=
  passphrase: YWRtaW4=

Get logs -> from kiali pod:
kubectl logs -n istio-system kiali-568997dcdb-269h2

Get port with:
kubectl get svc -n istio-system kiali --output 'jsonpath={.spec.ports[*].nodePort}'
and than use this port for forwarding:
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=kiali -o jsonpath='{.items[0].metadata.name}') ${32362}

Open website:
https://192.168.99.100:32363/kiali

Open Grafana, start port forwarding to port 3000:
kubectl -n istio-system port-forward grafana-59b8896965-fs7k8 3000
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000
Open website:
http://localhost:3000


Open tracing Jaeger:
Open Grafana, start port forwarding to port 16686:
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686
Open website:
http://localhost:16686

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
minikube dashboard — url
Ssh into minikube
minikube ssh

# Run kubectl commands against minikube
kubectl config use-context minikube
kubectl config current-context
kubectl get po -n kube-system
kubectl get pods —-all-namespaces
kubectl get all —-all-namespaces
kubectl api-versions | grep rbac
kubectl version
kubectl cluster-info
kubectl api-versions
kubectl get pods --show-labels

# Get logs, this log runs with istio enabled. bomc-order is one conatiner in pod, istio is the other one.
kubectl logs -n bomc-app bomc-order-7dcfbdd94c-srstx bomc-order

# Show environment variables of pod.
kubectl exec bomc-order-77c89d78cd-j9t7l -n=bomc-app -- printenv


# Test minikube kubernetes deployment
kubectl run hello-minikube — image=k8s.gcr.io/echoserver:1.4 — port=8080 
kubectl expose deployment hello-minikube — type=NodePort
kubectl get services
kubectl get deploy
kubectl get pod
# Print out environment variables for services 
kubectl exec bomc-order-6b6dcdd5f8-txjcj -n bomc-app -- printenv | grep SERVICE

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

# _______________________________________________
# List Dangling images
docker images -f dangling=true

# Remove Dangling Images
docker rmi $(docker images -f dangling=true -q)

