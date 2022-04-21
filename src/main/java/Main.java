import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Задача 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String jsonFileName = "data.json";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, jsonFileName);

        // Задача 2
        List<Employee> listXML = parseXML("data.xml");
        String json2 = listToJson(listXML);
        String jsonFileName2 = "data2.json";
        writeString(json2, jsonFileName2);

        // Задача 3
        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);
    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> result = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            result = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> parseXML(String str) {
        List<Employee> list = new ArrayList<>();
        List<String> elementList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(str));

            Node node = doc.getDocumentElement();
            NodeList nodeList = node.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nodeTmp = nodeList.item(i);

                if (nodeTmp.getNodeName().equals("employee")) {
                    NodeList nodeList1 = nodeTmp.getChildNodes();
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        Node node_ = nodeList1.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            elementList.add(node_.getTextContent());
                        }
                    }
                    list.add(new Employee(
                            Long.parseLong(elementList.get(0)),
                            elementList.get(1),
                            elementList.get(2),
                            elementList.get(3),
                            Integer.parseInt(elementList.get(4))));
                    elementList.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    static String readString(String new_data) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new_data))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    static List<Employee> jsonToList(String json3) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(json3);
            JSONArray jsonArrays = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object o : jsonArrays) {
                list.add(gson.fromJson(o.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}
