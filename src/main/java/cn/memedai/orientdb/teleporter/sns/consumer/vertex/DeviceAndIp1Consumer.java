package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp1Consumer extends AbstractBlockingQueueConsumer {

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");

        String ip = (String) dataMap.get("IP");
        String ipCity = (String) dataMap.get("IP_CITY");
        processApplyAndIp(tx, applyNo, ip, ipCity);

        String deviceId = (String) dataMap.get("DEVICE_ID");
        processApplyAndDevice(tx, applyNo, deviceId);
    }

    @Override
    protected String processApplyAndIp(ODatabaseDocumentTx tx,
                                       String applyNo,
                                       String ip,
                                       String ipCity) {
        String ipRid = super.processApplyAndIp(tx, applyNo, ip, ipCity);
        if (StringUtils.isNotBlank(ipRid)) {
            CacheUtils.setOrderNoIpRid(applyNo, ipRid);
        }
        return ipRid;
    }

    @Override
    protected String processApplyAndDevice(ODatabaseDocumentTx tx,
                                           String applyNo,
                                           String deviceId) {
        String deviceRid = super.processApplyAndDevice(tx, applyNo, deviceId);
        if (StringUtils.isNotBlank(deviceRid)) {
            CacheUtils.setOrderNoDeviceRid(applyNo, deviceRid);
        }
        return deviceRid;
    }

}
