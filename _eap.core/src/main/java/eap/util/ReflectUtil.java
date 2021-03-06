package eap.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class ReflectUtil extends ReflectionUtils {
	
//	public static Field getField(Class<?> clazz, String fieldName) {
//		Field field = findField(clazz, fieldName);
//		makeAccessible(field);
//
//		return field;
//	}
	
	public static Field getField(Object target, String fieldName) {
		Field field = findField((target instanceof Class) ? (Class<?>)target : target.getClass(), fieldName);
		makeAccessible(field);

		return field;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object target, String fieldName, Class<T> requireType) {
		return (T) getFieldValue(target, fieldName);
	}
	
	public static Object getFieldValue(Object target, String fieldName) {
		Field field = getField(target, fieldName);
		return getField(field, target);
	}
	
	public static void setFieldValue(Object target, String fieldName, Object value) {
		Field field = getField(target, fieldName);
		setField(field, target, value);
	}
	
	public static Object invokeMethod(Object obj, String methodName, Object[] args) {
		return invokeMethod(obj, methodName, null, args);
	}
	
	public static Object invokeMethod(Object obj, String methodName, Class<?>[] argsClasses, Object[] args) {
		Class<?> objClass = (obj instanceof Class) ? (Class<?>)obj : obj.getClass();
		if (argsClasses == null) {
			if (args != null && args.length > 0) {
				argsClasses = new Class[args.length];
				for (int i = 0; i < args.length; i++) {
					argsClasses[i] = args[i].getClass();
				}
			}
		}
		
		try {
			if (argsClasses == null || argsClasses.length == 0) {
				Method objMethod = objClass.getDeclaredMethod(methodName); 
				objMethod.setAccessible(true);
				return objMethod.invoke(obj);
			} else {
				Method objMethod = objClass.getDeclaredMethod(methodName, argsClasses); 
				objMethod.setAccessible(true);
				return objMethod.invoke(obj, args);
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}
			throw new IllegalArgumentException(t.getMessage(), t);
		} catch (RuntimeException e) {
			throw (RuntimeException) e;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static Map<String, Object> getClassConstants(String className) {
		Map<String, Object> constants = new HashMap<String, Object>();
		
		try {
			Class clazz = ClassUtils.forName(className);
			
			Field[] fields = clazz.getFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
					Object value = field.get(null);
					if (value != null) {
						constants.put(field.getName(), value);
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		return constants;
	}
}