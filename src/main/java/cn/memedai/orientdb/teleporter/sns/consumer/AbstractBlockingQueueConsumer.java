package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConstantUtils;
import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.BlockingQueue;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractBlockingQueueConsumer extends AbstractConsumer {

    private static final String SQL_DEVICE = "update Device set deviceId=? upsert return after where deviceId=?";

    private static final String SQL_IP = "update Ip set ip=?,ipCity=? upsert return after where ip=?";

    private BlockingQueue<Object> blockingQueue;

    protected long useTime;

    public void run() {
        long s = System.currentTimeMillis();
        int countPerPage = ConfigUtils.getInt("countPerPage");
        log.debug("Start to process data from BlockingQueue ... ");
        ODatabaseDocumentTx tx = getTx();
        int count = 0;
        long s1 = System.currentTimeMillis();
        before(tx);
        try {
            while (true) {
                Object obj = getBlockingQueue().take();
                if (ConstantUtils.isSilverBolt(obj)) { //接收到银弹，该消费者线程结束
                    after();
                    break;
                }
                long startTime = System.currentTimeMillis();
                try {
                    process(tx, obj);
                } catch (ODatabaseException e) {
                    log.info("Tx is close. Retry to get it again.");
                    tx = getTx();
                    process(tx, obj);
                }
                useTime += (System.currentTimeMillis() - startTime);
                if (++count % countPerPage == 0) {
                    log.debug("{}@{}ms,{}@{}ms", count, (System.currentTimeMillis() - s1), countPerPage, useTime);
                    useTime = 0;
                }
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            log.debug("final : {}@{}ms,{}@{}ms", count, (System.currentTimeMillis() - s), countPerPage, useTime);
            DbUtils.close(tx);
        }
    }

    protected abstract void process(ODatabaseDocumentTx tx, Object obj);

    protected BlockingQueue<Object> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    protected void before(ODatabaseDocumentTx tx) {

    }

    protected void after() {
        try {
            //再次发送银弹，通知其它消费者结束
            getBlockingQueue().put(ConstantUtils.SILVER_BOLT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String processMemberAndPhone(ODatabaseDocumentTx tx,
                                           String memberId,
                                           String phone) {
        if (StringUtils.isBlank(memberId) || StringUtils.isBlank(phone)) {
            return null;
        }
        //Phone
        String phoneRid = getPhoneRid(tx, phone);

        //memberId->Phone
        String phoneRids = CacheUtils.getMemberPhoneRids(memberId);
        if (StringUtils.isBlank(phoneRids)) {
            CacheUtils.setMemberPhoneRids(memberId, phoneRid);
        } else {
            if (!phoneRids.contains(phoneRid)) {
                CacheUtils.setMemberPhoneRids(memberId, phoneRids + "|" + phoneRid);
            }
        }
        return phoneRid;
    }

    protected String processApplyAndIp(ODatabaseDocumentTx tx,
                                       String applyNo,
                                       String ip,
                                       String ipCity) {
        String ipRid = null;
        if (StringUtils.isNotBlank(ip)) {
            ipRid = CacheUtils.getIpRid(ip);
            if (StringUtils.isBlank(ipRid)) {
                ipRid = getRid(execute(tx, SQL_IP, ip, ipCity, ip));
                CacheUtils.setIpRid(ip, ipRid);
            }
            if (StringUtils.isNotBlank(applyNo)) {
                CacheUtils.setApplyNoIpRid(applyNo, ipRid);
            }
        }
        return ipRid;
    }

    protected String processApplyAndDevice(ODatabaseDocumentTx tx,
                                           String applyNo,
                                           String deviceId) {
        String deviceRid = null;
        if (StringUtils.isNotBlank(deviceId)) {
            deviceRid = CacheUtils.getDeviceRid(deviceId);
            if (StringUtils.isBlank(deviceRid)) {
                deviceRid = getRid(execute(tx, SQL_DEVICE, deviceId, deviceId));
                CacheUtils.setDeviceRid(deviceId, deviceRid);
            }
            if (StringUtils.isNotBlank(applyNo)) {
                CacheUtils.setApplyNoDeviceRid(applyNo, deviceRid);
            }
        }
        return deviceRid;
    }

}
