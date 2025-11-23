package org.example;

public class SortOptions {
    private static final String defaultFiboSortTapesPath = "sortTapes";
    private static final String defaultInputPath = "inOutTapes\\inTape.bin";
    private static final String defaultOutputPath = "inOutTapes\\outTape.bin";
    private static final boolean defaultIfAscOrder = true;
    private static final Mode defaultModeOfOperation = Mode.Random;
    private static final int defaultRandomRecordNumber = 10;
    private static final int defaultKeyboardRecordNumber = 5;
    public int randRecNum,keyRecNum;
    public boolean ifAsc,
            printBeforeSort,
            printAfterSort,
            printEachPhase,
            printPhaseNum,
            printFReadNum,
            printFSaveNum,
            printDReadNum,
            printDSaveNum,
            saveInput;
    public Mode modeOfOperation;
    String inFilePath,outFilePath,tapesPath;

    public SortOptions(){
        ifAsc = defaultIfAscOrder;
        randRecNum = defaultRandomRecordNumber;
        keyRecNum = defaultKeyboardRecordNumber;
        outFilePath = defaultOutputPath;
        printEachPhase = false;
        printBeforeSort = false;
        printAfterSort = false;
        printFReadNum = false;
        printFSaveNum = false;
        printDReadNum = false;
        printDSaveNum = false;
        printPhaseNum = false;
        saveInput = false;
        inFilePath = defaultInputPath;
        tapesPath = defaultFiboSortTapesPath;
        modeOfOperation = defaultModeOfOperation;
    }

    enum Mode{
        Keyboard,
        Random,
        File
    }
}


