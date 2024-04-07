package org.example;// author Zephyr369 

import com.sun.tools.javac.Main;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {
    // 规定一次传输包的最大大小
    private static final int MAX_PACKET_SIZE = 16;
    // 规定host
    private static final String HOST = "localhost";
    // 规定端口
    private static final int PORT = 1234;

    public static void main(String[] args) throws  Exception{
        try(DatagramSocket socket = new DatagramSocket()){
            // 拿到地址
            InetAddress address = InetAddress.getByName(HOST);
            // 发送一段长消息
            String message = "He said let's get out of this town.Drive out of the city away from the crowds.I.thought Heaven can't help me now.Nothing lasts forever.But this is gonna.take me down.He's so tall and handsome as hell.He's so bad but he does it so well.I can see the end as it begins.My one condition is.Say you'll remember me.Standing in a nice dress staring at the sunset babe";
            // 消息字节
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            int sequenceNumber = 0;
            boolean ackRecived = true;
            int tryCounter = 0;

            for(int i = 0 ; i < messageBytes.length ; i += MAX_PACKET_SIZE){
                // 收到了ACK
                if(ackRecived) {
                    int end = Math.min(messageBytes.length, i + MAX_PACKET_SIZE);
                    // 计算出需要复制的字节长度，确保不会超过原来数组的长度
                    int length = end - i;
                    byte[] packetData = new byte[length];
                    System.arraycopy(messageBytes, i, packetData, 0, length);

                    String header = "SEQ:" + sequenceNumber + ":LEN:" + packetData.length + ":";
                    byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
                    byte[] sendData = new byte[headerBytes.length + packetData.length];

                    System.arraycopy(headerBytes, 0, sendData, 0, headerBytes.length);
                    System.arraycopy(packetData, 0, sendData, headerBytes.length, packetData.length);

                    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, PORT);
                    socket.send(packet);
                    ackRecived = false;
                    Thread.sleep(500);
                }

                // 等ACK
                byte ackBytes[] = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);

                try{
                    socket.setSoTimeout(2000); // 设置2000ms的超时
                    socket.receive(ackPacket);
                    String ackMsg = new String(ackPacket.getData(), 0, ackPacket.getLength());
                    if(ackMsg.equals("ACK:" + sequenceNumber)){
                        System.out.println("ACK已经被收到了，序号是:" + sequenceNumber);
                        sequenceNumber++;//等下一个
                        ackRecived = true;
                        tryCounter = 0; // counter置零
                    }
                } catch(Exception e){
                    System.out.println("很遗憾，这个数据包超时了,序号:" + sequenceNumber);
                    if(++tryCounter > 3){
                        // 尝试三次，防止无限次重传，在实验中，由于是模拟丢失 因此不会发生这个情况，但是为了完整 还是添加了这个逻辑
                        System.out.println("重传了三次，都没有收到");
                        break;
                    }
                    i -= MAX_PACKET_SIZE;
                }
            }
        }
    }
}
