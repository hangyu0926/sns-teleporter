package cn.memedai.orientdb.teleporter.sns.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return callTo.getReportTime().compareTo(this.getReportTime());
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

    public static void main(String[] args) {
        List<CallTo> callTos = new ArrayList<CallTo>();
        CallTo callTo1 = new CallTo();
        callTo1.setReportTime("2017-04-21 00:00:00");
        callTos.add(callTo1);

        CallTo callTo2 = new CallTo();
        callTo2.setReportTime("2017-04-21 10:00:00");
        callTos.add(callTo2);

        Collections.sort(callTos);
        System.out.println(callTos);
    }
}
