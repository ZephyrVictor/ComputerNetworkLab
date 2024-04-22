package lab5;// author Zephyr369 

public class Main {
    public static void main(String[] args){
        PacketProcessor processor = new PacketProcessor();
        for (int i = 0 ; i < 100 ; i++){
            String filename = String.format("D:/2022111915/pro%s.pcap",i);
            processor.processPcapFile(filename,i);
        }
        //调试
//        String filename = String.format("D:/2022111915/pro%s.pcap",40);
//        processor.processPcapFile(filename,40);
    }
}
