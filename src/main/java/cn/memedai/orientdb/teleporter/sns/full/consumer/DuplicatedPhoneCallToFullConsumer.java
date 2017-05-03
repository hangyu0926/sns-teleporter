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
import cn.memedai.orientdb.teleporter.sns.common.model.CallTo;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import java.util.*;

/**
 * Created by kisho on 2017/4/6.
 */
public class DuplicatedPhoneCallToFullConsumer extends BlockingQueueDataConsumer {

    private static final String SQL_CALLTO = "select expand($c) from (select from V limit 1) let $a = (select expand(in_CallTo) from Phone where phone = ?), $b = (select expand(out_CallTo) from Phone where phone = ?), $c = unionall($a,$b)";

    @Override
    protected Object process(Object obj) {
        Map<String, String> dataMap = (Map<String, String>) obj;
        String phone = dataMap.get("cellphone");
        OResultSet ocrs = execute(SQL_CALLTO, phone, phone);

        if (ocrs != null && !ocrs.isEmpty()) {
            Map<String, List<CallTo>> allCallTos = new HashMap<String, List<CallTo>>(ocrs.size());
            for (int i = 0; i < ocrs.size(); i++) {
                ODocument doc = (ODocument) ocrs.get(i);
                CallTo callTo = createCallTo(doc);
                String key = callTo.getOut() + callTo.getIn();
                List<CallTo> value = allCallTos.get(key);
                if (value == null) {
                    value = new ArrayList<CallTo>();
                }
                value.add(callTo);
                allCallTos.put(key, value);
            }
            for (Map.Entry<String, List<CallTo>> entry : allCallTos.entrySet()) {
                List<CallTo> callTos = entry.getValue();
                if (callTos.size() > 1) {
                    Collections.sort(callTos);
                    callTos.remove(0);
                    for (CallTo callTo : callTos) {
                        execute(" delete edge " + callTo.getRid());
                    }
                }
            }
        }
        return null;
    }

    private CallTo createCallTo(ODocument doc) {
        CallTo callTo = new CallTo();
        ORecordId oRecordId = doc.field("@rid");
        callTo.setRid(oRecordId.getIdentity().toString());
        callTo.setReportTime("" + doc.field("reportTime"));
        ODocument inDoc = doc.field("in");
        ORecordId inRid = inDoc.field("@rid");
        callTo.setIn(inRid.getIdentity().toString());
        ODocument outDoc = doc.field("out");
        ORecordId outRid = outDoc.field("@rid");
        callTo.setOut(outRid.getIdentity().toString());
        return callTo;
    }

}
