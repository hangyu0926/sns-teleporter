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

import cn.memedai.orientdb.teleporter.AbstractTxConsumer;
import org.springframework.stereotype.Service;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class UpdateMemberOverdueToDefaultConsumer extends AbstractTxConsumer {

    private static final String SQL_UPDATE_IS_OVERDUE = "update Member set isOverdue=false where isOverdue=true";

    @Override
    protected void process() {
        execute(SQL_UPDATE_IS_OVERDUE);
    }


}
