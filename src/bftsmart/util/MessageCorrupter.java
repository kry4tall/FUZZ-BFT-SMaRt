package bftsmart.util;

import bftsmart.consensus.messages.ConsensusMessage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

public class MessageCorrupter {

    static String corruptOutputFilePath;

    static Random random = new Random(1234567);

    static{
        Properties properties = new Properties();
        try{
            InputStream in = ClassLoader.getSystemResourceAsStream("test.properties");
            properties.load(in);
            Objects.requireNonNull(in).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        corruptOutputFilePath = properties.getProperty("corrupt-output-file");
    }

    public static int updateRandomNum()
    {
        return random.nextInt();
    }

    public static boolean isByzantineNode(int nodeId)
    {
        return nodeId == 0;
    }

    public boolean isToCorrupt(ConsensusMessage msg, int receiver){
        return true;
    }

    public static ConsensusMessage corruptMessage(ConsensusMessage msg){
        int randomNum = updateRandomNum();

        int from = msg.getSender();
        int type = msg.getType();

        int id = msg.getNumber();
        int epoch = msg.getEpoch();
        byte[] value = msg.getValue();

        switch (randomNum % 3) {
            case 0: // corrupt view #
                if(id == 0)
                    id = 1;
                else switch ((randomNum/2) % 2) {
                    case 0:
                        id = id - 1; break;
                    case 1:
                        id = id + 1;break;
                }
                break;
            case 1: // corrupt seq #
                if(epoch == 0)
                    epoch = 1;
                else switch ((randomNum/2) % 2) {
                    case 0: // corrupt seq #
                        epoch = epoch - 1; break;
                    case 1: // corrupt seq #
                        epoch = epoch + 1; break;
                }
                break;
            case 2: // do nothing
                return msg;
        }
        ConsensusMessage newCM = new ConsensusMessage(type,id,epoch, from, value);
        try {
            File outputFile = new File(corruptOutputFilePath);
            FileWriter fw = new FileWriter(outputFile, true);
            PrintWriter pw = new PrintWriter(fw);
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            pw.println("Original message: " + msg + "; After mutated: " + newCM + "; current time: " + formatter.format(date));
            System.out.println("Original message: " + msg + "; After mutated: " + newCM + "; current time: " + formatter.format(date));
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newCM;
    }
}
