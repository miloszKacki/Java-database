package org.example.comparators;

import org.example.Record;

import java.util.Comparator;

public class RecordAngleComparator implements Comparator<Record> {
    @Override
    public int compare(Record a,Record b){
        return Float.compare(a.getAngle(),b.getAngle());
    }
}
