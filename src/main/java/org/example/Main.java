package org.example;


import org.example.files.MockFile;
import org.example.files.myFileable;

public class Main {
    public static void main(String[] args) {

        MockFile test = new MockFile();

        int numberOfTestRecords = 200;

        Record[] testRecords = Record.getRandomRecords(numberOfTestRecords);

        for(int i=0;i<numberOfTestRecords;i++){
            test.saveRecord(testRecords[i]);
        }

        System.out.println(test);
        Sorting testSort = new Sorting(test,false);

        myFileable[] a = testSort.createFiboTapesFromFile(test);
        System.out.println(a[0]);
        System.out.println(a[1]);
        for(int i=0;i<3;i++) System.out.println(testSort.getNumsOfRuns()[i]);
    }
}