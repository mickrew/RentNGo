package main.java.entities;

import java.util.ArrayList;

public class Service {
    String nameService;
    Double price;
    String multiplicator;

    public Service(String name, Double price){
        this.nameService = name;
        this.price = price;
    }

    public Service(){}

    public static ArrayList<Service> clientServices(ArrayList<Service> services){
        ArrayList<Service> clientServicies = new ArrayList<>();
        for(Service s: services) {
            if (!s.getNameService().equals("Young Driver 19/20") && !s.getNameService().equals("Young Driver 21/24")
                    && !s.getNameService().equals("Administrative expenses for fines/tolls/parking")
                    && !s.getNameService().equals("Administrative expenses for damages")
                    && !s.getNameService().equals("Deductible for insolvency or passive claim / car accident")
                    && !s.getNameService().equals("Truck Service")){
                clientServicies.add(s);
            }
        }
        return clientServicies;
    }

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

    public void printService(){
        System.out.println("Name service: " + "\t" + nameService);
        System.out.println("Price: " + "\t" + price + "€");
        System.out.println("Multiplicator: " + "\t" + multiplicator);
    }

}
