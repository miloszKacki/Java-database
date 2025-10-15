package org.example;


public class Main {
    public static void main(String[] args) {
        Record record1 = new Record(2,2,(float)(Math.PI/4));
        Record record2 = new Record(4,4,(float)(Math.PI/4));
        int a = (record1.compareTo(record2));
    }
}