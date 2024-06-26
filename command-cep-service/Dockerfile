FROM openjdk:8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH
WORKDIR /app
COPY flink-1.14.6-bin-scala_2.12.tgz /app/flink-1.14.6-bin-scala_2.12.tgz
RUN tar -xvf /app/flink-1.14.6-bin-scala_2.12.tgz
WORKDIR /app/flink-1.14.6
COPY ./script.sh .
COPY ./target/command-cep-service-1.0-SNAPSHOT.jar .
COPY ./flink-conf.yaml /app/flink-1.14.6/conf
RUN chmod +x ./script.sh
EXPOSE 8081
CMD ["/bin/bash"]