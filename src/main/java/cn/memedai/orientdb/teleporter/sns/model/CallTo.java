package cn.memedai.orientdb.teleporter.sns.model;

/**
 * Created by kisho on 2017/4/14.
 */
public class CallTo implements Comparable {

    private String rid;
    private String reportTime;
    private String out;
    private String in;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }


    public int compareTo(Object o) {
        CallTo callTo = (CallTo) o;
        return this.getReportTime().compareTo(callTo.getReportTime());
    }

    @Override
    public String toString() {
        return "CallTo{" +
                "rid='" + rid + '\'' +
                ", reportTime='" + reportTime + '\'' +
                ", out='" + out + '\'' +
                ", in='" + in + '\'' +
                '}';
    }
}
