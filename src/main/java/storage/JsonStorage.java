package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();
    private final String filePath;

    public JsonStorage(String filePath) {
        this.filePath = filePath;
    }

    // Listni faylga saqlash
    public <T> void saveAll(List<T> list) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.out.println("Xato: " + e.getMessage());
        }
    }

    // Fayldan listni o'qish
    public <T> List<T> loadAll(Class<T> clazz) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(filePath)) {
            Type listType = TypeToken.getParameterized(
                    List.class, clazz).getType();
            List<T> result = gson.fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Xato: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}