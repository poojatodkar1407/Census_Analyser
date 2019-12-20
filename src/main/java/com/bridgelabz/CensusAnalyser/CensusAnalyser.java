package com.bridgelabz.CensusAnalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    Map<String, IndiaCensusDAO> censusStateMap = null;

    public CensusAnalyser() {
        this.censusStateMap = new HashMap<>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> csvFileIterator = csvBuilder.getCsvFileIterator(reader, IndiaCensusCSV.class);
            Iterable<IndiaCensusCSV> csvIterable = () -> csvFileIterator;
            StreamSupport.stream(csvIterable.spliterator(), false)
                    .forEach(censusCSV -> censusStateMap.put(censusCSV.state, new IndiaCensusDAO(censusCSV)));
            return this.censusStateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.SOME_FILE_ISSUE);
        }
    }

    public int loadIndiaStateCode(String csvFilePath) throws CensusAnalyserException {
        int counter = 0;
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCsv> stateCsvIterator = csvBuilder.getCsvFileIterator(reader, IndiaStateCodeCsv.class);
            while (stateCsvIterator.hasNext()) {
                IndiaStateCodeCsv stateCSV = stateCsvIterator.next();
                counter++;
                IndiaCensusDAO censusDAO = censusStateMap.get(stateCSV.state);
                if (censusDAO == null) continue;
                censusDAO.StateCode = stateCSV.stateCode;
            }
            return counter;
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.SOME_FILE_ISSUE);
        }
    }

    public String getStateWiseSortedCensusData() throws CensusAnalyserException {
        this.checkValue();
        Comparator<IndiaCensusDAO> censusComparator = Comparator.comparing(census -> census.state);
        return this.getSort(censusComparator);
    }

    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException {
        this.checkValue();
        Comparator<IndiaCensusDAO> sortedPopulationCensusJson = Comparator.comparing(census -> census.population, Comparator.reverseOrder());
        return this.getSort(sortedPopulationCensusJson);
    }

    public String getAreaWiseSortedCensusData() throws CensusAnalyserException {
        this.checkValue();
        Comparator<IndiaCensusDAO> sortedPopulationCensusJson = Comparator.comparing(census -> census.areaInSqKm, Comparator.reverseOrder());
        return this.getSort(sortedPopulationCensusJson);
    }

    public String getDensityWiseSortedCensusData() throws CensusAnalyserException {
        this.checkValue();
        Comparator<IndiaCensusDAO> sortedPopulationCensusJson = Comparator.comparing(census -> census.densityPerSqKm, Comparator.reverseOrder());
        return this.getSort(sortedPopulationCensusJson);
    }

    private void checkValue() throws CensusAnalyserException {
        if (censusStateMap == null || censusStateMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data",
                    CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
    }

    private String getSort(Comparator<IndiaCensusDAO> censusComparator) {
        List<IndiaCensusDAO> censusDAOS = censusStateMap.values().
                stream().collect(Collectors.toList());
        this.sort(censusDAOS, censusComparator);
        String sortedStateCensusJson = new Gson().toJson(censusDAOS);
        return sortedStateCensusJson;
    }

    private void sort(List<IndiaCensusDAO> censusDAOS, Comparator<IndiaCensusDAO> censusComparator) {
        for (int i = 0; i < censusDAOS.size() - 1; i++) {
            for (int j = 0; j < censusDAOS.size() - 1; j++) {
                IndiaCensusDAO census1 = censusDAOS.get(j);
                IndiaCensusDAO census2 = censusDAOS.get(j + 1);
                if (censusComparator.compare(census1, census2) > 0) {
                    censusDAOS.set(j, census2);
                    censusDAOS.set(j + 1, census1);
                }
            }
        }
    }
}
