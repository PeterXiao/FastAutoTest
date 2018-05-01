package yph.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RuntimeUtil {

    private static String filter[] = new String[]{"List of devices attached", "offline", "adb server version",
            "daemon not running", "adb server is out of date", "daemon started successfully", "not found", "Failed to"};

    public static List<String> exec(String cmd) {
        System.out.println("exec " + cmd);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> list = exec(process);
        return list;
    }

    public static List<String> exec(Process process) {
        List<String> list = new ArrayList<>();
        try {
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (filter(line)) {
                    list.add(line);
                }
            }
            process.waitFor();
            inputStream.close();
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("出错：" + e.getMessage());
        }
        return list;
    }

    private static boolean filter(String line) {
        if (line.trim().equals("")) return false;
        boolean b = true;
        for (String filt : filter) {
            if (line.contains(filt)) {
                b = false;
                break;
            }
        }
        return b;
    }

    public static void execAsync(String cmd, final AsyncInvoke syncInvoke) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
//                    System.out.println("exec " + cmd);
                    Process process = Runtime.getRuntime().exec(cmd);
                    InputStream inputStream = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (filter(line)) {
                            if (syncInvoke != null) syncInvoke.invoke(line);
                        }
                    }
                    process.waitFor();
                    inputStream.close();
                    reader.close();
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("出错：" + e.getMessage());
                }
            }
        }, 2000, 1000);
    }

    interface AsyncInvoke {
        void invoke(String line);
    }
}
