package org.ternlang.studio.resource.action.validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface Validator {
   Set<String> validateObject(Object object) throws Exception;
   Set<String> validateProperty(Object object, Field field) throws Exception;
   Set<String> validateParameter(Method method, Object argument, int index) throws Exception;
   Set<String> validateParameter(Constructor factory, Object argument, int index) throws Exception;
}
