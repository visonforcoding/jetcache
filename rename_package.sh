#!/bin/bash

# 确保脚本在出错时立即退出
source_package="org.vison.cache"
target_package="org.vison.cahce"

# 查找所有Java文件并进行处理
find . -name "*.java" | while read file; do
    # 检查文件中是否包含源包名
    if grep -q "package $source_package" "$file"; then
        echo "Processing $file..."

        # 直接在文件中修改，添加修改说明和原包信息
        sed -i '' "s/package $source_package/package // Modified: Package renamed from $source_package to $target_package\
// Original package: &\
package $target_package/" "$file"

        echo "Done processing $file"
    fi
done

# 更新pom.xml中的groupId
pom_file="pom.xml"
echo "Updating $pom_file groupId..."
sed -i '' "s/<groupId>$source_package<\/groupId>/<groupId>$target_package<\/groupId>/g" "$pom_file"

# 更新所有模块中的pom.xml文件
find . -name "pom.xml" | grep -v "$pom_file" | while read module_pom; do
    echo "Updating $module_pom..."
    # 更新groupId和依赖
    sed -i '' "s/<groupId>$source_package<\/groupId>/<groupId>$target_package<\/groupId>/g" "$module_pom"
    sed -i '' "s/<artifactId>$source_package/<artifactId>$target_package/g" "$module_pom"
done

echo "All files have been processed successfully!"