package org.example.files;

import org.example.Record;
import org.example.exceptions.FileEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class SeqFile implements myFileable{

    //1 record is 12bytes, 10 records for page (for now)
    private final static int pageSize = 120;

    ArrayList<Record> inBuffer = new ArrayList<Record>();
    ArrayList<Record> outBuffer = new ArrayList<Record>();
    File theFile;
    FileInputStream inStream;
    FileOutputStream outStream;

    public SeqFile(String path) {

    }

    public void close(){
        //TODO yea
    }

    @Override
    public Record getRecord() throws FileEmptyException {
        if(!outBuffer.isEmpty()){
            return outBuffer.removeFirst();
        }
        throw new FileEmptyException();
    }

    @Override
    public void saveRecord(Record record) {
        if(inBuffer.size() < pageSize/12){
            inBuffer.add(record);
        }
    }

    @Override
    public boolean isEmpty(){
        return (inBuffer.isEmpty() && outBuffer.isEmpty());
    }
}
