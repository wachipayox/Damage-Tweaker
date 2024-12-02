package com.wachi.damagetweaker.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;

public class FileManager {

    private static final Gson gson = new Gson();


    public static void createFile(File file) {
        checkAndDeleteFile(file);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            System.out.println("Created new file called '" + file.getName() + "' in '" + file.getPath() + "'");
        } catch (Exception e) {

            System.out.println("An error has ocurred while trying to create a new file called '" + file.getName()
                    + "' in '" + file.getPath() + "'");
            e.printStackTrace();
        }
    }


    private static void checkAndDeleteFile(File file) {

        if (file.exists()) {
            file.delete();
            System.out.println("Deleted '" + file.getName() + "'' in '" + file.getPath() + "'");
        }
    }

    public static void writeJOinFile(File file, JsonObject object) {
        Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(mainGSONBuilderVariable.toJson(object));
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void writeJEinFile(File file, JsonElement e, String name) throws Exception {
        JsonObject o = getJOFromFile(file);
        o.add(name, e);
        writeJOinFile(file, o);
    }

    public static void writeObjInFile(File file, Object obj, String name) throws Exception {
        writeJEinFile(file, gson.toJsonTree(obj), name);
    }


    public static JsonObject getJOFromFile(File file) throws Exception {

        if (!file.exists())
            throw new Exception("File doesn't exist");

        JsonObject read_object = new JsonObject();
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder jsonstringbuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonstringbuilder.append(line);
            }
            bufferedReader.close();


            read_object = new Gson().fromJson(jsonstringbuilder.toString(),
                    JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return (read_object != null) ? read_object : new JsonObject();
    }

    public static JsonElement getValueFromFile(File file, String name) throws Exception {
        JsonObject JO = getJOFromFile(file);
        if (!JO.has(name))
            throw new Exception("Value from file is null");
        return JO.get(name);
    }

    public static JsonElement getOrCreateValueFromFile(File file, String name, Object df) throws Exception {
        try {
            return getValueFromFile(file, name);
        } catch (Exception e) {
            writeObjInFile(file, df, name);
            return getValueFromFile(file, name);
        }
    }

}
