package collection.map_s;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author dongfeng
 * @date 2022/8/13 14:50
 * map接口特点
 * 1. 用于存储任意键值对（key - value）
 * 2. 键：无序、无下标、不允许重复（唯一）
 * 3. 值：无序、无下标、允许重复
 * 4.键值对这里可以理解为张三是人，李四也是人，前键后值
 */
public class map_demo {
    public static void main(String[] args) {
        //创建Map集合
        Map<String, String> map = new HashMap<>();


        // 1. 添加元素
        map.put("cike", "dongfeng");
        map.put("lifashi", "dongfeng");
        map.put("cike", "dongfeng1hao");//会替换前面的值
        map.put("lvshi", "luoxiang");//{lvshi=luoxiang, cike=dongfeng1hao, lifashi=dongfeng}
        System.out.println(map);

        // 2. 删除
        map.remove("cike");
        System.out.println(map);//{lvshi=luoxiang, lifashi=dongfeng}

        // 3.根据健返回值
        map.get("lifashi");//dongfeng

        // 4. 遍历
        // 4.1 使用KeySet()所有Key的set集合
        Set<String> keyset = map.keySet(); // [lvshi, lifashi]

        // 4.2获取值的集合
        Collection<String> values = map.values();//[luoxiang, dongfeng]

        // 4.3 使用entrySet()
        Set<Map.Entry<String, String>> entries = map.entrySet();//[lvshi=luoxiang, lifashi=dongfeng]
    }
}
