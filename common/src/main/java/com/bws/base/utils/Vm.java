package com.bws.base.utils;

/*
* Copyright (c) 2006 Anton Kraievoy, Alexander Iotko.
*/
import com.bws.base.ReflectiveConfigurationError;
import java.lang.reflect.Field;

import java.util.*;
import java.util.logging.Level;

/**
 * @author Anton Kraievoy
 */
public class Vm {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Vm.class.getName());

    private Vm() { /*closed constructor for utility class*/ }

    public static StackTraceElement getStackTraceElement(int stepBackCount) {
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement[] stack = e.getStackTrace();
            return stack[stepBackCount + 1];
        }
    }

    public static StackTraceElement[] getStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {
            return e.getStackTrace();
        }
    }

    /**
     * Returns all interfaces that are implemented by specified class.
     */
    public static Class[] getIndirectInterfaces(final Class clazz) {
        final List<Class> allInterfaces = new ArrayList<Class>();

        Class currentClazz = clazz;

        while (currentClazz.getSuperclass() != null) {
            final Class[] interfaces = currentClazz.getInterfaces();
            for (Class anInterface : interfaces) {
                if (!allInterfaces.contains(anInterface)) {
                    allInterfaces.add(anInterface);
                }
            }

            currentClazz = currentClazz.getSuperclass();
        }

        return allInterfaces.toArray(new Class[allInterfaces.size()]);
    }

    public static boolean declaresField(final Class clazz, final String fieldName) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().equals(fieldName)) {
                return true;
            }
        }

        return false;
    }

    public static Field resolveField(final Class entityClass, final String fieldName) throws NoSuchFieldException {
        if (declaresField(entityClass, fieldName)) {
            return entityClass.getDeclaredField(fieldName);
        }

        final Class superClass = entityClass.getSuperclass();
        if (superClass != null) {
            return resolveField(superClass, fieldName);
        }

        throw new ReflectiveConfigurationError("Field '" + fieldName + "' was not found in hierarchy");
    }

    public static Object getValue(final Object entityInstance, final String fieldName) {
        final Class entityClass = entityInstance.getClass();
        try {
            final Field field = Vm.resolveField(entityClass, fieldName);
            field.setAccessible(true);
            return field.get(entityInstance);
        } catch (NoSuchFieldException e) {
            throw new ReflectiveConfigurationError("Field " + fieldName + " not found in class " + entityClass, e);
        } catch (IllegalAccessException e) {
            final String message = "Field " + fieldName + " in class " + entityClass + " is not accessible";
            throw new ReflectiveConfigurationError(message, e);
        }
    }

    public static boolean setValue(final Object entityInstance, final String fieldName, final Object fieldValue, final boolean resilient) throws ReflectiveConfigurationError {
        final Class entityClass = entityInstance.getClass();
        final Field field;

        try {
            field = Vm.resolveField(entityClass, fieldName);
        } catch (NoSuchFieldException e) {
            throw new ReflectiveConfigurationError("Field " + fieldName + " not found in class " + entityClass, e);
        }

        try {
            field.setAccessible(true);
            field.set(entityInstance, fieldValue);
            return true;
        } catch (IllegalAccessException e) {
            final String message = "Field " + fieldName + " in class " + entityClass + " is not accessible";
            throw new ReflectiveConfigurationError(message, e);
        } catch (IllegalArgumentException e) {
            final String fieldValueClass = fieldValue != null ? fieldValue.getClass().toString() : "null";
            final String message = "Exception while setting '" + fieldValue + "'(" + fieldValueClass + ") to field '" + fieldName + "' (" + field.getType() + ")";
            if (resilient) {
                log.log(Level.SEVERE, "setValue() " + e.getMessage(), e);
            } else {
                throw new ReflectiveConfigurationError(message, e);
            }
            return false;
        }
    }

    public static Throwable getRootCause(final Throwable ex) {
        Throwable exCause = ex;
        while (exCause.getCause() != null && exCause.getCause() != exCause) {
            exCause = exCause.getCause();
        }
        return exCause;
    }

    public static String report(final Throwable throwable) {
        final Throwable cause= getRootCause(throwable);
        return cause.getClass().getName() + "{" + cause.getMessage() + "}";
    }

    /** @unchecked Class.newInstance */
    public static<T> T newInstance(Class<T> klass) {
        try {
            return klass.newInstance();
        } catch (Exception e) {
            throw Die.criticalConfigError(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T tryToCreateByName(String className, Class<T> expectedClass) {
        Object result;
        try {
            result= Class.forName(className).newInstance();
            if (expectedClass.isInstance(result)) {
                return (T) result;
            } else {
                final String message = "loaded " + result.getClass().getName() + " is not instance of " + expectedClass.getName();
                throw Die.criticalConfigError(message);
            }
        } catch (ClassNotFoundException e) {
            log.info("failed: " + report(e) + " returning null");
            return null;

        } catch (IllegalAccessException e) {
            throw Die.criticalConfigError(e);
        } catch (InstantiationException e) {
            throw Die.criticalConfigError(e);
        }
    }

}
