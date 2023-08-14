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

    static String stateFilePath;

    static int nodeNum;


    static CountDownLatch proposeCDL;

    static CountDownLatch writeCDL;

    static CountDownLatch acceptCDL;

    static int writeMsgCount = 0;

    static int acceptMsgCount = 0;

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
        stateFilePath = properties.getProperty("state-file");
        nodeNum = Integer.parseInt(properties.getProperty("nodeNum"));
    }

    public static void initProposeCDL(){
        proposeCDL = new CountDownLatch(nodeNum);
    }

    public static synchronized void addWriteMsgCount(){
        writeMsgCount += (nodeNum - 1);
    }

    public static synchronized void addAcceptMsgCount(){
        acceptMsgCount += (nodeNum - 1);
    }

    public static synchronized void writeToLog(String state, Integer processId){
        try {
            File outputFile = new File(stateFilePath);
            FileWriter fw = new FileWriter(outputFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(String.format("Node%d's state: %s", processId, state));
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isToDrop(ConsensusMessage msg, int receiver) {
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
                System.out.println("Drop message: " + msg + ", to=" + receiver);
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

    public static synchronized void syncWrite(String type){
//        switch (type) {
//            case "PROPOSE":
//                proposeCDL.countDown();
//                if (proposeCDL.getCount() == 0)
//                    writeCDL = new CountDownLatch(writeMsgCount);
//                break;
//            case "WRITE":
//                writeCDL.countDown();
//                if (writeCDL.getCount() == 0)
//                    acceptCDL = new CountDownLatch(acceptMsgCount);
//                break;
//            case "ACCEPT":
//                acceptCDL.countDown();
//                break;
//        }
    }

    public static void syncRead(String type) throws InterruptedException {
        switch (type) {
            case "PROPOSE":
                proposeCDL.await();
                break;
            case "WRITE":
                writeCDL.await();
                break;
            case "ACCEPT":
                acceptCDL.await();
                break;
        }
    }
}
