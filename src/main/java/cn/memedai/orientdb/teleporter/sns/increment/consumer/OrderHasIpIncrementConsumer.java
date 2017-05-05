package cn.memedai.orientdb.teleporter.sns.increment.consumer;

import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsCommonAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class OrderHasIpIncrementConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createOrderHasIp}")
    private String createOrderHasIp;

    @Override
    protected void process() {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_IPRID.entrySet()) {
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isBlank(fromRid)) {
                continue;
            }
            createEdge(createOrderHasIp, "OrderHasIp", fromRid, toRid);
        }
    }


}
