#!/bin/bash

# Function to print usage information
print_usage() {
    echo "Usage: $0 [--env <local|kube>] [--jar <jar_path>]"
    exit 1
}

# Default action
default_action() {
    echo "Starting local Flink cluster..."
    ./bin/start-cluster.sh

    if [ -n "$jar_path" ]; then
        echo "Running Flink job from JAR: $jar_path"
        ./bin/flink run -c com.hackathon.cepservice.Main "$jar_path" rule-based-partitioning
    else
        echo "Running Flink job from default JAR"
        ./bin/flink run -c com.hackathon.cepservice.Main command-cep-service-1.0-SNAPSHOT.jar rule-based-partitioning
fi
}

# Action to perform when environment is 'local'
local_action() {
    cd ../flink-siddhi
    mvn clean install
    cd ../command-cep-service
    mvn clean install
    docker rmi cep-flink
    docker build -t cep-flink .
    docker run -p 9092:8081 -it cep-flink
    sleep 3
    container_id=$(docker ps -qf "ancestor=cep-flink")
    if [ -z "$container_id" ]; then
        echo "Container with image name 'cep-flink' is not running."
        exit 1
    fi
    docker exec -it "$container_id" -- /bin/bash
}

# Action to perform when environment is 'kube'
kube_action() {
    echo "Running Flink job on Kubernetes..."
    ./bin/flink run -c com.hackathon.cepservice.Main command-cep-service-1.0-SNAPSHOT.jar rule-based-partitioning &
}

build_and_push() {
    docker build -t cep-flink .
    docker tag cep-flink shreddersk/cep-flink:latest
    docker push shreddersk/cep-flink:latest
}

# Check if no arguments provided
if [ $# -eq 0 ]; then
    default_action
fi

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --env)
            shift
            env_value="$1"
            ;;
        --jar)
            shift
            jar_path="$1"
            ;;
        *)
            default_action
            ;;
    esac
    shift
done

# Check if --env flag is provided
if [ -z "$env_value" ]; then
    default_action
fi

# Determine action based on the provided environment value
case "$env_value" in
    local)
        local_action
        ;;
    kube)
        kube_action
        ;;
    build_and_push)
        build_and_push
        ;;
    *)
        default_action
        ;;
esac
