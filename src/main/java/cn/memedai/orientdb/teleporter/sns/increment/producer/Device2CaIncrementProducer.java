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
package cn.memedai.orientdb.teleporter.sns.increment.producer;

import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsJdbcDataProducer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;

/**
 * Created by kisho on 2017/4/6.
 */
public class Device2CaIncrementProducer extends SnsJdbcDataProducer {

    @Override
    protected void onStart() {
        setInSqlData(CacheUtils.CACHE_APPLYINFO_RID.keySet().toArray());
    }

}

