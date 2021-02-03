package main.java.actors;

import main.java.entities.Car;
import main.java.connections.MongoDBConnection;
import main.java.entities.Office;
import main.java.entities.Order;
import main.java.entities.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Worker extends User {
    int salary;
    Date hiringDate = new Date();
    String office;

    public Worker (){
        super();
    }

    public Worker(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate, String office){
        super(surname, name, email, password, dateofbirth);
        this.salary = salary;
        this.hiringDate = hiringDate;
        this.office = office;

    }

    public int getSalary(){
        return salary;
    }

    public void setSalary(int salary){
        this.salary = salary;
    }

    public Date getHiringDate(){
        return hiringDate;
    }

    public void setHiringDate(String hiringDate) throws ParseException {
        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(hiringDate);
        this.hiringDate = d;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public void showMenu(){
        System.out.println("0) Exit");
        System.out.println("1) Search Car by Parameters");
        System.out.println("2) Show Orders by Parameters"); //even delete or modify
        System.out.println("3) Search User");
        System.out.println("4) Pick Car");
        System.out.println("5) Delivery Car");
        System.out.println("6) Make car unavailable");
        System.out.println("7) Show Cars in Maintenance");
    }


    public static void searchCars(MongoDBConnection db){
        System.out.println("Select the parameter by which you want to search cars. ");
        Scanner sc = new Scanner(System.in);
        System.out.println("0) Exit");
        System.out.println("1) Search by Carplate");
        System.out.println("2) Search by Brand");
        int choice = Integer.valueOf(sc.nextLine());
        Car c;

        if (choice==0){
            System.out.println("Exit");
            return;
        }else if (choice==1){
            System.out.print("Insert carplate: ");
            String plate = sc.nextLine();
            c = db.findCar(plate);
            c.printCar();
            System.out.println();
        } else if (choice==2){
            System.out.print("Insert Brand: ");
            String brand = sc.nextLine();
            db.findCarByBrand(brand);
            System.out.println();
        }
    }

    public static void searchUser(MongoDBConnection db){
        System.out.print("Insert user-email: ");
        Scanner sc = new Scanner(System.in);
        String email;
        email = sc.nextLine();
        User u = new User();
        u = db.findUser(email);
        if(u!=null)
            u.printUser();
        else
        {
            System.out.println("User not found!");
        }
    }

    public static void searchOrders(MongoDBConnection db) throws ParseException {
        Scanner sc = new Scanner(System.in);
        Order or = new Order();

        System.out.println("Select the parameter by which you want to search orders. ");
        System.out.println("0) Exit");
        System.out.println("1) Search by Email");
        System.out.println("2) Search by CarPlate");
        System.out.println("3) Search by PickOffice and PickDate");
        String pickOffice = null;
        String carplate = null;
        String date1 = null;
        String date2 = null;
        int choice=0;
        try{
            choice = Integer.valueOf(sc.nextLine());
        } catch (Exception e){
            System.out.println("Insert the correct value!");
        }


        if (choice==0){
            System.out.println("Exit");
            return;
        } else if (choice==1){
            System.out.print("Insert email of user of which you want to search orders: ");
            String email = sc.nextLine();
            db.showListOrders(email);
        } else if (choice == 2){
            System.out.print("Insert carplate: ");
            carplate = sc.nextLine();
            db.showListOrdersByParameters(carplate, pickOffice, new Date().getTime());
        } else if (choice == 3){


            System.out.println("Insert pick office: ");
            Office o = Office.selectOffice(db.listOffices());
            if (o == null)
                return;
            //pickOffice = sc.nextLine();
            System.out.print("Insert pick date: ");
            SimpleDateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy");

            Date d = new Date();
            try {
                d = formatter.parse(sc.nextLine());
            }catch (ParseException p){
                System.out.println("Error. Wrong Date");
                return ;
            }

            db.showListOrdersByParameters(carplate, o.getName(), d.getTime());

        }

    }


    public void changeStatusOrder(MongoDBConnection db, Scanner sc) {
        System.out.println("Insert the plate:");
        String plate = sc.nextLine();
        System.out.println("Insert the Email:");
        String email = sc.nextLine();
        db.changeStatusOrder(plate, email,"PickDate", new Date(), "Picked", null, 0.0);
        System.out.println("Car picked successfully!\n");
    }

    public void changeStatusOrderInDelivery(MongoDBConnection db, Scanner sc) {
        System.out.println("Insert the plate:");
        String plate = sc.nextLine();
        System.out.println("Insert the Email:");
        String email = sc.nextLine();
        System.out.println("Insert the booked delivery date:");
        Date d ;
        Date d2 = new Date();
        String dateString = sc.nextLine();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse(dateString);
        } catch (ParseException p){
            System.out.println("Error. Wrong Date");
            return;
        }

        String damage = "";
        Double taxDelay = 50.0;
        Double damageCost;
        if(d2.getTime() > d.getTime())
            damageCost = ((d2.getTime() - d.getTime())*taxDelay)/(1000*60*60*24);
        else
            damageCost = 0.0;

        int p=0;
        ArrayList<Service> services = Service.chooseServices(db.getServicesWorker());

    /*    do {
            for(Service s: services){
                System.out.print(p+") ");
                s.printService();
                p++;
            }
            System.out.println("Select one (Press -1 to exit)");
            try{
                p=Integer.valueOf(sc.nextLine());
            } catch (Exception e){
                p=-1;
            }
            if(p>=0 && p<services.size()){
                if(!damage.contains(services.get(p).getNameService())) {
                    damage += services.get(p).getName() + ", ";
                    damageCost += services.get(p).getPrice();
                    p=0;
                }
            }
        }while(p!=-1); */
        System.out.println("The list of additional services is: " + damage );
        System.out.println("The surcharge is: " + Math.ceil(damageCost)+ "€\n");
        db.changeStatusOrder(plate, email, "DeliveryDate",d, "Completed", services, damageCost);
    }
}
