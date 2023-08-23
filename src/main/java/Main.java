import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";
        String fileNameJson = "data.json";
        String fileNameJson2 = "data2.json";

        // Читаем и парсим исходный csv-файл
        List<Employee> list = parseCSV(columnMapping, fileNameCsv);
        // Преобразуем полученные данные из csv в json
        String json = listToJson(list);
        // Пишем json в новый файл
        writeString(json, fileNameJson);

        // Читаем и парсим исходный XML-файл
        list = parseXML(fileNameXml);
        // Преобразуем полученные данные из csv в json
        json = listToJson(list);
        // Пишем json в новый файл
        writeString(json, fileNameJson2);
    }

    // Чтение и парсинг csv-файла
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            // Стратегия маппинга полей
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                .withMappingStrategy(strategy)
                .build();

            // Преобразование полученных данных из файла в массив объектов
            return csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Employee> parseXML(String fileNameXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileNameXml));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            List<Employee> employeeList = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;

                    long employeeId = Long.parseLong(((Element) node).getElementsByTagName("id").item(0).getTextContent());
                    String employeeFirstName = ((Element) node).getElementsByTagName("firstName").item(0).getTextContent();
                    String employeeLastName = ((Element) node).getElementsByTagName("lastName").item(0).getTextContent();
                    String employeeCountry = ((Element) node).getElementsByTagName("country").item(0).getTextContent();
                    int employeeAge = Integer.parseInt(((Element) node).getElementsByTagName("age").item(0).getTextContent());

                    employeeList.add(new Employee(employeeId, employeeFirstName, employeeLastName, employeeCountry, employeeAge));
                }
            }

            return employeeList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Создание json из списка объектов
    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(list);
    }

    private static void writeString(String json, String fileNameJson) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameJson))) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
