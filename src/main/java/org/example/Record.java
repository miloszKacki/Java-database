package org.example;

import org.example.exceptions.InvalidRecordParameterException;

import java.util.Random;

public class Record implements Comparable<Record>{

    //This is a record of a parallelogram
    //          ____________________
    //         /                   /
    //        /                   /
    //       /                   /
    //      /___________________/    :3



    //side lengths
    private int a;
    private int b;

    //angle in radians
    private float angle;

    public Record(int a, int b, float angle){
        setA(a);
        setB(b);
        setAngle(angle);
    }

    //setters
    public void setA(int a) {
        if (a > 0) this.a = a;
        else
            throw new InvalidRecordParameterException();
    }

    public void setB(int b) {
        if (b > 0) this.b = b;
        else
            throw new InvalidRecordParameterException();
    }

    public void setAngle(float angle) {
        if (angle > 0 && angle < (Math.PI/2)) this.angle = angle;
        else
            throw new InvalidRecordParameterException();
    }

    //getters
    public int getA(){
        return this.a;
    }
    public int getB(){
        return this.b;
    }
    public float getAngle(){
        return this.angle;
    }
    //last two methods are needed for the assignment to be graded well
    public float getField(){
        return (float)(a*Math.sin(angle)*b);
    }
    public static Record[] getRandomRecords(int numOfRandRecords){
        Record[] output = new Record[numOfRandRecords];

        Random random = new Random();

        int a;
        int b;
        float angle;

        for(int i=0;i<numOfRandRecords;i++){

            a = Math.abs(random.nextInt());
            b = Math.abs(random.nextInt());
            //min + rand*(max-min)
            angle = 0 + random.nextFloat()*((float)(Math.PI/2));

            output[i] = new Record(a,b,angle);
        }

        return output;
    }
    @Override
    public int compareTo(Record other) {
        return Float.compare(this.getField(),other.getField());
    }
}
