package org.example;


import org.example.files.MockFile;
import org.example.files.myFileable;

public class Main {
    public static void main(String[] args) {

        MockFile test = new MockFile();

        int numberOfTestRecords = 2478;
        Record[] testRecords = Record.getRandomRecords(numberOfTestRecords);
        for (Record rec : testRecords){
            System.out.println(rec.getA() + ", " + rec.getB() + ", " + rec.getAngle());
        }
/*
        Record[] testRecords = {
                new Record(1615876894, 1908695397, 0.73914415f),
                new Record(1885438856, 183966590, 0.68063563f),
                new Record(192108441, 1773683771, 0.2615689f),
                new Record(1588695872, 1824866896, 1.4616414f),
                new Record(836731289, 1475561654, 1.0700437f),
                new Record(141620799, 1535947480, 0.32005998f),
                new Record(143851786, 657174947, 1.017207f),
                new Record(737181308, 1246399382, 1.4527184f),
                new Record(1197091121, 630255331, 1.10315f),
                new Record(798013048, 708989719, 1.3520416f),
                new Record(1876630992, 2095981970, 1.1997545f),
                new Record(711212474, 309525881, 0.55677605f),
                new Record(2141206135, 2069958471, 0.3264369f),
                new Record(1538144299, 42300300, 0.5835461f),
                new Record(473793221, 1807366013, 0.41588587f),
                new Record(1790338795, 5742948, 1.1871688f),
                new Record(177171567, 1577930604, 0.4028327f),
                new Record(455044298, 594904542, 1.3604095f),
                new Record(2122268546, 1126752139, 1.1820328f),
                new Record(167611109, 1789251416, 0.5901317f)
        };
*/
        for(int i=0;i<numberOfTestRecords;i++){
            test.saveRecord(testRecords[i]);
        }

        System.out.println(test);
        Sorting testSort = new Sorting(false);

        myFileable a = testSort.FibosoSort(test);

        System.out.println(a);
    }



}