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
import cn.memedai.orientdb.teleporter.sns.common.SnsService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class Device2CaCommonConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");

        String deviceId = (String) dataMap.get("DEVICE_ID");
        snsService.processApplyAndDevice(getODatabaseDocumentTx(), applyNo, deviceId);
        return null;
    }

}
