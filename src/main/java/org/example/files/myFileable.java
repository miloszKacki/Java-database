package org.example.files;

import org.example.exceptions.FileEmptyException;
import org.example.Record;

public interface myFileable {
    public Record getRecord() throws FileEmptyException;
    public void saveRecord(Record record);
    public boolean isEmpty();
}
