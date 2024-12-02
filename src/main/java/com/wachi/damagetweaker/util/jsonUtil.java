package com.wachi.damagetweaker.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class jsonUtil {

    public static <T> List<T> jsonArrayToArray(JsonArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            for (JsonElement e : array) {
                list.add(jsonElementToObject(e, clazz));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public static JsonArray arrayToJsonArray(List<?> l) {
        JsonArray array = new JsonArray();
        Gson gson = new Gson();

        for (Object o : l) {
            JsonElement element = gson.toJsonTree(o);
            if (!element.isJsonNull())
                array.add(element);
        }
        return array;
    }

    public static JsonElement oTj(Object obj) {
        Gson gson = new Gson();
        return gson.toJsonTree(obj);
    }

    public static <T> T jsonElementToObject(JsonElement e, Class<T> clazz) {
        if (clazz == String.class) {
            return clazz.cast(e.getAsString());

        } else if (clazz == Integer.class) {
            return clazz.cast(e.getAsInt());

        } else if (clazz == Double.class) {
            return clazz.cast(e.getAsDouble());

        } else if (clazz == Boolean.class) {
            return clazz.cast(e.getAsBoolean());
        }

        return null;
    }
}
