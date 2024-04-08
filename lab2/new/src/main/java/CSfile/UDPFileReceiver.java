package CSfile;// author Zephyr369 
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static CSfile.UDPSender.MAX_PACKET_SIZE;

public class UDPFileReceiver {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            receiveFile(PORT, "img/recv/");
        } catch (IOException e) {
            System.out.println("接收过程中发生错误：" + e.getMessage());
        }
    }

    private static void receiveFile(int port, String saveDir) throws IOException {
        File dir = new File(saveDir);
        boolean isSimulated = false;
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        File file = new File(dir, "received_file");


        try (DatagramSocket socket = new DatagramSocket(port)) {
            // 先接收文件名
            String fileName = receiveFileName(socket);
            if (fileName == null || fileName.isEmpty()) {
                throw new IOException("未接收到有效的文件名");
            }
            File file = new File(saveDir, fileName); // 使用接收到的文件名创建文件
            try(FileOutputStream fos = new FileOutputStream(file)){
                boolean endOfFile = false;
                while (!endOfFile) {

                    byte[] receiveBuffer = new byte[MAX_PACKET_SIZE + 10];
                    DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivedPacket);

                    ByteArrayInputStream bais = new ByteArrayInputStream(receivedPacket.getData());
                    DataInputStream dis = new DataInputStream(bais);
                    int sequenceNumber = dis.readInt();
                    int length = dis.readInt();

                    if (length == 0) { // 接收到结束包
                        endOfFile = true;
                        System.out.println("文件接收完毕。");
                    } else {
                        if(sequenceNumber % 100 == 0 && sequenceNumber != 0 && !isSimulated){
                            System.out.println("序号:" + sequenceNumber + " 模拟丢失");
                            isSimulated = true;
                            continue;
                        }
                        isSimulated = false;
                        byte[] data = new byte[length];
                        dis.readFully(data, 0, length);
                        fos.write(data);
                        sendAck(socket, receivedPacket.getAddress(), receivedPacket.getPort(), sequenceNumber);
                    }
                }
            } catch(IOException e){
                System.out.println("在文件接受的时候出现了错误" + e.getMessage());
            }
        }
    }

    private static String receiveFileName(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
        if (received.startsWith("FILENAME:")) {
            return received.substring(9); // 移除"FILENAME:"前缀
        }
        return null; // 或抛出异常，因为期望首先接收文件名
    }


    private static void sendAck(DatagramSocket socket, InetAddress address, int port, int sequenceNumber) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(sequenceNumber);
        byte[] ackData = baos.toByteArray();

        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, address, port);
        socket.send(ackPacket);
        System.out.println("发送ACK " + sequenceNumber);
    }

}
