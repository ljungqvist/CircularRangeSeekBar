package info.ljungqvist.android.util;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Created by ljunpe on 08/10/14.
 */
public class CrsbUtils {
    public static void setLayoutDirection(View view, Drawable drawable, int layoutDirection) {
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                setLayoutDirection17(drawable, layoutDirection);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
    @TargetApi(17)
    private static void setLayoutDirection17(Drawable drawable, int layoutDirection)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class c = Class.forName("android.graphics.drawable.Drawable");
        Method m = c.getMethod("setLayoutDirection", new Class[] {int.class});
        Object o = m.invoke(drawable, new Object[]{layoutDirection});
    }

    private static Class R_styleable = null;
    private static Object getStyleable(String name) {

        if (null == R_styleable)
            for (Class c : android.R.class.getClasses()) {
                if (c.getName().indexOf("styleable") >= 0) {
                    R_styleable = c;
                }
            }
        if (null != R_styleable) {
            try {
                return R_styleable.getField(name).get(android.R.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
    public static int getStyleableInt(String name) {
        Object object = getStyleable(name);
        if (null != object)
            return (Integer) object;
        return 0;
    }
    public static int[] getStyleableIntArray(String name) {
        Object object = getStyleable(name);
        if (null != object)
            return (int[]) object;
        return new int[]{};
    }

    public static int getPrivateInt(Class c, Object object, String fieldName) {
        try {
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            return  (Integer) f.get(object);
        } catch (NoSuchFieldError e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -127;
    }
}
