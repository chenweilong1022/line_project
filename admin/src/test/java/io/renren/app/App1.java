package io.renren.app;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2024/3/24 00:21
 */
public class App1 {
    public static void main(String[] args) {

        String path = "/Users/chenweilong/Downloads/";
        String fileName = "根组  3.23.txt";

        List<String> strings = FileUtil.readLines(path + fileName, "UTF-8");

        List<String> sj = new ArrayList<>();
        List<String> lz = new ArrayList<>();

        boolean sjFlag = false;
        boolean lzFlag = false;

        boolean nameFlag = true;
        String groupName = "";

        for (String string : strings) {
            if (StrUtil.isEmpty(string.trim())) {
                continue;
            }

            if (nameFlag) {
                if (string.toLowerCase().contains("binance")) {
                    groupName = string;
                }
            }

            if (string.contains("水军") || string.contains("管理")) {
                sjFlag = true;
                lzFlag = false;
                continue;
            }
            if (string.contains("料子") || string.contains("mobile")) {
                lzFlag = true;
                sjFlag = false;
                continue;
            }


            if (sjFlag) {
                string = StrUtil.cleanBlank(string);
                string = string.replace("+", "");
                sj.add(string);
            }

            if (lzFlag) {
                if (string.startsWith("66")) {
                    lz.add(string);
                }
            }
        }

        int size = sj.size();
        sj.add(0,groupName);
        String[] split = fileName.split("\\.");
        FileUtil.writeUtf8Lines(sj,path + "24拉群/" + split[0] + "/sj("+size+")-("+groupName+").txt");
        FileUtil.writeUtf8Lines(lz,path + "24拉群/" + split[0] + "/lz("+lz.size()+")-("+groupName+").txt");
        System.out.println(lz);
        System.out.println(sj);
    }
}
