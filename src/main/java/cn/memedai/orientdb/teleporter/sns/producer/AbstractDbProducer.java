package cn.memedai.orientdb.teleporter.sns.producer;

import cn.memedai.orientdb.teleporter.sns.model.DataCountLock;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractDbProducer extends AbstractProducer {

    //多线程获取数据
    private DataCountLock dataCountLock = new DataCountLock();

    public void run() {
        Connection connection = getConnection();
        int tryConnectCount = 0;
        try {
            onBefore(connection);
            int page = ConfigUtils.getInt("countPerPage");
            String sql = getSql() + " limit ?,?";
            while (true) {
                int startRow = getDataCountLock().getAndIncrement() * page;
                PreparedStatement pstmt = null;
                try {
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, startRow);
                    pstmt.setInt(2, page);

                    long startTime = System.currentTimeMillis();
                    ResultSet rs = null;
                    int count = 0;
                    try {
                        rs = pstmt.executeQuery();
                        log.debug("{} limit {},{} @ {}ms", getSql(), startRow, page, (System.currentTimeMillis() - startTime));
                        while (rs.next()) {
                            count++;
                            process(rs);
                        }
                        if (count < page) {
                            break;
                        }
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                } catch (CommunicationsException e) {
                    if (++tryConnectCount >= 5) {
                        throw e;
                    }
                    //睡眠1s后再次尝试获取连接，最多尝试5次
                    Thread.sleep(1000);
                    getDataCountLock().getAndDecrement();
                    //关闭原始连接
                    DbUtils.close(connection);
                    connection = getConnection();
                    log.error("CommunicationsException : {} retry to get connection {} times", e.getMessage(), tryConnectCount);
                    continue;
                } finally {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }
            }
            onAfter(connection);
        } catch (SQLException e) {
            log.error("", e);
        } catch (InterruptedException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                //发送银弹，通知消费者结束了
                getBlockingQueue().put("");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            DbUtils.close(connection);
        }
    }

    protected Connection getConnection() {
        return DbUtils.getConnection(ConfigUtils.getProperty("sourceDbUrl"), ConfigUtils.getProperty("sourceDbUserName"), ConfigUtils.getProperty("sourceDbPassword"));
    }

    protected void process(ResultSet rs) throws SQLException, InterruptedException {
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();
        Map<String, Object> dataMap = new HashMap();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = rsm.getColumnName(i);
            dataMap.put(columnName, rs.getObject(i));
        }
        getBlockingQueue().put(dataMap);
    }

    protected abstract String getSql();

    protected DataCountLock getDataCountLock() {
        return dataCountLock;
    }

    public void setDataCountLock(DataCountLock dataCountLock) {
        this.dataCountLock = dataCountLock;
    }

    protected void onBefore(Connection connection) {

    }

    protected void onAfter(Connection connection) {

    }

}
