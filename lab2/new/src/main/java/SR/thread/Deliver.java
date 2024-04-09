package SR.thread;// author Zephyr369 

import SR.entity.BaseNum;
import SR.entity.cache2;

import java.util.Iterator;
import java.util.List;

// 接收方从缓存将包顺序的交给上层应用
public class Deliver extends Thread{
    private List<cache2> buffer;
    private BaseNum baseNum;

    public Deliver(List<cache2> buffer, BaseNum baseNum) {
        this.buffer = buffer;
        this.baseNum = baseNum;
    }
    // 模拟向上层交付数据
    @Override
    public void run(){
        if(!buffer.isEmpty() && buffer.get(0).getSeqNum() == baseNum.baseNum){
            Iterator<cache2> iterator = buffer.iterator();
            int index = baseNum.baseNum;
            while(iterator.hasNext()){
                cache2 ca = iterator.next();
                if(ca.getSeqNum() == baseNum.baseNum){
                    iterator.remove();
                    baseNum.baseNum++;
                    index = baseNum.baseNum;
                }
                else if(ca.getSeqNum() == index + 1){
                    // 连续的下一个数据包
                    iterator.remove();
                    baseNum.baseNum++;
                    index++;
                }
                else {
                    break;
                }
            }
        }
    }
}
