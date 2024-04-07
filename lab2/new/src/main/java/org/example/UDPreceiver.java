package org.example;
// author = "Zephyr369"
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class UDPreceiver {
    private static final int PORT = 1234;
    private static int packetCount = 0;
    private static int expectedSequenceNumber = 0;

    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            Properties prop = new Properties();
            try (InputStream input = UDPSender.class.getClassLoader().getResourceAsStream("application.properties")) {
                prop.load(input);
            }

            String message = prop.getProperty("message");
            Map<Integer, String> receivedMessages = new HashMap<>();
            boolean endOfMessageReceived = false;

            while (!endOfMessageReceived) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivedPacket);

                String receivedData = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), StandardCharsets.UTF_8);
                if (receivedData.startsWith("SEQ:")) {
                    String[] parts = receivedData.split(":", 5);
                    int sequenceNumber = Integer.parseInt(parts[1]);
                    String recData = parts[4]; // 假设数据立即跟随长度信息

                    if (sequenceNumber == expectedSequenceNumber) {
                        packetCount++;
                        // 当收到期望的数据包时，更新期望的序列号
                        expectedSequenceNumber++;

                        if (packetCount % 6 == 0) {
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

            // 组合并打印整个消息
            String fullMessage = receivedMessages.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining());
            // 去除末尾
            if (fullMessage.endsWith("END")) {
                fullMessage = fullMessage.substring(0, fullMessage.length() - 3);
            }
            System.out.println("完整消息: " + fullMessage);
            if (fullMessage.equals(message)) {
                System.out.println("字符串一致");
            } else {
                System.out.println("校验失败");
            }
        }
    }
}
