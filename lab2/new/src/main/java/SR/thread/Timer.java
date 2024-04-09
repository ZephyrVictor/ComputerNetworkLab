package SR.thread;// author Zephyr369 

import SR.entity.cache;

import java.util.List;

// SR的计时器 发送方
public class Timer extends Thread{
    private int seqNum;
    private int timeLim;
    private List<cache> buffer;

    public Timer(int seqNum, int timeLim, List<cache> buffer) {
        this.seqNum = seqNum;
        this.timeLim = timeLim;
        this.buffer = buffer;
    }

    @Override
    public void run(){
        //以秒为单位
        for(int i = 0 ; i < timeLim / 1000 ; i++){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//  计时器对应的数据在缓存中 而且没确认 说明超时了
        if(!buffer.isEmpty()){
            for(cache ca : buffer){
                if(ca.getSeqNum() == seqNum && !ca.isACKed()){
                    ca.timeout();
                }
            }
        }
    }

}
