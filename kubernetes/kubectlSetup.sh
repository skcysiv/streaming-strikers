#!/bin/bash

# Define the files for deployment, update, and deletion
DEPLOY_FILES=("zookeeper-deployment-service.yaml", "./kafka/kafka-deployment-service.yaml", "./schema-registry/schema-registry-pod.yaml", "./flink/flink-configuration-configmap.yaml", "./flink/jobmanager-deployment.yaml", "./flink/jobmanager-rest-service.yaml", "./flink/jobmanager-service.yaml", "./flink/taskmanager-deployment.yaml", "./kafka-data-ingestion/kafka-data-ingestion-deployment.yaml")
UPDATE_FILES=("update-file1.yaml" "update-file2.yaml")
DELETE_FILES=("./kafka/zookeeper-deployment-service.yaml", "./kafka/kafka-deployment-service.yaml", "./schema-registry/schema-registry-pod.yaml", "./flink/flink-configuration-configmap.yaml", "./flink/jobmanager-deployment.yaml", "./flink/jobmanager-rest-service.yaml", "./flink/jobmanager-service.yaml", "./flink/taskmanager-deployment.yaml", "./kafka-data-ingestion/kafka-data-ingestion-deployment.yaml")
DEPLOYMENT=("kafka-data-ingestion-deployment", "flink-taskmanager", "flink-jobmanager", )

create_topics(){
  echo "Trying to create topics in kafka pod"
  PODS=$(kubectl get pods --no-headers -o custom-columns=":metadata.name" | grep '^kafka-' || true)

  if [ -z "$PODS" ]; then
      echo "No pods starting with 'kafka-' found."
      exit 1
  fi

  # Define the command to execute on each pod
  COMMAND="/app/topic-management.sh -c"

  # Loop through each pod and execute the command
  for pod in $PODS; do
      echo "Executing command on pod $pod..."
      kubectl exec -it "$pod" -- bash -c "$COMMAND"
      echo "Command executed on pod $pod."
  done
}

# Function to deploy pods
deploy_pods() {
    echo "Starting deployment of pods..."
    kubectl apply -f ./kafka/zookeeper-deployment-service.yaml
    sleep 10
    kubectl apply -f ./kafka/kafka-deployment-service.yaml
    sleep 30
    create_topics
    sleep 5
    kubectl apply -f ./schema-registry/schema-registry-pod.yaml
    kubectl apply -f ./flink/flink-configuration-configmap.yaml
    kubectl apply -f ./flink/jobmanager-deployment.yaml
    kubectl apply -f ./flink/jobmanager-rest-service.yaml
    kubectl apply -f ./flink/jobmanager-service.yaml
    kubectl apply -f ./flink/taskmanager-deployment.yaml
    kubectl apply -f ./kafka-data-ingestion/kafka-data-ingestion-deployment.yaml
}

# Function to update pods
update_pods() {
    echo "Starting update of pods..."
    for file in "${UPDATE_FILES[@]}"; do
        echo "Updating $file"
        kubectl apply -f "$file"
        if [ $? -ne 0 ]; then
            echo "Failed to update $file"
        else
            echo "Successfully updated $file"
        fi
    done
}

# Function to delete pods
delete_pods() {
    echo "Starting deletion of pods..."
    kubectl delete deployment kafka-data-ingestion-deployment --force
    kubectl delete deployment flink-taskmanager --force
    kubectl delete deployment flink-jobmanager --force
    kubectl delete configmap flink-config --force
    kubectl delete statefulset kafka --cascade=orphan
    kubectl delete statefulset zookeeper --cascade=orphan
    kubectl delete service kafka --force
    kubectl delete service zookeeper --force
    kubectl delete service flink-jobmanager-rest --force
    kubectl delete service flink-jobmanager --force
    kubectl delete pod schema-registry --force
    kubectl get pods --no-headers -o custom-columns=":metadata.name" | grep '^zookeeper' | xargs kubectl delete pods --force
    kubectl get pods --no-headers -o custom-columns=":metadata.name" | grep '^kafka' | xargs kubectl delete pods --force
}

port_forward(){
  read -p "Enter service name: " choice
  if [[ $choice == *"flink-jobmanager"* ]]; then
    kubectl port-forward $choice 9090:8081 &
  fi
  if [[ $choice == *"kafka-data-ingestion-deployment"* ]]; then
    kubectl port-forward $choice 8181:8181
  fi
}

# Main script execution
echo "Kubernetes Pod Management Script"
echo "1. Deploy Pods"
echo "2. Update Pods"
echo "3. Delete Pods"
echo "4. Port Forward"
echo "5. Exit"
read -p "Choose an option [1-4]: " choice

case $choice in
    1)
        deploy_pods
        ;;
    2)
        update_pods
        ;;
    3)
        delete_pods
        ;;
    4)
        port_forward
        ;;
    5)
        echo "Exiting script."
        exit 0
        ;;
    *)
        echo "Invalid option. Exiting script."
        exit 1
        ;;
esac