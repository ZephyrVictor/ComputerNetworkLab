package lab5;// author Zephyr369 

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args){
        PacketProcessor processor = new PacketProcessor();
        clearFile();
        for (int i = 0 ; i < 100 ; i++){
            String filename = String.format("D:/2022111915/pro%s.pcap",i);
            processor.processPcapFile(filename,i);
        }
        //调试
//        String filename = String.format("D:/2022111915/pro%s.pcap",40);
//        processor.processPcapFile(filename,40);
    }
    private static void clearFile(){
        try(FileWriter fw = new FileWriter("D:/2022111915/result2.txt")) {
            fw.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;

    }
}
