import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaChangeProducer {
    public static void main(String[] args) {
        String bootstrapServer = "127.0.0.1:9092";
        String topic = "wikimedia.demo";
        // create Producer Properties
        Properties properties = new Properties();

        // connect to localhost
        properties.setProperty("bootstrap.servers", bootstrapServer);

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName()); //producer로 문자열이 들어오면 직렬화
        properties.setProperty("value.serializer", StringSerializer.class.getName()); //producer로 문자열이 들어오면 직렬화

        // set safe producer configs (kafka <=2.8)
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        // set high throughput producer configs
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024) );
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy"); //json 같은 text 데이터 인 경우 사용하면 좋음
        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<String, String>(properties);

        EventHandler eventHandler = new WikimediaChangeHandler(kafkaProducer,topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();

        //start the producer in another thread
        eventSource.start();

        //produce for 10 mins and block the program until then
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
