package SR;// author Zephyr369 

import SR.entity.BaseNum;
import SR.entity.cache2;
import SR.thread.Deliver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Receiver {
    private static int maxSecNum = 100;
    private static int sizeOfWindows = 10;
    private static int base = 0;

    private static int port = 1235; // 对面的端口
    private static double lossRate = 0.2; // 丢包率，例如0.1表示10%的概率丢包
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket receiver=new DatagramSocket(1234);
        List<cache2> buffer=new ArrayList<>();//接收方缓存
        BaseNum baseNum=new BaseNum(base);
        Random random = new Random(); // 用于生成随机数，判断是否丢包
        System.out.println("接收，启动");
        while(true){
            System.out.println("接收窗口:" + baseNum.baseNum);
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            receiver.receive(packet);
            String newData = new String(packet.getData(), 0, packet.getLength());
            int seqNum=Integer.parseInt(newData.split(":")[0]);

            if(random.nextDouble() > lossRate){//不丢包
                //收到了小于baseNum的 发送目前最小的ack 不缓存
                if(seqNum < baseNum.baseNum){
                    System.out.println("接收到的序列号:" + seqNum + "数据为:" + newData);
                    String ack = seqNum + ":ACK";
                    DatagramPacket ACK = new DatagramPacket(ack.getBytes(),ack.getBytes().length, InetAddress.getByName("localhost"),port);
                    receiver.send(ACK);
                    // 向发送方回传接收到的数据
                    DatagramPacket responsePacket = new DatagramPacket(newData.getBytes(), newData.getBytes().length, InetAddress.getByName("localhost"), port);
                    receiver.send(responsePacket); // 回传数据
                    System.out.println("Responded with same data to sender: " + newData);
                }
                //
                else if(seqNum >= baseNum.baseNum && seqNum <= baseNum.baseNum+sizeOfWindows){
                    System.out.println("接收到的序列号:" + seqNum + "数据为:" + newData);
                    String ack = seqNum + ":ACK";
                    DatagramPacket ACK = new DatagramPacket(ack.getBytes(),ack.getBytes().length, InetAddress.getByName("localhost"),port);
                    receiver.send(ACK);
                    String receivedData = Arrays.toString(packet.getData());
                    buffer.add(new cache2(seqNum,packet));
                    // 向发送方回传接收到的数据
                    DatagramPacket responsePacket = new DatagramPacket(receivedData.getBytes(), receivedData.getBytes().length, InetAddress.getByName("localhost"), port);
                    receiver.send(responsePacket); // 回传数据
                    System.out.println("Responded with same data to sender: " + receivedData);
                }
                else {
                    System.out.println("序列号不在接受范围内，等会...");
                }
            }
            else{//丢包
                System.out.println("模拟丢包，不发送ACK:" + seqNum);
            }

            //将缓存中的packet按序列号升序排列 冒泡排序就行
            for(int i=0;i<buffer.size()-1;i++){
                for(int j=0;j< buffer.size()-1-i;j++){
                    if(buffer.get(j).getSeqNum() > buffer.get(j+1).getSeqNum()){
                        cache2 a=buffer.get(j);
                        cache2 b=buffer.get(j+1);
                        buffer.set(j+1,a);
                        buffer.set(j,b);
                    }
                }
            }
            Thread.sleep(100);//yanchi
            // 模拟向上层交付
            new Deliver(buffer,baseNum).start();
        }
    }
}
