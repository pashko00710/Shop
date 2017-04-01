package me.uptop.mvpgoodpractice.di;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DaggerService {
    private static Map<Class, Object> sComponentMap = new HashMap<>();
    public static String SERVICE_NAME = "MY_DAGGER_SERVICE";

    @SuppressWarnings("unchecked")
    public static <T> T getDaggerComponent(Context context) {
        return (T) context.getSystemService(SERVICE_NAME);
    }


    public static void registerComponent(Class componentClass, Object daggerCompoent) {
        sComponentMap.put(componentClass, daggerCompoent);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getComponent(Class<T> componentClass) {
        Object component = sComponentMap.get(componentClass);

        return (T) component;
    }

//    public static void unregisterScope(Class<? extends Annotation> scopeAnnitation) {
//        Iterator<Map.Entry<Class, Object>> iterator = sComponentMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Class, Object> entry = iterator.next();
//            if(entry.getKey().isAnnotationPresent(scopeAnnitation)) {
//                Log.e(TAG, "unregisterScope: "+entry.getKey().getName());
//                iterator.remove();
//            }
//        }
//    }
}
