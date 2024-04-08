package ImprovedAndCSFiler;// author Zephyr369 

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final String message = "这是一个很长的字符串，用于全双工传输测试。";

        // 定义双方使用的端口号，以避免冲突
        int firstPortToSend = 1235; // 第一方发送端口
        int firstPortToReceive = 1234; // 第一方接收端口
        int secondPortToSend = 1234; // 第二方发送端口
        int secondPortToReceive = 1235; // 第二方接收端口

        // 第一方发送和接收线程
        Thread firstSender = new Thread(() -> {
            try {
                UDPFileTransfer.sendFile(message, firstPortToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread firstReceiver = new Thread(() -> {
            try {
                UDPFileTransfer.receiveFile(firstPortToReceive);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 第二方发送和接收线程
        Thread secondSender = new Thread(() -> {
            try {
                UDPFileTransfer.sendFile(message, secondPortToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread secondReceiver = new Thread(() -> {
            try {
                UDPFileTransfer.receiveFile(secondPortToReceive);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 启动所有线程
        firstReceiver.start();
        try{
            Thread.sleep(1000);//确保接受者启动了
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        firstSender.start();
        secondReceiver.start();
        try{
            Thread.sleep(1000);//确保接受者启动了
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        secondSender.start();

    }
}