package org.example.files;

import org.example.exceptions.DummyFileUsedAsFileException;
import org.example.exceptions.FileEmptyException;
import org.example.Record;

public class DummyFile implements myFileable{
    @Override
    public Record getRecord() throws FileEmptyException { throw new DummyFileUsedAsFileException();}

    @Override
    public void saveRecord(Record record) { throw new DummyFileUsedAsFileException(); }

    @Override
    public boolean isEmpty() {
        return true;
    }


}
