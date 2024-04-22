package org.example;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) {
        PacketProcessor processor = new PacketProcessor();
        for (int i = 0 ; i < 100 ; i++){
            String fileName = String.format("E:\\studying\\2\\下\\计算机网络\\计网实验\\lab4\\2022111915\\pro%d.pcap",i);
            processor.processPcapFile(fileName);

        }
    }
}