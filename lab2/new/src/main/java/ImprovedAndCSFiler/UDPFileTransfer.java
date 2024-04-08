package ImprovedAndCSFiler;// author Zephyr369 

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
    private static int packetCount = 0; // 用于接收方模拟丢包

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

        try (DatagramSocket socket = new DatagramSocket()) {
            // 主机地址
            InetAddress address = InetAddress.getByName(HOST);
            for (int i = 0; i < messageBytes.length; i += MAX_PACKET_SIZE - 10) {
                int end = Math.min(messageBytes.length, i + MAX_PACKET_SIZE - 10);
                int length = end - i;
                byte[] messageChunk = new byte[length];
                System.arraycopy(messageBytes, i, messageChunk, 0, length);

                String header = "SEQ:" + sequenceNumber + ":";
                byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
                byte[] packetBytes = new byte[headerBytes.length + messageChunk.length];

                System.arraycopy(headerBytes, 0, packetBytes, 0, headerBytes.length);
                System.arraycopy(messageChunk, 0, packetBytes, headerBytes.length, messageChunk.length);

                DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, address, port);
                socket.send(packet);

                // 等待ACK
                boolean ackReceived = false;
                int tryCounter = 0;
                while (!ackReceived && tryCounter < 3) {
                    byte[] ackBuffer = new byte[1024];
                    DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

                    try {
                        socket.setSoTimeout(2000);
                        socket.receive(ackPacket);
                        int senderPort = ackPacket.getPort();
                        String ackMsg = new String(ackPacket.getData(), 0, ackPacket.getLength(), StandardCharsets.UTF_8);
                        if (ackMsg.equals("ACK:" + sequenceNumber)) {
                            ackReceived = true;
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("超时，重传序列号：" + sequenceNumber);
                        socket.send(packet); // 超时重传
                        tryCounter++;
                    }
                }

                if (!ackReceived) {
                    System.out.println("序列号 " + sequenceNumber + " 重试三次仍未能成功接收ACK，中止传输。");
                    return;
                }

                sequenceNumber++;
            }

            // 发送结束标志
            sendEndPacket(socket, address, sequenceNumber, port);
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
            while (!endOfFileReceived) {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedData = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                if (receivedData.startsWith("SEQ:")) {
                    String[] parts = receivedData.split(":", 3);
                    int sequenceNumber = Integer.parseInt(parts[1]);
                    String data = new String(packet.getData(), parts[0].length() + parts[1].length() + 2, packet.getLength() - parts[0].length() - parts[1].length() - 2, StandardCharsets.UTF_8);

                    if (sequenceNumber == expectedSequenceNumber) {
                        packetCount++;
                        if (packetCount % 6 == 0) {
                            System.out.println("模拟丢包，序列号: " + sequenceNumber);
                            continue;
                        }

                        if ("END".equals(data)) {
                            endOfFileReceived = true;
                        } else {
                            receivedMessages.put(sequenceNumber, data);
                        }

                        // 发送ACK
                        String ackMsg = "ACK:" + sequenceNumber;
                        byte[] ackBytes = ackMsg.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, packet.getAddress(), port);
                        socket.send(ackPacket);

                        expectedSequenceNumber++;
                    }
                }
            }
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


