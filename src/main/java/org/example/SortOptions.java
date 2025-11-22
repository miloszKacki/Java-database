package org.example;

public class SortOptions {
    private final String defaultFiboSortTapesPath = "sortTapes";
    private final String defaultInputPath = "inTape";
    private final boolean defaultIfAscOrder = true;
    private final Mode defaultModeOfOperation = Mode.Random;
    private final int defaultrandomRecordNumber = 10;
    public int randRecNum;
    public boolean ifAsc,printBeforeSort,printAfterSort,printEachPhase,printPhaseNum,printFReadNum,printFSaveNum;
    public Mode modeOfOperation;
    String filePath,tapesPath;

    public SortOptions(){
        ifAsc = defaultIfAscOrder;
        randRecNum = defaultrandomRecordNumber;
        printEachPhase = false;
        printBeforeSort = false;
        printAfterSort = false;
        printFReadNum = false;
        printFSaveNum = false;
        printPhaseNum = false;
        filePath = defaultInputPath;
        tapesPath = defaultFiboSortTapesPath;
        modeOfOperation = defaultModeOfOperation;
    }

    enum Mode{
        Keyboard,
        Random,
        File
    }
}


