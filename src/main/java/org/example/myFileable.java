package org.example;

public interface myFileable {
    public Record popRecord() throws FileEmptyException;
    public void saveRecord(Record record);
    public boolean isEmpty();
}
