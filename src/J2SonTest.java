import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class J2SonTest {


    private final int SEATS = 1;
    private final String NAME = "FERRARI SUPER ASS CAR";
    private final boolean IS_COOL = true;

    private final int WHEEL_1_DIAMETER = 20;
    private final double WHEEL_1_WIDTH = 12.3;
    private final String WHEEL_1_BRAND = "GHOST";

    private final int WHEEL_2_DIAMETER = 26;
    private final double WHEEL_2_WIDTH = 45;
    private final String WHEEL_2_BRAND = "INSERT NAME";

    private final String expectedResult = "{ \n" +
            "   \"seats\":1,\n" +
            "   \"wheels\":[ \n" +
            "      { \n" +
            "         \"diameter\":20,\n" +
            "         \"width\":12.3,\n" +
            "         \"brand\":\"GHOST\"\n" +
            "      },\n" +
            "      { \n" +
            "         \"diameter\":26,\n" +
            "         \"width\":45.0,\n" +
            "         \"brand\":\"INSERT NAME\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"name\":\"FERRARI SUPER ASS CAR\"\n" +
            "}";


    @org.junit.jupiter.api.Test
    void convert() {
        final Wheel firstWheel = new Wheel();
        firstWheel.setBrand(WHEEL_1_BRAND);
        firstWheel.setDiameter(WHEEL_1_DIAMETER);
        firstWheel.setWidth(WHEEL_1_WIDTH);

        final Wheel secondWheel = new Wheel();
        secondWheel.setBrand(WHEEL_2_BRAND);
        secondWheel.setDiameter(WHEEL_2_DIAMETER);
        secondWheel.setWidth(WHEEL_2_WIDTH);

        final Car car = new Car();
        car.setCool(IS_COOL);
        car.setName(NAME);
        car.setSeats(SEATS);
        car.setWheels(Arrays.asList(firstWheel, secondWheel));


        final String result = J2Son.convert(car);

        final JsonParser parser = new JsonParser();
        final JsonElement firstJson = parser.parse(result);
        final JsonElement secondParser = parser.parse(expectedResult);
        assertEquals(firstJson, secondParser);

    }
}