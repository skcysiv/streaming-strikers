mvn clean install
docker rmi cep-flink
docker build -t cep-flink .
docker run -p 9090:8081 -it cep-flink
# run ./script.sh once you get prompt