package com.habjan;

import com.habjan.model.Tweet;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AvroDeserializer implements KafkaDeserializationSchema<Tweet> {
    private final String registryUrl;
    private transient KafkaAvroDeserializer inner;

    public AvroDeserializer(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    @Override
    public boolean isEndOfStream(Tweet tweet) {
        return false;
    }

    @Override
    public Tweet deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        checkInitialized();
        return (Tweet) inner.deserialize(consumerRecord.topic(), consumerRecord.value());
    }

    @Override
    public TypeInformation<Tweet> getProducedType() {
        return TypeExtractor.getForClass(Tweet.class);
    }

    private void checkInitialized(){
        if (inner == null) {
            Map<String, Object> props = new HashMap<>();
            props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
            SchemaRegistryClient client =
                    new CachedSchemaRegistryClient(
                            registryUrl, AbstractKafkaAvroSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT);
            inner = new KafkaAvroDeserializer(client, props);
        }
    }
}
