package cn.memedai.orientdb.teleporter.sns.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractProducer implements Runnable {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private BlockingQueue blockingQueue;

    protected BlockingQueue getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

}
