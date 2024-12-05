package com.wachi.damagetweaker.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wachi.damagetweaker.DamageTweakerMod;

import java.io.*;

public class FileManager {

    private static final Gson gson = new Gson();


    @SuppressWarnings("StringConcatenationArgumentToLogCall")
    public static void createFile(File file) {
        checkAndDeleteFile(file);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            DamageTweakerMod.LOGGER.info("Created new file called '" + file.getName() + "' in '" + file.getPath() + "'");
        } catch (Exception e) {

            DamageTweakerMod.LOGGER.error("An error has occurred while trying to create a new file called '" + file.getName()
                    + "' in '" + file.getPath() + "'");
            DamageTweakerMod.LOGGER.error(e.getMessage());
        }
    }


    private static void checkAndDeleteFile(File file) {

        if (file.exists()) {
            file.delete();
            DamageTweakerMod.LOGGER.info("Deleted '" + file.getName() + "'' in '" + file.getPath() + "'");
        }
    }

    public static void writeJOinFile(File file, JsonObject object) {
        Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(mainGSONBuilderVariable.toJson(object));
            fileWriter.close();
        } catch (IOException e) {
            DamageTweakerMod.LOGGER.error(e.getMessage());
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
            DamageTweakerMod.LOGGER.error(e.getMessage());
        }

        return (read_object != null) ? read_object : new JsonObject();
    }

    public static JsonElement getValueFromFile(File file, String name) throws Exception {
        JsonObject JO = getJOFromFile(file);
        if (!JO.has(name))
            throw new Exception("Value from file is null");
        return JO.get(name);
    }

}
