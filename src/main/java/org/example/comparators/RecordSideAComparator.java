package org.example.comparators;

import org.example.Record;

import java.util.Comparator;

public class RecordSideAComparator implements Comparator<Record> {
    @Override
    public int compare(Record a,Record b){
        return Integer.compare(a.getA(),b.getA());
    }
}
