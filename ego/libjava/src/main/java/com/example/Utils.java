package com.example;

import a.m.a.s.tools.vmstat.ThreadScope;

public class Utils {
    public static void printHello() {
        System.out.println("Hello");
    }


    public static void main(String[] argv) {
        //ApkDiff.TEST(argv);
        ThreadScope.TEST(argv);
        //System.out.println(ThreadScope.getVersionCode("com.mobilesrepublic.appy"));
    }
}
