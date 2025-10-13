package org.example;

import java.util.ArrayList;

public class MockFile implements myFileable{

    private ArrayList<Record> recordlist;

    @Override
    public Record popRecord() throws FileEmptyException{
        if(!recordlist.isEmpty()){
            return recordlist.removeFirst();
        }
        throw new FileEmptyException();
    }

    @Override
    public void saveRecord(Record record) {
        recordlist.add(record);
    }

    @Override
    public boolean isEmpty(){
        return recordlist.isEmpty();
    }
}
