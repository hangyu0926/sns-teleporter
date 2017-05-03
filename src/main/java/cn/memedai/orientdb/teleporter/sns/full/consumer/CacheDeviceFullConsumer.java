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

import cn.memedai.orientdb.teleporter.AbstractDataConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.springframework.stereotype.Service;

@Service
public class CacheDeviceFullConsumer extends AbstractDataConsumer {

    public void run() {
        long startTime = System.currentTimeMillis();
        OResultSet ocrs = execute("select from Device");
        if (ocrs != null && !ocrs.isEmpty()) {
            for (int i = 0; i < ocrs.size(); i++) {
                ODocument doc = (ODocument) ocrs.get(i);
                CacheUtils.setDeviceRid((String) doc.field("deviceId"), doc.field("@rid").toString());
            }
        }
        log.debug("cache Device use time : {}ms", (System.currentTimeMillis() - startTime));
    }

}
