package ru.nikstep.kafka.connect.random

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance
import org.apache.kafka.common.config.ConfigDef.NonEmptyString
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector

class RandomSourceConnector : SourceConnector() {

    private lateinit var props: Map<String, String>

    override fun version(): String = "1"

    override fun start(props: Map<String, String>) {
        this.props = props
    }

    override fun taskClass(): Class<out Task?> =
        RandomSourceTask::class.java

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> =
        listOf(props)

    override fun stop() {}

    override fun config(): ConfigDef =
        CONFIG_DEF

    companion object {
        val CONFIG_DEF: ConfigDef =
            ConfigDef()
                .define(
                    "prefix",
                    ConfigDef.Type.STRING,
                    "value-",
                    Importance.HIGH,
                    "Prefix for all values"
                )
                .define(
                    "topic",
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    NonEmptyString(),
                    Importance.HIGH,
                    "The topic to publish data to"
                )
    }
}