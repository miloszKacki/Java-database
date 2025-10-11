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
        int runEnded;

        //Merge until one of the runs ends
        while(true){
            if(comparator.compare(record1,record2) > 0){

                Tapes.get(indexTo).saveRecord(record1);

                //if this was the last record in file, just get the run from the other file
                if (Tapes.get(indexFrom1).isEmpty()){
                    runEnded = 1;
                    break;
                }

                record1Last = record1;
                record1 = Tapes.get(indexFrom1).popRecord();

                // if record is less than the last record on tape, the run has ended on the tape
                // put back the first record of the new run,
                // and signal to just get the rest of the run from the other tape (runEnded)
                if(comparator.compare(record1,record1Last) < 0){
                    Tapes.get(indexFrom1).saveRecord(record1);
                    runEnded = 1;
                    break;
                }
            }
            else{
                Tapes.get(indexTo).saveRecord(record2);

                if (Tapes.get(indexFrom2).isEmpty()){
                    runEnded = 2;
                    break;
                }

                record2Last = record2;
                record2 = Tapes.get(indexFrom2).popRecord();

                if(comparator.compare(record2,record2Last) < 0){
                    Tapes.get(indexFrom2).saveRecord(record2);
                    runEnded = 2;
                    break;
                }
            }
        }

        //put the rest of the not ended run in the destination file
        while(true){
            //checking this inside of the loop is suboptimal but cleaner
            if (runEnded == 1){
                Tapes.get(indexTo).saveRecord(record2);

                if (Tapes.get(indexFrom2).isEmpty()) return;

                record2Last = record2;
                record2 = Tapes.get(indexFrom2).popRecord();

                if(comparator.compare(record2,record2Last) < 0){
                    Tapes.get(indexFrom2).saveRecord(record2);
                    return;
                }
            }
            else{
                Tapes.get(indexTo).saveRecord(record1);

                if (Tapes.get(indexFrom1).isEmpty()) return;

                record1Last = record1;
                record1 = Tapes.get(indexFrom1).popRecord();

                if(comparator.compare(record1,record1Last) < 0){
                    Tapes.get(indexFrom1).saveRecord(record1);
                    return;
                }
            }
        }
    }


}
