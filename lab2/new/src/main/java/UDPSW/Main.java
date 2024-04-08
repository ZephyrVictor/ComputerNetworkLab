package UDPSW;// author Zephyr369

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final String message = "发送方给接收方的消息:Once upon a time, a few mistakes ago.I was in your sights, you got me alone.You found me, you found me, you found me.I guess you didn't care, and I guess I liked that.And when I fell hard you took a step back.Without me, without me, without me..And he's long gone when he's next to me.And I realize the blame is on me..'Cause I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..No apologies. He'll never see you cry,.Pretends he doesn't know that he's the reason why.You're drowning, you're drowning, you're drowning.Now I heard you moved on from whispers on the street.A new notch in your belt is all I'll ever be.And now I see, now I see, now I see..He was long gone when he met me.And I realize the joke is on me, yeah!..I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..And the saddest fear comes creeping in.That you never loved me or her, or anyone, or anything, yeah..I knew you were trouble when you walked in.So shame on me now.Flew me to places I'd never been.'Til you put me down, oh.I knew you were trouble when you walked in.(You're right there, you're right there).So shame on me now.Flew me to places I'd never been.Now I'm lying on the cold hard ground.Oh, oh, trouble, trouble, trouble.Oh, oh, trouble, trouble, trouble..I knew you were trouble when you walked in.Trouble, trouble, trouble.I knew you were trouble when you walked in.Trouble, trouble, trouble.";
//        final String messageBack = "接收方给发送方的消息:ONCE UPON A TIME, A FEW MISTAKES AGO.I WAS IN YOUR SIGHTS, YOU GOT ME ALONE.YOU FOUND ME, YOU FOUND ME, YOU FOUND ME.I GUESS YOU DIDN'T CARE, AND I GUESS I LIKED THAT.AND WHEN I FELL HARD YOU TOOK A STEP BACK.WITHOUT ME, WITHOUT ME, WITHOUT ME..AND HE'S LONG GONE WHEN HE'S NEXT TO ME.AND I REALIZE THE BLAME IS ON ME..'CAUSE I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.'TIL YOU PUT ME DOWN, OH.I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.NOW I'M LYING ON THE COLD HARD GROUND.OH, OH, TROUBLE, TROUBLE, TROUBLE.OH, OH, TROUBLE, TROUBLE, TROUBLE..NO APOLOGIES. HE'LL NEVER SEE YOU CRY,.PRETENDS HE DOESN'T KNOW THAT HE'S THE REASON WHY.YOU'RE DROWNING, YOU'RE DROWNING, YOU'RE DROWNING.NOW I HEARD YOU MOVED ON FROM WHISPERS ON THE STREET.A NEW NOTCH IN YOUR BELT IS ALL I'LL EVER BE.AND NOW I SEE, NOW I SEE, NOW I SEE..HE WAS LONG GONE WHEN HE MET ME.AND I REALIZE THE JOKE IS ON ME, YEAH!..I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.'TIL YOU PUT ME DOWN, OH.I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.NOW I'M LYING ON THE COLD HARD GROUND.OH, OH, TROUBLE, TROUBLE, TROUBLE.OH, OH, TROUBLE, TROUBLE, TROUBLE..AND THE SADDEST FEAR COMES CREEPING IN.THAT YOU NEVER LOVED ME OR HER, OR ANYONE, OR ANYTHING, YEAH..I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.'TIL YOU PUT ME DOWN, OH.I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.(YOU'RE RIGHT THERE, YOU'RE RIGHT THERE).SO SHAME ON ME NOW.FLEW ME TO PLACES I'D NEVER BEEN.NOW I'M LYING ON THE COLD HARD GROUND.OH, OH, TROUBLE, TROUBLE, TROUBLE.OH, OH, TROUBLE, TROUBLE, TROUBLE..I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.TROUBLE, TROUBLE, TROUBLE.I KNEW YOU WERE TROUBLE WHEN YOU WALKED IN.TROUBLE, TROUBLE, TROUBLE.";
        final String messageBack = "接收方给发送方的消息:Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50Vivo50";
        // 定义双方使用的端口号，以避免冲突，其实UDP就是只有目的端口 但是开发过程中 在sender中的端口 就是目的端口 receiver的端口 也是目的端口
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
                UDPFileTransfer.sendFile(messageBack, secondPortToSend);
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
        firstSender.start();
        secondReceiver.start();
        secondSender.start();

    }
}