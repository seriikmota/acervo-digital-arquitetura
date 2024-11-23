package br.ueg.acervodigitalarquitetura.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {

    public static List<Field> getFields(Object object) {
        List<Field> resultFields = new ArrayList<>();
        Class<?> clazz;
        if (object instanceof Class) {
            clazz = (Class<?>) object;
        } else {
            clazz = object.getClass();
        }
        while(clazz != null && !clazz.equals(Object.class)){
            Field[] modelFields = clazz.getDeclaredFields();
            resultFields.addAll(Arrays.asList(modelFields));
            clazz = clazz.getSuperclass();
        }
        return resultFields;
    }
}
