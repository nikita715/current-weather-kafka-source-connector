package com.nikstep.kafka.connect.weather

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class CurrentWeatherSourceTask : SourceTask() {
    private val httpClient = OkHttpClient()
    private val objectMapper = ObjectMapper()

    private val log = LoggerFactory.getLogger(CurrentWeatherSourceTask::class.java)

    private lateinit var apiKey: String
    private lateinit var location: String
    private lateinit var topic: String
    private var pollPeriodMinutes by Delegates.notNull<Long>()

    override fun version(): String =
        CurrentWeatherSourceConnector().version()

    override fun start(props: Map<String, String>) {
        location = props.getValue("location")
        apiKey = props.getValue("apiKey")
        topic = props.getValue("topic")
        pollPeriodMinutes = props.getValue("pollPeriodMinutes").toLong()
    }

    private fun sourcePartition(): Map<String, String> =
        mapOf("location" to location)

    private fun sourceOffset(): Map<String, String> =
        mapOf("updatedAt" to OffsetDateTime.now(ZoneOffset.UTC).toString())

    override fun poll(): List<SourceRecord> {
        TimeUnit.MINUTES.sleep(pollPeriodMinutes)

        log.info("Polling weather in $location")

        val currentWeather = getCurrentTemperature(
            location = location,
            apiKey = apiKey,
        )

        return listOf(
            SourceRecord(
                sourcePartition(),
                sourceOffset(),
                topic,
                null,
                null,
                location,
                null,
                currentWeather,
                Instant.now().toEpochMilli()
            )
        )
    }

    override fun stop() {}

    private fun getCurrentTemperature(location: String, apiKey: String): String {
        val url =
            "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$location?unitGroup=metric&key=$apiKey&contentType=json&include=current".toHttpUrl()
        val responseBody = httpClient.newCall(Request.Builder().url(url).build()).execute().body
            ?: throw RuntimeException("Response body is null")
        val temperature = objectMapper.readTree(responseBody.string())
            .get("currentConditions")
            .get("temp").toString()
        responseBody.closeQuietly()
        return temperature
    }

}
