/*
 * -------------------------------------------------------------------------------------
 * Mi-Me Confidential
 *
 * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 * All rights reserved.
 *
 * No part of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior
 * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 * -------------------------------------------------------------------------------------
 */
package cn.memedai.orientdb.teleporter.sns.common.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class Ip2CtaCommonConsumer extends BlockingQueueDataConsumer {

    @Value("#{snsOrientSqlProp.updateIp}")
    private String updateIp;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String orderNo = (String) dataMap.get("ORDER_ID");

        String ip = (String) dataMap.get("IP");
        String ipCity = (String) dataMap.get("IP_CITY");
        processOrderAndIp(orderNo, ip, ipCity);

        return null;
    }

    private String processOrderAndIp(String orderNo,
                                     String ip,
                                     String ipCity) {
        String ipRid = null;
        if (StringUtils.isNotBlank(ip)) {
            ipRid = CacheUtils.getIpRid(ip);
            if (StringUtils.isBlank(ipRid)) {
                ipRid = getRid(execute(updateIp, updateIp, new Object[]{ip, ipCity, ip}));
                CacheUtils.setIpRid(ip, ipRid);
            }
            if (StringUtils.isNotBlank(orderNo)) {
                CacheUtils.setOrderNoIpRid(orderNo, ipRid);
            }
        }
        return ipRid;
    }

}
