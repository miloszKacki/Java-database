package org.example;


import org.example.files.TapeFile;

public class Main {
    public static void main(String[] args) {

        TapeFile test = new TapeFile("test.bin");

        int numberOfTestRecords = 4621;
        Record[] testRecords = Record.getRandomRecords(numberOfTestRecords);

        /*
        Record[] testRecords = {
                new Record(865694.3f, 136356.77f, 0.11628471f),
                new Record(740440.25f, 761734.0f, 0.91862684f),
                new Record(492699.16f, 677292.44f, 0.23536757f)
        };*/

        /*
        Record[] testRecords = {
                new Record(678597.7f, 604920.25f, 0.7564926f),
                new Record(536905.8f, 288768.06f, 0.32110205f),
                new Record(536714.5f, 687137.6f, 0.33067903f),
                new Record(632479.5f, 33195.496f, 0.4236811f),
                new Record(854989.7f, 825001.94f, 9.568655E-4f),
                new Record(369056.47f, 329593.9f, 1.3774606f),
                new Record(281048.78f, 500088.8f, 0.022039272f),
                new Record(614713.94f, 474476.56f, 0.92315125f),
                new Record(828702.9f, 718409.7f, 1.0321467f),
                new Record(975394.3f, 937983.2f, 0.8560452f)
        };*/

        for(Record each : testRecords){
            test.saveRecord(each);
            System.out.println(each.getA()+"f, "+each.getB()+"f, "+ each.getAngle()+"f");
        }

        test.openFile("C:\\Users\\milos\\IdeaProjects\\SBD_projekt\\inputTapes\\inTape.bin");

        Sorting testSort = new Sorting(false);

        TapeFile a = (TapeFile)testSort.FibosoSort(test);

        a.printToConsole();

    }
    //"C:\\Users\\milos\\IdeaProjects\\SBD_projekt\\inputTapes\\inTape.bin"


}