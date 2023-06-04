package kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Logger;

public class ProducerWithCallbackDemo {
    private static final Logger log = Logger.getLogger(ProducerWithCallbackDemo.class.getSimpleName());
    public static void main(String[] args) throws InterruptedException {
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

        for (int j=0; j<10; j++) {
            for(int i=0; i<30; i++) {
                // create a Producer Record -> 카프카로 보낼 레코드
                ProducerRecord<String,String> producerRecord = new ProducerRecord<>("demo_java", "hello kafka!" + i);
                //send data
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        // executes every time a record successfully sent or on exception is thrown
                        if (e == null) {
                            log.info("received new metadata");
                            log.info("Topic : " + metadata.topic());
                            log.info("Partition : " + metadata.partition());
                            log.info("Timestamp : " + metadata.timestamp());
                            log.info("Offset : " + metadata.offset());

                        } else {
                            log.info("error caused");
                        }
                    }
                });
            }
            Thread.sleep(500);
        }


        // tell the producer to send all data and block until done - synchronous
        producer.flush();

        // flush and close the producer
        producer.close();

    }
}
