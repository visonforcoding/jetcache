#!/bin/bash

# 确保脚本在出错时立即退出
set -e

# 源包路径和目标包路径
source_path="src/main/java/com/alicp/jetcache"
target_path="src/main/java/org/vison/cache"

# 源包名和目标包名
source_package="com.alicp.jetcache"
target_package="org.vison.cache"

# 遍历所有模块目录
for module in jetcache-core jetcache-anno jetcache-anno-api jetcache-test jetcache-support/jetcache-redis \
              jetcache-support/jetcache-redis-lettuce jetcache-support/jetcache-redis-springdata \
              jetcache-support/jetcache-redisson jetcache-starter/jetcache-autoconfigure;
do
    echo "Processing module: $module"
    
    # 检查模块是否包含Java源代码
    if [ -d "$module/$source_path" ]; then
        echo "Found Java source files in $module"
        
        # 创建目标目录结构
        mkdir -p "$module/$target_path"
        
        # 复制源文件到目标目录，保持子包结构
        find "$module/$source_path" -type f -name "*.java" | while read file;
        do
            # 计算相对于源路径的相对路径
            relative_path="${file#$module/$source_path/}"
            # 创建目标文件的父目录
            target_dir="$module/$target_path/$(dirname "$relative_path")"
            mkdir -p "$target_dir"
            # 复制文件
            cp "$file" "$target_dir/$(basename "$file")"
        done
        
        echo "Successfully copied Java files for $module"
    else
        echo "No Java source files found in $module"
    fi
done

# 更新所有Java文件中的包声明
find . -name "*.java" | while read file;
do
    # 检查文件是否在新的目录结构中
    if [[ $file == *"$target_path"* ]]; then
        # 替换包声明
        sed -i '' "s/package $source_package/package $target_package/g" "$file"
        # 替换import语句中的包名
        sed -i '' "s/import $source_package/import $target_package/g" "$file"
    fi
done

# 更新pom.xml中的groupId
pom_file="pom.xml"
echo "Updating $pom_file groupId..."
sed -i '' "s/<groupId>$source_package<\/groupId>/<groupId>$target_package<\/groupId>/g" "$pom_file"

# 更新所有模块中的pom.xml文件
find . -name "pom.xml" | grep -v "$pom_file" | while read module_pom;
do
    echo "Updating $module_pom..."
    # 更新groupId和依赖
    sed -i '' "s/<groupId>$source_package<\/groupId>/<groupId>$target_package<\/groupId>/g" "$module_pom"
    sed -i '' "s/<artifactId>$source_package/<artifactId>$target_package/g" "$module_pom"
done

echo "All directories and files have been renamed successfully!"
echo "Please note that you may need to manually clean up the old directory structure if needed."