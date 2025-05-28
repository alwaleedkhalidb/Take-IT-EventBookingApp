public class Event {
    public String id, name, location, description;
    public double price;

    public Event() {} // Needed for Firebase

    public Event(String id, String name, String location, String description, double price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.price = price;
    }
}