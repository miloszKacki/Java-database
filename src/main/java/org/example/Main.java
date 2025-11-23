package org.example;


import org.example.files.TapeFile;

//TODO sprawko

public class Main {
    public static void main(String[] args) {

        ConsoleInputHandler c = new ConsoleInputHandler(args);
        FiboSort sorting = new FiboSort(c.options);
        TapeFile tmp = new TapeFile("sortTapes\\tmpTestFile.bin");
        //MockFile tmp = new MockFile();

        if(c.options.modeOfOperation == SortOptions.Mode.Random){

            Record[] testRecords = Record.getRandomRecords(c.options.randRecNum);
            for(Record each : testRecords) tmp.saveRecord(each);

        }
        else if(c.options.modeOfOperation == SortOptions.Mode.Keyboard){
            for(int i=0;i<c.options.keyRecNum;i++){
                tmp.saveRecord(Record.getRecordFromConsole());
            }
            System.out.println("Successfully read records from console!");
        }
        else if(c.options.modeOfOperation == SortOptions.Mode.File){
            tmp = TapeFile.readFromTapeFile("sortTapes\\tmpTestFile.bin",c.options.inFilePath);
        }
        else {
            System.out.println("Ooops! Unsupported mode of operation. Check the argument \"ModeOfOperation\" for typos :3");
            System.out.println("Or ConsoleInputHandler class for bugs... <_<'");
            return;
        }

        if(c.options.saveInput)TapeFile.saveTapeFileToElsewhere(c.options.inFilePath,tmp);

        TapeFile.pageSaveCount = 0;
        TapeFile.saveCount = 0;

        TapeFile outputFile = (TapeFile)sorting.FibosoSort(tmp);
        TapeFile.saveTapeFileToElsewhere(c.options.outFilePath,outputFile);

        if(c.options.printDReadNum) System.out.println("Disc read operation number: "+TapeFile.pageReadCount);
        if(c.options.printDSaveNum) System.out.println("Disc save operation number: "+TapeFile.pageSaveCount);
        if(c.options.printFReadNum) System.out.println("Read record operation number: "+TapeFile.readCount);
        if(c.options.printFSaveNum) System.out.println("Save record operation number: "+TapeFile.saveCount);

    }
    //"C:\\Users\\milos\\IdeaProjects\\SBD_projekt\\inputTapes\\inTape.bin"
}