package org.example;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args){
        // receiver 在端口1234
        //启动receiver
        Thread receiverThread = new Thread(() -> {
            try{
                UDPreceiver.main(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //启动sender
        Thread senderThread = new Thread(() -> {
            try{
                UDPSender.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 启动线程
        receiverThread.start();
        try{
            Thread.sleep(1000);//确保接受者启动了
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        senderThread.start();

        // sender在端口1235
        Thread receiverThreadBack = new Thread(() -> {
            try{
                UDPreceiver.PORT = 1235;
                UDPreceiver.main(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //启动sender
        Thread senderThreadBack = new Thread(() -> {
            try{
                UDPSender.PORT = 1235;
                UDPSender.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 启动线程
        receiverThreadBack.start();
        try{
            Thread.sleep(1000);//确保接受者启动了
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        senderThreadBack.start();
    }


}
