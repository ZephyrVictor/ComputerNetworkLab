package org.example;

import org.pcap4j.core.PcapNativeException;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) throws PcapNativeException {
        PacketProcessor processor = new PacketProcessor();
        for (int i = 0 ; i < 100 ; i++){
            String filename = String.format("D:/2022111915/pro%s.pcap",i);
            processor.processPcapFile(filename,i);

        }
    }
}