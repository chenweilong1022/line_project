package io.renren;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2024/3/5 13:45
 */
public class App {

    public static void main(String[] args) {

        ArrayList<Integer> i1 = CollUtil.newArrayList(1, 2, 3);
        ArrayList<Integer> i2 = CollUtil.newArrayList(1, 3);
        i1.removeAll(i2);
        System.out.println(i1);
    }
    public static void main1(String[] args) {

        List<String> strings = FileUtil.readLines("/Users/chenweilong/Downloads/3300.txt", "UTF-8");

        List<List<String>> partition = Lists.partition(strings, 450);

        int i = 0;
        for (List<String> stringList : partition) {
            i = i + 1;
            FileUtil.writeUtf8Lines(stringList,"/Users/chenweilong/Desktop/java代码/line_project/admin/src/test/java/io/renren/l_" + i + ".txt");
        }
    }
}
