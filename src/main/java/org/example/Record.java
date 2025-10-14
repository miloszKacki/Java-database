package org.example;

import org.example.exceptions.InvalidRecordParameterException;

public class Record {

    //This is a record of a parallelogram
    //          ____________________
    //         /                   /
    //        /                   /
    //       /                   /
    //      /___________________/    :3



    //side lengths
    private int a;
    private int b;

    //angle
    private float angle;

    public void Record(int a, int b, float angle){
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
        if (a > 0) this.b = b;
        else
            throw new InvalidRecordParameterException();
    }

    public void setAngle(float angle) {
        if (a > 0) this.angle = angle;
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
}
