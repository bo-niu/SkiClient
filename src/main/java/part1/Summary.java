package part1;

import java.util.concurrent.atomic.AtomicInteger;

public class Summary {
    private AtomicInteger successfulRequestCount;
    private AtomicInteger unsuccessfulRequestCount;

    public Summary(AtomicInteger successfulRequestCount, AtomicInteger unsuccessfulRequestCount) {
        this.successfulRequestCount = successfulRequestCount;
        this.unsuccessfulRequestCount = unsuccessfulRequestCount;
    }

    public AtomicInteger getSuccessfulRequestCount() {
        return successfulRequestCount;
    }

    public AtomicInteger getUnsuccessfulRequestCount() {
        return unsuccessfulRequestCount;
    }
}
