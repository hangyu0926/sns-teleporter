package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.DuplicatedPhoneCallTo2Consumer;
import cn.memedai.orientdb.teleporter.sns.consumer.ReexecuteErrorSqlConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.edge.*;
import cn.memedai.orientdb.teleporter.sns.consumer.increment.*;
import cn.memedai.orientdb.teleporter.sns.consumer.vertex.*;
import cn.memedai.orientdb.teleporter.sns.model.DataCountLock;
import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbProducer;
import cn.memedai.orientdb.teleporter.sns.producer.MemberProducer;
import cn.memedai.orientdb.teleporter.sns.producer.PhoneWithCallTo2Producer;
import cn.memedai.orientdb.teleporter.sns.producer.StoreInfoProducer;
import cn.memedai.orientdb.teleporter.sns.producer.increment.*;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     增量copy
 * </pre>
 */
public class Teleporter2Increment extends Teleporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Teleporter2Increment.class);

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        try {
            //crate vertex
            submitStage1();

            //IP, Device
            submitStage11();

            //create edge
            submitStage2();

            //delete 重复的CallTo
            submit(DuplicatedPhoneCallTo2Consumer.class);

            blockUntilPreviousFinish();

            //扫尾工作
            submitStage3();
        } finally {
            ExecutorUtils.getES().shutdown();
            LOGGER.info("all use time : " + (System.currentTimeMillis() - START_TIME) + "ms");
        }
    }

    private static void submitStage1() throws ExecutionException, InterruptedException {

        submit(StoreInfoProducer.class, StoreInfoConsumer.class);

        submit(MemberProducer.class, MemberIncrementConsumer.class);

        submit(ApplyInfoIncrementProducer.class, ApplyInfoConsumer.class);

        submit(OrderInfoIncrementProducer.class, OrderInfoConsumer.class);

        blockUntilPreviousFinish();

        FUTURES.clear();
        LOGGER.info("stage 1 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage11() throws ExecutionException, InterruptedException {
        Set<String> set = CacheUtils.CACHE_APPLYINFO_RID.keySet();
        if (set != null && !set.isEmpty()) {
            Device2CaIncrementProducer producer = new Device2CaIncrementProducer();
            producer.setInSqlData(set.toArray());
            submit(producer, new Device2CaConsumer());

            Ip2CaIncrementProducer producer2 = new Ip2CaIncrementProducer();
            producer2.setInSqlData(set.toArray());
            submit(producer2, new Ip2CaConsumer());
        }

        Set<String> set3 = CacheUtils.CACHE_ORDERINFO_RID.keySet();
        if (set3 != null && !set3.isEmpty()) {
            DeviceAndIp2CtaIncrementProducer producer = new DeviceAndIp2CtaIncrementProducer();
            producer.setInSqlData(set3.toArray());
            submit(producer, new DeviceAndIp2CtaConsumer());
        }

        blockUntilPreviousFinish();

        FUTURES.clear();
        LOGGER.info("stage 1 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage2() throws InterruptedException, ExecutionException {

        submit(HasApplyIncrementConsumer.class);

        submit(HasDeviceIncrementConsumer.class);

        submit(HasIpIncrementConsumer.class);

        submit(HasOrderIncrementConsumer.class);

        submit(HasPhoneIncrementConsumer.class);

        submit(HasStoreIncrementConsumer.class);

        submit(PhoneWithCallTo2Producer.class, PhoneWithCallTo2Consumer.class);

        blockUntilPreviousFinish();

        LOGGER.info("stage 2 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage3() {
        LOGGER.info("error sql : " + CacheUtils.COMMAND_SQL);
        new ReexecuteErrorSqlConsumer().run();
    }

    protected static void submit(AbstractDbProducer producer,
                                 AbstractBlockingQueueConsumer consumer) {
        int[] queueConfig = ConfigUtils.getIntArr(producer.getClass().getSimpleName());
        int queueCount = queueConfig[0];
        int queueLength = queueConfig[1];
        int producerCount = queueConfig[2];
        int consumerCount = queueConfig[3];
        DataCountLock dataCountLock = new DataCountLock();
        for (int i = 0; i < queueCount; i++) {
            BlockingQueue<Object> bq = new ArrayBlockingQueue<Object>(queueLength);
            for (int j = 0; j < producerCount; j++) {
                producer.setBlockingQueue(bq);
                producer.setDataCountLock(dataCountLock);
                submit(producer);
            }
            for (int j = 0; j < consumerCount; j++) {
                consumer.setBlockingQueue(bq);
                submit(consumer);
            }
        }
    }

}


