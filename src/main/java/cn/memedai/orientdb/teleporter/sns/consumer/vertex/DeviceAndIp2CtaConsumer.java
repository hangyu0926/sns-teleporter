package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp2CtaConsumer extends AbstractBlockingQueueConsumer {


    private static final String SQL_DEVICE = "update Device set deviceId=? upsert return after where deviceId=?";

    private static final String SQL_IP = "update Ip set ip=?,ipCity=? upsert return after where ip=?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String orderNo = (String) dataMap.get("ORDER_ID");

        String ip = (String) dataMap.get("IP");
        String ipCity = (String) dataMap.get("IP_CITY");
        processOrderAndIp(tx, orderNo, ip, ipCity);

        String deviceId = (String) dataMap.get("DEVICE_ID");
        processOrderAndDevice(tx, orderNo, deviceId);
    }

    private String processOrderAndIp(ODatabaseDocumentTx tx,
                                       String orderNo,
                                       String ip,
                                       String ipCity) {
        String ipRid = null;
        if (StringUtils.isNotBlank(ip)) {
            ipRid = CacheUtils.getIpRid(ip);
            if (StringUtils.isBlank(ipRid)) {
                ipRid = getRid(execute(tx, SQL_IP, ip, ipCity, ip));
                CacheUtils.setIpRid(ip, ipRid);
            }
            if (StringUtils.isNotBlank(orderNo)) {
                CacheUtils.setOrderNoIpRid(orderNo, ipRid);
            }
        }
        return ipRid;
    }

    protected String processOrderAndDevice(ODatabaseDocumentTx tx,
                                           String orderNo,
                                           String deviceId) {
        String deviceRid = null;
        if (StringUtils.isNotBlank(deviceId)) {
            deviceRid = CacheUtils.getDeviceRid(deviceId);
            if (StringUtils.isBlank(deviceRid)) {
                deviceRid = getRid(execute(tx, SQL_DEVICE, deviceId, deviceId));
                CacheUtils.setDeviceRid(deviceId, deviceRid);
            }
            if (StringUtils.isNotBlank(orderNo)) {
                CacheUtils.setOrderNoDeviceRid(orderNo, deviceRid);
            }
        }
        return deviceRid;
    }

}
