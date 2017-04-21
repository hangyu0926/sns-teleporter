package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.*;
import cn.memedai.orientdb.teleporter.sns.model.DataCountLock;
import cn.memedai.orientdb.teleporter.sns.producer.*;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ExecutorUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by kisho on 2017/4/6.
 */
class Teleporter {

    private static final Logger LOG = LoggerFactory.getLogger(Teleporter.class);

    protected static final Map<String, Future> FUTURES = new HashMap<String, Future>();

    protected static final long START_TIME = System.currentTimeMillis();

    protected static void submit(Runnable runnable) {
        Future future = ExecutorUtils.getES().submit(runnable);
        FUTURES.put(runnable.getClass().getSimpleName() + "-" + future, future);
    }

    protected static void submit(Class<? extends AbstractTxConsumer> consumeCls) {
        submit(newInstance(consumeCls));
    }

    protected static void submit(Class<? extends AbstractDbProducer> producerClass,
                                 Class<? extends AbstractBlockingQueueConsumer> consumerClass) {
        int[] queueConfig = ConfigUtils.getIntArr(producerClass.getSimpleName());
        int queueCount = queueConfig[0];
        int queueLength = queueConfig[1];
        int producerCount = queueConfig[2];
        int consumerCount = queueConfig[3];
        DataCountLock dataCountLock = new DataCountLock();
        for (int i = 0; i < queueCount; i++) {
            BlockingQueue<Object> bq = new ArrayBlockingQueue<Object>(queueLength);
            for (int j = 0; j < producerCount; j++) {
                AbstractDbProducer producer = newInstance(producerClass);
                producer.setBlockingQueue(bq);
                producer.setDataCountLock(dataCountLock);
                submit(producer);
            }
            for (int j = 0; j < consumerCount; j++) {
                AbstractBlockingQueueConsumer consumer = newInstance(consumerClass);
                consumer.setBlockingQueue(bq);
                submit(consumer);
            }
        }
    }

    protected static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void blockUntilPreviousFinish() throws InterruptedException, ExecutionException {
        if (MapUtils.isNotEmpty(FUTURES)) {
            LOG.info("thread count : " + FUTURES.size());
            LOG.info("futures : " + FUTURES);
            for (Map.Entry<String, Future> entry : FUTURES.entrySet()) {
                String futureName = entry.getKey();
                Future future = entry.getValue();
                if (future.isDone()) {
                    LOG.info("finish : " + futureName);
                } else {
                    LOG.info("wait : " + futureName);
                    future.get();
                    LOG.info("finish : " + futureName);
                }
            }
        }
    }

}


