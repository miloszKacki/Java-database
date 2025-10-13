package org.example;

public class DummyFile implements myFileable{
    @Override
    public Record popRecord() throws FileEmptyException {
        throw new DummyFileUsedAsFileException();
    }

    @Override
    public void saveRecord(Record record) {
        throw new DummyFileUsedAsFileException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
