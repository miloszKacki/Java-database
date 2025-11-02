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
        Tapes[initialBiggerFiboTapeIndex] = firstFiboTapes[0];
        Tapes[initialSmallerFiboTapeIndex] = firstFiboTapes[1];

        //ammounts of dummy runs, on the bigger tape
        int NumOfDRuns = getDRunsInfo();

        //Designate tapes
        int emptyTapeIndex,smallerFiboTapeIndex,biggerFiboTapeIndex;
        emptyTapeIndex = initialFileTapeIndex;
        smallerFiboTapeIndex = initialSmallerFiboTapeIndex;
        biggerFiboTapeIndex = initialBiggerFiboTapeIndex;

        while(IntStream.of(numbersOfRuns).sum() > 1){
            //merge tapes
            //that it will end up with less runs than biggerFiboTape
            //mergeTapesFibo(biggerFiboTapeIndex,smallerFiboTapeIndex,emptyTapeIndex);
            //update tape designations
            int tmp = smallerFiboTapeIndex;
            smallerFiboTapeIndex = emptyTapeIndex;
            emptyTapeIndex = biggerFiboTapeIndex;
            biggerFiboTapeIndex = tmp;
        }

        //we use this index because the last merge was into the empty tape
        // witch's index got written into smallerFiboTapeIndex during last loop iteration
        return Tapes[smallerFiboTapeIndex];
    }

    private void mergeTapesFibo(int indexFromBigger, int indexFromSmaller, int indexTo,int dummyRunsNumber){

        mergeRecs recs = new mergeRecs();
        // here recs.someRecs[x] x = 0 for bigger tape, x = 1 for smaller tape
        recs.currRecs[0] = Tapes[indexFromBigger].getRecord();
        recs.currRecs[1] = Tapes[indexFromSmaller].getRecord();

        recs.lastRecs[0] = recs.currRecs[0];
        recs.lastRecs[1] = recs.currRecs[1];

        while(dummyRunsNumber > 0){
            //if empty tape pops up here, sth went wrong earlier - too much dummy runs maybe
            Tapes[indexTo].saveRecord(recs.currRecs[1]);
            recs.topRecOnToTape = recs.currRecs[1];
            recs.lastRecs[1] = recs.currRecs[1];
            recs.currRecs[1] = Tapes[indexFromSmaller].getRecord();

            if(correctOrder(recs.lastRecs[1], recs.currRecs[1])){
                dummyRunsNumber -= 1;
                numbersOfRuns[indexFromSmaller] -= 1;
                numbersOfRuns[indexTo] += 1;
            }
        }

        while(!Tapes[indexFromSmaller].isEmpty()){
            mergeRunsFibo(recs,indexFromSmaller,indexFromBigger,indexTo);
            numbersOfRuns[indexFromSmaller] -=1;
            numbersOfRuns[indexFromBigger] -=1;
            numbersOfRuns[indexTo] +=1;
        }
    }

    //While(any of two runs has not ended)
    //  if(r1 ended) put from r2
    //  else if(r2 ended) put from r1
    //  else compare and put the good one
    private void mergeRunsFibo(mergeRecs lastsCurrsRecs, int smallTapeIdx, int bigTapeIdx, int tapeToIdx){

        //0 is big tape 1 is small tape
        lastsCurrsRecs.lastRecs[0] = lastsCurrsRecs.currRecs[0];
        lastsCurrsRecs.lastRecs[1] = lastsCurrsRecs.currRecs[1];

        while((!Tapes[bigTapeIdx].isEmpty() && correctOrder(lastsCurrsRecs.lastRecs[0],lastsCurrsRecs.currRecs[0])) ||
                (!Tapes[smallTapeIdx].isEmpty() && correctOrder(lastsCurrsRecs.lastRecs[1],lastsCurrsRecs.currRecs[1])))
        {
            if(Tapes[bigTapeIdx].isEmpty() || !correctOrder(lastsCurrsRecs.lastRecs[0],lastsCurrsRecs.currRecs[0])){
                Tapes[tapeToIdx].saveRecord(lastsCurrsRecs.currRecs[1]);
                lastsCurrsRecs.lastRecs[1] = lastsCurrsRecs.currRecs[1];
                lastsCurrsRecs.currRecs[1] = Tapes[smallTapeIdx].getRecord();
            }
            else if (Tapes[smallTapeIdx].isEmpty() || !correctOrder(lastsCurrsRecs.lastRecs[1],lastsCurrsRecs.currRecs[1])) {
                Tapes[tapeToIdx].saveRecord(lastsCurrsRecs.currRecs[0]);
                lastsCurrsRecs.lastRecs[0] = lastsCurrsRecs.currRecs[0];
                lastsCurrsRecs.currRecs[0] = Tapes[bigTapeIdx].getRecord();
            }
            else{
                if (correctOrder(lastsCurrsRecs.currRecs[0],lastsCurrsRecs.currRecs[1])){
                    Tapes[tapeToIdx].saveRecord(lastsCurrsRecs.currRecs[0]);
                    lastsCurrsRecs.lastRecs[0] = lastsCurrsRecs.currRecs[0];
                    lastsCurrsRecs.currRecs[0] = Tapes[bigTapeIdx].getRecord();
                }
                else{
                    Tapes[tapeToIdx].saveRecord(lastsCurrsRecs.currRecs[1]);
                    lastsCurrsRecs.lastRecs[1] = lastsCurrsRecs.currRecs[1];
                    lastsCurrsRecs.currRecs[1] = Tapes[smallTapeIdx].getRecord();
                }
            }
        }
    }

    //We want to try make it so that the bigger tape can be adjusted to fibo proportions by adding dummy runs
    //Chose a tape
    //Put one run on both tapes
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

                //this "if" accounts for run fusion
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

    private class mergeRecs{
        public Record[] lastRecs;
        public Record[] currRecs;
        public Record topRecOnToTape;
        public mergeRecs(){
            lastRecs = new Record[2];
            currRecs = new Record[2];
        }
    }

}
