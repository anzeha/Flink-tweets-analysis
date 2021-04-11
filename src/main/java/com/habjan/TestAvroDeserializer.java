package com.habjan;

import com.habjan.model.Tweet;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.Map;

public class TestAvroDeserializer implements KafkaDeserializationSchema<GenericRecord> {
    private final String registryUrl;
    private transient KafkaAvroDeserializer inner;

    public TestAvroDeserializer(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    @Override
    public boolean isEndOfStream(GenericRecord genericRecord) {
        return false;
    }

    @Override
    public GenericRecord deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        checkInitialized();
        return (GenericRecord) inner.deserialize(consumerRecord.topic(), consumerRecord.value());
    }

    @Override
    public TypeInformation<GenericRecord> getProducedType() {
        return TypeExtractor.getForClass(GenericRecord.class);
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
