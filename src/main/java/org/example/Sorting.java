package org.example;

import org.example.files.MockFile;
import org.example.files.myFileable;

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
    //r1=get; r2=get; r1.compare(r2) >= 0 is desired order
    //Likewise, when r1.compare(r2) = 1 r1 goes in first
    Comparator<Record> comparator;
    boolean descendingOrder;

    public Sorting(myFileable fileToSort, boolean descendingOrder){
        this.descendingOrder = descendingOrder;
        this.OriginFile = fileToSort;
    }

    private void emptyTape(int tapeNumber){
        while (Tapes[tapeNumber].isEmpty()){
            Tapes[tapeNumber].getRecord();
        }
        numbersOfRuns[tapeNumber] =0;
    }

    //Prolly shouldn't be used (!)
    private void insertFileIntoTape(myFileable fileToInsert,int tapeNumber){
        emptyTape(tapeNumber);
        if(fileToInsert.isEmpty()) return;

        Record record,lastRecord;

        record = fileToInsert.getRecord();
        Tapes[tapeNumber].saveRecord(record);
        numbersOfRuns[tapeNumber] +=1;

        while (!fileToInsert.isEmpty()){
            lastRecord = record;
            record = fileToInsert.getRecord();
            Tapes[tapeNumber].saveRecord(record);

            if(comparator.compare(lastRecord,record) > 0)
                numbersOfRuns[tapeNumber] +=1;
        }
    }

    public myFileable FibosoSort(myFileable fileToSort,Comparator<Record> comparator){

        myFileable[] firstFiboTapes = createFiboTapesFromFile(fileToSort);

        //ammounts of dummy runs, on the bigger tape
        int NumOfDRuns = getDRunsInfo();

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

            //assignDummyRuns(dummyRunsInfo);
        }

        //we use this index because the last merge was into the empty tape
        // witch's index got written into smallerFiboTapeIndex during last loop iteration
        return Tapes[smallerFiboTapeIndex];
    }

    private void mergeTapesFibo(int indexFromBigger, int indexFromSmaller, int indexTo){
    }

    //We want to try make it so that the bigger tape can be adjusted to fibo proportions by adding dummy runs
    //Put one run on both tapes
    //Chose a tape
    //loop:
    //Get next fibo number - initial is 1
    //Put runs on the chosen tape till you reach the fibo number
    //Chose the other tape
    //Repeat, till the file is empty
    //File we're currently working on is the bigger tape
    //Update tracked run amounts
    //Returns [biggerTape,smallerTape]
    //TODO Unpublic this. Its public for testing.
    public myFileable[] createFiboTapesFromFile(myFileable fileToInsert){

        myFileable[] fiboTapes = {new MockFile(),new MockFile()};
        int[] fiboTapeRunAmmounts = {0,0};
        Record[] lastRecords = new Record[2];
        Record currentRecord;

        if(fileToInsert.isEmpty()) return fiboTapes;
        currentRecord = fileToInsert.getRecord();
        int chosenTapeIndex = 0;

        for(int i=0;i<2;i++){
            if(fileToInsert.isEmpty()) break;
            chosenTapeIndex = i;
            lastRecords[i] = currentRecord; //for compare in the loop
            fiboTapeRunAmmounts[i] = 1;

            while(!fileToInsert.isEmpty() && correctOrder(lastRecords[i],currentRecord)){
                lastRecords[i] = currentRecord;
                fiboTapes[i].saveRecord(currentRecord);
                currentRecord = fileToInsert.getRecord();
            }
        }

        int[] fiboNumPair = {1,1};

        while(!fileToInsert.isEmpty()){

            chosenTapeIndex = (chosenTapeIndex+1)%2;
            fiboNumPair = increaseFiboPair(fiboNumPair);

            while(fiboTapeRunAmmounts[chosenTapeIndex] < fiboNumPair[1] && !fileToInsert.isEmpty()){

                //this accounts for run fusion
                if(!correctOrder(lastRecords[chosenTapeIndex],currentRecord)) fiboTapeRunAmmounts[chosenTapeIndex] +=1;

                do{
                    lastRecords[chosenTapeIndex] = currentRecord;
                    fiboTapes[chosenTapeIndex].saveRecord(currentRecord);
                    currentRecord = fileToInsert.getRecord();
                }while(!fileToInsert.isEmpty() && correctOrder(lastRecords[chosenTapeIndex],currentRecord));
            }
        }

        //accounts for the currentRecord - (last in file)
        if(fiboTapeRunAmmounts[chosenTapeIndex] == fiboNumPair[1] && !correctOrder(lastRecords[chosenTapeIndex],currentRecord)) chosenTapeIndex = (chosenTapeIndex+1)%2;
        if(fiboTapeRunAmmounts[chosenTapeIndex] == 0 || !correctOrder(lastRecords[chosenTapeIndex],currentRecord))
            fiboTapeRunAmmounts[chosenTapeIndex] +=1;
        fiboTapes[chosenTapeIndex].saveRecord(currentRecord);


        myFileable[] retArray = {fiboTapes[chosenTapeIndex],fiboTapes[(chosenTapeIndex+1)%2]};
        numbersOfRuns[initialBiggerFiboTapeIndex] = fiboTapeRunAmmounts[chosenTapeIndex];
        numbersOfRuns[initialSmallerFiboTapeIndex] = fiboTapeRunAmmounts[(chosenTapeIndex+1)%2];

        return retArray;
    }

    private int getDRunsInfo(){
        int[] fiboPair = {1,1};

        while(numbersOfRuns[initialBiggerFiboTapeIndex] < fiboPair[1]){
            fiboPair = increaseFiboPair(fiboPair);
        }

        return fiboPair[1]-numbersOfRuns[initialBiggerFiboTapeIndex];
    }
    //CorrectOrder of inserting into file
    private boolean correctOrder(Record firstOnTape,Record secondOnTape) {
        return (secondOnTape.compareTo(firstOnTape) >= 0) ^ descendingOrder;
    }

    private int[] increaseFiboPair(int[] fiboPair){
        int next = fiboPair[0] + fiboPair[1];
        fiboPair[0] = fiboPair[1];
        fiboPair[1] = next;
        return fiboPair;
    }

    public int[] getNumsOfRuns(){
        return numbersOfRuns;
    }

}
