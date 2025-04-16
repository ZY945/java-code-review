package com.dongfeng.springboot.tcp;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class Server {
    // 字节数组的长度
    private static final int BYTE_LENGTH = 20;
 
    public static void main(String[] args) throws IOException {
        // 创建 Socket 服务器
        ServerSocket serverSocket = new ServerSocket(8888);
        // 获取客户端连接
        Socket clientSocket = serverSocket.accept();
        // 得到客户端发送的流对象
        InputStream is = clientSocket.getInputStream();
        while (true) {
            // 循环获取客户端发送的信息
            byte[] bytes = new byte[BYTE_LENGTH];
            // 读取客户端发送的信息
            try {
                int count = is.read(bytes, 0, BYTE_LENGTH);
                if (count > 0) {
                    // 成功接收到有效消息并打印
                    System.out.println("接收到客户端的信息是:" + new String(bytes, StandardCharsets.UTF_8));
                }
                count = 0;
            } catch (Exception e) {
                // ignore
            }
        }
    }
}