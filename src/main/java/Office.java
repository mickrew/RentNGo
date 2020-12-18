package main.java;

public class Office {
    String city;
    String region;
    String id;
    String name;
    Integer capacity;

    public Office(String city, String region, String name, String id, Integer capacity){
        this.city=city;
        this.region=region;
        this.name = name;
        this.id=id;
        this.capacity=capacity;
    }

    public Office(){};

    public String getCity() {
        return city;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }


}
