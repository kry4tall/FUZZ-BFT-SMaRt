package bftsmart.util;

import bftsmart.consensus.messages.ConsensusMessage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
        // return 2;
    }

    public static boolean isByzantineNode(int nodeId)
    {
        return nodeId == 0;
        //return false;
    }

    public static ConsensusMessage corruptMessage(ConsensusMessage msg){
        int randomNum = updateRandomNum();

        int from = msg.getSender();
        int type = msg.getType();

        int id = msg.getNumber();
        int epoch = msg.getEpoch();
        byte[] value = msg.getValue();

        switch (randomNum % 4) {
            case 0: // corrupt view #
                if(id == 0)
                    id = 1;
                else switch ((randomNum/2) % 2) {
                    case 0:
                        id = id - 1;
                    case 1:
                        id = id + 1;
                }
                break;
            case 1: // corrupt seq #
                if(epoch == 0)
                    epoch = 1;
                else switch ((randomNum/2) % 2) {
                    case 0: // corrupt seq #
                        epoch = epoch - 1;
                    case 1: // corrupt seq #
                        epoch = epoch + 1;
                }
                break;
            case 2:
                if(type == 44781)
                    type = 44782;
                else if(type == 44783)
                    type = 44782;
                else switch ((randomNum/2) % 2) {
                    case 0: // corrupt seq #
                        type = type - 1;
                    case 1: // corrupt seq #
                        type = type + 1;
                }
                break;
            case 3: // do nothing
                return msg;
        }
        ConsensusMessage newCM = new ConsensusMessage(type, id, epoch, from, value);
        try {
            File outputFile = new File(corruptOutputFilePath);
            FileWriter fw = new FileWriter(outputFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("Original message: " + msg + "; After mutated: " + newCM);
            System.out.println("Original message: " + msg + "; After mutated: " + newCM);
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
