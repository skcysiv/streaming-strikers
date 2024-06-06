#!/bin/bash

KAFKA_BROKER="kafka:9092"
DEFAULT_TOPICS=("rules-topic" "signals-topic" "events-topic")


# Function to create Kafka topics
create_topics() {
    local topics=("$@")
    if [ ${#topics[@]} -eq 0 ]; then
        topics=("${DEFAULT_TOPICS[@]}")
    fi

    for topic in "${topics[@]}"; do
      echo "Creating $topic"
        kafka-topics.sh --create --topic "$topic" --if-not-exists --bootstrap-server "$KAFKA_BROKER" --partitions 1 --replication-factor 1
        if [ $? -eq 0 ]; then
            echo "Topic '$topic' created successfully"
        else
            echo "Failed to create topic '$topic'"
        fi
    done
}

# Function to delete Kafka topics
delete_topics() {
    local topics=("$@")
    if [ ${#topics[@]} -eq 0 ]; then
        topics=("${DEFAULT_TOPICS[@]}")
    fi

    for topic in "${topics[@]}"; do
      echo "Deleting $topic"
        kafka-topics.sh --delete --topic "$topic" --bootstrap-server "$KAFKA_BROKER"
        if [ $? -eq 0 ]; then
            echo "Topic '$topic' deleted successfully"
        else
            echo "Failed to delete topic '$topic'"
        fi
    done
}

list_topic(){
    kafka-topics.sh --list --bootstrap-server "$KAFKA_BROKER"
}

# Main script
while getopts ":cdl" opt; do
    case $opt in
        c)  # Create topics
            shift $(($OPTIND - 1))
            create_topics "$@"
            exit;;
        d)  # Delete topics
            shift $(($OPTIND - 1))
            delete_topics "$@"
            exit;;
        l) # List topics
           list_topic
           exit;;
        \?)
            echo "Invalid option: -$OPTARG" >&2
            exit 1;;
    esac
done

echo "Usage: $0 [-c topics] [-d topics]"
echo "Options:"
echo "  -c    Create Kafka topics"
echo "  -d    Delete Kafka topics"
echo "Example usage:"
echo "  $0 -c topic1 topic2"
echo "  $0 -d topic1 topic2"
echo "  $0 -l"