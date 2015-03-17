package com.munepom.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface ObjectCopyUtil {

	default Object shallowCopy(Object obj) {
		try {
			Class<?> cls = obj.getClass();
			Object clone = cls.newInstance();

			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				if (!Modifier.isFinal(field.getModifiers())) {
					field.set(clone, field.get(obj));
				}
			}

			while (true) {
				cls = cls.getSuperclass();
				if (Object.class.equals(cls)) {
					break;
				}
				Field[] sFields = cls.getDeclaredFields();
				for (int i = 0; i < sFields.length; i++) {
					Field field = sFields[i];
					field.setAccessible(true);
					if (!Modifier.isFinal(field.getModifiers())) {
						field.set(clone, field.get(obj));
					}
				}
			}
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	default Object deepCopy(Object obj) {
		try {
			Class<?> cls = obj.getClass();
			Object clone = cls.newInstance();

			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				if (!Modifier.isFinal(field.getModifiers())) {
					if (field.getType().isPrimitive()) {
						field.set(clone, field.get(obj));
					} else {
						field.set(clone, deepCopyObject(field.get(obj)));
					}
				}
			}

			while (true) {
				cls = cls.getSuperclass();
				if (Object.class.equals(cls)) {
					break;
				}
				Field[] sFields = cls.getDeclaredFields();
				for (int i = 0; i < sFields.length; i++) {
					Field field = sFields[i];
					if (!Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						if (field.getType().isPrimitive()) {
							field.set(clone, field.get(obj));
						} else {
							field.set(clone, deepCopyObject(field.get(obj)));
						}
					}
				}
			}
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	default Object deepCopyObject(Object obj) {
		Object ret = null;

		try (
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos =  new ObjectOutputStream(baos);
		){
			if( obj instanceof Path ){
				oos.writeObject(((Path) obj).toString());
			}
			else {
				oos.writeObject(obj);
			}
			try ( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) ) {
				ret = ois.readObject();
				if( obj instanceof Path ){
					ret = Paths.get((String) ret);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

}
