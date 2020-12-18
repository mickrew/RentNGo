package main.java;

public class Service {
    String nameService;
    Double price;
    String multiplicator;

    public Service(String name, Double price){
        this.nameService = name;
        this.price = price;
    }

    public Service(){}

    public void setName(String name) {
        this.nameService = name;
    }

    public String getName() {
        return nameService;
    }

    public String getMultiplicator() {
        return multiplicator;
    }

    public String getNameService() {
        return nameService;
    }

    public Double getPrice() {
        return price;
    }

    public void setMultiplicator(String multiplicator) {
        this.multiplicator = multiplicator;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
