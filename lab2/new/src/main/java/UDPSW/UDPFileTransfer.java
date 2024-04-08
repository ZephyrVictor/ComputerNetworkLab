package UDPSW;// author Zephyr369

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UDPFileTransfer {
//    private static final int PORT = 1234;
    private static final String HOST = "localhost";
    // 一个包最大的
    private static final int MAX_PACKET_SIZE = 128;
//    private static int packetCount = 0; // 用于接收方模拟丢包

    public static void main(String[] args) {
        // 接收命令行参数：操作模式（send/receive），消息内容（若为发送模式），端口
        if (args.length > 1) {
            int port = Integer.parseInt(args[1]); // 发送或接收的端口
            String operationMode = args[0]; // 操作模式
            String message = args.length > 2 ? args[2] : ""; // 消息内容

            if ("send".equals(operationMode) && !message.isEmpty()) {
                try {
                    sendFile(message, port);
                } catch (IOException e) {
                    System.out.println("发送过程中发生错误：" + e.getMessage());
                }
            } else if ("receive".equals(operationMode)) {
                try {
                    receiveFile(port);
                } catch (IOException e) {
                    System.out.println("接收过程中发生错误：" + e.getMessage());
                }
            }
        }
    }

    public static void sendFile(String message, int port) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int sequenceNumber = 0;
        int tryCounter = 0;

        try (DatagramSocket socket = new DatagramSocket()) {
            boolean ackReceived = false;
            // 主机地址
            InetAddress address = InetAddress.getByName(HOST);
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

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
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

            DatagramPacket endPacket = new DatagramPacket(endSendData, endSendData.length, address, port);
            socket.send(endPacket);
            System.out.println("发送 END");
            byte[] ackBytes = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
            tryCounter = 0;
            while (ackReceived) {
                try {
                    socket.setSoTimeout(2000); // 设置超时
                    socket.receive(ackPacket);
                    String ackMsg = new String(ackPacket.getData(), 0, ackPacket.getLength());

                    if (ackMsg.equals("ACK:" + sequenceNumber)) {
                        System.out.println("收到ACK " + sequenceNumber);
                        sequenceNumber++;
                        ackReceived = true;
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

    private static void sendEndPacket(DatagramSocket socket, InetAddress address, int sequenceNumber, int port) throws IOException {
        String endMsg = "END";
        byte[] endMsgBytes = endMsg.getBytes(StandardCharsets.UTF_8);
        String header = "SEQ:" + sequenceNumber + ":";
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] packetBytes = new byte[headerBytes.length + endMsgBytes.length];

        System.arraycopy(headerBytes, 0, packetBytes, 0, headerBytes.length);
        System.arraycopy(endMsgBytes, 0, packetBytes, headerBytes.length, endMsgBytes.length);

        DatagramPacket endPacket = new DatagramPacket(packetBytes, packetBytes.length, address, port);
        socket.send(endPacket);

    }

    public static void receiveFile(int port) throws IOException {
        Map<Integer, String> receivedMessages = new HashMap<>();
        boolean endOfFileReceived = false;
        int expectedSequenceNumber = 0;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            boolean endOfMessageReceived = false;
            int packetCount = 0;

            while (!endOfMessageReceived) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivedPacket);

                String receivedData = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), StandardCharsets.UTF_8);
                if (receivedData.startsWith("SEQ:")) {
                    String[] parts = receivedData.split(":", 5);
                    int sequenceNumber = Integer.parseInt(parts[1]);
                    String recData = parts[4]; //

                    if (sequenceNumber == expectedSequenceNumber) {
                        packetCount++;
                        // 当收到期望的数据包时，更新期望的序列号
                        expectedSequenceNumber++;

                        if (packetCount % 8 == 0) {
                            System.out.println("模拟丢包，序列号: " + sequenceNumber);
                            // 这里我们不递增expectedSequenceNumber，因为我们期望收到这个丢失的包的重传
                            // 因为实际上并没有正确处理（即没有发送ACK），所以期望的序列号应该保持不变以等待重传
                            // 但是由于在下一次循环开始时expectedSequenceNumber会自增，这里需要先递减来抵消那次自增
                            expectedSequenceNumber--;// 这次错了 所以相当于没有

                            continue; // Skip ACK for simulated lost packet
                        }

                        receivedMessages.put(sequenceNumber, recData);
                        System.out.println("收到序列号 " + sequenceNumber + " 的内容: " + recData);
                        Thread.sleep(100);
                        // 发送ACK
                        String ackMsg = "ACK:" + sequenceNumber;
                        byte[] ackData = ackMsg.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, receivedPacket.getAddress(), receivedPacket.getPort());
                        socket.send(ackPacket);

                        // 判断是否是最后一个数据包
                        if (recData.contains("END")) {
                            endOfMessageReceived = true;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 组装消息并打印
        String fullMessage = receivedMessages.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());
        // 去除末尾
        if (fullMessage.endsWith("END")) {
            fullMessage = fullMessage.substring(0, fullMessage.length() - 3);
        }
        System.out.println("完整消息接收并重组完成: " + fullMessage);
    }

}


