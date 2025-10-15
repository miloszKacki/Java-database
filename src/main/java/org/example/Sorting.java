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
    boolean descendingOrder;

    public Sorting(myFileable fileToSort, boolean descendingOrder){
        this.descendingOrder = descendingOrder;
        this.OriginFile = fileToSort;
    }

    private void emptyTape(int tapeNumber){
        while (Tapes[tapeNumber].isEmpty()){
            Tapes[tapeNumber].popRecord();
        }
        numbersOfRuns[tapeNumber] =0;
    }

    //Prolly shouldn't be used (!)
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

        insertFileIntoTapeFibo(fileToSort,initialBiggerFiboTapeIndex,initialSmallerFiboTapeIndex);

        //ammounts of dummy runs, on tapes with selected index
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

    /*
    //TODO Ask prof wtf he ment in his lecture slideshow
    private int[] assignDummyRuns(int biggetFiboTapeIndex, int smallerFiboTapeIndex){
        if ()
        int bestBigFiboSize = numbersOfRuns[smallerFiboTapeIndex]
    }*/

    private void mergeTapesFibo(int indexFromBigger, int indexFromSmaller, int indexTo){
    }

    //We want to try make it so that the bigger tape can be adjusted to fibo proportions by adding dummy runs
    //Get a pair of ints that follow the proportions
    //put the runs into smaller tape
    //put the runs into larger tape
    //repeat
    //we hope that file ends on the big tape's "turn"
    //TODO maybe move some runs around at the end if it doesnt happen?
    private void insertFileIntoTapeFibo(myFileable fileToInsert,int bigTapeIndex, int smallTapeIndex){
        emptyTape(bigTapeIndex);
        emptyTape(smallTapeIndex);
        numbersOfRuns[bigTapeIndex] =0;
        numbersOfRuns[smallTapeIndex] =0;

        if(fileToInsert.isEmpty()) return;

        Record record,lastRecord;
        int[] fiboPair = {1,1};

        record = fileToInsert.popRecord();
        Tapes[bigTapeIndex].saveRecord(record);
        numbersOfRuns[bigTapeIndex] +=1;

        while(!fileToInsert.isEmpty()){

            while(numbersOfRuns[smallTapeIndex] < fiboPair[0]){
                if(fileToInsert.isEmpty()) break;
                //Get record from file
                lastRecord = record;
                record = fileToInsert.popRecord();
                //Put record on file -keep track of the
                Tapes[smallTapeIndex].saveRecord(record);
                if ((record.compareTo(lastRecord) < 0)^(descendingOrder)) numbersOfRuns[smallTapeIndex] += 1;
            }

            while(numbersOfRuns[bigTapeIndex] < fiboPair[1]){
                if(fileToInsert.isEmpty()) break;

                lastRecord = record;
                record = fileToInsert.popRecord();

                Tapes[bigTapeIndex].saveRecord(record);
                if ((record.compareTo(lastRecord) < 0)^(descendingOrder)) numbersOfRuns[bigTapeIndex] += 1;
            }

            increaseFiboPair(fiboPair);
        }
    }

    private void increaseFiboPair(int[] fiboPair){
        int next = fiboPair[0] + fiboPair[1];
        fiboPair[0] = fiboPair[1];
        fiboPair[1] = next;
    }

}
