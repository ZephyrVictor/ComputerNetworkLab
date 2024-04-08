package org.example;// author Zephyr369 

import com.sun.tools.javac.Main;

import javax.xml.crypto.Data;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class UDPSender {
    // 规定一次传输包的最大大小
    private static final int MAX_PACKET_SIZE = 128;
    // 规定host
    public static final String HOST = "localhost";
    // 规定端口,destination是1234
    public static int PORT = 1234;

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            // 拿到地址
            InetAddress address = InetAddress.getByName(HOST);
            // 发送一段长消息
            Properties prop = new Properties();
            try (InputStream input = UDPSender.class.getClassLoader().getResourceAsStream("application.properties")) {
                prop.load(input);
            }

            String message = prop.getProperty("message");
            // 消息字节
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            int sequenceNumber = 0;
            boolean ackReceived = false;
            int tryCounter = 0;

            for (int i = 0; i < messageBytes.length; i += MAX_PACKET_SIZE) {
                ackReceived = false;
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
                // 等待ACK
                byte[] ackBytes = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);

                while (!ackReceived) {
                    try {
                        socket.setSoTimeout(1000); // 设置超时
                        socket.receive(ackPacket);
                        String ackMsg = new String(ackPacket.getData(), 0, ackPacket.getLength());

                        if (ackMsg.equals("ACK:" + sequenceNumber)) {
                            System.out.println("收到ACK " + sequenceNumber);
                            sequenceNumber++;
                            ackReceived = true;
                            tryCounter = 0;
                            break; // 跳出等待ACK的循环
                        }
                    } catch (Exception e) {
                        System.out.println("超时，重传 " + sequenceNumber);
                        socket.send(packet); // 超时后重发
                        if (++tryCounter > 3) {
                            System.out.println("三次重传依然没有ACK，程序终止");
                            return;
                        }
                    }
                }
            }
            // Send "END" as the last message
            byte[] endMessage = "END".getBytes(StandardCharsets.UTF_8);
            String endHeader = "SEQ:" + sequenceNumber + ":LEN:" + endMessage.length + ":";
            byte[] endHeaderBytes = endHeader.getBytes(StandardCharsets.UTF_8);
            byte[] endSendData = new byte[endHeaderBytes.length + endMessage.length];

            System.arraycopy(endHeaderBytes, 0, endSendData, 0, endHeaderBytes.length);
            System.arraycopy(endMessage, 0, endSendData, endHeaderBytes.length, endMessage.length);

            DatagramPacket endPacket = new DatagramPacket(endSendData, endSendData.length, address, PORT);
            socket.send(endPacket);
            System.out.println("发送 END");
            byte[] ackBytes = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
            while (ackReceived) {
                try {
                    socket.setSoTimeout(2000); // 设置超时
                    socket.receive(ackPacket);
                    String ackMsg = new String(ackPacket.getData(), 0, ackPacket.getLength());

                    if (ackMsg.equals("ACK:" + sequenceNumber)) {
                        System.out.println("收到ACK " + sequenceNumber);
                        sequenceNumber++;
                        ackReceived = true;
                        tryCounter = 0;
                        break; // 跳出等待ACK的循环
                    }
                } catch (Exception e) {
                    System.out.println("超时，重传 " + sequenceNumber);
                    socket.send(endPacket); // 超时后重发
                    if (++tryCounter > 3) {
                        System.out.println("三次重传依然没有ACK，程序终止");
                        return;
                    }
                }
            }
        }
    }
}
