package part2;

import com.google.gson.Gson;
import util.RandomNumberGenerator;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Phase2Thread extends PhaseCommon {

    private final List<Integer> skierIDs;

    public Phase2Thread(int numLifts, String ip, String port, CountDownLatch completed, List<Integer> skierIDs, int numPostRequest, Summary summary) {
        this.completed = completed;
        this.skierIDs = skierIDs;
        this.numPostRequest = numPostRequest;
        this.summary = summary;
        this.ip = ip;
        this.port = port;

        startTime = 91;
        endTime = 360;
        startLiftID = 1;
        endLiftID = numLifts - 1;
    }

    @Override
    public void run() {
//        System.out.println("phase 2 thread starts: " + toString());
        try {
            for (int i=0; i<numPostRequest; i++) {
                String url = "http://" + ip + ":" + port + "/SkiResorts_war/skiers/2/seasons/1/days/1/skiers/"
                        + skierIDs.get(RandomNumberGenerator.getRandomNumberBetween(0, skierIDs.size()));
                if (client.postJson(url, new Gson().toJson(getRandomLiftUsage()))) {
                    successfulRequestCount += 1;
                } else {
                    unsuccessfulRequestCount += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        summary.pushOneRecord(client.getResult());
        completed.countDown();

//        System.out.println("phase 2 thread ends: " + toString());
    }
}
