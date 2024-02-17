public class Airport {
    private String id;
    private String city;

    // default constructor
    Airport(){}

    //constructor overloading
    Airport(String id, String city){
        this.id = id;
        this.city = city;
    }
    public String getId() {
        return id;
    }
    public String getCity() {
        return city;
    }

}
