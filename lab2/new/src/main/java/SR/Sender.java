package SR;// author Zephyr369 

import SR.entity.BaseNum;
import SR.entity.NextSec;
import SR.entity.cache;
import SR.thread.AcceptAck;
import SR.thread.OutPut;
import SR.thread.timeOut;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Sender {
    private static int timeLim = 5000;
    private static int maxSecNum = 100; // 最大序列号设置为100
    private static int sizeOfWindow = 10;// 滑动窗口大小
    private static int base = 0;// 起始位置
    private static int next = 0;// 下一个序列号
    private static int port = 1234;//目标进程端口号
    private static String HOST = "localhost";

    public static void main(String[] args) throws SocketException {
        BaseNum baseNum = new BaseNum(base);
        NextSec nextSec = new NextSec(next);
        List<cache> buffer = new ArrayList<>();//sender缓存
        DatagramSocket senderSocket=new DatagramSocket(1235);
        System.out.println("发送，启动！");
        // 发送
        new OutPut(buffer,timeLim,baseNum,nextSec,sizeOfWindow,port,senderSocket).start();
        // 接收ack
        new AcceptAck(senderSocket, baseNum, buffer).start();
        // 超时检查
        new timeOut(buffer, senderSocket, timeLim).start();
    }


}
