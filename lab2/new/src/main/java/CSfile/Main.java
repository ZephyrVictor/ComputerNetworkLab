package CSfile;// author Zephyr369 

public class Main {
    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            try {
                UDPFileReceiver.main(null); // 启动服务器端
            } catch (Exception e) {
                System.out.println("服务器线程异常：" + e.getMessage());
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                // 确保服务器已启动
                Thread.sleep(100); // 稍微延迟启动客户端，等待服务器准备就绪
                UDPSender.main(null); // 启动客户端
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("客户端启动等待被中断：" + e.getMessage());
            } catch (Exception e) {
                System.out.println("客户端线程异常：" + e.getMessage());
            }
        });

        serverThread.start(); // 先启动服务器，确保监听端口
        clientThread.start(); // 启动客户端，发送数据
    }
}
