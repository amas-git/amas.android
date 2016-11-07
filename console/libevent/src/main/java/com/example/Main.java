package com.example;

import a.m.a.s.log.S;

public class Main {
    public static void main(String[] argv) {
        System.out.println(S.padding("(1) ", "Hello World", 4));
        System.out.println(S.padding("(1) ", "Hello World", 2));
        System.out.println(S.padding("(1) ", "Hello World", 1));
    }
}
