#!/bin/bash

# 定义颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 定义模块
MODULE="rpc-core"

# 获取脚本所在目录作为项目根目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"

# 定义源目录和目标目录
SOURCE_DIR="${MODULE}/target/generated-sources/protobuf/java/com/web/rpc/core/protocol/proto"
TARGET_DIR="${MODULE}/src/main/java/com/web/rpc/core/protocol/proto"

echo -e "${YELLOW}开始生成和移动Protobuf文件...${NC}"

# 第1步：确认当前目录
echo -e "${GREEN}当前目录: $(pwd)${NC}"

# 第2步：使用Maven生成protobuf文件
echo -e "${YELLOW}正在使用Maven生成Protobuf文件...${NC}"
mvn -pl ${MODULE} clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}Maven构建失败${NC}"
    exit 1
fi
echo -e "${GREEN}Maven构建成功${NC}"

# 第3步：检查源目录是否存在
if [ ! -d "${SOURCE_DIR}" ]; then
    echo -e "${RED}源目录不存在: ${SOURCE_DIR}${NC}"
    exit 1
fi
echo -e "${GREEN}源目录存在: ${SOURCE_DIR}${NC}"

# 第4步：创建目标目录（如果不存在）
mkdir -p "${TARGET_DIR}"
echo -e "${GREEN}已创建目标目录: ${TARGET_DIR}${NC}"

# 第5步：移动文件
echo -e "${YELLOW}正在移动文件...${NC}"
cp -R "${SOURCE_DIR}"/* "${TARGET_DIR}/"
if [ $? -ne 0 ]; then
    echo -e "${RED}移动文件失败${NC}"
    exit 1
fi
echo -e "${GREEN}文件移动成功${NC}"

# 列出移动的文件
echo -e "${YELLOW}已移动以下文件:${NC}"
ls -la "${TARGET_DIR}"

# 清理target目录，避免重复类问题
echo -e "${YELLOW}正在清理target目录...${NC}"
mvn -pl ${MODULE} clean
if [ $? -ne 0 ]; then
    echo -e "${RED}Maven clean失败${NC}"
    exit 1
fi
echo -e "${GREEN}Maven clean成功${NC}"

echo -e "${GREEN}Protobuf文件生成和移动完成!${NC}"
echo -e "${YELLOW}注意: 请重新打开IDE以刷新项目结构${NC}"
