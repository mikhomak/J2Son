import java.util.List;

public class Car {

    private int seats;
    private String name;
    private boolean isCool;
    private List<Wheel> wheels;

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCool() {
        return isCool;
    }

    public void setCool(boolean cool) {
        isCool = cool;
    }

    public List<Wheel> getWheels() {
        return wheels;
    }

    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }
}
