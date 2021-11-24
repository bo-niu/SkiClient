package part2;

import com.google.gson.Gson;
import util.RandomNumberGenerator;

import java.io.IOException;

public class Phase3Thread extends PhaseCommon {

    private final int startSkierID;
    private final int endSkierID;

    public Phase3Thread(int numLifts, int startSkierID, int endSkierID, String ip, String port, int numPostRequest, Summary summary) {
        this.startSkierID = startSkierID;
        this.endSkierID = endSkierID;
        this.numPostRequest = numPostRequest;
        this.summary = summary;
        this.ip = ip;
        this.port = port;

        startTime = 361;
        endTime = 420;
        startLiftID = 1;
        endLiftID = numLifts - 1;
    }

    @Override
    public void run() {
//        System.out.println("phase 3 thread starts: " + toString());
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

//        System.out.println("phase 3 thread ends: " + toString());
    }
}
