# spring-kafka-avro

sample curl to send event<br>
```shell
curl --location 'http://localhost:8181/events' \
--header 'Content-Type: application/json' \
--data '[
    {
        "timestamp": "1713868374506",
        "name": "sk-33",
        "price": "5",
        "id": "22e9e814-34f3-47ab-b87b-b4c52fdf740b"
    },
    {
        "timestamp": "1713868374508",
        "name": "sk-44",
        "price": "5",
        "id": "22e9e814-34f3-47ab-b87b-b4c52fdf740b"
    }
]'```