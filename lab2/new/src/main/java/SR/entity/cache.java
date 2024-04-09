package SR.entity;// author Zephyr369
import java.net.DatagramPacket;
// 缓存数据包的信息，包括序列号、数据包本身、是否超时、是否已被确认（ACKed）

public class cache {
    // 数据包
    private DatagramPacket packet;
    // 序列号
    private int SeqNum;
    // 是否超时
    private boolean isTimeout=false;
    // 是否被确认
    private boolean isACKed=false;
    public cache(int SerNum,DatagramPacket packet){
        this.SeqNum=SerNum;
        this.packet=packet;
    }
    public boolean isTimeout(){
        return isTimeout;
    }
    public boolean isACKed(){
        return isACKed;
    }
    public int getSeqNum(){
        return this.SeqNum;
    }
    public DatagramPacket getPacket(){
        return this.packet;
    }
    public void timeout(){
        this.isTimeout=true;
    }
    public void ACKed(){
        isACKed=true;
    }
    public void setPacket(DatagramPacket packet){
        this.packet=packet;
    }
    public void setSerNum(int SerNum){
        this.SeqNum=SerNum;
    }

}
