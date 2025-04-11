package io.bio;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BIOUploadClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 8080;
        Socket server = new Socket(host, port);

        String filePath = "";
        try (InputStream fileInputStream = Files.newInputStream(Paths.get(filePath))) {
            OutputStream ops = server.getOutputStream();
            DataOutputStream dos = new DataOutputStream(ops);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = fileInputStream.read()) > 0) {
                dos.write(bytes, 0, len);
            }
        }
    }
}
