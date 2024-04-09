package SR.entity;// author Zephyr369
import java.net.DatagramPacket;
//用于接收方的数据缓存。
public class cache2 {
    private int SeqNum;
    private DatagramPacket packet;
    public cache2(int SerNum,DatagramPacket packet){
        this.SeqNum=SerNum;
        this.packet=packet;
    }
    public int getSeqNum(){
        return this.SeqNum;
    }
    public DatagramPacket packet(){
        return this.packet;
    }
}
