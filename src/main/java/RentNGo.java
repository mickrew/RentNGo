package main.java;


import main.java.connections.LevelDBConnection;
import main.java.connections.MongoDBConnection;
import main.java.actors.Admin;
import main.java.actors.UnregisteredUser;
import main.java.actors.User;
import main.java.actors.Worker;
import main.java.entities.Car;
import main.java.entities.Order;
import main.java.entities.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RentNGo {
    static private MongoDBConnection db;
    static private LevelDBConnection ldb;
    static private User u;


    public static ArrayList<String> setParameters(){
        String email;
        String password;
        ArrayList<String> parameters= new ArrayList<String>();

        System.out.println("Insert the Email");
        Scanner sc = new Scanner(System.in);
        email = sc.nextLine();
        parameters.add(email);

        System.out.println("Insert the Password");
        password = sc.nextLine();
        parameters.add(password);


        return parameters;
    }

    public static void main(String args[]) throws ParseException {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        db = new MongoDBConnection("RentNGO");
        ldb = new LevelDBConnection();
        ldb.openDB();
        ldb.updateLDB(db.getListOfRecentOrders());
        ldb.closeDB();
        db.closeConnection();


        int i = 0;

            do {
            System.out.println("0) Exit");
            System.out.println("1) Log in");
            System.out.println("2) Sign in");
            Scanner sc =new Scanner(System.in);
            try{
                i = Integer.valueOf(sc.nextLine());

            }
            catch(Exception e){
                System.out.println("Error. Didn't insert an integer");
                i=4;
            }

            if(i == 1) {
                db = new MongoDBConnection("RentNGO");
                UnregisteredUser u = db.getUser(UnregisteredUser.logIn());
                db.closeConnection();

                if (u == null) {
                    System.out.println("Login failed\n");
                    continue;
                }
                if (u instanceof User) {
                    int j = 1;
                    while (j != 0) {
                        ((User) u).showMenu();
                        try {
                            j = Integer.valueOf(sc.nextLine());
                            db = new MongoDBConnection("RentNGO");

                            ldb.openDB();
                        }catch (Exception e){
                            System.out.println("Didn't insert an integer");
                            j=100;
                        }
                        switch (j) {
                            case 0:
                                j=0;
                                u =null;
                                break;
                            case 1:
                                Order o = ((User) u).createOrder(db.listOffices());
                                if(o == null){
                                     break;
                                }
                                int category = 0;
                                System.out.println("Choose the class: (if different from 1,2,3 --> ALL CLASSES)");
                                System.out.println("1) Class  (55-75kw)");
                                System.out.println("2) Class  (76-120kw)");
                                System.out.println("3) Class  (121-over)");
                                try {
                                    category = Integer.valueOf(sc.nextLine());
                                }
                                catch(Exception e){
                                    category = 4;
                                }

                                ldb.searchCar(o.getpickOffice(),o.getDeliveryOffice(), o.getPickDate(), o.getDeliveryDate(), db.getListOfCars(o.getOfficePickPosition(), category), (User)u);
                                break;
                            case 2:
                                db.showListOrders(u.getEmail());
                                break;
                            case 3:

                                ArrayList<Car> cars = ldb.getListOfCarsInCart(u.getEmail());
                                double total;
                                if(cars != null){
                                    for(Car c: cars){
                                        c.printCar();
                                        System.out.println("The car price per day is: "+ Math.ceil(c.calcolatePrice()) + "€");
                                    }
                                    ldb.showOrderInfo(u.getEmail());

                                    System.out.println("Do you want to proceed with the payment? Y/N");
                                    String  a = sc.nextLine();
                                    if(a.equals("Y")){
                                        Order order = ldb.payment(u.getEmail(), ((User)u).chooseCar(cars));
                                        if(order == null){
                                            System.out.println("Car is already rented");
                                        } else {
                                            order.printOrder();
                                            Long millisDay = 86400000L;
                                            Long numDays = (order.getDeliveryDate().getTime() - order.getPickDate().getTime())/(millisDay);
                                            total = order.getPriceCar() * numDays + order.getPriceAccessories();
                                            System.out.println("The total is: " + total + "\n");


                                            db.insertOrder(order);
                                        }
                                    }
                                }
                                break;
                            case 4:
                                db.deleteUser(u.getEmail());
                                ldb.deleteUserCart(u.getEmail());
                                u = null;
                                i=0;
                                j=0;
                                break;
                            case 5:
                                ArrayList<Service> services= new ArrayList<>();
                                services = db.getServices();//Service.clientServices(db.getServices());
                                System.out.println("Do you want to delete(D) or add(A)?");
                                String ad =sc.nextLine();
                                int choice =0;
                                for(Service s: services){
                                    System.out.print(choice + ") ");
                                    s.printService();
                                    choice ++;
                                }
                                while(choice!=-1) {
                                    System.out.println("Which Accessories do you want to add/remove? (-1 to stop)");
                                    choice = Integer.valueOf(sc.nextLine());
                                    if(choice <= services.size() && choice > -1) {
                                        if(ad.equals("A"))
                                            ldb.addAccessories(u.getEmail(), services.get(choice).getNameService(), services.get(choice).getPrice(), services.get(choice).getMultiplicator());
                                        else if(ad.equals("D"))
                                            ldb.deleteAccessories(u.getEmail(), services.get(choice).getNameService(), services.get(choice).getPrice(), services.get(choice).getMultiplicator());
                                    }
                                }
                                break;
                            default:
                                System.out.println("Try again.");
                        }
                        db.closeConnection();
                        ldb.closeDB();
                    }
                } else if (u instanceof Worker && u instanceof Admin == false) {
                    int j = 1;
                    while (j != 0) {
                        ((Worker) u).showMenu();
                        try{
                            j = Integer.valueOf(sc.nextLine());
                            db = new MongoDBConnection("RentNGO");

                            ldb.openDB();
                        } catch(Exception e){
                            j=100;
                        }
                        switch (j) {
                            case 0:
                                break;
                            case 1:
                                ((Worker) u).searchCars(db);
                                break;
                            case 2:
                                ((Worker) u).searchOrders(db);
                                break;
                            case 3:
                                ((Worker) u).searchUser(db);
                                break;
                            case 4:
                                //((Worker) u).pickCar();
                                System.out.println("Insert the plate:");
                                String plate = sc.nextLine();
                                System.out.println("Insert the Email:");
                                String email = sc.nextLine();
                                db.changeStatusOrder(plate, email,"PickDate", new Date(), "Picked", "", 0.0);
                                break;
                            case 5:
                                //((Worker) u).deliveryCar();
                                System.out.println("Insert the plate:");
                                plate = sc.nextLine();
                                System.out.println("Insert the Email:");
                                email = sc.nextLine();
                                System.out.println("Insert the booked delivery date:");
                                Date d = new Date();
                                Date d2 = new Date();
                                String dateString = sc.nextLine();
                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    d = formatter.parse(dateString);
                                } catch (ParseException p){
                                    System.out.println("Error. Wrong Date");
                                    break;
                                }

                                String damage = "";
                                Double taxDelay = 50.0;
                                Double damageCost;
                                if(d2.getTime() > d.getTime())
                                    damageCost = ((d2.getTime() - d.getTime())*taxDelay)/(1000*60*60*24);
                                else
                                    damageCost = 0.0;

                                int p=0;
                                ArrayList<Service> services = db.getServicesWorker();
                                do {
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
                                }while(p!=-1);

                                db.changeStatusOrder(plate, email, "DeliveryDate",d, "Completed", damage, damageCost);
                                break;
                            case 6:
                                System.out.println("Insert the plate:");
                                plate = sc.nextLine();
                                System.out.println("Insert the dates in which you want to make it unavailable:");
                                d = new Date();
                                d2 = new Date();
                                System.out.println("Date Start");
                                dateString = sc.nextLine();
                                formatter = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    d = formatter.parse(dateString);
                                } catch (ParseException p2){
                                    System.out.println("Error. Wrong Date");
                                    break;
                                }
                                System.out.println("Date End");
                                dateString = sc.nextLine();
                                try {
                                    d2 = formatter.parse(dateString);
                                } catch (ParseException p3){
                                    System.out.println("Error. Wrong Date");
                                    break;
                                }
                                if(d2.getTime() <= d.getTime()){
                                    System.out.println("Date End can't be smaller than Date Start");
                                    break;
                                }
                                ldb.carNotAvailable(plate, d, d2);
                                db.showUsersOrdersForDate(plate, d, d2);
                                break;
                            default:
                                System.out.println("Try again.");
                        }
                        db.closeConnection();
                        ldb.closeDB();
                    }
                    //u.showMenu
                } else if (u instanceof Admin) {
                    int j = 1;
                    while (j != 0) {
                        ((Admin) u).showMenu();
                        try{
                            j = Integer.valueOf(sc.nextLine());
                            db = new MongoDBConnection("RentNGO");

                            ldb.openDB();
                        } catch(Exception e){
                            j=1000;
                        }
                        switch (j) {
                            case 0:
                                break;
                            case 1:
                                {
                                ((Admin) u).searchOrders(db);
                                break;
                            }
                            case 2:
                                {
                                ((Admin) u).searchCars(db);
                                break;
                            }

                            case 3:
                                {
                                ((Admin) u).modifyCar(db);
                                break;
                            }
                            case 4:
                                {
                                    System.out.println("0) Exit");
                                System.out.println("1) Add car");
                                System.out.println("2) Remove car");
                                sc = new Scanner(System.in);
                                try {
                                    i = Integer.valueOf(sc.nextLine());
                                } catch (Exception e) {
                                    System.out.println("Error. Didn't insert an integer");

                                }
                                switch (i) {
                                    case 1:
                                        ((Admin) u).insertNewCar(db);
                                        break;
                                    case 2:
                                        ((Admin) u).deleteCar(db);
                                        break;
                                    case 3:
                                        continue;

                                }
                                break;
                            }
                            case 5:
                                {
                                ((Admin) u).findWorker(db);
                                break;
                            }
                            case 6:
                                {
                                ((Admin) u).addRemoveWorker(db);
                                break;
                            }
                            case 7:
                                {
                                ((Admin) u).promoteWorker(db);
                                break;
                            }
                            case 8: {
                                ((Admin) u).modifyWorker(db);
                                break;
                            }
                            case 9: {
                                ((Admin) u).searchUser(db);
                                break;
                            }
                            case 10: {
                                ((Admin) u).removeUser(db);
                                break;
                            }
                            case 11: {
                                System.out.println("1) Get most used car per Office");
                                System.out.println("2) Get les eco friendly Office");
                                System.out.println("3) Search user for future discount");
                                int choice=0;
                                try {
                                    choice = Integer.valueOf(sc.nextLine());
                                }
                                catch(Exception e){
                                    choice = 100;
                                }

                                /*
                                * fare try catch per date
                                * */
                                switch (choice) {
                                    case 1:
                                        System.out.print("Insert the Office: ");
                                        String office = sc.nextLine();
                                        System.out.print("Insert the date in which you want to start statistic: ");
                                        String date = sc.nextLine();
                                        Date date1 =new SimpleDateFormat("dd/MM/yyyy").parse(date.trim());
                                        db.getMostUsedCarsPerOffice(office.trim(), date1.getTime());
                                        break;
                                    case 2:
                                        db.getLessEcoFriendlyOffice();
                                        break;
                                    case 3:
                                        System.out.print("Insert the year in which you want to start statistic: ");
                                        String lastYear = sc.nextLine();

                                        System.out.print("Insert the date in which you want to end statistic: ");
                                        String currentYear = sc.nextLine();

                                        date1 =new SimpleDateFormat("dd/MM/yyyy").parse("01/01/"+lastYear.trim());
                                        Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/"+currentYear.trim());
                                        db.searchUserForDiscount(date2.getTime(), date1.getTime());
                                        break;
                                }
                                break;

                            }
                            default:
                                System.out.println("Try again. Wrong Choice !");
                                break;
                        }
                        db.closeConnection();
                        ldb.closeDB();
                    }

                }
            }  else if (i == 2){
                db.insertUser(UnregisteredUser.signIn());
            }
        } while(i!=0);

        System.out.println("Fine");
        ldb.closeDB();
        db.closeConnection();
    }


}
