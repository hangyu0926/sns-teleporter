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
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallToCache02FullConsumer extends BlockingQueueDataConsumer {

    @Override
    protected Object process(Object obj) {
        Map<String, String> dataMap = (Map<String, String>) obj;
        String reportno = dataMap.get("REPORTNO");
        String applyNo = dataMap.get("APPL_NO");
        if (StringUtils.isNotBlank(reportno) && StringUtils.isNotBlank(applyNo)) {
            String phone = CacheUtils.CACHE_APPLYNO_PHONE.get(applyNo);
            if (StringUtils.isNotBlank(phone)) {
                CacheUtils.setReporternoPhone(reportno, phone);
            }
        }

        return null;
    }


}
