package part2;

public class Record {
    private long startTime;
    private String requestType;
    private long latency;
    private String responseCode;

    public Record(long startTime, String requestType, long latency, String responseCode) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public long getLatency() {
        return latency;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
