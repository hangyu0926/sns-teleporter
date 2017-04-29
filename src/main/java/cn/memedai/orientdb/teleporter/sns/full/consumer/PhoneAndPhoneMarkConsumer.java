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

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */

public class PhoneAndPhoneMarkConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Override
    protected Override process(Object obj) {

        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String phone = (String) dataMap.get("PHONE_NO");

        String phoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), phone);
        if (StringUtils.isBlank(phoneRid)) {
            return null;
        }

        String mark = (String) dataMap.get("PHONE_TYPE");
        //vertex : PhoneMark
        String markRid = CacheUtils.getPhoneMarkRid(mark);
        if (StringUtils.isBlank(markRid)) {
            Object docObj = super.process(obj);
            markRid = getRid(docObj);
            CacheUtils.setPhoneMarkRid(mark, markRid);
        }

        //edge : HasPhoneMark
        execute("create edge HasPhoneMark from " + phoneRid + " to " + markRid);

        return null;
    }

}
