Add connector to Kafka Connect:

```bash
curl --location 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data '{
"name": "weather-connector",
"config": {
"connector.class": "com.nikstep.kafka.connect.weather.CurrentWeatherSourceConnector",
"key.converter": "org.apache.kafka.connect.storage.StringConverter",
"value.converter": "org.apache.kafka.connect.storage.StringConverter",
"tasks.max": "5",
"topic": "current-weather-topic",
"locations": "Tokyo,Paris,Moscow,Washington,Jakarta",
"apiKey": "",
"pollPeriodMinutes": "1"
}
}'
```
