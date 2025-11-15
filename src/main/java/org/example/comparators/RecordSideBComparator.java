package org.example.comparators;

import org.example.Record;

import java.util.Comparator;

public class RecordSideBComparator implements Comparator<Record> {
    @Override
    public int compare(Record a,Record b){
        return Float.compare(a.getB(),b.getB());
    }
}
