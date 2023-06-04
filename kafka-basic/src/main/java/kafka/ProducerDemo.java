package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Logger;

public class ProducerDemo {
    private static final Logger log = Logger.getLogger(ProducerDemo.class.getSimpleName());
    public static void main(String[] args) {
        System.out.println("hello world!");

        // create Producer Properties
        Properties properties = new Properties();

        // connect to localhost
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName()); //producer로 문자열이 들어오면 직렬화
        properties.setProperty("value.serializer", StringSerializer.class.getName()); //producer로 문자열이 들어오면 직렬화

        // create the kafka producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);
        // create a Producer Record -> 카프카로 보낼 레코드
        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("demo_java", "hello kafka!");
        //send data
        producer.send(producerRecord);
        // tell the producer to send all data and block until done - synchronous
        producer.flush();

        // flush and close the producer
        producer.close();

    }
}
