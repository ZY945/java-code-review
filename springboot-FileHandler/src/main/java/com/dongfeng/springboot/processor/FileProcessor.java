package com.dongfeng.springboot.processor;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FileProcessor {
    public void processLargeCsv(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 逐行处理
                System.out.println(line);
            }
        }
    }

//
//    public void processLargeExcel(File file) throws IOException {
//        try (InputStream is = new FileInputStream(file);
//             Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(is), 100)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                // 逐行处理
//            }
//        }
//    }
}