docker exec -it prometheus-sample-service-web bash

history|grep run , dann mit 'nummer'! auswählen

docker build -t bomc/prometheus-sample-service-web .

# Run wildfly with data-container.
docker run -it -p 8280:8080 -p 9290:9090 --name prometheus-sample-service-web -h prometheus-sample-service-web --volumes-from data-container bomc/prometheus-sample-service-web

# Test url, port 8080 is exposed to 8280
curl -v -H "Accept: application/json" -X GET "http://192.168.99.100:8280/sample-service/rest/metrics"