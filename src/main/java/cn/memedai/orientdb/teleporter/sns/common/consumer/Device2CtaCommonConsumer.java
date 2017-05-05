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
public class Device2CtaCommonConsumer extends BlockingQueueDataConsumer {

    @Value("#{snsOrientSqlProp.updateDevice}")
    private String updateDevice;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String orderNo = (String) dataMap.get("ORDER_ID");

        String deviceId = (String) dataMap.get("DEVICE_ID");
        processOrderAndDevice(orderNo, deviceId);
        return null;
    }

    protected String processOrderAndDevice(String orderNo,
                                           String deviceId) {
        String deviceRid = null;
        if (StringUtils.isNotBlank(deviceId)) {
            deviceRid = CacheUtils.getDeviceRid(deviceId);
            if (StringUtils.isBlank(deviceRid)) {
                deviceRid = getRid(execute(updateDevice, updateDevice, new Object[]{deviceId, deviceId}));
                CacheUtils.setDeviceRid(deviceId, deviceRid);
            }
            if (StringUtils.isNotBlank(orderNo)) {
                CacheUtils.setOrderNoDeviceRid(orderNo, deviceRid);
            }
        }
        return deviceRid;
    }

}
