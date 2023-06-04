package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class ConsumerDemo {
    private static final Logger log = Logger.getLogger(ConsumerDemo.class.getSimpleName());
    public static void main(String[] args) {
        System.out.println("consumer !");
        String groupId = "java-application-consumer";
        String topicId = "demo_java";
        // create Producer Properties
        Properties properties = new Properties();

        // connect to localhost
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.deserializer", StringDeserializer.class.getName()); //producer로 문자열이 들어오면 직렬화
        properties.setProperty("value.deserializer", StringDeserializer.class.getName()); //producer로 문자열이 들어오면 직렬화
        properties.setProperty("group.id", groupId);
        //none, earliest, latest
        //none : consumer group이 없을 때는 작동하지 않는다.
        //earliest : 맨 처음부터 읽는다.
        //latest : 지금부터 오는 새로운 메세지만 읽는다
        properties.setProperty("auto.offset.reset","earliest");

        // create a cousumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);
        // subscribe to a topic
        consumer.subscribe(Arrays.asList(topicId));
        //poll for data
        while(true) {
            log.info("polling");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

            for(ConsumerRecord<String, String> record: records) {
                log.info("key : " + record.key());
                log.info("value: " + record.value());
                log.info("Partition : " + record.partition());
            }
        }
    }
}
