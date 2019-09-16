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
    private final static String RIGHT_CURLY_BRACE = "}";
    private final static String LINE_BREAK = "\n";
    private final static String STRING_EMPTY = "";
    private final static List<Class> CLASSES_WITHOUT_QUOTATION = Arrays.asList(int.class, double.class, float.class, Integer.class, long.class, boolean.class);
    private final static List<Class> COLLECTION_CLASSES = Arrays.asList(Collection.class, List.class, Set.class);


    public static <T extends Object> String convert(final T object) {
        final StringBuilder result = new StringBuilder(LEFT_CURLY_BRACE + LINE_BREAK);
        result.append(convertItem(object));
        result.replace(0,result.length(),replaceLast(result.toString(), COMMA, LINE_BREAK));
        result.append(RIGHT_CURLY_BRACE);
        return result.toString();
    }

    private static <T extends Object> String convertItem(final T object) {
        if (object == null) {
            return STRING_EMPTY;
        }
        final StringBuffer result = new StringBuffer();
        List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
        methods.stream().filter(method -> method.getName().startsWith("get")).forEach(method -> result.append(addField(method, object)));
        return result.toString();
    }


    private static <T extends Object> String addField(final Method method, final T object) {
        final StringBuilder field = new StringBuilder(QUOTATION_MARK);
        field.append(removeGetFromMethodName(method.getName()));
        field.append(QUOTATION_MARK + COLON);
        field.append(addAttributeValue(method, object));
        field.append(COMMA);
        field.append(LINE_BREAK);
        return field.toString();
    }

    private static <T extends Object> String addAttributeValue(final Method method, final T object) {
        final StringBuilder result = new StringBuilder();
        if (method.getReturnType() == String.class) {
            result.append(addStringValue(method, object));
        } else if (COLLECTION_CLASSES.contains(method.getReturnType())) {
            result.append(addCollectionValue(method, object));
        } else if (method.getReturnType().getSuperclass() == Object.class) {
            result.append(addItemValue(method, object));
        } else if (CLASSES_WITHOUT_QUOTATION.contains(method.getReturnType())) {
            result.append(addPrimitiveValue(method, object));
        }
        return result.toString();
    }

    private static <T extends Object> String addPrimitiveValue(final Method method, final T object) {
        try {
            return method.invoke(object).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return STRING_EMPTY;
    }

    private static <T extends Object> String addItemValue(final Method method, final T object) {
        try {
            return convert((T) method.invoke(object));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return STRING_EMPTY;
    }

    private static <T extends Object> String addCollectionValue(final Method method, final T object) {
        try {
            final StringBuilder result = new StringBuilder(LEFT_BRACKET);
            String resultString = result.toString();
            final Collection<T> returnedList = (Collection) method.invoke(object);
            if (isEmpty(returnedList) == false) {
                returnedList.forEach(item -> result.append(convert(item)).append(COMMA));
                resultString = result.substring(0, result.length() - 1);
            }
            resultString += RIGHT_BRACKET;
            return resultString;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return STRING_EMPTY;
    }

    private static <T extends Object> String addStringValue(final Method method, final T object) {
        try {
            return QUOTATION_MARK + replaceNullByEmptyString((String) method.invoke(object)) + QUOTATION_MARK;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return STRING_EMPTY;
    }

    private static String removeGetFromMethodName(final String methodName) {
        final String result = methodName.substring(3);
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    private static String replaceNullByEmptyString(final String string) {
        return isEmpty(string) ? STRING_EMPTY : string;
    }

    private static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    private static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.equals(" ");
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

}
