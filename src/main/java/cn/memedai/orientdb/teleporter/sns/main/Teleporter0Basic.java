package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.PhoneWithCallTo1Consumer;
import cn.memedai.orientdb.teleporter.sns.consumer.ReporternoAndPhoneConsumer;
import cn.memedai.orientdb.teleporter.sns.producer.FileProducer;
import cn.memedai.orientdb.teleporter.sns.producer.ReporternoAndPhoneProducer;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 * 初始化基础数据:
 * vertex : Ip,Device,Phone
 * edge   : CallTo
 * </pre>
 */
public class Teleporter0Basic extends Teleporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Teleporter0Basic.class);

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        try {
            submit(ReporternoAndPhoneProducer.class, ReporternoAndPhoneConsumer.class);
            blockUntilPreviousFinish();

            FileProducer producer = new FileProducer();
            submit(producer, PhoneWithCallTo1Consumer.class);

            blockUntilPreviousFinish();

        } finally {
            ExecutorUtils.getES().shutdown();
            LOGGER.info("all use time : " + (System.currentTimeMillis() - START_TIME) + "ms");
        }
    }

    private static void submit(FileProducer producer,
                               Class<? extends AbstractBlockingQueueConsumer> consumerClass) {
        String[] queueConfig = ConfigUtils.getStrArr(consumerClass.getSimpleName());
        String queueLength = queueConfig[0];
        String filePath = queueConfig[1];
        producer.setFilePath(filePath);

        BlockingQueue<Object> bq = new ArrayBlockingQueue<Object>(Integer.parseInt(queueLength));
        producer.setBlockingQueue(bq);
        submit(producer);

        AbstractBlockingQueueConsumer consumer = newInstance(consumerClass);
        consumer.setBlockingQueue(bq);
        submit(consumer);
    }

}


