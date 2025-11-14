package org.example.files;

import org.example.Record;
import org.example.exceptions.FileEmptyException;

import java.util.ArrayList;

public class SeqFile implements myFileable{

    //1 record is 12bytes, 10 records for page (for now)
    private final static int pageSize = 120;

    ArrayList<Record> inBuffer = new ArrayList<Record>();
    ArrayList<Record> outBuffer = new ArrayList<Record>();

    public SeqFile(String path){

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
