package org.example;// author Zephyr369 

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IllegalPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;

import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketProcessor {
    private static final InetAddress LOCAL_ADDRESS;

    static {
        try {
            LOCAL_ADDRESS = InetAddress.getByName("192.168.110.138");
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to initialize local IP address", e);
        }
    }

    public void processPcapFile(String filename,int SerialNumber) {
        try (PcapHandle handle = Pcaps.openOffline(filename)) {
            Packet packet;
            while (true) {
                packet = handle.getNextPacket();
                if (packet == null) {
                    break; // No more packets, end the loop
                }

                try {
                    IpV4Packet ipv4Packet = packet.get(IpV4Packet.class);
                    if (ipv4Packet != null) {
                        String validationResult = validateIpV4Packet(ipv4Packet,SerialNumber);
                        System.out.println(validationResult);
                        appendToFile(validationResult);
                    }
                    else{
                        String validationResult = "头部长度错";
                        System.out.println("头部长度错");
                        appendToFile(validationResult);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Malformed packet error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error processing packet: " + e.getMessage());
                }
            }
        } catch (PcapNativeException e) {
            System.out.println("Error opening pcap file: " + e.getMessage());
        } catch (NotOpenException e) {
            System.out.println("IO error reading pcap file: " + e.getMessage());
        }
    }

    private String validateIpV4Packet(IpV4Packet ipv4Packet, int serialNumber) {
        System.out.println(serialNumber);

        // TTL检查
        if (ipv4Packet.getHeader().getTtlAsInt() == 0) {
            return "TTL错";
        }

        // 版本号检查
        if (ipv4Packet.getHeader().getVersion().value() != 4) {
            return "版本号错";
        }

        // IP头部长度检查
        if (ipv4Packet.getHeader().getIhlAsInt() * 4 != 20) {
            return "头部长度错";
        }

// 校验和检查
        if (!isChecksumCorrect(ipv4Packet)) {
            return "校验和错";
        }

        // 目标地址检查
        if (!ipv4Packet.getHeader().getDstAddr().equals(LOCAL_ADDRESS)) {
            return "错误目标地址";
        }

        // 如果所有检查都通过，则认为数据包是正确的
        return "正确";
    }
    private boolean isChecksumCorrect(IpV4Packet ipv4Packet){
        boolean result = false;
        byte[] rawData = ipv4Packet.getRawData();
        int headerLength = ipv4Packet.getHeader().getIhlAsInt() * 4;
        // 将校验和的部分设置为0
        byte[] header = ByteArrays.getSubArray(rawData, 0, headerLength);
        header[10] = 0;
        header[11] = 0;

        //计算校验和
        int checkSum = 0;
//        for(int i = 0; i < header.length ; i += 2){
//            checkSum += ByteArrays.getShort(header, i) &0xffff;
//        }
//        // 处理溢出
//        checkSum = (checkSum >> 16) + (checkSum & 0xffff);
//        checkSum += (checkSum >> 16);
//        checkSum = ~checkSum & 0xffff;
//
//        if(checkSum == ByteArrays.getShort(rawData, 10)){
//            result = true;
//        }
        for(int i = 0 ; i < headerLength; i+= 2){
            int word = (((header[i] << 8) & 0xFF00) | header[i+1] & 0xFF);
            checkSum += word;
            if((checkSum & 0xFFFF0000) != 0){
                checkSum &= 0xFFFF; // 高位清零
                checkSum++;
            }
        }
        checkSum = (~checkSum) & 0xFFFF;
        int originSum = ((rawData[10] & 0xFF) << 8) | (rawData[11] & 0xFF);
        if(checkSum==originSum){
            result = true;
        }


        return result;
    }

    private void appendToFile(String text){
        try(FileWriter fw = new FileWriter("D:/2022111905/result1.txt", true)){
            PrintWriter out = new PrintWriter(fw);
            out.println(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    private boolean isChecksumCorrect(IpV4Packet ipv4Packet) {
//        byte[] header = ipv4Packet.getHeader().getRawData();
//        int headerLength = ipv4Packet.getHeader().getIhlAsInt() * 4; // IHL 字段指示的头部长度
//
//        // 确保校验和字段为0
//        header[10] = 0;
//        header[11] = 0;
//
//        int sum = 0;
//        for (int i = 0; i < headerLength; i += 2) {
//            // 将两个字节组合成一个16位整数
//            int word = ((header[i] << 8) & 0xFF00) | (header[i + 1] & 0xFF);
//            sum += word;
//
//            // 检查溢出
//            if ((sum & 0xFFFF0000) != 0) {
//                sum &= 0xFFFF;
//                sum++;
//            }
//        }
//
//        // 取反操作得到校验和
//        sum = ~sum;
//        sum = sum & 0xFFFF;
//
//        // 检查计算出的校验和是否为0
//        return sum == 0;
//    }
}
