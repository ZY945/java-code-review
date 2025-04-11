package com.dongfeng.springboot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FileControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // 初始化 MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testUploadFile() throws Exception {
        // 创建模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",           // 参数名
                "test.txt",       // 文件名
                "text/plain",     // 内容类型
                "Hello, World!".getBytes() // 文件内容
        );

        // 测试普通文件上传
        mockMvc.perform(multipart("/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("上传成功"));
    }

    @Test
    public void testUploadChunk() throws Exception {
        // 创建模拟分片文件
        MockMultipartFile chunk = new MockMultipartFile(
                "file",
                "chunk1.txt",
                "text/plain",
                "Chunk 1 content".getBytes()
        );

        // 测试分片上传（非最后一个分片）
        mockMvc.perform(multipart("/upload/chunk")
                        .file(chunk)
                        .param("chunkNumber", "0")
                        .param("totalChunks", "2")
                        .param("fileName", "testfile.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string("分片上传成功"));

        // 测试最后一个分片，触发合并
        MockMultipartFile lastChunk = new MockMultipartFile(
                "file",
                "chunk2.txt",
                "text/plain",
                "Chunk 2 content".getBytes()
        );
        mockMvc.perform(multipart("/upload/chunk")
                        .file(lastChunk)
                        .param("chunkNumber", "1")
                        .param("totalChunks", "2")
                        .param("fileName", "testfile.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string("分片上传成功"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        // 测试文件下载
        mockMvc.perform(get("/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    public void testDownloadRange() throws Exception {
        // 测试范围下载
        mockMvc.perform(get("/download/range")
                        .header("Range", "bytes=0-9"))
                .andExpect(status().isPartialContent())
                .andExpect(header().exists("Content-Range"));
    }
}