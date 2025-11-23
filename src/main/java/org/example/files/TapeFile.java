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

    public static int
            readCount =0,
            saveCount =0,
            pageReadCount =0,
            pageSaveCount =0;

    private File theFile;

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

    @Override
    public Record getRecord() throws FileEmptyException {

        readCount +=1;

        if(inWritingMode)
            switchToReadingMode();


        if (!fileBuffer.isEmpty()){
            return fileBuffer.removeFirst();
        }
        else if (theFile.length() > fileReadingIdx){

            pageReadCount += 1;

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
            }
            return fileBuffer.removeFirst();
        }

        throw new FileEmptyException();
    }

    @Override
    public void saveRecord(Record record) {

        saveCount += 1;

        if(!inWritingMode)
            switchToWritingMode();

        fileBuffer.add(record);

        if(fileBuffer.size() >= pageSize/recordLength){

            pageSaveCount += 1;

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

    @Override
    public void printToConsole(){

        System.out.println("________ Printing " + theFile.getPath() + " to console. ________");

        try {
            FileInputStream printStream = new FileInputStream(theFile);
            if(!inWritingMode && fileReadingIdx >0){
                byte[] alreadyRead = new byte[fileReadingIdx];
                printStream.read(alreadyRead);
            }

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

    //longs instead of ints, because of where its used
    private long printBuffer(long currentIdx){
        for (Record each : fileBuffer){
            printRecordFancy(each,currentIdx);
            currentIdx += 1;
        }
        return currentIdx;
    }

    private static final String numberPrintFormat = "%.5g";
    private void printRecordFancy(Record rec, long idx){
        System.out.println("Record number: "+idx+
                " A: "+ String.format(numberPrintFormat,rec.getA())+
                " B: "+ String.format(numberPrintFormat,rec.getB())+
                " Angle: "+ String.format(numberPrintFormat,rec.getAngle())+
                " Field: "+ String.format(numberPrintFormat,rec.getField())
        );

    }

    public static boolean saveTapeFileToElsewhere(String path,TapeFile file){
        File fileOut = new File(path);
        try {
            if(fileOut.exists()){
                fileOut.delete();
                fileOut.createNewFile();
            }
            FileOutputStream fOutStream = new FileOutputStream(fileOut);

            FileInputStream fInStream = new FileInputStream(file.theFile);
            if(!file.inWritingMode && file.fileReadingIdx >0){
                byte[] alreadyRead = new byte[file.fileReadingIdx];
                fInStream.read(alreadyRead);
            }

            long recIdx = 0;
            Record fRec = new Record(1f,1f,1f);
            ByteBuffer fBuff = ByteBuffer.allocate(recordLength);
            byte[] fBytes = new byte[recordLength];

            if (!file.inWritingMode){
                for (Record each : file.fileBuffer){
                    fBuff.putFloat(each.getA());
                    fBuff.putFloat(each.getB());
                    fBuff.putFloat(each.getAngle());
                    fOutStream.write(fBuff.array());
                    fBuff.clear();
                }
            }

            //this if is not a return, cuz we still may want to return the file buffer after the loop
            if (file.theFile.length() > 0){
                while(fInStream.read(fBytes) == recordLength){
                    fBuff.clear();
                    fBuff.put(fBytes);
                    fOutStream.write(fBuff.array());
                }
            }

            fBuff.clear();
            if (file.inWritingMode){
                for (Record each : file.fileBuffer){
                    fBuff.putFloat(each.getA());
                    fBuff.putFloat(each.getB());
                    fBuff.putFloat(each.getAngle());
                    fOutStream.write(fBuff.array());
                    fBuff.clear();
                }
            }

            fInStream.close();
            fOutStream.close();
            return true;
        }
        catch(FileNotFoundException e){
            System.err.println("FNF error: " + e);
        }
        catch(IOException e) {
            System.err.println("IO error: " + e);
        }

        return false;
    }

    //creates file in writing mode and saves all records from the file specified by the pathFrom
    public static TapeFile readFromTapeFile(String pathTo,String pathFrom){
        TapeFile retFile = new TapeFile(pathTo);
        File fromFile = new File(pathFrom);

        try {
            if(!fromFile.exists()){
                throw new FileNotFoundException();
            }

            FileInputStream fInStream = new FileInputStream(fromFile);

            ByteBuffer fBuff = ByteBuffer.allocate(recordLength);
            byte[] fBytes = new byte[recordLength];

            while(fInStream.read(fBytes) == recordLength){
                fBuff.clear();
                fBuff.put(fBytes);
                fBuff.flip();

                retFile.saveRecord(new Record(
                        fBuff.getFloat(),
                        fBuff.getFloat(),
                        fBuff.getFloat()
                ));
            }

            fInStream.close();
        }
        catch(FileNotFoundException e){
            System.err.println("FNF error: " + e);
        }
        catch(IOException e) {
            System.err.println("IO error: " + e);
        }

        return retFile;
    }

}
