package com.bridgelabz.CensusAnalyser;

public class CensusAnalyserException extends Exception {
    enum ExceptionType {
        CENSUS_FILE_PROBLEM, NO_CENSUS_DATA, SOME_FILE_ISSUE, ERROR_FROM_CSV_BUILDER,UNABLE_TO_PARSE
    }

    ExceptionType type;

    public CensusAnalyserException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }
}
