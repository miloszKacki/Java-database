package org.example;

import org.example.files.MockFile;
import org.example.files.myFileable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class Sorting {

    private static final int numberOfTapes = 3;
    private static final int initialFileTapeIndex = 2;
    private static final int initialBiggerFiboTapeIndex = 0;
    private static final int initialSmallerFiboTapeIndex = 1;
    myFileable[] Tapes = new MockFile[numberOfTapes];
    //TODO switch those two arraylists to suitable data structures
    int[] numbersOfRuns = new int[numberOfTapes];
    myFileable OriginFile;

    //records are supposed to be rising towards "input"
    //r1=get r2=get compare(r1,r2) >= 0 is desired order
    //Likewise, when compare(r1,r2) = 1 r1 goes in first
    Comparator<Record> comparator;

    public Sorting(myFileable fileToSort, Comparator<Record> comparator){
        this.comparator = comparator;
        this.OriginFile = fileToSort;
    }

    private void emptyTape(int tapeNumber){
        while (Tapes[tapeNumber].isEmpty()){
            Tapes[tapeNumber].popRecord();
        }
        numbersOfRuns[tapeNumber] =0;
    }

    private void insertFileIntoTape(myFileable fileToInsert,int tapeNumber){
        emptyTape(tapeNumber);
        if(fileToInsert.isEmpty()) return;

        Record record,lastRecord;

        record = fileToInsert.popRecord();
        Tapes[tapeNumber].saveRecord(record);
        numbersOfRuns[tapeNumber] +=1;

        while (!fileToInsert.isEmpty()){
            lastRecord = record;
            record = fileToInsert.popRecord();
            Tapes[tapeNumber].saveRecord(record);

            if(comparator.compare(lastRecord,record) > 0)
                numbersOfRuns[tapeNumber] +=1;
        }
    }

    public myFileable FibosoSort(myFileable fileToSort,Comparator<Record> comparator){

        insertFileIntoTape(fileToSort,initialFileTapeIndex);
        splitIntoRunsFibo(initialFileTapeIndex, initialBiggerFiboTapeIndex, initialSmallerFiboTapeIndex);

        //ammount of dummy runs, paired with index of a tape they're on
        int[] dummyRunsInfo = new int[numberOfTapes];
        Arrays.fill(dummyRunsInfo,0);

        assignDummyRuns(dummyRunsInfo);

        //Designate tapes
        int emptyTapeIndex,smallerFiboTapeIndex,biggerFiboTapeIndex;
        emptyTapeIndex = initialFileTapeIndex;
        smallerFiboTapeIndex = initialSmallerFiboTapeIndex;
        biggerFiboTapeIndex = initialBiggerFiboTapeIndex;

        while(IntStream.of(numbersOfRuns).sum() > 1){
            //merge tapes
            //note that the bigger fibo tape will always be left with fibonacciable run amount
            //TODO: what if so many runs on the mergedIinto tape get merged,
            //that it will end up with less runs than biggerFiboTape
            mergeTapesFibo(biggerFiboTapeIndex,smallerFiboTapeIndex,emptyTapeIndex);
            //update tape designations
            int tmp = smallerFiboTapeIndex;
            smallerFiboTapeIndex = emptyTapeIndex;
            emptyTapeIndex = biggerFiboTapeIndex;
            biggerFiboTapeIndex = tmp;

            assignDummyRuns(dummyRunsInfo);
        }

        //we use this index because the last merge was into the empty tape
        // witch's index got written into smallerFiboTapeIndex during last loop iteration
        return Tapes[smallerFiboTapeIndex];
    }

    private void assignDummyRuns(int[] dummyTapesArray){

    }

    private void mergeTapesFibo(int indexFromBigger, int indexFromSmaller, int indexTo){
    }

    //Returns the number of dummy files needed for the longer fibo tape
    private void splitIntoRunsFibo(int indexFrom, int indexToBigger, int indexToSmaller){
    }

    
}
