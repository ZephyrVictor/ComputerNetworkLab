package CSfile;// author Zephyr369 
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class UDPSender {

    private static final String HOST = "localhost";
    public static final int MAX_PACKET_SIZE = 512;
    private static final int PORT = 1234;

    public static void main(String[] args){
        String filePath = "E:\\studying\\2\\下\\计算机网络\\exp\\lab2\\new\\src\\main\\java\\CSfile\\avatar.png";

        try{
            sendFile(filePath);
        } catch (Exception e) {
            System.out.println("发送文件出现了错误:" + e.getMessage());
        }
    }

    private static void sendFile(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        int sequenceNumber = 0;
        // 获得文件名
        File file = new File(filePath);
        String fileName = file.getName();
        System.out.println("文件名:" + fileName);


        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(HOST);
            // 先发送文件名称
            sendFileName(socket, address, PORT, fileName);
            for (int i = 0; i <= fileContent.length; i += MAX_PACKET_SIZE) {
                boolean isLastPacket = i + MAX_PACKET_SIZE >= fileContent.length;
                int end = isLastPacket ? fileContent.length : i + MAX_PACKET_SIZE;
                int length = end - i;
                byte[] packetData = new byte[length];

                System.arraycopy(fileContent, i, packetData, 0, length);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(sequenceNumber);
                dos.writeInt(length);
                dos.write(packetData);
                byte[] sendData = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, UDPSender.PORT);
                socket.send(packet);
                waitForAck(socket, sequenceNumber,packet);
                sequenceNumber++;

                if (isLastPacket) {
                    sendEndPacket(socket, address, sequenceNumber, UDPSender.PORT);
                    break;
                }
            }
        }
    }

    private static void sendFileName(DatagramSocket socket, InetAddress address, int port, String fileName) throws IOException {
        String header = "FILENAME:" + fileName;
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(headerBytes, headerBytes.length, address, port);
        socket.send(packet);

    }

    private static void waitForAck(DatagramSocket socket, int sequenceNumber,DatagramPacket packet) throws IOException {
        byte[] ackBytes = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length);
        int tryCounter = 0;
        while (true) {
            try{
                socket.setSoTimeout(500); // 设置超时时间
                socket.receive(ackPacket);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ackPacket.getData()));
                int ackSeq = dis.readInt();
                if (ackSeq == sequenceNumber) {
                    System.out.println("收到ACK " + sequenceNumber);
                    break;
                }
            } catch (Exception e){
                System.out.println("超时，重新发送" + sequenceNumber);
                socket.send(packet);
                if(++tryCounter > 3){
                    System.out.println("三次仍然没有ACK，终止");
                    return;
                }
            }
        }
    }

    private static void sendEndPacket(DatagramSocket socket, InetAddress address, int sequenceNumber, int port) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(sequenceNumber);
        dos.writeInt(0); // END包的长度为0
        byte[] endData = baos.toByteArray();

        DatagramPacket endPacket = new DatagramPacket(endData, endData.length, address, port);
        socket.send(endPacket);
        System.out.println("发送结束包，序列号：" + sequenceNumber);
    }
}
