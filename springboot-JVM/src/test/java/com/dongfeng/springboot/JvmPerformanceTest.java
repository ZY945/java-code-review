package com.dongfeng.springboot;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class JvmPerformanceTest {

    private static final String JAR_PATH = "target/springboot-JVM.jar";
    private static final int WAIT_TIME_SECONDS = 60;
    
    @Test
    public void testDifferentJvmConfigurations() throws IOException, InterruptedException {
        // Ensure the jar file exists
        File jarFile = new File(JAR_PATH);
        if (!jarFile.exists()) {
            log.error("JAR file not found at: {}", JAR_PATH);
            log.info("Please run 'mvn clean package' first to build the JAR file.");
            return;
        }
        
        // Define different JVM configurations to test
        List<JvmConfig> configs = Arrays.asList(
            new JvmConfig("Low Load", 
                "-Xms512m -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC " +
                "-XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC"),
                
            new JvmConfig("Medium Load", 
                "-Xms1g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC " +
                "-XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ParallelRefProcEnabled"),
                
            new JvmConfig("High Load", 
                "-Xms2g -Xmx4g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g -XX:+UseG1GC " +
                "-XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=40 -XX:+ParallelRefProcEnabled " +
                "-XX:+UseStringDeduplication")
        );
        
        // Create logs directory if it doesn't exist
        Path logsDir = Paths.get("logs");
        if (!Files.exists(logsDir)) {
            Files.createDirectory(logsDir);
        }
        
        // Test each configuration
        for (JvmConfig config : configs) {
            log.info("Testing JVM configuration: {}", config.getName());
            log.info("Parameters: {}", config.getParameters());
            
            // Build the command to run the jar with the specified JVM parameters
            String gcLogPath = "logs/gc-" + config.getName().replaceAll("\\s+", "-").toLowerCase() + ".log";
            String command = String.format("java %s -Xlog:gc*=info:file=%s:time,uptime,level,tags " +
                    "-jar %s --server.port=%d", 
                    config.getParameters(), gcLogPath, JAR_PATH, 
                    8080 + configs.indexOf(config)); // Use different ports for each test
            
            log.info("Executing command: {}", command);
            
            // Start the process
            Process process = Runtime.getRuntime().exec(command);
            
            // Read the output in a separate thread
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[{}] {}", config.getName(), line);
                    }
                } catch (IOException e) {
                    log.error("Error reading process output", e);
                }
            }).start();
            
            // Read the error output in a separate thread
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.error("[{}] {}", config.getName(), line);
                    }
                } catch (IOException e) {
                    log.error("Error reading process error output", e);
                }
            }).start();
            
            // Wait for the application to start
            log.info("Waiting for application to start...");
            TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);
            
            // Stop the process
            log.info("Stopping application...");
            process.destroy();
            
            // Wait for the process to terminate
            boolean terminated = process.waitFor(30, TimeUnit.SECONDS);
            if (!terminated) {
                log.warn("Process did not terminate gracefully, forcing termination");
                process.destroyForcibly();
            }
            
            log.info("Test completed for configuration: {}", config.getName());
            log.info("GC log saved to: {}", gcLogPath);
            log.info("----------------------------------------------------");
            
            // Wait between tests
            TimeUnit.SECONDS.sleep(5);
        }
    }
    
    // Inner class to represent a JVM configuration
    private static class JvmConfig {
        private final String name;
        private final String parameters;
        
        public JvmConfig(String name, String parameters) {
            this.name = name;
            this.parameters = parameters;
        }
        
        public String getName() {
            return name;
        }
        
        public String getParameters() {
            return parameters;
        }
    }
}
