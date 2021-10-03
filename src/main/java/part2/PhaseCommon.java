package part2;

import model.LiftUsage;
import util.RandomNumberGenerator;

import java.util.concurrent.CountDownLatch;

abstract class PhaseCommon implements Runnable {
    protected int startTime;
    protected int endTime;
    protected int startLiftID;
    protected int endLiftID;
    protected CountDownLatch completed;
    protected int numPostRequest;
    protected final SkiHttpClient client = new SkiHttpClient();
    protected int successfulRequestCount;
    protected int unsuccessfulRequestCount;
    protected String ip;
    protected String port;
    protected Summary summary;

    public PhaseCommon() {
        this.successfulRequestCount = 0;
        this.unsuccessfulRequestCount = 0;
    }

    protected LiftUsage getRandomLiftUsage() {
        int liftID = RandomNumberGenerator.getRandomNumberBetween(startLiftID, endLiftID);
        int time = RandomNumberGenerator.getRandomNumberBetween(startTime, endTime);
        return new LiftUsage(time, liftID);
    }

    public int getSuccessfulRequestCount() {
        return successfulRequestCount;
    }

    public int getUnsuccessfulRequestCount() {
        return unsuccessfulRequestCount;
    }
}
