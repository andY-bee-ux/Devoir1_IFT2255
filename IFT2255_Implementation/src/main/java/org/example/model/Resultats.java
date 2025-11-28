package org.example.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
public class Resultats {
    private int admis;
    private int echecs;
    private float moyenne;

    private String coursId;
    private static final String CSVpath = "src/resources/resultats.csv";
    public Resultats( String coursId) {
        this.coursId = coursId;
        Map<String, List<String>> resultats = transformCSVToList(CSVpath);
        this.admis = Integer.parseInt(resultats.get(coursId).get(0));
        this.echecs = Integer.parseInt(resultats.get(coursId).get(1));
        this.moyenne = Float.parseFloat(resultats.get(coursId).get(2));
    }
    public String getCSVpath() {
        return CSVpath;
    }
    public Map<String, List<String>> transformCSVToList(String CSVpath) {
        Map<String, List<String>> ResultatsMap = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(CSVpath))){
          String line;
          while((line = br.readLine()) != null){
              String[] row = line.split(",");
              String key = row[0];
              String[] values = Arrays.copyOfRange(row, 1, row.length);
              ResultatsMap.put(key, Arrays.asList(values));
          }
        } catch (IOException e) {
            e.printStackTrace();
        }
      return ResultatsMap;
    }


}