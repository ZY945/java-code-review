    package com.dongfeng.springboot.tcp;
 
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


    public class Client {
    public static void main(String[] args) throws IOException {
        // 创建 Socket 客户端并尝试连接服务器端
        Socket socket = new Socket("127.0.0.1", 8888);
        // 发送的消息内容
        final String message = "夏洛特烦恼";
        // 使用输出流发送消息
        OutputStream os = socket.getOutputStream();
        // 给服务器端发送 10 次消息
        for (int i = 0; i < 10; i++) {
            // 发送消息
            os.write(message.getBytes(StandardCharsets.UTF_8));
        }
    }
}