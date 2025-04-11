package com.dongfeng.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

@RestController
@Slf4j
public class FileController {

    private static final String rootPath = System.getProperty("user.dir"); // 上传文件保存的目录

    /**
     * 处理普通文件上传请求
     *
     * @param file 客户端上传的文件，封装为 MultipartFile 对象
     * @return 上传结果的字符串提示
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        // 检查文件是否为空
        if (!file.isEmpty()) {
            try {
                // 将上传的文件保存到指定路径，文件名为原始文件名
                String fileName = rootPath + File.separator + "uploadFile" + File.separator + file.getOriginalFilename();
                File tempDir = new File(fileName);
                file.transferTo(tempDir);
                return "上传成功";
            } catch (IOException e) {
                log.info("{}", String.valueOf(e));
                // 捕获 IO 异常，返回失败提示
                return "上传失败";
            }
        }
        // 文件为空时返回提示
        return "文件为空";
    }

    /**
     * 处理分片上传请求，用于大文件分片上传
     *
     * @param chunk       当前上传的文件分片
     * @param chunkNumber 当前分片编号（从 0 开始）
     * @param totalChunks 总分片数
     * @param fileName    原始文件名，用于存储和合并
     * @return 分片上传结果的字符串提示
     * @throws IOException 文件操作可能抛出的异常
     */
    @PostMapping("/upload/chunk")
    public String uploadChunk(@RequestParam("file") MultipartFile chunk,
                              @RequestParam("chunkNumber") int chunkNumber,
                              @RequestParam("totalChunks") int totalChunks,
                              @RequestParam("fileName") String fileName) throws IOException {
        // 创建临时目录，用于存储分片文件
        // 将上传的文件保存到指定路径，文件名为原始文件名
        String finalFileName = rootPath + File.separator + "uploadChunk" + File.separator + "temp";

        File tempDir = new File(finalFileName);
        tempDir.mkdirs(); // 确保目录存在
        // 'File. mkdirs()' 的结果已忽略怎么办

        // 将当前分片保存为临时文件，文件名格式为 "分片编号.part"
        chunk.transferTo(new File(tempDir, chunkNumber + ".part"));
        // 如果当前分片是最后一个分片，则合并所有分片
        if (chunkNumber == totalChunks - 1) {
            mergeChunks(tempDir, fileName);
        }
        return "分片上传成功";
    }

    /**
     * 处理文件下载请求，使用流式下载方式
     *
     * @return ResponseEntity 包含文件流和响应头信息的实体
     * @throws IOException 文件读取可能抛出的异常
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile() throws IOException {
        String fileName = rootPath + File.separator + "downloadFile" + File.separator + "test.txt";

        // 模拟文件,如果不存在则创建一个文件
        File file = new File(fileName);

        if (!file.exists()) {
            file.mkdirs(); // 确保目录存在
            file.createNewFile();
            // 写入测试数据
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write("This is a test file.".getBytes());
            }
        }

        // 正常逻辑,判断是否存在
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // 创建输入流资源，用于流式传输文件内容
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        // 返回带有文件流和响应头的 ResponseEntity
        return ResponseEntity.ok()
                // 设置响应头，指定文件下载时的文件名
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                // 设置文件长度，便于客户端显示下载进度
                .contentLength(file.length())
                // 设置内容类型为二进制流
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                // 设置响应体为文件流资源
                .body(resource);
    }

    /**
     * 处理支持范围请求的文件下载，用于断点续传或分片下载
     *
     * @param request HTTP 请求对象，用于获取 Range 头信息
     * @return ResponseEntity 包含指定范围文件内容和响应头信息的实体
     * @throws IOException 文件读取可能抛出的异常
     */
    @GetMapping("/download/range")
    public ResponseEntity<byte[]> downloadRange(HttpServletRequest request) throws IOException {
        // 指定要下载的文件路径
        File file = new File("/path/to/file.zip");
        // 获取请求中的 Range 头，例如 "bytes=0-1023"
        String range = request.getHeader("Range");
        // 使用 RandomAccessFile 以支持随机读取文件内容
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        // 默认下载范围为文件开头到结尾
        long start = 0, end = raf.length() - 1;
        // 如果 Range 头存在，解析请求的字节范围
        if (range != null && range.startsWith("bytes=")) {
            String[] ranges = range.substring(6).split("-");
            start = Long.parseLong(ranges[0]); // 起始字节
            end = ranges.length > 1 ? Long.parseLong(ranges[1]) : end; // 结束字节，默认到文件末尾
        }
        // 创建缓冲区，大小为请求范围的字节数
        byte[] buffer = new byte[(int) (end - start + 1)];
        // 定位到起始字节并读取指定范围的内容
        raf.seek(start);
        raf.readFully(buffer);
        raf.close(); // 关闭文件流
        // 返回部分内容响应，状态码为 206
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                // 设置 Content-Range 头，格式为 "bytes 起始-结束/总长度"
                .header("Content-Range", "bytes " + start + "-" + end + "/" + file.length())
                // 返回指定范围的字节数组
                .body(buffer);
    }

    /**
     * 合并分片文件
     *
     * @param tempDir  临时分片文件目录
     * @param fileName 最终合并后的文件名
     * @throws IOException 文件操作可能抛出的异常
     */
    private void mergeChunks(File tempDir, String fileName) throws IOException {
        // 目标文件路径
        String finalFileName = rootPath + File.separator + fileName;

        File targetFile = new File(finalFileName);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            // 获取临时目录中的所有分片文件并按分片编号排序
            File[] chunks = tempDir.listFiles((dir, name) -> name.endsWith(".part"));
            if (chunks != null) {
                Arrays.sort(chunks, Comparator.comparing(f -> Integer.parseInt(f.getName().split("\\.")[0])));
                // 依次读取每个分片并写入目标文件
                for (File chunk : chunks) {
                    try (FileInputStream fis = new FileInputStream(chunk)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
        // 合并完成后删除临时目录及其内容
        deleteDirectory(tempDir);
    }

    /**
     * 递归删除目录及其内容
     *
     * @param directory 要删除的目录
     */
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }
}