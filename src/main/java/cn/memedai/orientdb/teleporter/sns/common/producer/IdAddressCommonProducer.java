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
package cn.memedai.orientdb.teleporter.sns.common.producer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class IdAddressCommonProducer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdAddressCommonProducer.class);

    @Value("#{idAddressProp.idAddress}")
    private String idAddress;

    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, String>> idArressList = mapper.readValue(idAddress, new TypeReference<List<Map<String, String>>>() {
            });
            for (Map<String, String> idArress : idArressList) {
                CacheUtils.ID_ADDRESS.put(idArress.get("ID_PREFIX"), idArress);
            }
            LOGGER.debug("id_address size : " + CacheUtils.ID_ADDRESS.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
