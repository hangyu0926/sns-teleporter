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
package cn.memedai.orientdb.teleporter.sns.increment.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;

import java.util.Map;

public class MemberIncrementConsumer extends BlockingQueueDataConsumer {

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String idNo = (String) dataMap.get("ID_NO");
        if (idNo != null) {
            Map<String, String> idAddress = CacheUtils.ID_ADDRESS.get(idNo.substring(0, 6));
            if (idAddress != null) {
                dataMap.putAll(idAddress);
            }
        }

        Object docObj = super.process(obj);
        String memberId = dataMap.get("MEMBER_ID").toString();

        CacheUtils.setMemberRid(memberId, getRid(docObj));
        return getFirstODocumnet(docObj);
    }

}
