package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class ConsumerDemoWithShutdown {
    private static final Logger log = Logger.getLogger(ConsumerDemoWithShutdown.class.getSimpleName());
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

        //get a reference to the main thread
        final Thread mainThread = Thread.currentThread();
        //adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeuop()");
                consumer.wakeup();

                //join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            // subscribe to a topic
            consumer.subscribe(Arrays.asList(topicId));
            //poll for data
            while(true) {
                log.info("polling");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for(ConsumerRecord<String, String> record: records) {
                    log.info("Partition : " + record.partition() + " | key : " + record.key() + " | value: " + record.value());
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer is stsarting to shut down");
        } catch (Exception e) {
            log.info("unexpected exception in the consumer" + e);
        } finally {
          consumer.close();
          log.info("the consumer is now close");
        }

    }
}
