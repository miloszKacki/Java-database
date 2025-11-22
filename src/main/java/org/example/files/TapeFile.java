package org.example.files;

import org.example.Record;
import org.example.exceptions.FileBrokenException;
import org.example.exceptions.FileEmptyException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TapeFile implements myFileable{

    //1 record is 12bytes, 10 records for page (for now)
    private final static int pageSize = 120;
    private final static int recordLength = 12;

    File theFile;

    ArrayList<Record> fileBuffer;
    int fileReadingIdx;
    boolean inWritingMode;
    FileInputStream inStream;
    FileOutputStream outStream;

    //Doesnt open a file at path, but deletes it.
    //For opening new Files openFile should be used
    public TapeFile(String path) {
        theFile = new File(path);
        try {
            if(theFile.exists()){
                theFile.delete();
                theFile.createNewFile();
            }

            inWritingMode = true;
            outStream = new FileOutputStream(theFile);

        }
        catch(FileNotFoundException e) {
            System.err.println("FNF error: " + e);
        }
        catch(IOException e) {
            System.err.println("IO error: " + e);
        }

        fileBuffer = new ArrayList<Record>();
        fileReadingIdx = 0;

    }

    //Resets this TapeFile object and opens the file at specified pathname
    public void openFile(String path){
        File newFile = new File(path);
        if (newFile.exists()){
            theFile.delete();
            theFile = newFile;
            fileReadingIdx = 0;
            fileBuffer = new ArrayList<Record>();
            inWritingMode = true;

            try {
                //note we dont create inStream in the constructor
                if(inStream != null)inStream.close();
                outStream.close();
                outStream = new FileOutputStream(theFile);
            }
            catch (IOException e){
                System.err.println("IO error: " + e);
            }
        }
    }

    @Override
    public Record getRecord() throws FileEmptyException {

        if(inWritingMode)
            switchToReadingMode();


        if (!fileBuffer.isEmpty()){
            return fileBuffer.removeFirst();
        }
        else if (theFile.length() > fileReadingIdx){

            int readlength = pageSize;

            if(theFile.length() - fileReadingIdx < pageSize)
                readlength = (int)(theFile.length() - fileReadingIdx);

            if (readlength%recordLength != 0) throw new FileBrokenException();

            byte[] pageBytes = new byte[readlength];
            ByteBuffer recBuffer = ByteBuffer.allocate(readlength);


            try{
                inStream.read(pageBytes,0,readlength);
                recBuffer.put(pageBytes);
                fileReadingIdx += readlength;
            }
            catch(IOException e) {
                System.err.println("IO error: " + e);
            }

            recBuffer.flip();

            Record tmpRec;
            for (int i=0;i<readlength/recordLength;i++){
                tmpRec = new Record(
                        recBuffer.getFloat(),
                        recBuffer.getFloat(),
                        recBuffer.getFloat()
                );
                fileBuffer.add(tmpRec);
                //outBuffer.add(tmpRec.copy()); TODO check if this .copy() thing is needed here
            }
            return fileBuffer.removeFirst();
        }

        throw new FileEmptyException();
    }

    @Override
    public void saveRecord(Record record) {
        //TODO test this
        if(!inWritingMode)
            switchToWritingMode();

        fileBuffer.add(record);

        if(fileBuffer.size() >= pageSize/recordLength){

            ByteBuffer recBuffer = ByteBuffer.allocate(pageSize);
            Record tmpRec;

            for(int i=0;i<pageSize/recordLength;i++){
                tmpRec = fileBuffer.removeFirst();
                recBuffer.putFloat(tmpRec.getA());
                recBuffer.putFloat(tmpRec.getB());
                recBuffer.putFloat(tmpRec.getAngle());
            }

            byte[] bytePage = recBuffer.array();

            try {
                outStream.write(bytePage);
            }
            catch(IOException e) {
                System.err.println("IO error: " + e);
            }
        }
    }

    private void switchToWritingMode() {
        if (inWritingMode) return;

        try {
            inStream.close();

            theFile.delete();
            theFile.createNewFile();

            outStream = new FileOutputStream(theFile);
        }
        catch(IOException e){
            System.err.println("IO error: " + e);
        }
        inWritingMode = true;
    }

    private void switchToReadingMode(){
        if (!inWritingMode) return;

        ByteBuffer recBuffer = ByteBuffer.allocate(fileBuffer.size()*12);
        Record tmpRec;

        while(!fileBuffer.isEmpty()){
            tmpRec = fileBuffer.removeFirst();
            recBuffer.putFloat(tmpRec.getA());
            recBuffer.putFloat(tmpRec.getB());
            recBuffer.putFloat(tmpRec.getAngle());
        }

        byte[] bytePage = recBuffer.array();

        try {
            outStream.write(bytePage);
            outStream.close();
            inStream = new FileInputStream(theFile);
        }
        catch(IOException e) {
            System.err.println("IO error: " + e);
        }

        fileReadingIdx = 0;

        inWritingMode = false;
    }

    @Override
    public boolean isEmpty() {
        if (inWritingMode){
            return (theFile.length() == 0 && fileBuffer.isEmpty());
        }
        else return (fileReadingIdx >= theFile.length() && fileBuffer.isEmpty());
    }

    @Override
    public String toString() {
        return "Tape file :"+ theFile.getPath();
    }

    //prints all the records from This to console
    public void printToConsole(){

        System.out.println("________ Printing " + theFile.getPath() + " to console. ________");

        try {
            FileInputStream printStream = new FileInputStream(theFile);

            long recIdx = 0;
            Record pRec = new Record(1f,1f,1f);
            ByteBuffer pBuff = ByteBuffer.allocate(recordLength);
            byte[] pBytes = new byte[recordLength];

            if (!inWritingMode){
                recIdx = printBuffer(recIdx);
            }

            //this if is not a return, cuz we still may want to return the file buffer after the loop
            if (theFile.length() > 0){
                while(printStream.read(pBytes) == recordLength){
                    pBuff.clear();
                    pBuff.put(pBytes);
                    pBuff.flip();

                    pRec.setA(pBuff.getFloat());
                    pRec.setB(pBuff.getFloat());
                    pRec.setAngle(pBuff.getFloat());
                    printRecordFancy(pRec,recIdx);
                    recIdx += 1;
                }

            }

            if (inWritingMode){
                recIdx = printBuffer(recIdx);
            }

            printStream.close();
        }
        catch(FileNotFoundException e){
            System.err.println("FNF error: " + e);
        }
        catch(IOException e) {
            System.err.println("IO error: " + e);
        }


    }

    private long printBuffer(long currentIdx){
        for (Record each : fileBuffer){
            printRecordFancy(each,currentIdx);
            currentIdx += 1;
        }
        return currentIdx;
    }

    private void printRecordFancy(Record rec, long idx){
        String numberFormat = "%.5g";
        System.out.println("Record number: "+idx+
                " A: "+ String.format(numberFormat,rec.getA())+
                " B: "+ String.format(numberFormat,rec.getB())+
                " Angle: "+ String.format(numberFormat,rec.getAngle())+
                " Field: "+ String.format(numberFormat,rec.getField())
        );

    }

}
