package com.nikstep.kafka.connect.weather

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance
import org.apache.kafka.common.config.ConfigDef.NonEmptyString
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector

class CurrentWeatherSourceConnector : SourceConnector() {

    private lateinit var props: Map<String, String>

    override fun version(): String = "1"

    override fun start(props: Map<String, String>) {
        this.props = props
    }

    override fun taskClass(): Class<out Task?> =
        CurrentWeatherSourceTask::class.java

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> =
        props.getValue("locations").split(",").map { location ->
            HashMap(props).plus("location" to location)
        }.take(maxTasks)

    override fun stop() {}

    override fun config(): ConfigDef =
        CONFIG_DEF

    companion object {
        val CONFIG_DEF: ConfigDef =
            ConfigDef()
                .define(
                    "locations",
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    NonEmptyString(),
                    Importance.HIGH,
                    "CSV of locations to get current weather, i.e. London,New York"
                )
                .define(
                    "pollPeriodMinutes",
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    NonEmptyString(),
                    Importance.HIGH,
                    "Weather poll period in minutes"
                )
                .define(
                    "topic",
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    NonEmptyString(),
                    Importance.HIGH,
                    "The topic to publish data to"
                )
                .define(
                    "apiKey",
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    NonEmptyString(),
                    Importance.HIGH,
                    "Api key for https://www.visualcrossing.com/"
                )
    }
}