package cn.memedai.orientdb.teleporter.sns.producer;

import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConstantUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class FileProducer extends AbstractProducer {

    private String filePath;

    public void run() {
        BufferedReader br = null;
        FileReader fr = null;
        int recordCount = 0;
        long startTime = System.currentTimeMillis();
        String[] columnArr = null;
        String rowColumnStr = null;
        String rowStr = null;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            log.debug("start to read file {}", filePath);
            while ((rowStr = br.readLine()) != null) {
                String[] rowDataArr = getRowDataArr(rowStr);
                if (recordCount <= 1) {
                    ++recordCount;
                    continue;
                } else if (recordCount == 2) {
                    columnArr = rowDataArr;
                    rowColumnStr = rowStr;
                } else {
                    if (recordCount > ConfigUtils.getInt("skipRowCount")) {
                        Map<String, String> dataMap = new HashMap<String, String>(rowDataArr.length);
                        for (int i = 0; i < rowDataArr.length; i++) {
                            dataMap.put(columnArr[i], rowDataArr[i]);
                        }
                        getBlockingQueue().put(dataMap);
                    }
                }
                ++recordCount;
            }
        } catch (Exception e) {
            log.debug("row head : " + rowColumnStr);
            log.debug("row str  : " + rowColumnStr);
            log.error("", e);
        } finally {
            close(br);
            close(fr);
            try {
                getBlockingQueue().put(ConstantUtils.SILVER_BOLT);//发送银弹
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("finish {}@{}ms", recordCount, (System.currentTimeMillis() - startTime));
        }
    }

    private String[] getRowDataArr(String rowStr) {
        return rowStr.replaceAll("^\"", "").replaceAll("\"$", "").split("\",\"", -1);
    }

    private void close(BufferedReader br) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(FileReader fr) {
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public static void main(String[] args) {
        String rowStr = "\"2015-07-28 09:52:11.000\",\"\",\"\",\"\"";
//        String rowStr = "\"ID\",\"REPORTNO\",\"PHONE_NUM\",\"PHONE_NUM_LOC\",\"CONTACT_NAME\",\"CALL_CNT\",\"CALL_LEN\",\"CALL_OUT_CNT\",\"CALL_IN_CNT\",\"P_RELATION\",\"CONTACT_1W\",\"CONTACT_1M\",\"CONTACT_3M\",\"CONTACT_EARLY_MORNING\",\"CONTACT_MORNING\",\"CONTACT_NOON\",\"CONTACT_AFTERNOON\",\"CONTACT_NIGHT\",\"CONTACT_ALL_DAY\",\"CONTACT_WEEKDAY\",\"CONTACT_WEEKEND\",\"CONTACT_HOLIDAY\",\"REPORTTIME\",\"RISK_TYPE\",\"NEEDS_TYPE\",\"CONTACT_3M_PLUS\"";
        FileProducer producer = new FileProducer();
        System.out.println(producer.getRowDataArr(rowStr).length);
        for (String s : producer.getRowDataArr(rowStr)) {
            System.out.println(s);
        }
    }
}
