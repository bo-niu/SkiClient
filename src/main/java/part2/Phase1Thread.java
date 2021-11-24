package part2;

import com.google.gson.Gson;
import util.RandomNumberGenerator;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Phase1Thread extends PhaseCommon {

    private final int startSkierID;
    private final int endSkierID;

    public Phase1Thread(int numLifts, int startSkierID, int endSkierID,
                        String ip, String port, int numPostRequest,
                        CountDownLatch completed, Summary summary) {
        this.startSkierID = startSkierID;
        this.endSkierID = endSkierID;
        this.numPostRequest = numPostRequest;
        this.completed = completed;
        this.summary = summary;
        this.ip = ip;
        this.port = port;

        startTime = 1;
        endTime = 90;
        startLiftID = 1;
        endLiftID = numLifts - 1;
    }

    @Override
    public void run() {
//        System.out.println("phase 1 thread starts: " + toString());
        for (int i=0; i<numPostRequest; i++) {
//            String url = "http://" + ip + ":" + port + "/SkiResorts_war_exploded/skiers/2/seasons/1/days/1/skiers/"
            String url = "http://" + ip + ":" + port + "/SkiResorts_war/skiers/2/seasons/1/days/1/skiers/"
                    + RandomNumberGenerator.getRandomNumberBetween(startSkierID, endSkierID);
            try {
                if (client.postJson(url, new Gson().toJson(getRandomLiftUsage()))) {
                    successfulRequestCount += 1;
                } else {
                    unsuccessfulRequestCount += 1;
                }
            } catch (IOException | InterruptedException e) {
                unsuccessfulRequestCount += 1;
                e.printStackTrace();
            }
            if (client.getResult() != null) {
                summary.pushOneRecord(client.getResult());
            }
        }

        summary.getSuccessfulRequestCount().addAndGet(successfulRequestCount);
        summary.getUnsuccessfulRequestCount().addAndGet(unsuccessfulRequestCount);
        completed.countDown();

//        System.out.println("phase 1 thread ends: " + toString());
    }
}
