package com.habjan;

import com.habjan.model.Tweet;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessAllWindowFunction extends org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tweet, Object, TimeWindow> {
    @Override
    public void process(Context context, Iterable<Tweet> iterable, Collector<Object> collector) throws Exception {
        System.out.println("---------Window start------------");
        System.out.println(context.window().toString());
        iterable.forEach((tweet -> {

            System.out.println(tweet.getId());
            System.out.println(tweet.getCreatedAt());
        }));
        System.out.println("---------Window stop------------");
        String s = null;

        try {

            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec("echo hello");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println(s);
    }


}
