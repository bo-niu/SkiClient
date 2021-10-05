package part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Summary {
    private final List<Record> records;
    private final AtomicInteger successfulRequestCount;
    private final AtomicInteger unsuccessfulRequestCount;
    private long wallTime;

    public Summary(AtomicInteger successfulRequestCount, AtomicInteger unsuccessfulRequestCount, List<Record> records) {
        this.successfulRequestCount = successfulRequestCount;
        this.unsuccessfulRequestCount = unsuccessfulRequestCount;
        this.records = records;
    }

    public AtomicInteger getSuccessfulRequestCount() {
        return successfulRequestCount;
    }

    public AtomicInteger getUnsuccessfulRequestCount() {
        return unsuccessfulRequestCount;
    }

    public synchronized void pushOneRecord(Record record) {
        records.add(record);
    }

    public synchronized Record getRecord(int i) {
        return records.get(i);
    }

    public synchronized double getMeanResponseTime() { //ms
        List<Long> times = getResponseTimeList();
        assert(times.size() > 0);
        Long mean = Long.valueOf(0);
        for (Long l: times) {
            mean += l;
        }
        return TimeUnit.MILLISECONDS.toMillis(mean / times.size());
    }

    public synchronized double getMedianResponseTime() { //ms
        List<Long> times = getResponseTimeList();
        assert(times.size() > 0);
        Collections.sort(times);
        return TimeUnit.MILLISECONDS.toMillis(times.get(times.size() / 2));
    }

    public synchronized double getThroughput() { // per second
        List<Long> times = getResponseTimeList();
        assert(times.size() > 0);
        return (double)times.size() / TimeUnit.MILLISECONDS.toMillis(wallTime) * 1000;
    }

    public synchronized double getP99ResponseTime() {
        List<Long> times = getResponseTimeList();
        assert(times.size() > 0);
        Collections.sort(times);
        return times.get((int) (times.size() * 0.99));
    }

    public synchronized double getMaxResponseTime() {
        List<Long> times = getResponseTimeList();
        assert(times.size() > 0);
        Collections.sort(times);
        return times.get(times.size()-1);
    }

    public synchronized void toCSV(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        fw.write("start time, request type, latency, response code\n");
        for (Record r: records) {
            fw.write("" + r.getStartTime() + ","
                    + r.getRequestType() + ","
            + r.getLatency() + ","
            + r.getResponseCode() + "\n");
        }
        fw.close();
    }

    private List<Long> getResponseTimeList() {
        List<Long> res = new ArrayList<>();
        for (Record r: records) {
            res.add(r.getLatency());
        }
        return res;
    }

    public long getWallTime() {
        return wallTime;
    }

    public void setWallTime(long wallTime) {
        this.wallTime = wallTime;
    }
}
