package utils;

public class Printer {

    public static void print(String msg) {
        System.out.println(msg + " in thread " + Thread.currentThread().getName());
    }
}
