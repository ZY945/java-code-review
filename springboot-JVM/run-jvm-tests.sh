#!/bin/bash

# JVM性能测试脚本
# 该脚本用于使用不同的JVM参数运行Spring Boot应用，并使用JMeter进行性能测试

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # 无颜色

# 配置参数
JAR_FILE="target/springboot-JVM.jar"
JMETER_HOME="/path/to/jmeter" # 请修改为您的JMeter安装路径
RESULTS_DIR="performance-results"
LOG_DIR="logs"

# 确保目录存在
mkdir -p $RESULTS_DIR
mkdir -p $LOG_DIR

# 打印标题
echo -e "${GREEN}=======================================${NC}"
echo -e "${GREEN}   Spring Boot JVM 性能测试脚本      ${NC}"
echo -e "${GREEN}=======================================${NC}"

# 检查JAR文件是否存在
if [ ! -f $JAR_FILE ]; then
    echo -e "${RED}错误: JAR文件 $JAR_FILE 不存在${NC}"
    echo -e "${YELLOW}正在构建项目...${NC}"
    mvn clean package -DskipTests
    
    if [ ! -f $JAR_FILE ]; then
        echo -e "${RED}构建失败，无法找到JAR文件。请先运行 'mvn clean package'${NC}"
        exit 1
    fi
fi

# JVM配置列表
declare -a JVM_CONFIGS=(
    "低负载配置:-Xms512m -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC"
    "中等负载配置:-Xms1g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ParallelRefProcEnabled"
    "高负载配置:-Xms2g -Xmx4g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=40 -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication"
)

# 测试计划列表
declare -a TEST_PLANS=(
    "jmeter/qps-test-plan.jmx:QPS测试"
    "jmeter/tps-test-plan.jmx:TPS测试"
    "jmeter/mixed-load-test-plan.jmx:混合负载测试"
)

# 运行测试函数
run_test() {
    local config_name=$1
    local jvm_params=$2
    local test_plan=$3
    local test_name=$4
    local port=$5
    
    # 替换测试计划中的端口
    sed -i '' "s/<stringProp name=\"Argument.value\">8080<\/stringProp>/<stringProp name=\"Argument.value\">$port<\/stringProp>/g" $test_plan
    
    echo -e "${GREEN}=======================================${NC}"
    echo -e "${GREEN}开始测试: $config_name - $test_name${NC}"
    echo -e "${GREEN}JVM参数: $jvm_params${NC}"
    echo -e "${GREEN}端口: $port${NC}"
    echo -e "${GREEN}=======================================${NC}"
    
    # GC日志文件
    local gc_log="$LOG_DIR/gc-${config_name//[[:space:]]/-}-${test_name//[[:space:]]/-}.log"
    gc_log=${gc_log//:/}
    
    # 启动应用
    echo -e "${YELLOW}启动Spring Boot应用...${NC}"
    java $jvm_params -Xlog:gc*=info:file=$gc_log:time,uptime,level,tags -jar $JAR_FILE --server.port=$port > $LOG_DIR/app-$port.log 2>&1 &
    APP_PID=$!
    
    # 等待应用启动
    echo -e "${YELLOW}等待应用启动...${NC}"
    sleep 20
    
    # 检查应用是否成功启动
    if ! curl -s http://localhost:$port/api/users/metrics > /dev/null; then
        echo -e "${RED}应用启动失败，请检查日志: $LOG_DIR/app-$port.log${NC}"
        kill -9 $APP_PID 2>/dev/null
        return 1
    fi
    
    # 运行JMeter测试
    echo -e "${YELLOW}运行JMeter测试...${NC}"
    local result_file="$RESULTS_DIR/${config_name//[[:space:]]/-}-${test_name//[[:space:]]/-}.jtl"
    result_file=${result_file//:/}
    
    $JMETER_HOME/bin/jmeter -n -t $test_plan -l $result_file
    
    # 生成HTML报告
    local report_dir="$RESULTS_DIR/${config_name//[[:space:]]/-}-${test_name//[[:space:]]/-}-report"
    report_dir=${report_dir//:/}
    
    $JMETER_HOME/bin/jmeter -g $result_file -o $report_dir
    
    # 停止应用
    echo -e "${YELLOW}停止应用...${NC}"
    kill -15 $APP_PID
    sleep 5
    
    # 确保应用已停止
    if ps -p $APP_PID > /dev/null; then
        echo -e "${RED}应用未正常停止，强制终止...${NC}"
        kill -9 $APP_PID
    fi
    
    # 恢复测试计划中的端口
    sed -i '' "s/<stringProp name=\"Argument.value\">$port<\/stringProp>/<stringProp name=\"Argument.value\">8080<\/stringProp>/g" $test_plan
    
    echo -e "${GREEN}测试完成: $config_name - $test_name${NC}"
    echo -e "${GREEN}结果保存在: $result_file${NC}"
    echo -e "${GREEN}HTML报告: $report_dir${NC}"
    echo -e "${GREEN}GC日志: $gc_log${NC}"
}

# 主测试循环
port=8080
for config in "${JVM_CONFIGS[@]}"; do
    IFS=':' read -r config_name jvm_params <<< "$config"
    
    for test_plan in "${TEST_PLANS[@]}"; do
        IFS=':' read -r test_file test_name <<< "$test_plan"
        
        run_test "$config_name" "$jvm_params" "$test_file" "$test_name" $port
        
        # 增加端口号，避免端口冲突
        port=$((port + 1))
        
        # 等待一段时间，确保系统资源释放
        sleep 10
    done
done

echo -e "${GREEN}=======================================${NC}"
echo -e "${GREEN}所有测试完成!${NC}"
echo -e "${GREEN}结果保存在: $RESULTS_DIR${NC}"
echo -e "${GREEN}日志保存在: $LOG_DIR${NC}"
echo -e "${GREEN}=======================================${NC}"

# 生成简单的比较报告
echo -e "${YELLOW}生成比较报告...${NC}"
echo "# JVM性能测试比较报告" > $RESULTS_DIR/comparison-report.md
echo "" >> $RESULTS_DIR/comparison-report.md
echo "## 测试环境" >> $RESULTS_DIR/comparison-report.md
echo "- 测试时间: $(date)" >> $RESULTS_DIR/comparison-report.md
echo "- 操作系统: $(uname -a)" >> $RESULTS_DIR/comparison-report.md
echo "" >> $RESULTS_DIR/comparison-report.md

echo "## JVM配置" >> $RESULTS_DIR/comparison-report.md
for config in "${JVM_CONFIGS[@]}"; do
    IFS=':' read -r config_name jvm_params <<< "$config"
    echo "### $config_name" >> $RESULTS_DIR/comparison-report.md
    echo "\`\`\`" >> $RESULTS_DIR/comparison-report.md
    echo "$jvm_params" >> $RESULTS_DIR/comparison-report.md
    echo "\`\`\`" >> $RESULTS_DIR/comparison-report.md
    echo "" >> $RESULTS_DIR/comparison-report.md
done

echo "## 测试结果" >> $RESULTS_DIR/comparison-report.md
echo "请查看各个测试的HTML报告以获取详细结果。" >> $RESULTS_DIR/comparison-report.md
echo "" >> $RESULTS_DIR/comparison-report.md

echo "## GC性能分析" >> $RESULTS_DIR/comparison-report.md
echo "请使用GCViewer或其他工具分析GC日志以获取更详细的GC性能信息。" >> $RESULTS_DIR/comparison-report.md
echo "" >> $RESULTS_DIR/comparison-report.md

echo -e "${GREEN}比较报告已生成: $RESULTS_DIR/comparison-report.md${NC}"
