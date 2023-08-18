package bftsmart.util;

import bftsmart.consensus.messages.ConsensusMessage;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class MessageDropper {

    static String scenarioFilePath;
    static String dropOutputFilePath;
    static String stateFilePath;

    static int nodeNum;

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

    public static void addProposeMsgCount(){
        //propose file +1

    }

    public static void addWriteMsgCount(){
    }

    public static void addAcceptMsgCount(){
    }

    public static int concurrentReadFile(String fileName) throws IOException {
        RandomAccessFile randomAccessFile = null;
        FileChannel channel = null;
        String result = null;
        try {
            randomAccessFile = new RandomAccessFile(fileName, "rw");
            channel = randomAccessFile.getChannel();
            FileLock lock = null;
            while (true) {
                lock = channel.tryLock();
                if (null == lock) {
                    System.out.println("Read Process : get lock failed");
                    Thread.sleep(100);
                } else {
                    break;
                }
            }
            byte[] target = new byte[8];
            int len = randomAccessFile.read(target);
            result = new String(target,0, len).trim();
            System.out.println("Read RandomAccessFile : get " + result);
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != randomAccessFile) {
                randomAccessFile.close();
            }
            if (null != channel) {
                channel.close();
            }
        }
        return result == null ? 0 : Integer.parseInt(result);
    }

    public static void concurrentWriteFile(String fileName, String content) throws IOException {
        RandomAccessFile randomAccessFile = null;
        FileChannel channel = null;
        try {
            randomAccessFile = new RandomAccessFile(fileName, "rw");
            channel = randomAccessFile.getChannel();
            FileLock lock = null;
            while (true) {
                lock = channel.tryLock();
                if (null == lock) {
                    System.out.println("Read Process : get lock failed");
                    Thread.sleep(100);
                } else {
                    break;
                }
            }
            System.out.println("Write RandomAccessFile : get lock");
            randomAccessFile.seek(0);
            randomAccessFile.setLength(0);
            randomAccessFile.writeBytes(content);
            lock.release();
            System.out.println("Write RandomAccessFile : release lock");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != randomAccessFile) {
                randomAccessFile.close();
            }
            if (null != channel) {
                channel.close();
            }
        }
    }

    public static void writeToLog(String state, Integer processId){
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

    public static void syncWrite(String type){
        switch (type) {
            case "PROPOSE":
                //propose file count -1
                break;
            case "WRITE":
                break;
            case "ACCEPT":
                break;
        }
    }

    public static void syncRead(String type) throws InterruptedException {
        switch (type) {
            case "PROPOSE":
                //propose file count = 0
                break;
            case "WRITE":
                break;
            case "ACCEPT":
                break;
        }
    }
}
