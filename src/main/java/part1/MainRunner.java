package part1;

import org.apache.commons.cli.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainRunner {

    public static void main(String[] args) throws InterruptedException {


        Options options = new Options();

        Option tt = new Option("t", "numThreads", true, "maximum number of threads to run (numThreads - max 256)");
        tt.setRequired(true);
        options.addOption(tt);

        Option s = new Option("s", "numSkiers", true, "number of skier to generate lift rides for (numSkiers - max 100000), This is effectively the skier’s ID (skierID)");
        s.setRequired(true);
        options.addOption(s);

        Option l = new Option("l", "numLifts", true, "number of ski lifts (numLifts - range 5-60, default 40)");
        l.setRequired(true);
        options.addOption(l);

        Option r = new Option("r", "numRuns", true, "mean numbers of ski lifts each skier rides each day (numRuns - default 10, max 20)");
        r.setRequired(true);
        options.addOption(r);

        Option ii = new Option("i", "ip", true, "IP address of the server");
        ii.setRequired(true);
        options.addOption(ii);

        Option p = new Option("p", "port", true, "port of the server");
        p.setRequired(true);
        options.addOption(p);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;


        int numThreads = 128;
        int numSkiers = 20000;
        int numLifts = 40;
        int numRuns = 10;
        String ip = "localhost";
        String port = "8080";

        try {
            cmd = parser.parse(options, args);
            numThreads = Integer.parseInt(cmd.getOptionValue("numThreads"));
            numSkiers = Integer.parseInt(cmd.getOptionValue("numSkiers"));
            numLifts = Integer.parseInt(cmd.getOptionValue("numLifts"));
            numRuns = Integer.parseInt(cmd.getOptionValue("numRuns"));
            ip = cmd.getOptionValue("ip");
            port = cmd.getOptionValue("port");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        System.out.println("numThreads: "+ numThreads);
        System.out.println("numSkiers: "+ numSkiers);
        System.out.println("numLifts: "+ numLifts);
        System.out.println("numRuns: "+ numRuns);
        System.out.println("ip: "+ ip);
        System.out.println("port: "+ port);


        int phase1TotalThreads = numThreads / 4;
        int phase2TotalThreads = numThreads;
        int phase3TotalThreads = numThreads / 4;
        Thread[] phase1Threads = new Thread[phase1TotalThreads];
        Thread[] phase2Threads = new Thread[phase2TotalThreads];
        Thread[] phase3Threads = new Thread[phase3TotalThreads];
        Summary summary = new Summary(new AtomicInteger(0), new AtomicInteger(0));
        Timestamp start = new Timestamp(System.currentTimeMillis());

        //create threads for phase1.
        CountDownLatch phase1Complete10Percent = new CountDownLatch(phase1TotalThreads / 10 + 1); // round up
        int numSkiersEachThread = numSkiers / phase1TotalThreads;
        for (int i=0; i<phase1TotalThreads; i++) {
            Phase1Thread phase1Thread = new Phase1Thread(numLifts,
                    i * numSkiersEachThread,
                    (i + 1) * numSkiersEachThread,
                    ip,
                    port,
                    (int) (numRuns * 0.2 * numSkiersEachThread),
                    phase1Complete10Percent,
                    summary);
            Thread t = new Thread(phase1Thread);
            t.start();
            phase1Threads[i] = t;
        }
        phase1Complete10Percent.await();

        //create threads for phase2.
        CountDownLatch phase2Complete10Percent = new CountDownLatch(phase2TotalThreads / 10 + 1); // round up
        ArrayList<Integer> skierIDList = new ArrayList<>();
        for (int i=0; i<numSkiers; i++) {
            skierIDList.add(i);
        }
        Collections.shuffle(skierIDList);
        for (int i=0; i<phase2TotalThreads; i++) {
            Phase2Thread phase2Thread = new Phase2Thread(numLifts,
                    ip,
                    port,
                    phase2Complete10Percent,
                    skierIDList.subList(i, i + numSkiers / numThreads),
                    (int) (numRuns * 0.6 * numSkiers / numThreads),
                    summary);
            Thread t = new Thread(phase2Thread);
            t.start();
            phase2Threads[i] = t;
        }



        phase2Complete10Percent.await();

        //create threads for phase3.
        for (int i=0; i<phase3TotalThreads; i++) {
            Phase3Thread phase3Thread = new Phase3Thread(numLifts,
                    i * numSkiersEachThread,
                    (i + 1) * numSkiersEachThread,
                    ip,
                    port,
                    (int) (0.1 * numRuns),
                    summary);
            Thread t = new Thread(phase3Thread);
            t.start();
            phase3Threads[i] = t;
        }


        //wait for all threads finished.
        for (int i=0; i<phase1TotalThreads; i++) {
            phase1Threads[i].join();
        }
        for (int i=0; i<phase2TotalThreads; i++) {
            phase2Threads[i].join();
        }
        for (int i=0; i<phase3TotalThreads; i++) {
            phase3Threads[i].join();
        }




        Timestamp end = new Timestamp(System.currentTimeMillis());
        long diff = end.getTime() - start.getTime();
        long ms = TimeUnit.MILLISECONDS.toMillis(diff);
        int successfulRequestCount = summary.getSuccessfulRequestCount().get();
        int unsuccessfulRequestCount = summary.getUnsuccessfulRequestCount().get();

        System.out.println("Successful request count: " + successfulRequestCount);
        System.out.println("Unsuccessful request count: " + unsuccessfulRequestCount);
        System.out.println("Total run time (wall time) is " + ms + "ms");
        System.out.println("Total throughput in requests per second: "
                + (successfulRequestCount + unsuccessfulRequestCount) / ((double)(ms) / 1000.0));
    }

}
