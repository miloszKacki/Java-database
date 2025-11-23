package org.example;

import java.util.Objects;

public class ConsoleInputHandler {
    //Is supposed to make program do what is in the args
    //args it handles:
    //ModeOfOperation:Keyboard/Random,NumberOfRandomRecords/ExistingFile,Filepath
    //SortOrder:Asc/Desc
    //Display:PhaseNum,FReadNum,FSaveNum,DReadNum,DSaveNum,PEachPhase,PBeforeSort,PAfterSort,SaveInput

    public SortOptions options;

    public ConsoleInputHandler(String[] args){
        options = new SortOptions();
        handleDisplayResults(findArgParameters("Display",args));
        handleModeOfOperation(findArgParameters("ModeOfOperation",args));
        handleAscOrder(findArgParameters("SortOrder",args));
    }

    private void handleModeOfOperation(String[] params){
        if (Objects.equals(params[0], "Keyboard")){
            options.modeOfOperation = SortOptions.Mode.Keyboard;
            if(params.length > 1) options.keyRecNum = Integer.parseInt(params[1]);
        } else if (Objects.equals(params[0], "Random")) {
            options.modeOfOperation = SortOptions.Mode.Random;
            if(params.length > 1) options.randRecNum = Integer.parseInt(params[1]);
        } else if (Objects.equals(params[0], "ExistingFile")) {
            options.modeOfOperation = SortOptions.Mode.File;
            if(params.length > 1) options.inFilePath = params[1];
        }
    }
    private void handlePrintEachPhase(String[] params){
        if (Objects.equals(params[0], "Y")){
            options.printEachPhase = true;
            System.out.println("Printing at each phase not supported yet");
        } else if (Objects.equals(params[0], "N")) {
            options.printEachPhase = false;
        }
    }
    private void handlePrintBeforeSort(String[] params){
        if (Objects.equals(params[0], "Y")){
            options.printBeforeSort = true;
        } else if (params[0] == "N") {
            options.printBeforeSort = false;
        }
    }
    private void handlePrintAfterSort(String[] params){
        if (Objects.equals(params[0], "Y")){
            options.printAfterSort = true;
        } else if (Objects.equals(params[0], "N")) {
            options.printAfterSort = false;
        }
    }
    private void handleAscOrder(String[] params){
        if (Objects.equals(params[0], "Asc")) {
            options.ifAsc = true;
        }
        else if (Objects.equals(params[0], "Desc")) {
            options.ifAsc = false;
        }
    }
    private void handleDisplayResults(String[] params){
        for (String each : params){
            if (Objects.equals(each, "PhaseNum")) options.printPhaseNum = true;
            if (Objects.equals(each, "FReadNum")) options.printFReadNum = true;
            if (Objects.equals(each, "FSaveNum")) options.printFSaveNum = true;
            if (Objects.equals(each, "DReadNum")) options.printDReadNum = true;
            if (Objects.equals(each, "DSaveNum")) options.printDSaveNum = true;
            if (Objects.equals(each, "PEachPhase")) options.printEachPhase = true;
            if (Objects.equals(each, "PBeforeSort")) options.printBeforeSort = true;
            if (Objects.equals(each, "PAfterSort")) options.printAfterSort = true;
            if (Objects.equals(each, "SaveInput")) options.saveInput = true;
        }
    }
    private String[] findArgParameters(String Argument, String[] programArgs){
        String outArgs = "noArg";
        for (String pArg : programArgs){
            if (Objects.equals(pArg.split(":")[0], Argument))
                outArgs = pArg.split(":")[1];
        }
        return outArgs.split(",");
    }
}
