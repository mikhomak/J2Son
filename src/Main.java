import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        final TestObjectDependency testObjectDependency = new TestObjectDependency();
        testObjectDependency.setAge(123);
        testObjectDependency.setPhoneNumber("+7895123156");
        final TestObject testObject = new TestObject();
        testObject.setLastName("Sucker");
        testObject.setName("Cock");
        testObject.setTestObjectDependency(testObjectDependency);

        System.out.println(J2Son.convert(testObject);
    }

}
