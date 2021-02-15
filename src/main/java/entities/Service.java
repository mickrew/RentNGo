package main.java.entities;

import java.util.ArrayList;
import java.util.Scanner;

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

    public static ArrayList<Service> chooseServices(ArrayList<Service> services){
        int i = 1;
        System.out.println("Add Services (Insert -2 to exit)");
        ArrayList<Service> chosenServices = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        for(Service s : services){
            System.out.print(i++ + ") ");
            s.printService();

        }
        System.out.println("Choose service (Insert -2 to exit)");
        while(i != -2) {
            try {
                i = Integer.valueOf(sc.nextLine());
            } catch (Exception p) {
                System.out.println("Error. Didn't insert an integer");
                break;
            }
            if (i > services.size() || i <= 0) {
                if(i!=-2)
                    System.out.println("Value "+i+" out of range");
                break;
            } else{

                System.out.println("Service inserted (Insert -2 to exit)");
                chosenServices.add(services.get(i-1));
            }
        }
        return chosenServices;
    }

    public void printService(){
        System.out.println("Name service: " + "\t" + nameService);
        System.out.println("Price: " + "\t" + price + "€");
        System.out.println("Multiplicator: " + "\t" + multiplicator );
        System.out.println();
    }

}
