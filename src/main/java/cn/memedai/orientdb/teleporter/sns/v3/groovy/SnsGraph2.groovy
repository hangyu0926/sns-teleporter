package cn.memedai.orientdb.teleporter.sns.v3.groovy

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.sql.OCommandSQL

def executeSql = {
    sql, args ->
        tx = new ODatabaseDocumentTx('remote:localhost/sns')
        tx.open('admin', 'admin')
        return tx.command(new OCommandSQL(sql)).execute(args)
}
//queryVal = '15016681031'
queryType = 'phone0'
//queryVal = '13866129993'
//queryVal = '18628110523'
//queryVal = '18628110523'

//queryVal = '1432193'
callOutCnt = 2
callInCnt = 2

//**********************************************************准备工作 Start**********************************************************
startTime = System.currentTimeMillis()
LOGGER = org.slf4j.LoggerFactory.getLogger("SnsGraph")
def myPrintln = {
    message ->
        LOGGER.info("${message}")
}
myPrintln("queryType->${queryType},queryVal->${queryVal},callOutCnt->${callOutCnt},callInCnt->${callInCnt}")
/**
 * 定义返回结果数据模型
 **/
def result = ['errors': [], 'config': [:], 'nodes': [], 'links': []]

def getRid = {
    record ->
        return record.field('@rid').getIdentity().toString()
}

try {
    /**
     * 检查入参
     **/
    if (queryVal == null || "" == queryVal.trim()) {
        result.errors.add("查询条件不能为空!")
        return result
    }

    if (queryType == 'phone0' && queryVal.trim().length() < 11) {
        result.errors.add("手机号的长度不能小于11位!")
        return result
    }

    newCallOutCnt = 0
    if (callOutCnt != null && callOutCnt.toString().trim() != '') {
        try {
            newCallOutCnt = Integer.parseInt(callOutCnt.toString().trim())
        } catch (e) {
        }
    }
    newCallInCnt = 0
    if (callInCnt != null && callInCnt.toString().trim() != '') {
        try {
            newCallInCnt = Integer.parseInt(callInCnt.toString().trim())
        } catch (e) {
        }
    }

    //默认为会员
    startSql = "select @rid as phoneRid0, unionall(in_CallTo,out_CallTo) as callTos,in('HasPhone') as members0 from (select expand(out('HasPhone')) from Member where memberId = ?)"
    if (queryType == 'phone0') {
        startSql = "select @rid as phoneRid0, unionall(in_CallTo,out_CallTo) as callTos,in('HasPhone') as members0 from Phone where phone = ?"
    }
    LOGGER.debug("startSql->{}", startSql)
    phoneInfos = executeSql(startSql, queryVal)
    if (phoneInfos == null || phoneInfos.size() == 0) {
        result.errors.add("根据手机号查询不到任何信息!")
        return result
    }
    phoneInfo = phoneInfos[0]
    memberRecords0 = phoneInfo.field('members0')
    if (memberRecords0 == null || memberRecords0.size() == 0) {
        result.errors.add("非会员手机号！")
        return result
    }

    /**
     * 定义全局变量
     **/
    phoneRecord0 = phoneInfo.field('phoneRid0')
    phoneRid0 = phoneRecord0.getIdentity().toString()
    memberRecord0 = null
    memberRid0 = null
    memberId0 = null
    ipIds = []
    deviceIds = []
    for (it in memberRecords0) {
        if (queryVal == it.field('phone')) {
            memberRecord0 = it
            memberRid0 = getRid(memberRecord0)
            memberId0 = memberRecord0.field('memberId')
            break
        }
    }

    def id2NodeMap = [:]//key为node的id, value为node的数据
    def id2LinkMap = [:]//key为link的id, value为link的数据

    result.config.category = ['当前手机号': ['icon': 'image://resources/images/phone0.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '当前会员' : ['icon': 'image://resources/images/member0.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '设备ID' : ['icon': 'image://resources/images/device.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              'IP'   : ['icon': 'image://resources/images/ip.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '一度手机号': ['icon': 'image://resources/images/phone1.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '二度手机号': ['icon': 'image://resources/images/phone2.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '会员'   : ['icon': 'image://resources/images/member.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '申请'   : ['icon': 'image://resources/images/apply.png', 'show': false, 'selected': false, 'nodeCount': 0],
                              '订单'   : ['icon': 'image://resources/images/order.png', 'show': false, 'selected': false, 'nodeCount': 0]
    ]
    categoryNodesMap = [:]
    for (it in result.config.category.keySet()) {
        categoryNodesMap[it] = []
    }
    def blackItemStyle = ['normal': ['color': "#4A4A4A"]]
    result.config.attributes = ['Phone0'       : ['modularity_class': '当前手机号', 'symbol': 'image://resources/images/phone0.png', 'symbolSize': 30, 'itemStyle': blackItemStyle],//当前查询手机号
                                'Phone1'         : ['modularity_class': '一度手机号', 'symbol': 'image://resources/images/phone1.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//一度联系人手机号
                                'Phone2'         : ['modularity_class': '二度手机号', 'symbol': 'image://resources/images/phone2.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//二度联系人手机号
                                'Member0'        : ['modularity_class': '当前会员', 'symbol': 'image://resources/images/member0.png', 'symbolSize': 30, 'itemStyle': blackItemStyle],//当前查询会员
                                'Member0_overdue': ['modularity_class': '当前会员', 'symbol': 'image://resources/images/member_overdue.png', 'symbolSize': 30, 'itemStyle': blackItemStyle],//当前查询会员
                                'Member'         : ['modularity_class': '会员', 'symbol': 'image://resources/images/member.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],
                                'Member_overdue' : ['modularity_class': '会员', 'symbol': 'image://resources/images/member_overdue.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//逾期会员
                                'Applynull'      : ['modularity_class': '申请', 'symbol': 'image://resources/images/apply.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//默认申请
                                'Apply1'         : ['modularity_class': '申请', 'symbol': 'image://resources/images/apply1.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//申请过件
                                'Apply0'         : ['modularity_class': '申请', 'symbol': 'image://resources/images/apply0.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//申请拒件
                                'Order1'         : ['modularity_class': '订单', 'symbol': 'image://resources/images/order1.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//订单过件
                                'Ordernull'      : ['modularity_class': '订单', 'symbol': 'image://resources/images/order0.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//订单拒件
                                'Order0'         : ['modularity_class': '订单', 'symbol': 'image://resources/images/order.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],//默认订单
                                'Device'         : ['modularity_class': '设备ID', 'symbol': 'image://resources/images/device.png', 'symbolSize': 15, 'itemStyle': blackItemStyle],
                                'IP'             : ['modularity_class': 'IP', 'symbol': 'image://resources/images/ip.png', 'symbolSize': 15, 'itemStyle': blackItemStyle]
    ]
    result.config.currentUser = ['phone'    : queryVal,
                                 'phoneRid' : phoneRid0,
                                 'memberRid': memberRid0,
                                 'memberId' : memberId0,
                                 'ipIds'    : ipIds,
                                 'deviceIds': deviceIds]

    limitNodeCount = 1000

    //*************************公共方法定义 Start*************************
    def checkPhone = {
        phone ->
            return phone.length() < 10 || phone.matches('^(00|10|400|800|100)\\w*|13800138000')
    }

    def getStoreInfo = {
        hasStores ->
            if (hasStores != null && hasStores.size() > 0) {
                storeRecord = hasStores.iterator().next().field('in')
                return ['storeName': storeRecord.field('storeName'), 'businessFirstType': storeRecord.field('businessFirstType')]
            } else {
                return ['storeName': '', 'businessFirstType': '']
            }
    }

    def createOrderNode = {
        orderRecord ->
            id = getRid(orderRecord)
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }

            orderNo = orderRecord.field("orderNo")
            amount = orderRecord.field("amount")
            status = orderRecord.field('status')
            //订单门店信息
            storeInfo = getStoreInfo(orderRecord.field('out_OrderHasStore'))
            nodeMap = ['id': id, 'name': orderNo + "|" + storeInfo.storeName + "|" + storeInfo.businessFirstType + "|" + amount, 'attributes': 'Order' + status]

            id2NodeMap.put(id, nodeMap)
            return nodeMap
    }

    def createApplyNode = {
        applyRecord ->
            id = getRid(applyRecord)
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }
            applyNo = applyRecord.field("applyNo")
            status = applyRecord.field('status')
            //申请门店信息
            storeInfo = getStoreInfo(applyRecord.field('out_ApplyHasStore'))
            nodeMap = ['id': id, 'name': applyNo + "|" + storeInfo.storeName + "|" + storeInfo.businessFirstType, 'attributes': 'Apply' + status]
            id2NodeMap.put(id, nodeMap)
            return nodeMap
    }

    def createMemberNode = {
        memberRecord, attributes ->
            id = getRid(memberRecord)
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }

            name = memberRecord.field('name') == null ? '' : memberRecord.field('name')
            city = memberRecord.field('city') == null ? '' : memberRecord.field('city')
            phone = memberRecord.field('phone') == null ? '' : memberRecord.field('phone')
            if (memberRecord.field('isOverdue')) {
                attributes += "_overdue"
            }

            nodeName = memberRecord.field('memberId') + "|" + name + "|" + city + "|" + phone
            nodeMap = ['id': id, 'name': nodeName, 'attributes': attributes]
            id2NodeMap.put(id, nodeMap)
    }

    def createPhoneNode = {
        phoneRecord, attributes ->
            id = getRid(phoneRecord)
            if (attributes != 'Phone1' && id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }
            nodeName = phoneRecord.field("phone")

            outHasPhoneMarks = phoneRecord.field('out_HasPhoneMark')
            phoneMarkList = []
            if (outHasPhoneMarks != null && outHasPhoneMarks.size() > 0) {
                outHasPhoneMarks.each {
                    phoneMark = it.field('in')
                    phoneMarkList.add(phoneMark.field('mark'))
                }
                nodeName += phoneMarkList.toString()
            }

            nodeMap = ['id': id, 'name': nodeName, 'attributes': attributes]
            id2NodeMap.put(id, nodeMap)
    }

    def createPhoneWithMarkNode = {
        phoneRecord, phoneMarks, attributes ->
            id = getRid(phoneRecord)
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }

            nodeMap = ['id': id, 'name': phoneRecord.field("phone") + "|" + phoneMarks.toString(), 'attributes': attributes]
            id2NodeMap.put(id, nodeMap)
    }

    def checkCallToCondition = {
        callToRecord ->
            boolean checkResult = true
            curCallOutCnt = 0
            if (callToRecord.field('callOutCnt') != null && callToRecord.field('callOutCnt').toString().trim() != '') {
                curCallOutCnt = Integer.parseInt(callToRecord.field('callOutCnt').toString().trim())
            }
            if (newCallOutCnt > 0 && newCallOutCnt > curCallOutCnt) {
                checkResult = false
            }
            curCallInCnt = 0
            if (callToRecord.field('callInCnt') != null && callToRecord.field('callInCnt').toString().trim() != '') {
                curCallInCnt = Integer.parseInt(callToRecord.field('callInCnt').toString().trim())
            }
            if (newCallInCnt > 0 && newCallInCnt > curCallInCnt) {
                checkResult = false
            }
            return checkResult
    }

    def createLink4CallTo = {
        aRid, bRid, callToRecord ->
            id = getRid(callToRecord)
            if (id2LinkMap.containsKey(id)) {
                return id2LinkMap.get(id)
            }

            linkMap = ['id': id, 'name': callToRecord.field('callInCnt') + "   " + callToRecord.field('callOutCnt'), 'source': bRid, 'target': aRid]
            id2LinkMap.put(id, linkMap)
    }

    def createPhoneMarkAndRelated = {
        phoneRecord, phoneMarks, attributes, fromPhoneRid, toPhoneRid, callToRecord ->
            if (!checkCallToCondition(callToRecord)) {
                return
            }

            callOutCnt = callToRecord.field('callOutCnt') == null ? 0 : Integer.parseInt(callToRecord.field('callOutCnt') + "");
            callInCnt = callToRecord.field('callInCnt') == null ? 0 : Integer.parseInt(callToRecord.field('callInCnt') + "");
            callLen = callToRecord.field('callLen') == null ? 0 : Integer.parseInt(callToRecord.field('callLen') + "");

            newPhoneMarks = []
            phoneMarks.each {
                it ->
                    if (callLen < 30) {
                        return
                    }
                    if ('贷款中介' == it) {
                        tempCallCnt = 0
                        if (getRid(phoneRecord) == fromPhoneRid) {
                            tempCallCnt = callOutCnt
                        } else {
                            tempCallCnt = callInCnt
                        }
                        if (tempCallCnt >= 1) {
                            newPhoneMarks.add(it)
                        }
                    } else if ('高风险号码' == it
                            || '套现中介' == it
                            || '分期竞品' == it
                            || '渠道中介' == it
                            || '商户联系人' == it
                            || '合作商户固话' == it
                            || '催收电话' == it) {
                        newPhoneMarks.add(it)
                    } else if ('非合作医美商户' == it) {
                        newPhoneMarks.add(it)
                    } else if ('信用卡' == it) {
                        tempCallCnt = 0
                        if (getRid(phoneRecord) == fromPhoneRid) {
                            tempCallCnt = callOutCnt
                        } else {
                            tempCallCnt = callInCnt
                        }
                        if (tempCallCnt >= 1) {
                            newPhoneMarks.add(it)
                        }
                    } else if ('商户法人' == it) {
                        tempCallCnt = 0
                        if (getRid(phoneRecord) == fromPhoneRid) {
                            tempCallCnt = callOutCnt
                        } else {
                            tempCallCnt = callInCnt
                        }
                        if (tempCallCnt >= 1 || callLen >= 60) {
                            newPhoneMarks.add(it)
                        }
                    } else if ('银行' == it) {
                        if (callLen >= 60) {
                            newPhoneMarks.add(it)
                        }
                    } else if ('小贷机构' == it) {
                        if (callLen >= 60) {
                            newPhoneMarks.add(it)
                        }
                    }
            }
            if (!newPhoneMarks.isEmpty()) {
                createPhoneWithMarkNode(phoneRecord, phoneMarks, attributes)

                createLink4CallTo(fromPhoneRid, toPhoneRid, callToRecord)
            }
    }


    def createIpNode = {
        ipRecord ->
            id = ipRecord.field("@rid").getIdentity().toString()
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }
            ip = ipRecord.field('ip')
            ipCity = ipRecord.field('ipCity') == null ? '' : ipRecord.field('ipCity')
            nodeName = ip + "|" + ipCity
            nodeMap = ['id': id, 'name': nodeName, 'attributes': 'IP']
            id2NodeMap.put(id, nodeMap)
    }

    def createDeviceNode = {
        deviceRecord ->
            id = getRid(deviceRecord)
            if (id2NodeMap.containsKey(id)) {
                return id2NodeMap.get(id)
            }
            nodeMap = ['id': id, 'name': deviceRecord.field('deviceId'), 'attributes': 'Device']
            id2NodeMap.put(id, nodeMap)
    }

    def createLink = {
        aRid, bRid ->
            id = aRid + "|" + bRid
            if (id2LinkMap.containsKey(id)) {
                return id2LinkMap.get(id)
            }
            linkMap = ['id': id, 'name': '', 'source': bRid, 'target': aRid]
            id2LinkMap.put(id, linkMap)
    }

    def createMemberAndRelatedNodeAndLink = {
        memberRecord, memberAttributes ->

            createMemberNode(memberRecord, memberAttributes)

            outMemberHasApplys = memberRecord.field('out_MemberHasApply')
            if (outMemberHasApplys != null && outMemberHasApplys.size() > 0) {
                outMemberHasApplys.each {
                    applyRecord = it.field('in')
                    createApplyNode(applyRecord)
                    createLink(getRid(memberRecord), getRid(applyRecord))
                }
            }

            outMemberHasOrders = memberRecord.field('out_MemberHasOrder')
            if (outMemberHasOrders != null && outMemberHasOrders.size() > 0) {
                outMemberHasOrders.each {
                    orderRecord = it.field('in')

                    createOrderNode(orderRecord)
                    createLink(getRid(memberRecord), getRid(orderRecord))
                }
            }
    }

    def createMemberAndPhoneAndRelatedNodeAndLink = {
        memberRecord, memberAttributes, phoneRecord, phoneAttributes ->

            createMemberAndRelatedNodeAndLink(memberRecord, memberAttributes)
            createPhoneNode(phoneRecord, phoneAttributes)
            createLink(getRid(memberRecord), getRid(phoneRecord))
    }

    //前提:一级联系人是我司会员
    def createCallTo2AndRelated1 = {
        phoneRecord1, callTos2 ->
            if (callTos2 != null && callTos2.size() > 0) {
                callTos2.each {
                    if (!checkCallToCondition(it)) {
                        return
                    }
                    tempPhoneRecordIn2 = it.field('in')
                    tempPhoneRecordOut2 = it.field('out')
                    //设置二度联系人的record
                    phoneRecord2 = getRid(tempPhoneRecordIn2) == getRid(phoneRecord1) ? tempPhoneRecordOut2 :
                            tempPhoneRecordIn2
                    if (checkPhone(tempPhoneRecordIn2.field('phone')) || checkPhone(tempPhoneRecordOut2.field('phone'))) {
                        return
                    }

                    inHasPhones2 = phoneRecord2.field('in_HasPhone')
                    //设置二度联系人相关node及link
                    if (inHasPhones2 != null && inHasPhones2.size() > 0) { //二级联系人手机号是我司会员手机号
                        inHasPhones2.each {
                            memberRecord2 = it.field('out')
                            if (phoneRecord2.field('phone') == memberRecord2.field('phone')) { //会员可能有多个手机号
                                createMemberAndPhoneAndRelatedNodeAndLink(memberRecord2, 'Member', phoneRecord2, 'Phone2')
                            }
                        }
                        createLink4CallTo(getRid(tempPhoneRecordOut2), getRid(tempPhoneRecordIn2), it)

                    } else { //二级联系人非我司会员
                        outHasPhoneMarks2 = phoneRecord2.field('out_HasPhoneMark')
                        if (outHasPhoneMarks2 != null && outHasPhoneMarks2.size() > 0) {
                            phoneMarkList = []
                            outHasPhoneMarks2.each {
                                phoneMark = it.field('in')
                                phoneMarkList.add(phoneMark.field('mark'))
                            }
                            createPhoneMarkAndRelated(phoneRecord2, phoneMarkList, 'Phone2', getRid(tempPhoneRecordOut2), getRid(tempPhoneRecordIn2), it)
                        }
                    }
                }
            }
    }

    //前提 ：一度联系人非公司会员
    def createCallTo2AndRelated2 = {
        phoneRecord0,
        tempPhoneRecordOut1,
        tempPhoneRecordIn1,
        callToRecord1,
        phoneRecord1,
        callTos2 ->
            hasSecondMember = false
            if (callTos2 != null && callTos2.size() > 1) {
                callTos2.each {
                    if (it == null) {
                        return
                    }
                    if (checkPhone(phoneRecord1.field('phone'))) {
                        return
                    }

                    if (!checkCallToCondition(it)) {
                        return
                    }
                    tempPhoneRecordIn2 = it.field('in')
                    tempPhoneRecordOut2 = it.field('out')

                    //设置二度联系人的record
                    phoneRecord2 = getRid(tempPhoneRecordIn2) == getRid(phoneRecord1) ? tempPhoneRecordOut2 : tempPhoneRecordIn2
                    if (getRid(phoneRecord0) == getRid(phoneRecord2)) {
                        return
                    }

                    if (checkPhone(phoneRecord2.field('phone'))) {
                        return
                    }

                    inHasPhones2 = phoneRecord2.field('in_HasPhone')

                    if (inHasPhones2 != null && inHasPhones2.size() > 0) { //二级联系人手机号是我司会员手机号
                        hasSecondMember = true

                        //设置一度联系人的相关node及link
                        createPhoneNode(phoneRecord1, 'Phone1')
                        createLink4CallTo(getRid(tempPhoneRecordOut1), getRid(tempPhoneRecordIn1), callToRecord1)

                        //设置二度联系人相关node及link
                        inHasPhones2.each {
                            memberRecord2 = it.field('out')
                            if (phoneRecord2.field('phone') == memberRecord2.field('phone')) { //会员可能有多个手机号
                                createMemberAndPhoneAndRelatedNodeAndLink(memberRecord2, 'Member', phoneRecord2, 'Phone2')
                            }
                        }
                        createLink4CallTo(getRid(tempPhoneRecordOut2), getRid(tempPhoneRecordIn2), it)
                    }
                }
            }
            return hasSecondMember
    }

    //*************************公共方法定义 End*************************

    //**********************************************************准备工作 End**********************************************************

    //**********************************************************组装数据 Start**********************************************************
    createPhoneNode(phoneRecord0, 'Phone0')

    //########################从当前会员出发遍历组装数据 Start########################
    memberRecords0.each {
        memberRid0 = it.field('@rid').getIdentity().toString()
        createMemberAndPhoneAndRelatedNodeAndLink(it, 'Member0', phoneRecord0, 'queryVal')

        //**********************************************************当前查询会员设备相关信息**********************************************************
        outMemberHasDevices = it.field('out_MemberHasDevice')
        if (outMemberHasDevices != null && outMemberHasDevices.size() > 0) {
            outMemberHasDevices.each {
                deviceRecord = it.field('in')
                deviceId = deviceRecord.field('deviceId')
                if ('00000000-0000-0000-0000-000000000000' == deviceId.trim()) { //过滤掉特殊的deviceId
                    return
                }
                deviceIds.add(deviceId)
                createDeviceNode(deviceRecord)
                inMemberHasDevices = deviceRecord.field('in_MemberHasDevice')
                if (inMemberHasDevices != null && inMemberHasDevices.size() > 0) {
                    inMemberHasDevices.each {
                        memberRecord = it.field('out')

                        createMemberNode(memberRecord, 'Member')

                        createLink(getRid(memberRecord), getRid(deviceRecord))

                        createMemberAndRelatedNodeAndLink(memberRecord, 'Member')
                    }
                }
            }
        }

        //**********************************************************当前查询会员IP相关信息**********************************************************
        outMemberHasIps = it.field('out_MemberHasIp')
        if (outMemberHasIps != null && outMemberHasIps.size() > 0) {
            outMemberHasIps.each {
                ipRecord = it.field('in')
                ipIds.add(getRid(ipRecord))
                createIpNode(ipRecord)
                inMemberHasIps = ipRecord.field('in_MemberHasIp')
                if (inMemberHasIps != null && inMemberHasIps.size() > 0) {
                    inMemberHasIps.each {
                        memberRecord = it.field('out')

                        createMemberNode(memberRecord, 'Member')

                        createLink(getRid(memberRecord), getRid(ipRecord))
                    }
                }
            }
        }
    }
    //########################从当前会员出发遍历组装数据 End########################

    //########################从当前手机的通讯记录出发遍历组装数据 Start########################
    callTos1 = phoneInfo.field('callTos')
    if (callTos1 != null && callTos1.size() > 0) {
        callTos1.each {
            if (!checkCallToCondition(it)) {
                return
            }
            callToRid = getRid(it)
            tempPhoneRecordIn1 = it.field('in')
            tempPhoneRecordOut1 = it.field('out')
            if (checkPhone(tempPhoneRecordIn1.field('phone')) || checkPhone(tempPhoneRecordOut1.field('phone'))) {
                return
            }

            //设置一级联系人的record
            phoneRecord1 = getRid(tempPhoneRecordIn1) == getRid(phoneRecord0) ? tempPhoneRecordOut1 : tempPhoneRecordIn1

            inHasPhones1 = phoneRecord1.field('in_HasPhone')

            if (inHasPhones1 != null && inHasPhones1.size() > 0) { //一级联系人手机是我司会员
                //设置一度联系人相关node及link
                inHasPhones1.each {
                    memberRecord1 = it.field('out')
                    if (phoneRecord1.field('phone') == memberRecord1.field('phone')) { //会员可能有多个手机号
                        createMemberAndPhoneAndRelatedNodeAndLink(memberRecord1, 'Member', phoneRecord1, 'Phone1')
                    }
                }
                createLink4CallTo(getRid(tempPhoneRecordOut1), getRid(tempPhoneRecordIn1), it)

                inCallTos2 = phoneRecord1.field('in_CallTo')
                createCallTo2AndRelated1(phoneRecord1, inCallTos2)

                outCallTos2 = phoneRecord1.field('out_CallTo')
                createCallTo2AndRelated1(phoneRecord1, outCallTos2)
            } else { //一级联系人手机非我司会员
                hasSecondMember = false

                inCallTos2 = phoneRecord1.field('in_CallTo')
                tempResult = createCallTo2AndRelated2(phoneRecord0, tempPhoneRecordOut1, tempPhoneRecordIn1, it, phoneRecord1, inCallTos2)
                hasSecondMember = tempResult ? true : hasSecondMember

                outCallTos2 = phoneRecord1.field('out_CallTo')
                tempResult = createCallTo2AndRelated2(phoneRecord0, tempPhoneRecordOut1, tempPhoneRecordIn1, it, phoneRecord1, outCallTos2)
                hasSecondMember = tempResult ? true : hasSecondMember

                if (!hasSecondMember) { //一级联系人没有二级联系会员
                    outHasPhoneMarks1 = phoneRecord1.field('out_HasPhoneMark')
                    if (outHasPhoneMarks1 != null && outHasPhoneMarks1.size() > 0) {
                        phoneMarkList = []
                        outHasPhoneMarks1.each {
                            phoneMark = it.field('in')
                            phoneMarkList.add(phoneMark.field('mark'))
                        }
                        createPhoneMarkAndRelated(phoneRecord1, phoneMarkList, 'Phone1', getRid(tempPhoneRecordOut1), getRid(tempPhoneRecordIn1), it)
                    }
                }
            }
        }
    }

    //########################从当前手机的通讯记录出发遍历组装数据 End########################

    newNodeId = 0
    id2NewIdMap = [:]
    for (it in id2NodeMap.values()) {
        id2NewIdMap[it.id] = '' + newNodeId++
        it.id = id2NewIdMap[it.id]
        categoryNodesMap[result.config.attributes[it.attributes].modularity_class].add(it)
    }

    tempLimitNodeCount = 0
    for (it in categoryNodesMap) {
        result.config.category[it.key].nodeCount = it.value.size()

        if (tempLimitNodeCount + it.value.size() <= limitNodeCount) {
            tempLimitNodeCount += it.value.size()

            result.config.category[it.key].selected = true
            result.config.category[it.key].show = true

            result.nodes.addAll(it.value)
        } else {
            categoryNodesMap[it.key] = null
        }
    }

    newLinkId = 0
    for (it in id2LinkMap.values()) {
        if (id2NodeMap[it.source] == null
                || id2NodeMap[it.target] == null
                || categoryNodesMap[result.config.attributes[id2NodeMap[it.source].attributes].modularity_class] == null
                || categoryNodesMap[result.config.attributes[id2NodeMap[it.target].attributes].modularity_class] == null) {
            continue
        }
        it.id = '' + newLinkId++
        it.source = id2NewIdMap[it.source]
        it.target = id2NewIdMap[it.target]
        result.links.add(it)
    }

    result.config.nodeCount = id2NodeMap.size()
    result.config.linkCount = id2LinkMap.size()
    //**********************************************************组装数据 End**********************************************************
    return result
} catch (Throwable e) {
    LOGGER.error("", e)
} finally {
    myPrintln("nodes->${result.nodes.size()},links->${result.links.size()}")
    myPrintln("Use time : ${System.currentTimeMillis() - startTime}ms")
}
