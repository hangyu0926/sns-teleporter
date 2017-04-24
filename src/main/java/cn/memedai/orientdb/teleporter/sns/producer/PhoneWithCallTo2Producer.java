package cn.memedai.orientdb.teleporter.sns.producer;

import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2Producer extends AbstractDbProducer {

    private static Properties lastReadRecord = new Properties();

    static {
        try {
            lastReadRecord.load(new FileInputStream(ConfigUtils.getProperty("lastReadRecord")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int lastMaxId;

    @Override
    protected void onBefore(Connection connection) {
        String lastMax = lastReadRecord.getProperty(getLastKey());
        if (StringUtils.isNotBlank(lastMax)) {
            lastMaxId = Integer.parseInt(lastMax);
        }
        log.debug("lastMaxId = " + lastMax);
    }

    @Override
    protected String getSql() {
        String sql = "select APPL_NO,PHONE_NO,CALL_CNT,CALL_LEN,CALL_IN_CNT,CALL_OUT_CNT,CREATE_TIME from network.ca_bur_operator_contact";
        if (lastMaxId > 0) {
            sql += " where ID>" + lastMaxId;
        } else {
            sql += " where CREATE_TIME>='" + ConfigUtils.getDefaultLimitCreatedDatetime() + "'";
        }
        return sql;
    }

    @Override
    protected void onAfter(Connection connection) {
        Statement stmt = null;
        ResultSet rs = null;
        int currentMaxId = 0;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select max(ID) as id from network.ca_bur_operator_contact");
            if (rs.next()) {
                currentMaxId = rs.getInt("id");
            }
            log.debug("currentMaxId : " + currentMaxId);
            String filePath = ConfigUtils.getProperty("lastReadRecord");
            try {
                //写入本次读取最大的id值
                PrintWriter writer = new PrintWriter(new File(filePath));
                writer.println(getLastKey() + "=" + lastMaxId);
                writer.print(getCurrentKey() + "=" + currentMaxId);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.close(stmt);
            DbUtils.close(rs);
        }
    }

    private static String getLastKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Calendar.getInstance().getTimeInMillis() - 3600 * 24 * 1000);
        return sdf.format(date);
    }

    private static String getCurrentKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Calendar.getInstance().getTimeInMillis() - 3600 * 24 * 1000);
        System.out.println(sdf.format(date));
    }

}
