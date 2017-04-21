package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.CacheDeviceConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.CacheIpConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.DeleteEdgeConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.ReexecuteErrorSqlConsumer;
import cn.memedai.orientdb.teleporter.sns.consumer.edge.*;
import cn.memedai.orientdb.teleporter.sns.consumer.vertex.*;
import cn.memedai.orientdb.teleporter.sns.producer.*;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     Phone & PhoneMark & HasPhoneMark
 * </pre>
 */
public class Teleporter3FullHasPhoneMark extends Teleporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Teleporter3FullHasPhoneMark.class);

    private static final String[] DELETE_EDGES = new String[]{
            "HasPhoneMark"
    };

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        try {
            //delete edge
            submitStage00();

            //cache
//            submitStage01();

            //create vertex
            submitStage1();

            //create edge
//            submitStage2();

            //扫尾工作
            submitStage3();
        } finally {
            ExecutorUtils.getES().shutdown();
            LOGGER.info("all use time : " + (System.currentTimeMillis() - START_TIME) + "ms");
        }
    }

    private static void submitStage00() throws ExecutionException, InterruptedException {
        DeleteEdgeConsumer consumer = new DeleteEdgeConsumer();
        consumer.setEdges(DELETE_EDGES);
        submit(consumer);

        blockUntilPreviousFinish();

        FUTURES.clear();
        LOGGER.info("stage 00 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage01() throws ExecutionException, InterruptedException {

        submit(new CacheDeviceConsumer());
        submit(new CacheIpConsumer());

        blockUntilPreviousFinish();

        FUTURES.clear();
        LOGGER.info("stage 01 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage1() throws ExecutionException, InterruptedException {

//        submit(StoreInfoProducer.class, StoreInfoConsumer.class);
//
//        submit(MemberProducer.class, MemberIncrementConsumer.class);
//
//        submit(ApplyInfoIncrementProducer.class, ApplyInfoIncrementConsumer.class);
//
//        submit(OrderInfoIncrementProducer.class, OrderInfoConsumer.class);

        submit(PhoneAndPhoneMarkProducer.class, PhoneAndPhoneMarkConsumer.class);
//
//        submit(Device2CaIncrementProducer.class, Device2CaConsumer.class);
//        submit(DeviceAndIp1Producer.class, DeviceAndIp1Consumer.class);
//        submit(DeviceAndIp2CtaIncrementProducer.class, DeviceAndIp2CtaConsumer.class);
//        submit(Ip2CaProducer.class, Ip2CaConsumer.class);

        blockUntilPreviousFinish();

        FUTURES.clear();
        LOGGER.info("stage 1 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage2() throws InterruptedException, ExecutionException {

        submit(HasStoreConsumer.class);

        submit(HasPhoneConsumer.class);

        submit(HasOrderConsumer.class);

        submit(HasIpConsumer.class);

        submit(HasDeviceConsumer.class);

        submit(HasApplyConsumer.class);

        submit(PhoneWithCallTo2Producer.class, PhoneWithCallTo2Consumer.class);

        blockUntilPreviousFinish();

        LOGGER.info("stage 2 use time : {}ms", (System.currentTimeMillis() - START_TIME));
    }

    private static void submitStage3() {
        LOGGER.info("error sql : " + CacheUtils.COMMAND_SQL);
        new ReexecuteErrorSqlConsumer().run();
    }

}


