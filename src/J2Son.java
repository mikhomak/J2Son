import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class J2Son {
    private final static String QUOTATION_MARK = "\"";
    private final static String COLON = ":";
    private final static String COMMA = ",";
    private final static String LEFT_BRACKET = "[";
    private final static String RIGHT_BRACKET = "]";
    private final static String LEFT_CURLY_BRACE = "{";
    private final static String RIGHT_CURLY_BRACE = "}";
    private final static String LINE_BREAK = "\n";
    private final static String EMPTY_STRING = "";
    private final static List<Class> CLASSES_WITHOUT_QUOTATION = Arrays.asList(int.class, double.class, float.class, Integer.class, long.class, boolean.class);
    private final static List<Class> COLLECTION_CLASSES = Arrays.asList(Collection.class, List.class, Set.class);


    public static <T extends Object> String convert(final T object) {
        return convert(object, new HashMap<>());
    }

    public static <T extends Object> String convert(final T object, final Map<String, List<String>> filter) {
        final StringBuilder result = new StringBuilder(LEFT_CURLY_BRACE + LINE_BREAK);
        result.append(convertItem(object, filter));
        result.append(RIGHT_CURLY_BRACE + LINE_BREAK);
        return removeLastComma(result);
    }

    private static <T extends Object> String convertItem(final T object, final Map<String, List<String>> filter) {
        if (object == null) {
            return EMPTY_STRING;
        }
        final StringBuffer result = new StringBuffer();
        List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
        methods.stream().filter(method -> method.getName().startsWith("get") && isFilterApplied(method, object, filter)).forEach(method -> result.append(addField(method, object, filter)));
        return result.toString();
    }

    private static <T extends Object> String addField(final Method method, final T object, final Map<String, List<String>> filter) {
        final String field = QUOTATION_MARK + removeGetFromMethodName(method.getName()) +
                QUOTATION_MARK + COLON +
                addAttributeValue(method, object, filter) +
                COMMA + LINE_BREAK;
        return field;
    }

    private static <T extends Object> String addAttributeValue(final Method method, final T object, final Map<String, List<String>> filter) {
        final StringBuilder result = new StringBuilder();
        if (method.getReturnType() == String.class) {
            result.append(addStringValue(method, object));
        } else if (COLLECTION_CLASSES.contains(method.getReturnType())) {
            result.append(addCollectionValue(method, object, filter));
        } else if (method.getReturnType().getSuperclass() == Object.class) {
            result.append(addItemValue(method, object, filter));
        } else if (CLASSES_WITHOUT_QUOTATION.contains(method.getReturnType())) {
            result.append(addPrimitiveValue(method, object));
        }
        return result.toString();
    }

    private static <T extends Object> String addPrimitiveValue(final Method method, final T object) {
        try {
            return method.invoke(object).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            showError(method.getName(), object.getClass().getName());
            ;
        }
        return EMPTY_STRING;
    }

    private static <T extends Object> String addItemValue(final Method method, final T object, final Map<String, List<String>> filter) {
        try {
            return convert((T) method.invoke(object), filter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            showError(method.getName(), object.getClass().getName());
            ;
        }
        return EMPTY_STRING;
    }

    private static <T extends Object> String addCollectionValue(final Method method, final T object, final Map<String, List<String>> filter) {
        try {
            final StringBuilder result = new StringBuilder(LEFT_BRACKET);
            final Collection<T> returnedList = (Collection) method.invoke(object);
            if (isNotEmpty(returnedList)) {
                returnedList.forEach(item -> result.append(convert(item, filter)).append(COMMA));
            }
            result.append(LINE_BREAK + RIGHT_BRACKET);
            return removeLastComma(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            showError(method.getName(), object.getClass().getName());
            ;
        }
        return EMPTY_STRING;
    }

    private static <T extends Object> String addStringValue(final Method method, final T object) {
        try {
            return QUOTATION_MARK + replaceNullByEmptyString((String) method.invoke(object)) + QUOTATION_MARK;
        } catch (IllegalAccessException | InvocationTargetException e) {
            showError(method.getName(), object.getClass().getName());
            ;
        }
        return EMPTY_STRING;
    }

    private static void showError(final String methodName, final String className) {
        System.out.println("No such method on converting to JSON: " + methodName + " in " + className);
    }

    private static boolean isNotEmpty(final Collection collection) {
        return !isEmpty(collection);
    }

    private static boolean isEmpty(final Collection collection) {
        return collection == null || collection.size() == 0;
    }

    private static boolean isEmpty(final String string) {
        return string == null || string.length() == 0;
    }

    private static <T extends Object> boolean isFilterApplied(final Method method, final T object, final Map<String, List<String>> filter) {
        final List<String> filteredFields = filter.get(object.getClass().getName());
        return isEmpty(filteredFields) || filteredFields.contains(removeGetFromMethodName(method.getName()));
    }

    private static String removeGetFromMethodName(final String methodName) {
        final String result = methodName.substring(3);
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    private static String replaceNullByEmptyString(final String string) {
        return isEmpty(string) ? EMPTY_STRING : string;
    }

    private static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    private static String removeLastComma(final StringBuilder stringBuilder) {
        return replaceLast(stringBuilder.toString(), COMMA, LINE_BREAK);
    }

}
