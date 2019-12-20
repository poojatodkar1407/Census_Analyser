package com.bridgelabz.CensusAnalyser;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

    public interface ICSVBuilder<E> {
//        public Iterator<E> getCSVFileIterator(Reader reader, Class<IndiaCensusCSV> indiaCensusCSVClass) throws CensusAnalyserException;
        public List<E> getCSVFileList(Reader reader, Class csvClass) throws CensusAnalyserException;

        Iterator<IndiaCensusCSV> getCsvFileIterator(Reader reader, Class<IndiaCensusCSV> indiaCensusCSVClass);
    }
