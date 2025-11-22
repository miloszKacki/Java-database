package org.example.files;

import org.example.Record;
import org.example.exceptions.FileBrokenException;
import org.example.exceptions.FileEmptyException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SeqFile implements myFileable{

    //1 record is 12bytes, 10 records for page (for now)
    private final static int pageSize = 120;
    private final static int recordLength = 12;

    ArrayList<Record> inBuffer = new ArrayList<Record>();
    ArrayList<Record> outBuffer = new ArrayList<Record>();
    File theFile;
    FileInputStream inStream;
    FileOutputStream outStream;

    public SeqFile(String path) {
        theFile = new File(path);
        try {
            if(!theFile.exists())
                theFile.createNewFile();

            inStream = new FileInputStream(theFile);
            outStream = new FileOutputStream(theFile);
        }
        catch(FileNotFoundException e) {
            System.err.println("FNF error: " + e);
        }
        catch(IOException e) {
            System.err.println("(opening)IO error: " + e);
        }
    }

    public void close(){
        //TODO yea

        try {
            inStream.close();
            outStream.close();
        }
        catch(IOException e) {
            System.err.println("(closing)IO error: " + e);
        }
    }

    @Override
    public Record getRecord() throws FileEmptyException {
        //TODO file.length() works differently than I thought. Gotta check whats up.
        if (!outBuffer.isEmpty()){
            return outBuffer.removeFirst();
        }
        else if (theFile.length() >= pageSize){

            byte[] pageBytes = new byte[pageSize];
            ByteBuffer recBuffer = ByteBuffer.allocate(pageSize);

            try{
                if(inStream.read(pageBytes) == -1) throw new FileBrokenException();
                recBuffer.put(pageBytes);
            }
            catch(IOException e) {
                System.err.println("(reading)IO error: " + e);
            }

            recBuffer.flip();

            Record tmpRec;
            for (int i=0;i<pageSize/recordLength;i++){
                tmpRec = new Record(
                        recBuffer.getFloat(),
                        recBuffer.getFloat(),
                        recBuffer.getFloat()
                );
                outBuffer.add(tmpRec);
                //outBuffer.add(tmpRec.copy()); TODO check if this .copy() thing is needed here
            }
            return outBuffer.removeFirst();
        }
        else if(!inBuffer.isEmpty()){
            return inBuffer.removeFirst();
        }
        throw new FileEmptyException();
    }

    @Override
    public void saveRecord(Record record) {
        //TODO test this
        inBuffer.add(record);
        if(inBuffer.size() >= pageSize/recordLength){

            ByteBuffer recBuffer = ByteBuffer.allocate(pageSize);
            Record tmpRec;

            for(int i=0;i<pageSize/recordLength;i++){
                tmpRec = inBuffer.removeFirst();
                recBuffer.putFloat(tmpRec.getA());
                recBuffer.putFloat(tmpRec.getB());
                recBuffer.putFloat(tmpRec.getAngle());
            }

            byte[] bytePage = recBuffer.array();

            try {
               outStream.write(bytePage);
            }
            catch(IOException e) {
                System.err.println("(writing)IO error: " + e);
            }
        }
    }

    @Override
    public boolean isEmpty(){
        return (inBuffer.isEmpty() && outBuffer.isEmpty() && theFile.length() == 0);
    }
}
