docker exec -it wildfly-full bash

history|grep run , dann mit 'nummer'! auswählen

docker build -t bomc/prometheus-metrics .

# Run wildfly with data-container.
docker run -it -p 8180:8080 -p 9190:9090 --name prometheus-metrics -h prometheus-metrics --volumes-from data-container bomc/prometheus-metrics

# Test url, port 8080 is exposed to 8180
curl -v -H "Accept: application/json" -H "Content-Type: application/json" -X PUT -d "{\"uri\":\"http://192.168.99.100:8280/sample-service/rest/metrics\",\"application\":\"sampleservice\"}" "http://192.168.99.100:8180/bomc-prometheus/rest/configurations/sampleservice"

curl -v -H "Accept: text/plain" -X GET "http://192.168.99.100:8180/bomc-prometheus/rest/metrics/sampleservice"