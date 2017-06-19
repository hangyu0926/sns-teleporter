/*
 *
 *  * -------------------------------------------------------------------------------------
 *  * Mi-Me Confidential
 *  *
 *  * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 *  * All rights reserved.
 *  *
 *  * No part of this file may be reproduced or transmitted in any form or by any means,
 *  * electronic, mechanical, photocopying, recording, or otherwise, without prior
 *  * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 *  * -------------------------------------------------------------------------------------
 *
 */
package cn.memedai.orientdb.teleporter.sns.common.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class CashCaResultCommonConsumer extends BlockingQueueDataConsumer {

    @Override
    protected Object process(Object obj) {
        Map<String, String> dataMap = (Map<String, String>) obj;
        CacheUtils.CASH_LOAN_CA_RESULT.put(dataMap.get("apply_no"), dataMap.get("pass"));
        return null;
    }

}
