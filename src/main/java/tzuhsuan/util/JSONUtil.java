package tzuhsuan.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JSONUtil {

    private static ObjectMapper objectMapper = null;

    /**
     * Initialize the mapper object
     *
     * @param createNewMap
     * @return
     */
    public static synchronized ObjectMapper getMapperInstance(boolean createNewMap) {
        if (createNewMap) {
            return new ObjectMapper();
        } else if (null == objectMapper) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static String beanToJson(Object obj) throws Exception {
        try {
            ObjectMapper objectMapper = getMapperInstance(false);
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Object jsonToBean(String json, Class<?> cls, Boolean createNew) throws IOException {

        ObjectMapper objectMapper = getMapperInstance(createNew);
        Object vo = objectMapper.readValue(json, cls);
        return vo;
    }

    /**
     * javaBean,list,array convert to json string
     */
    public static String objToJson(Object obj) throws IOException {
        return getMapperInstance(false).writeValueAsString(obj);
    }

    /**
     * javaBean,list,array convert to pretty json string
     */
    public static String objToPrettyJson(Object obj) throws IOException {
        return getMapperInstance(false).writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * javaBean,list,array convert to Map
     */
    public static Map objToMap(Object obj) throws IOException {
        return getMapperInstance(false).convertValue(obj, Map.class);
    }

    /**
     * json string convert to javaBean
     */
    public static <T> T jsonToPojo(String jsonStr, Class<T> clazz) throws IOException {
        return getMapperInstance(false).readValue(jsonStr, clazz);
    }

    /**
     * json string convert to map
     */
    public static <T> Map<String, Object> jsonToMap(String jsonStr) throws IOException {
        return getMapperInstance(false).readValue(jsonStr, LinkedHashMap.class);
    }

    /**
     * json string convert to map with javaBean
     */
    public static <T> Map<String, T> jsonToMapBean(String jsonStr, Class<T> clazz) throws IOException {
        Map<String, Map<String, Object>> map =
                (Map<String, Map<String, Object>>) objectMapper.readValue(jsonStr, new TypeReference<Map<String, T>>() {
                });
        Map<String, T> result = new HashMap<String, T>();
        for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), mapToPojo(entry.getValue(), clazz));
        }
        return result;
    }

    /**
     * json array string convert to list with javaBean
     */
    public static <T> List<T> jsonToList(String jsonArrayStr, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = getMapperInstance(false);
        List<Map<String, Object>> list = (List<Map<String, Object>>) objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
        });
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> map : list) {
            result.add(mapToPojo(map, clazz));
        }
        return result;
    }

    public static <T> List<T> jsonToObjList(String jsonArray, Class<T> clazz) throws IOException {

        ObjectMapper objectMapper = getMapperInstance(false);
        List<T> list = objectMapper.readValue(jsonArray, new TypeReference<List<T>>() {
        });

        return list;
    }

    public static List<Map> jsonToMapList(String jsonArray) throws IOException {

        ObjectMapper objectMapper = getMapperInstance(false);
        List<Map> list = objectMapper.readValue(jsonArray, new TypeReference<List<Map>>() {
        });

        return list;
    }

    /**
     * map convert to javaBean
     */
    public static <T> T mapToPojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }
}
