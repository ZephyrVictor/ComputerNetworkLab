package lab5;// author Zephyr369 

import org.pcap4j.core.*;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.packet.Packet;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.core.PcapStat;

public class PacketProcessor {
    private static final InetAddress LOCAL_ADDRESS;
    private static final Set<String> routingTable = new HashSet<>();

    //初始化
    static {
        try {
            LOCAL_ADDRESS = InetAddress.getByName("192.168.214.138");
            initializeRoutingTable();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize local IP address or routing table", e);
        }
    }
    //根据实验指导书初始化路由表
    private static void initializeRoutingTable() {
        // 添加路由表条目
        routingTable.add("192.168.214.2");
        routingTable.add("192.168.214.1");
        routingTable.add("192.168.214.3");
        routingTable.add("43.168.142.77");
        routingTable.add("192.168.213.2");
        routingTable.add("192.168.142.2");
        routingTable.add("192.168.142.77");
        routingTable.add("43.168.142.78");
        routingTable.add("43.168.142.65");
        routingTable.add("43.168.142.66");
        routingTable.add("97.43.142.226");
        routingTable.add("97.43.142.225");
        routingTable.add("97.43.142.229");
    }

    public void processPcapFile(String filename, int serialNumber) {
        try (PcapHandle handle = Pcaps.openOffline(filename)) {
            Packet packet;
            while ((packet = handle.getNextPacket()) != null) {
                try {
                    IpV4Packet ipv4Packet = packet.get(IpV4Packet.class);
                    if (ipv4Packet != null) {
                        // 验证逻辑
                        String validationResult = validateIpV4Packet(ipv4Packet, serialNumber);
                        System.out.println(validationResult);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing packet: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error opening pcap file: " + e.getMessage());
        }
    }
    private String validateIpV4Packet(IpV4Packet ipv4Packet, int serialNumber) throws IllegalRawDataException {
        StringBuilder result = new StringBuilder();
        result.append(String.format("序列号为%d的包：",serialNumber));
        boolean hasError = false;
        if (ipv4Packet.getHeader().getVersion().value() != 4) {
            result.append("版本号错误 ");
            hasError = true;
        }

        if (ipv4Packet.getHeader().getIhlAsInt() * 4 < 20) {
            result.append("头部长度错误 ");
            hasError = true;
        }

        if (ipv4Packet.getHeader().getTtlAsInt() == 0) {
            result.append("TTL错 ");
            hasError = true;
        }

        if (!isChecksumCorrect(ipv4Packet)) {
            result.append("校验和错 ");
            hasError = true;
        }

        String dstAddr = ipv4Packet.getHeader().getDstAddr().toString().substring(1);
        if (!dstAddr.equals(LOCAL_ADDRESS.getHostAddress()) && !routingTable.contains(dstAddr)) {
            result.append("错误目标地址 ");
            hasError = true;
        }

        if (!hasError) {
            if (routingTable.contains(dstAddr)) {
                IpV4Packet newPacket = modifyIpPacket(ipv4Packet);
                result.append(String.format("转发 新TTL=%d 新校验和=0x%X", newPacket.getHeader().getTtlAsInt(), newPacket.getHeader().getHeaderChecksum()));
                // 测试 检查校验和计算算法
                String test = String.valueOf(isChecksumCorrect(newPacket));
            } else {
                result.append("正确 接收");
            }
        } else {
            result.append(" 丢弃");
        }

        return result.toString().trim();
    }
    private IpV4Packet modifyIpPacket(IpV4Packet originalPacket) throws IllegalRawDataException {
        byte[] rawData = originalPacket.getRawData();
        int headerLength = originalPacket.getHeader().getIhlAsInt() * 4;

        // 创建新的头部数组副本
        byte[] newHeader = Arrays.copyOfRange(rawData, 0, headerLength);

        // 减少 TTL
        int newTtl = (newHeader[8] & 0xFF) - 1;
        newHeader[8] = (byte) newTtl;

        // 重置校验和字段为0
        newHeader[10] = 0;
        newHeader[11] = 0;

        // 重新计算校验和
        int checksum = 0;
        for (int i = 0; i < headerLength; i += 2) {
            // 每次处理16bits 将i处的（高位）右移八个 然后把低位清零（其实没必要）
            //然后低位的 和高位用 | 拼接

            int word = ((newHeader[i] << 8) & 0xFF00) | (newHeader[i + 1] & 0xFF);
            //加起来
            checksum += word;
            // 高位出现溢出 将高位清零，然后那个溢出的1回卷 加回去
            if ((checksum & 0xFFFF0000) != 0) {  // 处理溢出
                checksum &= 0xFFFF;
                checksum++;
            }
        }
        checksum = (~checksum) & 0xFFFF;

        // 将计算出的校验和设置回新头部
        newHeader[10] = (byte) (checksum >> 8);
        newHeader[11] = (byte) (checksum);

        // 创建新的 IP 包实例
        return IpV4Packet.newPacket(newHeader, 0, newHeader.length);
    }

    //校验和
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


}
