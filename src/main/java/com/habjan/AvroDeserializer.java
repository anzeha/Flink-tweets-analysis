package com.habjan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habjan.model.Tweet;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AvroDeserializer implements KafkaDeserializationSchema<Tweet> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaDeserializationSchema.class);
    private final String registryUrl;
    private transient KafkaAvroDeserializer inner;
    private ObjectMapper mapper;
    private transient BinaryDecoder decoder;
    private transient DatumReader<Tweet> reader;
    private final Class<Tweet> avroType;

    public AvroDeserializer(String registryUrl, Class<Tweet> avroType) {
        this.registryUrl = registryUrl;
        this.avroType = avroType;
    }

    @Override
    public boolean isEndOfStream(Tweet tweet) {
        return false;
    }

    @Override
    public Tweet deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        checkInitialized();
        decoder = DecoderFactory.get().binaryDecoder(consumerRecord.value(), decoder);
        return reader.read(null, decoder);
        //return (Tweet) inner.deserialize(consumerRecord.topic(), consumerRecord.value());
        /*if (mapper == null) {
            mapper = new ObjectMapper();
        }
        Tweet tweet = new Tweet();
        if (consumerRecord.value() != null) {
            LOG.info("\n------------------------------------------------------------------------------");
            LOG.info(new String(consumerRecord.value(), StandardCharsets.UTF_8));
            LOG.info("\n------------------------------------------------------------------------------");

            tweet = mapper.readValue(consumerRecord.value(), Tweet.class);
        }
        return tweet;*/
        /*return new Tweet(   consumerRecord.topic(),
                            consumerRecord.partition(),
                            consumerRecord.timestamp(),
                            consumerRecord.key(),
                            consumerRecord.value(),
                            consumerRecord.headers());*/
    }

    @Override
    public TypeInformation<Tweet> getProducedType() {
        return TypeExtractor.getForClass(Tweet.class);
    }

    private void checkInitialized(){
        if (inner == null) {
            Map<String, Object> props = new HashMap<>();
            props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
            SchemaRegistryClient client =
                    new CachedSchemaRegistryClient(
                            registryUrl, AbstractKafkaAvroSerDeConfig.MAX_SCHEMAS_PER_SUBJECT_DEFAULT);
            inner = new KafkaAvroDeserializer(client, props);
        }
        if(reader == null){
            reader = new SpecificDatumReader<Tweet>(avroType);
        }
    }
}
