package me.uptop.mvpgoodpractice.mortar;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import mortar.MortarScope;

public class ScreenScoper {
    private static final String TAG = "ScreenScoper";
    private static Map<String, MortarScope> sScopeMap = new HashMap<>();
//    private static final ModuleFactory NO_FACTORY = new ModuleFactory() {
//        @Override protected Object createDaggerModule(Resources resources, Object screen) {
//            throw new UnsupportedOperationException();
//        }
//    };
//
//    private final Map<Class, ModuleFactory> moduleFactoryCache = new LinkedHashMap<>();

    public static MortarScope getScreenScope(AbstractScreen screen) {
        if(!sScopeMap.containsKey(screen.getScopeName())) {
            Log.e(TAG, "getScreenScope: create new scope");
            return createScreenScope(screen);
        } else {
            Log.e(TAG, "getScreenScope: return has scope");
            return sScopeMap.get(screen.getScopeName());
        }
    }


    public static void registerScope(MortarScope scope) {
        sScopeMap.put(scope.getName(), scope);
    }

    public static void cleanScopeMap() {
        Iterator<Map.Entry<String, MortarScope>> iterator = sScopeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MortarScope> entry = iterator.next();
            if(entry.getValue().isDestroyed()) {
                iterator.remove();
            }
        }
    }

    public static void destroyScreenScope(String scopeName) {
        MortarScope mortarScope = sScopeMap.get(scopeName);
        if (mortarScope != null) {
            mortarScope.destroy();
        }
        cleanScopeMap();
    }

    @Nullable
    private static String getParentScopeName(AbstractScreen screen) {
        try {
            String genericName = ((Class)((ParameterizedType) screen.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0]).getName();

            String parentScopeName = genericName;

            if(parentScopeName.contains("$")) {
                parentScopeName = parentScopeName.substring(0, genericName.indexOf("$"));
            }

            return parentScopeName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static MortarScope createScreenScope(AbstractScreen screen) {
        String parentScopeName = getParentScopeName(screen);
        MortarScope parentScope = sScopeMap.get(parentScopeName);
        Object screenComponent = screen.createScreenComponent(parentScope.getService(DaggerService.SERVICE_NAME));
        MortarScope newScope = parentScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, screenComponent)
                .build(screen.getScopeName());
        registerScope(newScope);
        return newScope;
    }
}
