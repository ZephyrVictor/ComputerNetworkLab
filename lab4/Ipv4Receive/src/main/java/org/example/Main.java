package org.example;

import org.pcap4j.core.PcapNativeException;

import java.io.FileWriter;
import java.io.IOException;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) throws PcapNativeException {
        PacketProcessor processor = new PacketProcessor();
        clearFile();
        for (int i = 0 ; i < 100 ; i++){
            String filename = String.format("D:/2022111915/pro%s.pcap",i);
            processor.processPcapFile(filename,i);

        }
//        String filename = String.format("D:/2022111915/pro0.pcap",i);
//        processor.processPcapFile(filename,i);
    }
    private static void clearFile(){
        try(FileWriter fw = new FileWriter("D:/2022111915/result1.txt")) {
            fw.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;

    }
}