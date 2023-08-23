import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameJson = "data.json";

        // Читаем и парсим исходный csv-файл
        List<Employee> list = parseCSV(columnMapping, fileName);
        // Преобразуем полученные данные из csv в json
        String json = listToJson(list);
        // Пишем json в новый файл
        writeString(json, fileNameJson);
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
