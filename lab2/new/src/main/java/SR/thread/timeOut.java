package SR.thread;// author Zephyr369 

import SR.entity.cache;

import javax.imageio.IIOException;
import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.List;
// 超时处理
public class timeOut extends Thread{
    private List<cache> buffer;
    DatagramSocket socket;
    int timeLimit;

    public timeOut(List<cache> buffer,DatagramSocket socket,int timeLimit){
        this.buffer=buffer;
        this.socket=socket;
        this.timeLimit=timeLimit;
    }

    @Override
    public void run(){
        while(true){
            if(!buffer.isEmpty()){
                for(cache ca : buffer){
                    //如果超时了
                    if(ca.isTimeout()){
                        System.out.println("序列号:"+ca.getSeqNum() + "超时了QAQ");
                        try{
                            socket.send(ca.getPacket());
                            new Timer(ca.getSeqNum(), timeLimit, buffer).start(); // 重传 重新启动计时器
                        } catch (IOException e){
                            throw new RuntimeException(e);
                        }
                        System.out.println("序列号" + ca.getSeqNum() + "重传完成");
                    }
                }
            }
        }
    }

}
