package org.example;

import java.util.ArrayList;
import java.util.Comparator;

public class Sorting {
    static final int number_of_tapes = 3;
    ArrayList<myFileable> Tapes;
    //TODO switch those two arraylists to suitable data structures
    ArrayList<Integer> runsAmmounts;
    myFileable OriginFile;
    Comparator<Record> comparator;


    public Sorting(myFileable fileToSort, Comparator<Record> comparator){
        this.comparator = comparator;
        this.OriginFile = fileToSort;
        Tapes = new ArrayList<>();
    }

    private void emptyTape(int tapeNumber){
        while (!Tapes.get(tapeNumber).isEmpty()){
            Tapes.get(tapeNumber).popRecord();
        }
    }

    private void insertFileIntoTape(myFileable fileToInsert,int tapeNumber){
        while (!fileToInsert.isEmpty()){
            Tapes.get(tapeNumber).saveRecord(fileToInsert.popRecord());
        }
    }

    private void mergeTapes(int indexFrom1, int indexFrom2, int indexTo){

    }

    //Method "takes" a single run from two files and merges them into one run on the destination file
    //this method should not be called if one of the "from" tapes is empty TODO Exception?
    //TODO runs counting
    private void mergeRuns(int indexFrom1, int indexFrom2, int indexTo){

        Record record1,record2, record1Last, record2Last;

        record1 = Tapes.get(indexFrom1).popRecord();
        record2 = Tapes.get(indexFrom2).popRecord();

        //used to signal that one of the runs has ended
        //and on which tape did this occur.
        //"-1" means no run ended yet
        int runEnded = -1;

        //Merging
        while(true){
            if(runEnded == 2 || comparator.compare(record1,record2) > 0){

                Tapes.get(indexTo).saveRecord(record1);

                //if this was the last record in file,
                //get the run from the other file or end merging
                if (Tapes.get(indexFrom1).isEmpty()){
                    //"the run has ended and if the other one did too, stop the merge"
                    if (runEnded == -1){
                        runEnded = 1;
                        continue;
                    }
                    break;
                }

                record1Last = record1;
                record1 = Tapes.get(indexFrom1).popRecord();

                // if record is less than the last record on tape, the run has ended on the tape
                // put back the first record of the new run,
                // get the run from the other file or end merging
                if(comparator.compare(record1,record1Last) < 0){
                    Tapes.get(indexFrom1).saveRecord(record1);
                    if (runEnded == -1){
                        runEnded = 1;
                        continue;
                    }
                    break;
                }
            }
            else{
                Tapes.get(indexTo).saveRecord(record2);

                if (Tapes.get(indexFrom2).isEmpty()){
                    if (runEnded == -1){
                        runEnded = 2;
                        continue;
                    }
                    break;
                }

                record2Last = record2;
                record2 = Tapes.get(indexFrom2).popRecord();

                if(comparator.compare(record2,record2Last) < 0){
                    Tapes.get(indexFrom2).saveRecord(record2);

                    if (runEnded == -1){
                        runEnded = 2;
                        continue;
                    }
                    break;
                }
            }
        }



    }


}
