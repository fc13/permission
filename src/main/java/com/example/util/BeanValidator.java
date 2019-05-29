package com.example.util;


import com.example.exception.ParamException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidator {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private static <T> Map<String,String> validate(T t, Class... classes){
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> validateSet = validator.validate(t, classes);
        if (CollectionUtils.isEmpty(validateSet)) {
            return Collections.emptyMap();
        }else {
            LinkedHashMap<String,String> errors = Maps.newLinkedHashMap();
            for (ConstraintViolation<T> next : validateSet) {
                errors.put(next.getPropertyPath().toString(), next.getMessage());
            }
            return errors;
        }
    }

    private static Map<String,String> validateList(Collection<?> collections){
        Preconditions.checkNotNull(collections);
        Iterator<?> iterator = collections.iterator();
        Map<String,String> errors;
        do {
            if (!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object next = iterator.next();
            errors = validate(next);
        }while (errors.isEmpty());
        return errors;
    }

    private static Map<String,String> validateObjects(Object o, Object... objects){
        if (objects!=null&&objects.length>0){
            return validateList(Lists.asList(o,objects));
        }else {
            return validate(o);
        }
    }

    public static void check(Object param) throws ParamException{
        Map<String, String> map = validateObjects(param);
        if (MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }
    }
}
