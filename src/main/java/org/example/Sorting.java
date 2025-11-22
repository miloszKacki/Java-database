package org.example;

import org.example.files.MockFile;
import org.example.files.TapeFile;
import org.example.files.myFileable;

import java.util.Comparator;
import java.util.stream.IntStream;

public class Sorting {
    private static final int numberOfTapes = 3;
    private static final int initialFileTapeIndex = 2;
    private static final int initialBiggerFiboTapeIndex = 0;
    private static final int initialSmallerFiboTapeIndex = 1;
    private static final int initialEmptyFiboTapeIndex = 2;
    private static final String tapeFilesLocation = "sortTapes";
    myFileable[] Tapes = new TapeFile[numberOfTapes];
    int[] numbersOfRuns = new int[numberOfTapes];


    //records are supposed to be rising towards "input"
    //r1=get; r2=get; r1.compare(r2) >= 0 is desired order
    //Likewise, when r1.compare(r2) = 1 r1 goes in first
    Comparator<Record> comparator;
    boolean descendingOrder;

    public Sorting(boolean descendingOrder){
        this.descendingOrder = descendingOrder;
    }
    public void setOrder(boolean isInDescendingOrder){
        this.descendingOrder = isInDescendingOrder;
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

    public myFileable FibosoSort(myFileable fileToSort){

        myFileable[] firstFiboTapes = createFiboTapesFromFile(fileToSort);
        Tapes[initialBiggerFiboTapeIndex] = firstFiboTapes[0];
        Tapes[initialSmallerFiboTapeIndex] = firstFiboTapes[1];
        Tapes[initialEmptyFiboTapeIndex] = new TapeFile(tapeFilesLocation +"//tapeC.bin");

        //ammounts of dummy runs, on the bigger tape
        int numOfDRuns = getDrunNum();

        //Designate tapes
        int emptyTapeIndex,smallerFiboTapeIndex,biggerFiboTapeIndex;
        emptyTapeIndex = initialFileTapeIndex;
        smallerFiboTapeIndex = initialSmallerFiboTapeIndex;
        biggerFiboTapeIndex = initialBiggerFiboTapeIndex;

        //Since we cant just put back a record on tape, we gotta store them in recsAtHand for comparison
        mergeRecs recs = new mergeRecs();

        while(IntStream.of(numbersOfRuns).sum() > 1){
            //merge tapes
            //that it will end up with less runs than biggerFiboTape
            mergeTapesFibo(biggerFiboTapeIndex,smallerFiboTapeIndex,emptyTapeIndex,numOfDRuns,recs);
            numOfDRuns = 0;
            //update tape designations
            int tmp = emptyTapeIndex;
            emptyTapeIndex = smallerFiboTapeIndex;
            smallerFiboTapeIndex = biggerFiboTapeIndex;
            biggerFiboTapeIndex = tmp;
        }

        //we use this index because the last merge was into the empty tape
        // which's index got written into smallerFiboTapeIndex during last loop iteration
        return Tapes[biggerFiboTapeIndex];
    }

    //recs at hand is needed to store compared records, since they're not on tapes
    private void mergeTapesFibo(int idxBig, int idxSmall, int idxEmpty,int dummRunsNum,mergeRecs recsAtHand){

        //Since we cant just put back a record on tape - and thus compare records that are still on tapes,
        //we gotta store them - in recsAtHand
        //Due to tape role rotation we only need the idxSmall one from the last merge
        //Also the idxEmpty record pair could be emptied and checked if empty for debug reasons ????
        if(recsAtHand.currRecs[idxSmall] == null) {
            recsAtHand.currRecs[idxSmall] = Tapes[idxSmall].getRecord();
            recsAtHand.lastRecs[idxSmall] = recsAtHand.currRecs[idxSmall];
        }
        recsAtHand.currRecs[idxBig] = Tapes[idxBig].getRecord();
        recsAtHand.lastRecs[idxBig] = recsAtHand.currRecs[idxBig];



        while(dummRunsNum > 0){
            //if empty tape pops up here, sth went wrong earlier - too many dummy runs maybe
            Tapes[idxEmpty].saveRecord(recsAtHand.currRecs[idxSmall]);
            //recsAtHand.topRecOnToTape = recsAtHand.currRecs[1];
            recsAtHand.lastRecs[idxSmall] = recsAtHand.currRecs[idxSmall];
            recsAtHand.currRecs[idxSmall] = Tapes[idxSmall].getRecord();

            if(!correctOrder(recsAtHand.lastRecs[idxSmall], recsAtHand.currRecs[idxSmall])){
                dummRunsNum -= 1;
                numbersOfRuns[idxSmall] -= 1;
                numbersOfRuns[idxEmpty] += 1;
            }
        }

        while(numbersOfRuns[idxSmall] > 0){
            mergeRunsFibo(recsAtHand,idxSmall,idxBig,idxEmpty);
            numbersOfRuns[idxSmall] -=1;
            numbersOfRuns[idxBig] -=1;
            numbersOfRuns[idxEmpty] +=1;
        }
    }

    //While(!both runs have ended)
    //  if(rBig ended) put from r2
    //  else if(rSmall ended) put from r1
    //  else compare and put the good one
    private void mergeRunsFibo(mergeRecs recs, int smallTapeIdx, int bigTapeIdx, int tapeToIdx){

        boolean rBigEnded = false, rSmallEnded = false;

        while(!(rBigEnded && rSmallEnded)){
            if(rBigEnded){
                rSmallEnded = moveRecBetweenTapes(smallTapeIdx,tapeToIdx,recs);
            }
            else if (rSmallEnded) {
                rBigEnded = moveRecBetweenTapes(bigTapeIdx,tapeToIdx,recs);
            }
            else{
                if (correctOrder(recs.currRecs[bigTapeIdx],recs.currRecs[smallTapeIdx])){
                    rBigEnded = moveRecBetweenTapes(bigTapeIdx,tapeToIdx,recs);
                }
                else{
                    rSmallEnded = moveRecBetweenTapes(smallTapeIdx,tapeToIdx,recs);
                }
            }
        }
    }

    //made to replace repeated a code block - its commented below the method body
    //returns true if a run ended with this move
    private boolean moveRecBetweenTapes(int idxFrom,int idxTo,mergeRecs recs){

        Tapes[idxTo].saveRecord(recs.currRecs[idxFrom]);
        recs.lastRecs[idxFrom] = recs.currRecs[idxFrom];

        if (!Tapes[idxFrom].isEmpty()) {
            recs.currRecs[idxFrom] = Tapes[idxFrom].getRecord();
            if (!correctOrder(recs.lastRecs[idxFrom], recs.currRecs[idxFrom]))
                return true;
        }
        else return true;
        return false;
    }
    /*
    Tapes[tapeToIdx].saveRecord(lastsCurrsRecs.currRecs[0]);
    lastsCurrsRecs.lastRecs[0] = lastsCurrsRecs.currRecs[0];
    if (!Tapes[bigTapeIdx].isEmpty()) {
        lastsCurrsRecs.currRecs[0] = Tapes[bigTapeIdx].getRecord();
            if (!correctOrder(lastsCurrsRecs.lastRecs[0], lastsCurrsRecs.currRecs[0]))
                rBigEnded = true;
        }
    else rBigEnded = true;
    */

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
    private myFileable[] createFiboTapesFromFile(myFileable fileToInsert){

        myFileable[] fiboTapes = {new TapeFile(tapeFilesLocation +"//tapeA.bin"),new TapeFile(tapeFilesLocation +"//tapeB.bin")};
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

    private int getDrunNum(){
        int[] fiboPair = {1,1};

        while(numbersOfRuns[initialBiggerFiboTapeIndex] > fiboPair[1]){
            fiboPair = increaseFiboPair(fiboPair);
        }

        if(numbersOfRuns[initialBiggerFiboTapeIndex] <= numbersOfRuns[initialSmallerFiboTapeIndex]
        && numbersOfRuns[initialBiggerFiboTapeIndex] > 1)
            fiboPair = increaseFiboPair(fiboPair);

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
        //A way of "emptying" currRecs could be useful in the future.
        public Record[] lastRecs;
        public Record[] currRecs;
        public boolean wasSet;
        public mergeRecs(){
            lastRecs = new Record[3];
            currRecs = new Record[3];
            wasSet = false;
        }
    }

}
