package bftsmart.util;

import bftsmart.consensus.messages.ConsensusMessage;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MessageDropper {

    static String scenarioFilePath;
    static String dropOutputFilePath;
    static String testFilePath;

    static int nodeNum;


    static CountDownLatch proposeCDL;

    static CountDownLatch writeCDL;

    static CountDownLatch acceptCDL;

    static {
        Properties properties = new Properties();
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("test.properties");
            properties.load(in);
            Objects.requireNonNull(in).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scenarioFilePath = properties.getProperty("scenario-file-path"); //read from config
        dropOutputFilePath = properties.getProperty("drop-output-file");
        testFilePath = properties.getProperty("test-file");
    }

    public static boolean isToDrop(ConsensusMessage msg, int receiver) {
        if(receiver == 1) {
            try {
                File outputFile = new File(testFilePath);
                FileWriter fw = new FileWriter(outputFile, true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("Receive message: " + msg);
                pw.flush();
                fw.flush();
                pw.close();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ArrayList<String> lines = new ArrayList<>();
        try {
            File f = new File(scenarioFilePath);
            InputStream in = Files.newInputStream(f.toPath());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while(true)
            {
                line = br.readLine();
                if(line != null) {
                    lines.add(line);
                }
                else
                    break;
            }
            br.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String current = msg.getEpoch() + " " + msg.getType() + " " + msg.getSender() + " " + receiver;
        for(String line : lines) {
            if(line.equals(current)) {
                System.out.println("Drop message: " + msg);
                try {
                    File outputFile = new File(dropOutputFilePath);
                    FileWriter fw = new FileWriter(outputFile, true);
                    PrintWriter pw = new PrintWriter(fw);
                    pw.println("Drop message: " + msg);
                    pw.flush();
                    fw.flush();
                    pw.close();
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

    public static void syncWrite(String type){
        switch (type) {
            case "PROPOSE":
                proposeCDL.countDown();
                break;
            case "ACK":
                writeCDL.countDown();
                break;
            case "COMMIT":
                acceptCDL.countDown();
                break;
        }
    }

    public static void syncRead(String type) throws InterruptedException {
        switch (type) {
            case "PROPOSE":
                proposeCDL.await();
                break;
            case "ACK":
                writeCDL.await();
                break;
            case "COMMIT":
                acceptCDL.await();
                break;
        }
    }

    public static void readNodeNumber() throws IOException {
        Properties properties = new Properties();
        InputStream in = ClassLoader.getSystemResourceAsStream("test.properties");
        properties.load(in);
        nodeNum = Integer.parseInt(properties.getProperty("nodeNum"));
        proposeCDL = new CountDownLatch(nodeNum);
        writeCDL = new CountDownLatch(nodeNum);
        acceptCDL = new CountDownLatch(nodeNum);
    }
}
