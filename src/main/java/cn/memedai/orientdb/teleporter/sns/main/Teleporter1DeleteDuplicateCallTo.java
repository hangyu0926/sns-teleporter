package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.DuplicatedPhoneCallToConsumer;
import cn.memedai.orientdb.teleporter.sns.producer.DuplicatedPhoneCallToProducer;
import cn.memedai.orientdb.teleporter.sns.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     清理重复的edge : CallTo
 * </pre>
 */
public class Teleporter1DeleteDuplicateCallTo extends Teleporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Teleporter1DeleteDuplicateCallTo.class);

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        try {

            submit(DuplicatedPhoneCallToProducer.class, DuplicatedPhoneCallToConsumer.class);

            blockUntilPreviousFinish();

        } finally {
            ExecutorUtils.getES().shutdown();
            LOGGER.info("all use time : " + (System.currentTimeMillis() - START_TIME) + "ms");
        }
    }

}


