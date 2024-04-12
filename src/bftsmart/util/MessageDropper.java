package bftsmart.util;

import bftsmart.consensus.messages.ConsensusMessage;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class MessageDropper {

    static String scenarioFilePath;
    static String dropOutputFilePath;
    static String stateFilePath;

    static String path = "Z:\\hk\\bft-testing\\FUZZ-BFT-SMaRt\\src\\resource\\";

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
    }

    public static void initFile() throws IOException {
        //propose file +4
//        initFile("propose");
//        initFile("write");
//        initFile("accept");
    }

    private static void initFile(String roundType) throws IOException {
        String fileName = path + roundType;
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
                    Thread.sleep(1000);
                } else {
                    break;
                }
            }
            randomAccessFile.seek(0);
            randomAccessFile.setLength(0);
            randomAccessFile.writeBytes(String.valueOf(0));
            System.out.println("init file : write " + 0 + " to " + roundType);
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
    }

    public static boolean arriveToRound(String type) throws IOException {
//        if (onlyRead(type) == onlyRead(type+"1")) {
//            System.out.println("type: " + type + " messages clear!");
//            return false;
//        }
//        else {
//            System.out.println("type: " + type + " messages exist!");
//            return true;
//        }
        return false;
    }

    public static int onlyRead(String roundType) throws IOException {
        String fileName = path + roundType;
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
                    Thread.sleep(1000);
                } else {
                    break;
                }
            }
            byte[] target = new byte[8];
            int len = randomAccessFile.read(target);
            result = new String(target, 0, len).trim();
            System.out.println("Read RandomAccessFile : get " + result + " from " + roundType);
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

    public static void readAndAdd(String roundType, int num) throws IOException {
        String fileName = path + roundType;
        RandomAccessFile randomAccessFile = null;
        FileChannel channel = null;
        int result;
        try {
            randomAccessFile = new RandomAccessFile(fileName, "rw");
            channel = randomAccessFile.getChannel();
            FileLock lock = null;
            while (true) {
                lock = channel.tryLock();
                if (null == lock) {
                    System.out.println("Read Process : get lock failed");
                    Thread.sleep(1000);
                } else {
                    break;
                }
            }
            byte[] target = new byte[8];
            int len = randomAccessFile.read(target);
            result = Integer.parseInt(new String(target, 0, len).trim());
            System.out.println("RandomAccessFile : get " + result + " from " + roundType);
            randomAccessFile.seek(0);
            randomAccessFile.setLength(0);
            randomAccessFile.writeBytes(String.valueOf(result + num));
            System.out.println("RandomAccessFile : write " + (result + num) + " to " + roundType);
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
    }

    public static void writeToLog(String state, Integer processId) {
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
            while (true) {
                line = br.readLine();
                if (line != null) {
                    lines.add(line);
                } else {
                    break;
                }
            }
            br.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String current = msg.getEpoch() + " " + (msg.getType() - 44781) + " " + msg.getSender() + " " + receiver;
        // String current = (msg.getType() - 44781) + " " + msg.getSender() + " " + receiver;
        for (String line : lines) {
            if (line.equals(current)) {
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
}
