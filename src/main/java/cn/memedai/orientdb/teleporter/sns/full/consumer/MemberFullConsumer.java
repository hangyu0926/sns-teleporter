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

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class MemberFullConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

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

        Object docObj = super.process(dataMap);
        String memberId = dataMap.get("MEMBER_ID").toString();

        String memberRid = getRid(docObj);
        if (memberRid != null) {
            CacheUtils.setMemberRid(memberId, memberRid);
        }

        String phone = (String) dataMap.get("MOBILE_NO");
        snsService.processMemberAndPhone(getODatabaseDocumentTx(), memberId, phone);
        return null;
    }

    public static void main(String[] args) {
        System.out.println("12345678".substring(0, 6));
    }

}
