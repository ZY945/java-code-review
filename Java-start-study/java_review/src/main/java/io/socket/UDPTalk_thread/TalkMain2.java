package io.socket.UDPTalk_thread;

import java.net.SocketException;

/**
 * @author dongfeng
 * @date 2022/8/14 22:26
 */
public class TalkMain2 {
    public static void main(String[] args) throws SocketException {
        new Thread(new TalkSend(7777, "localhost", 8080), "xin").start();
        new Thread(new TalkReceive(6666, "dongfeng")).start();
    }
}
