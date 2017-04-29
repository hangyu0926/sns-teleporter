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
package cn.memedai.orientdb.teleporter.sns.full.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp1Consumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");

        String ip = (String) dataMap.get("IP");
        String ipCity = (String) dataMap.get("IP_CITY");
        processApplyAndIp(applyNo, ip, ipCity);

        String deviceId = (String) dataMap.get("DEVICE_ID");
        processApplyAndDevice(applyNo, deviceId);
        return null;
    }

    protected String processApplyAndIp(String applyNo,
                                       String ip,
                                       String ipCity) {
        String ipRid = snsService.processApplyAndIp(getODatabaseDocumentTx(), applyNo, ip, ipCity);
        if (StringUtils.isNotBlank(ipRid)) {
            CacheUtils.setOrderNoIpRid(applyNo, ipRid);
        }
        return ipRid;
    }

    protected String processApplyAndDevice(String applyNo,
                                           String deviceId) {
        String deviceRid = snsService.processApplyAndDevice(getODatabaseDocumentTx(), applyNo, deviceId);
        if (StringUtils.isNotBlank(deviceRid)) {
            CacheUtils.setOrderNoDeviceRid(applyNo, deviceRid);
        }
        return deviceRid;
    }

}
