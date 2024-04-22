package org.example;// author Zephyr369 

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IllegalPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;

import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketProcessor {
    private static final InetAddress LOCAL_ADDRESS;

    static {
        try {
            LOCAL_ADDRESS = InetAddress.getByName("192.168.214.138");
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to initialize local IP address", e);
        }
    }

    public void processPcapFile(String fileName) throws PcapNativeException {
        try(PcapHandle handle = Pcaps.openOffline(fileName)){
            while(1){
                try{
                    Packet packet = handle.getNextPacket();
                    IpV4Packet ipv4Packet = packet.get(IpV4Packet.class);//拿到ipv4分组
                    if(ipv4Packet != null){
                        String validationResult = validateIpV4Packet(ipv4Packet);
                        System.out.println(validationResult);
                    }
                } catch(Exception e){
                    System.out.println("Error:" + e.getMessage());
                }
            }
        } catch(Exception e){
            System.out.println("Error" + e.getMessage());
        }

    }

    private String validateIpV4Packet(IpV4Packet ipv4Packet) {
        StringBuilder result = new StringBuilder();

        //版本检查
        if(ipv4Packet.getHeader().getVersion().value() != 4){
            result.append("版本号错误");
        }

        //长度 四个byte一个单位
        if(ipv4Packet.getHeader().getTotalLengthAsInt() * 4 < 20){
            result.append("头部长度错误");
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
        }

        if (result.length() == 0) {
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
        for(int i = 0; i < header.length ; i += 2){
            checkSum += ByteArrays.getShort(header, i) &0xffff;
        }

        checkSum = (checkSum >> 16) + (checkSum & 0xffff);
        checkSum += (checkSum >> 16);
        checkSum = ~checkSum & 0xffff;

        if(checkSum == ByteArrays.getShort(rawData, 10)){
            result = true;
        }

        return result;
    }

}
