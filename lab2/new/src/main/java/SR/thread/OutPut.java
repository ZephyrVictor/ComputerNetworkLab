package SR.thread;// author Zephyr369 

import SR.entity.BaseNum;
import SR.entity.NextSec;
import SR.entity.cache;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

// 发送方核心线程
public class OutPut extends Thread{
    private List<cache> buffer;
    private int timeLimit;
    private BaseNum baseNum;
    private NextSec nextSec;
    private int port;// 目标端口
    private int sizeOfwindow;
    private DatagramSocket socket;

    public OutPut(List<cache> buffer,int timeLimit,BaseNum baseNum,NextSec nextSec,int sizeOfWin,int targetPort,DatagramSocket socket){
        this.buffer=buffer;
        this.timeLimit=timeLimit;
        this.baseNum=baseNum;
        this.nextSec=nextSec;
        this.sizeOfwindow=sizeOfWin;
        this.port=targetPort;
        this.socket=socket;
    }

    @Override
    public void run(){
//        while(true){
//            // 满了
//            if(nextSec.nextSec >= baseNum.baseNum + sizeOfwindow){
//                System.out.println("等会，现在没有可用的序列号，窗口满了");
//                try{
//                    // 歇会 接收方接受不动了
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            else{
//                // 发送数据
//                int seqNum = nextSec.nextSec;
//
//                System.out.println("发送方窗口:" + baseNum.baseNum + "| 下一个序列号是:" + nextSec.nextSec);
//                String content = seqNum + ":packet:" + data;
//                DatagramPacket packet;
//                try{
//                    packet = new DatagramPacket(content.getBytes(), content.getBytes().length, InetAddress.getByName("localhost"), port);
//                    socket.send(packet);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                cache newCache = new cache(seqNum, packet);
//                // 将发送的分组缓存在本地
//                buffer.add(newCache);
//                //SR 对每一个分组单独计时
//                new Timer(seqNum, timeLimit, buffer).start();
//            }
//        }
        String data = "message = Once upon a time, a few mistakes ago.I was in your sights, you got me alone.You found me, you found me, you found me.I guess you didn't care, and I guess I liked that.And when I fell hard you took a step back.Without me, without me, without me..And he's long gone when he's next to me.And I realize the blame is on me..'Cause I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..No apologies. He'll never see you cry,.Pretends he doesn't know that he's the reason why.You're drowning, you're drowning, you're drowning.Now I heard you moved on from whispers on the street.A new notch in your belt is all I'll ever be.And now I see, now I see, now I see..He was long gone when he met me.And I realize the joke is on me, yeah!..I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..And the saddest fear comes creeping in.That you never loved me or her, or anyone, or anything, yeah..I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.(You're right there, you're right there).So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..I knew you were trouble when you walked in.Trouble, trouble, trouble.I knew you were trouble when you walked in.Trouble, trouble, trouble.";
        //                System.out.println("请输入你想传输的数据:");
//                Scanner in=new Scanner(System.in);
//                String data=in.nextLine();
        String[] dataParts = data.split(" ");
        for(int i = 0 ; i < dataParts.length ; i++){
            // 满了
            if(nextSec.nextSec >= baseNum.baseNum + sizeOfwindow){
                System.out.println("等会，现在没有可用的序列号，窗口满了");
                try{
                    // 歇会 接收方接受不动了
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                // 发送数据
                int seqNum = nextSec.nextSec;

                System.out.println("发送方窗口:" + baseNum.baseNum + "| 下一个序列号是:" + nextSec.nextSec);
                String content = seqNum + ":packet:" + dataParts[i];
                DatagramPacket packet;
                try{
                    packet = new DatagramPacket(content.getBytes(), content.getBytes().length, InetAddress.getByName("localhost"), port);
                    socket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                cache newCache = new cache(seqNum, packet);
                // 将发送的分组缓存在本地
                buffer.add(newCache);
                //SR 对每一个分组单独计时
                new Timer(seqNum, timeLimit, buffer).start();
                try {
                    Thread.sleep(100); // 延迟 要不太快了
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
