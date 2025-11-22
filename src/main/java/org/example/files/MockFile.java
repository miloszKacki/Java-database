package org.example.files;

import org.example.exceptions.FileEmptyException;
import org.example.Record;

import java.util.ArrayList;

public class MockFile implements myFileable{

    private ArrayList<Record> recordlist;

    public MockFile(){
        recordlist = new ArrayList<Record>();
    }

    @Override
    public Record getRecord() throws FileEmptyException {
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

    @Override
    public void printToConsole() {
        System.out.println(this.toString());
    }

    @Override
    public String toString(){
        String s = new String("Mock: ");
        for (int i=0;i < recordlist.size();i++){
            s = s.concat(Float.toString(recordlist.get(i).getField())).concat(" ");
        }
        return s;
    }
}
