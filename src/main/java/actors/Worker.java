package main.java.actors;

import main.java.entities.Car;
import main.java.connections.MongoDBConnection;
import main.java.entities.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Worker extends UnregisteredUser {
    int salary;
    Date hiringDate = new Date();
    int office;

    public Worker (){
        super();
    }

    public Worker(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate, int office){
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

    public int getOffice() {
        return office;
    }

    public void setOffice(int office) {
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
        } else if (choice==2){
            System.out.print("Insert Brand: ");
            String brand = sc.nextLine();

           db.findCarByBrand(brand);
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
        System.out.println("2) Search by Carplate");
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
            System.out.print("Insert pick office: ");
            pickOffice = sc.nextLine();
            System.out.print("Insert pick date: ");
            SimpleDateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy");

            Date d = new Date();
            try {
                d = formatter.parse(sc.nextLine());
            }catch (ParseException p){
                System.out.println("Error. Wrong Date");
                return ;
            }

            db.showListOrdersByParameters(carplate, pickOffice, d.getTime());

        }

    }




}
