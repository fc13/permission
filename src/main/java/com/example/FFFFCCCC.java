package com.example;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

public class FFFFCCCC {
    public static void main(String[] args) {
//        String str = "abc";
//        System.out.println(str.indexOf("a"));
        // 求n的阶乘
//        System.out.println(f(5));
        //练习一：每 3 个可乐盖可兑换 1 瓶子可乐，求买 n 瓶可乐最终可获得的可乐瓶子数。
//        System.out.println(coleNumber(5));
        //练习二：从键盘接收一个文件夹路径，统计该文件夹大小
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("请输入文件夹路径：");
//        while (true) {
//            String path = scanner.nextLine();
//            System.out.println(getDirectorySize(new File(path)));
//        }
        //练习三：从键盘接收一个文件夹路径，删除该文件夹；(删除要慎重，因为删除的内容不经过回收站)
        //练习四：从键盘接收两个文件夹路径，把其中一个文件夹中(包含内容)拷贝到另一个文件夹中
        //练习五：从键盘接收一个文件夹路径，把文件夹中的所有文件以及文件夹的名字按层级打印
        /*
         * 练习六：斐波那契数列(数组求跟递归求两种方法)
         * 故事得从西元1202年说起，话说有一位意大利青年，名叫斐波那契。
         * 在他的一部著作中提出了一个有趣的问题：假设一对刚出生的小兔，一个月后就能长成大兔，再过一个月就能生下一对小兔，并且此后每个月都生下一对小兔
         * 问：一对刚出生的兔子，一年内繁殖成多少对兔子？
         * 1  1  2  3  5  8  13  21  34 ......
         * /
        /*练习七：约瑟夫环
        故事：从前有个叫约瑟夫环的国王，国王有一天很高兴，就把监狱里面的500个死囚犯带出来，说：“你们开始排队，从1开始以此报数，若是3的倍数就直接拉出去给杀掉，最后剩余1个人，无罪释放。”
        问题：键盘输入人数，然后进排序，从1开始以此报数，若是3的倍数的人会被杀掉，后面的人接着报数。
        例如：10个人
        1 2 3 4 5 6 7 8 9 10
        最后只有一个人生还，就是 4。*/
//        System.out.println(yuesefu(10));
        //练习八：一个人赶着鸭子去每个村庄卖，每经过一个村子卖去所赶鸭子的一半又一只。这样他经过了七个村子后还剩两只鸭子，问他出发时共赶多少只鸭子？经过每个村子卖出多少只鸭子？
        //练习九：角谷定理。输入一个自然数，若为偶数，则把它除以2，若为奇数，则把它乘以3加1。经过如此有限次运算后，总可以得到自然数值1。求经过多少次可得到自然数1。
    }

    private static int f(int n) {
        if (1 == n)
            return 1;
        else
            return n * f(n - 1);
    }

    private static int coleNumber(int n) {
        if (n < 3) {
            return n;
        } else {
            return (n - n % 3) + coleNumber(n / 3 + n % 3);
        }
    }

    private static long getDirectorySize(File file) {
        long sumSize = 0;
        if (file == null || !file.exists()) {
            return sumSize;
        }
        if (!file.isDirectory()) {
            sumSize += file.length();
        }else {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                sumSize += getDirectorySize(file1);
            }
        }
        return sumSize;
    }

    public static int yuesefu(int num){
        ArrayList<Integer> list = new ArrayList<>();	//创建集合存储1到num对象
        for (int i = 1; i <= num; i++) {				//将1到num存储在集合中
            list.add(i);
        }
        int count = 1;									//用来数数的，只要是3的倍数就杀人
        for (int i = 0;  list.size() != 1; i++) {	//只要集合中人数超过1，就要不断地杀
            if(i == list.size()){						//如果i增长到集合最大的索引+1时
                i = 0;									//重新归零
            }
            if(count % 3 == 0){							//如果是3的倍数就杀人
                list.remove(i--);
            }
            count++;
        }
        return list.get(0);
    }
}
