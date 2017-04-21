package cn.memedai.orientdb.teleporter.sns.producer;

import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractDbWithInProducer extends AbstractDbProducer {

    protected static final String IN_DATA_LIST = "#inDataList";

    private Object[] inSqlData;

    public void run() {
        Connection connection = getConnection();
        int tryConnectCount = 0;
        try {
            int countPerTime = 1000;
            while (true) {
                int time = getDataCountLock().getAndIncrement();
                int fromIndex = time * countPerTime;
                if (fromIndex > getInSqlData().length - 1) {
                    break;
                }
                int toIndex = fromIndex + countPerTime - 1;
                if (toIndex >= getInSqlData().length) {
                    toIndex = getInSqlData().length - 1;
                }
                StringBuilder inBuilder = new StringBuilder();
                for (int i = fromIndex; i <= toIndex; i++) {
                    inBuilder.append("'")
                            .append(getInSqlData()[i])
                            .append("'")
                            .append(",");
                }
                String inStr = inBuilder.toString().replaceAll(",$", "");
                String sql = getSql().replace(IN_DATA_LIST, inStr);
                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                    long startTime = System.currentTimeMillis();
                    ResultSet rs = null;
                    int count = 0;
                    try {
                        rs = stmt.executeQuery(sql);
                        log.debug("{} @ {}ms", sql, (System.currentTimeMillis() - startTime));
                        while (rs.next()) {
                            count++;
                            process(rs);
                        }
                        if (count < countPerTime) {
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
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
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

    private Object[] getInSqlData() {
        return inSqlData;
    }

    public void setInSqlData(Object[] inSqlData) {
        this.inSqlData = inSqlData;
    }

}
