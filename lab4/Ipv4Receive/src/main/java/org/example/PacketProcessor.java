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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketProcessor {
    private static final InetAddress LOCAL_ADDRESS;

    static {
        try {
            LOCAL_ADDRESS = InetAddress.getByName("192.168.214.1");
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

    private String validateIpV4Packet(IpV4Packet ipv4Packet,int SerialNumber) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("序号为%s的包：",SerialNumber));
        int length = result.length();

        //版本检查
        if(ipv4Packet.getHeader().getVersion().value() != 4){
            result.append("版本号错误 ");
        }

        //长度 四个byte一个单位
        if(ipv4Packet.getHeader().getTotalLengthAsInt() * 4 < 20){
            result.append("头部长度错误 ");
        }

        // TTL
        if (ipv4Packet.getHeader().getTtlAsInt() == 0) {
            result.append("TTL错 ");
        }

        // 校验和
        if (!isChecksumCorrect(ipv4Packet)) {
            result.append("校验和错 ");
        }

        // 目标地址
        if (!ipv4Packet.getHeader().getDstAddr().equals(LOCAL_ADDRESS)) {
            result.append("错误目标地址 ");
//            System.out.println("ip" + ipv4Packet.getHeader().getDstAddr());
        }

        if (result.length() == length) {
            return "正确";
        } else {
            return result.toString().trim();
        }
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
        if(checkSum == ByteArrays.getShort(rawData, 10)){
            result = true;
        }


        return result;
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
