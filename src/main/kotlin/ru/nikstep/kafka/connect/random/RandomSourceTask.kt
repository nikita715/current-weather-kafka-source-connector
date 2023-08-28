package ru.nikstep.kafka.connect.random

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import java.time.Instant
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class RandomSourceTask : SourceTask() {
    private lateinit var prefix: String
    private lateinit var topic: String

    private var lastUpdatedAt: OffsetDateTime? = null

    override fun version(): String =
        RandomSourceConnector().version()

    override fun start(props: Map<String, String>) {
        val config = AbstractConfig(RandomSourceConnector.CONFIG_DEF, props)
        prefix = config.getString("prefix")
        topic = config.getString("topic")
        initializeLastVariables()
    }

    private fun initializeLastVariables() {
        val lastSourceOffset: Map<String?, Any?>? = context.offsetStorageReader().offset(sourcePartition())
        lastUpdatedAt = if (lastSourceOffset == null) null else {
            OffsetDateTime.parse(lastSourceOffset["updatedAt"] as String)
        }
    }

    private fun sourcePartition(): Map<String, String> =
        mapOf("source" to "random1")

    private fun sourceOffset(): Map<String, String> =
        mapOf("updatedAt" to OffsetDateTime.now().toString())

    override fun poll(): List<SourceRecord> {
        TimeUnit.SECONDS.sleep(5)
        return listOf(
            SourceRecord(
                sourcePartition(),
                sourceOffset(),
                topic,
                null,
                KEY_SCHEMA,
                buildRecordKey(Random.nextInt(100).toString()),
                VALUE_SCHEMA,
                buildRecordValue(Random.nextInt(100).toString()),
                Instant.now().epochSecond
            )
        )
    }

    override fun stop() {}

    private fun buildRecordKey(key: String): Struct =
        Struct(KEY_SCHEMA)
            .put("key", key)

    private fun buildRecordValue(value: String): Struct =
        Struct(VALUE_SCHEMA)
            .put("value", prefix + value)

    companion object {

        var KEY_SCHEMA: Schema = SchemaBuilder.struct().name("key-schema")
            .version(1)
            .field("key", Schema.STRING_SCHEMA)
            .build()

        var VALUE_SCHEMA: Schema = SchemaBuilder.struct().name("value-schema")
            .version(1)
            .field("value", Schema.STRING_SCHEMA)
            .build()
    }
}