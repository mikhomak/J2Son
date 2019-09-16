import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class J2Son {
    private final static String QUOTATION_MARK = "\"";
    private final static String COLON = ":";
    private final static String COMMA = ",";
    private final static String LEFT_BRACKET = "[";
    private final static String RIGHT_BRACKET = "]";
    private final static String LEFT_CURLY_BRACE = "{";
    private final static String RiGHT_CURLY_BRACE = "}";
    private final static String LINE_BREAK = "\n";
    private final static List<Class> CLASSES_WITHOUT_QUOTATION = Arrays.asList(int.class, double.class, float.class, Integer.class, long.class, boolean.class);
    private final static List<Class> COLLECTION_CLASSES = Arrays.asList(Collection.class, List.class, Set.class);


    public static <T extends Object> String convert(final T itemModel) {
        String result = LEFT_CURLY_BRACE + LINE_BREAK;
        result += convertItem(itemModel);
        if(result.substring(result.length() - 1).equals(COMMA)) {
            result = result.substring(0, result.length() - 1);
        }
        result += RiGHT_CURLY_BRACE + LINE_BREAK;
        return result;
    }

    private static <T extends Object> String convertItem(final T itemModel) {
        if(itemModel == null){
            return Strings.EMPTY;
        }
        final StringBuffer result = new StringBuffer();
        List<Method> methods = Arrays.asList(itemModel.getClass().getDeclaredMethods());
        methods.stream().filter(method -> method.getName().startsWith("get")).forEach(method -> result.append(addField(method, itemModel)));
        return result.toString();
    }


    private static <T extends Object> String addField(final Method method, final T itemModel) {
        final StringBuilder field = new StringBuilder(QUOTATION_MARK);
        field.append(removeGetFromMethodName(method.getName()));
        field.append(QUOTATION_MARK + COLON);
        field.append(addAttributeValue(method, itemModel));
        field.append(COMMA);
        return field.toString();
    }

    private static <T extends Object> String addAttributeValue(final Method method, final T itemModel) {
        final StringBuilder result = new StringBuilder();
        if (method.getReturnType() == String.class) {
            result.append(addStringValue(method, itemModel));
        } else if (COLLECTION_CLASSES.contains(method.getReturnType())) {
            result.append(addCollectionValue(method, itemModel));
        } else if (method.getReturnType().getSuperclass() == Object.class) {
            result.append(addItemValue(method, itemModel));
        } else if (CLASSES_WITHOUT_QUOTATION.contains(method.getReturnType())) {
            result.append(addPrimitiveValue(method, itemModel));
        }


        return result.toString();
    }

    private static <T extends Object> String addPrimitiveValue(final Method method, final T itemModel) {
        try {
            return method.invoke(itemModel).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }

    private static <T extends Object> String addItemValue(final Method method, final T itemModel) {
        try {
            return convert((T) method.invoke(itemModel));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }

    private static <T extends Object> String addCollectionValue(final Method method, final T itemModel) {
        try {
            final StringBuilder result = new StringBuilder(LEFT_BRACKET);
            String resultString = result.toString();
            final Collection<T> returnedList = (Collection) method.invoke(itemModel);
            if (CollectionUtils.isNotEmpty(returnedList)) {
                returnedList.forEach(item -> result.append(convert(item)).append(COMMA));
                resultString = result.substring(0, result.length() - 1);
            }
            resultString += RIGHT_BRACKET;
            return resultString;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }

    private static <T extends Object> String addStringValue(final Method method, final T itemModel) {
        try {
            return QUOTATION_MARK + replaceNullByEmptyString((String) method.invoke(itemModel)) + QUOTATION_MARK;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }


    private static String removeGetFromMethodName(final String methodName) {
        final String result = methodName.substring(3);
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    private static String replaceNullByEmptyString(final String string) {
        return StringUtItemModelils.isEmpty(string) ? Strings.EMPTY: string;
    }



}
