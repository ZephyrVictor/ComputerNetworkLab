package SR.thread;// author Zephyr369



import SR.entity.BaseNum;
import SR.entity.cache;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.List;

public class AcceptAck extends Thread{
    private DatagramSocket socket;
    private BaseNum baseNum;
    private List<cache> buffer;

    public AcceptAck(DatagramSocket socket, BaseNum baseNum, List<cache> buffer) {
        this.socket = socket;
        this.baseNum = baseNum;
        this.buffer = buffer;
    }

    @Override
    public void run(){
        while(true){
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try{
                socket.receive(packet);
            } catch (IOException e){
                throw new RuntimeException(e);
            }

            String newData = new String(packet.getData());
            // 拿到ack number
            int ackNum = Integer.valueOf(newData.split(":")[0]);
            System.out.println("接收到了一个packet，序列号为:" + ackNum);
            // 更新ack
            for(cache ca:buffer){
                if(ca.getSeqNum()==ackNum){
                    ca.ACKed();
                }
            }
            // 升序排列，由于数据规模不大 用冒泡就行
            if(buffer.size() != 0){
                for(int i = 0 ; i < buffer.size() - 1 ; i++){
                    for(int j = 0 ; j < buffer.size() - 1 ; j++){
                        if(buffer.get(j).getSeqNum() > buffer.get(j+1).getSeqNum()){
                            cache a = buffer.get(j);
                            cache b = buffer.get(j + 1);
                            buffer.set(j + 1, a);
                            buffer.set(j, b);
                        }
                    }
                }
            }
            //清理缓存
            Iterator<cache> iterator = buffer.iterator();
            while(iterator.hasNext()){
                if(iterator.next().isACKed()){
                    // 如果ack了，那就删除 因为已经确认了 发送窗口的后沿就可以向前移动
                    iterator.remove();
                    baseNum.baseNum++;
                }
            }
            if(buffer.size() != 0){
                baseNum.baseNum = buffer.get(0).getSeqNum();
            }
        }
    }
}
