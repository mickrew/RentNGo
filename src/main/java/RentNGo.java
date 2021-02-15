package main.java;


import main.java.connections.LevelDBConnection;
import main.java.connections.MongoDBConnection;
import main.java.actors.Admin;

import main.java.actors.User;
import main.java.actors.Worker;
import main.java.entities.Car;
import main.java.entities.Office;
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


       public static void main(String args[]) throws ParseException {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

       try {
            db = new MongoDBConnection("RentNGO");
            ldb = new LevelDBConnection();
            ldb.openDB();
                //ldb.deleteAllCarsInfo();
                //ldb.updateLDB(db.getListOfRecentOrders());
            ldb.closeDB();
            db.closeConnection();
        } catch (Exception e){
            System.out.println("Error");
            return ;
        }

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
                //db = new MongoDBConnection("RentNGO");
                User u = User.login();
                //db.closeConnection();

                if (u == null) {
                    System.out.println("Login failed\n");
                    continue;
                } else {
                    u.printUser();
                }

                if (u instanceof User && !(u instanceof Worker)) {
                    int j = 1;
                    while (j != 0) {
                        (u).showMenu();
                        try {
                            j = Integer.valueOf(sc.nextLine());
                            db = new MongoDBConnection("RentNGO");
                            ldb.openDB();
                        }catch (Exception e){
                            e.printStackTrace();
                            j=100;
                        }
                        try{
                            switch (j) {
                                case 0:
                                    j=0;
                                    u =null;
                                    break;
                                case 1:
                                    u.searchForCar(db,ldb,u,sc);
                                    break;
                                case 2:
                                    u.showListOrder(db, u);
                                    break;
                                case 3:
                                    u.showCart(db,ldb, sc, u);
                                    break;
                                case 4:
                                    u.deleteAccount(db,ldb,u);
                                    i=0;
                                    j=0;
                                    break;
                                default:
                                    System.out.println("Try again.");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        db.closeConnection();
                        ldb.closeDB();
                    }
                } else if (u instanceof Worker && !(u instanceof Admin)) {
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
                                ((Worker) u).changeStatusOrder(db, sc);
                                break;
                            case 5:
                                //((Worker) u).deliveryCar();
                                ((Worker) u).changeStatusOrderInDelivery(db, sc);
                                break;
                            case 6:
                                ((Worker)u).makeCarUnavailable(db, ldb, sc);
                                break;
                            case 7:
                                db.showAllCarsInMaintenance();
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
                                System.out.println();
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
                                System.out.println("4) Most used accessories per year");
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
                                        System.out.println("Insert the Office: ");
                                        //String office = sc.nextLine();
                                        Office o = Office.selectOffice(db.listOffices());
                                        if(o == null)
                                            break;
                                        System.out.print("Insert the date in which you want to start statistic: ");
                                        String date = sc.nextLine();
                                        Date date1;
                                        try {
                                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date.trim());
                                        } catch(Exception e){
                                            System.out.println("Wrong date.");
                                            break;
                                        }
                                        //db.getMostUsedCarsPerOffice(office.trim(), date1.getTime());
                                        db.getMostUsedCarsPerOffice(o.getName(), date1.getTime());
                                        break;
                                    case 2:
                                        db.getLessEcoFriendlyOffice();
                                        break;
                                    case 3:
                                        System.out.print("Insert the year in which you want to start statistic (ONLY THE YEAR): ");
                                        String lastYear = sc.nextLine();

                                        System.out.print("Insert the date in which you want to end statistic (ONLY THE YEAR): ");
                                        String currentYear = sc.nextLine();
                                        Date date2;
                                        try {
                                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + lastYear.trim());
                                            if(date1.getTime() > new Date().getTime()) {
                                                System.out.println("Wrong date.");
                                                break;
                                            }
                                            date2 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + currentYear.trim());
                                            if((date2.getTime() < date1.getTime()) || (date2.getTime() > new Date().getTime())) {
                                                System.out.println("Wrong date.");
                                                break;
                                            }
                                        } catch(Exception e){
                                            System.out.println("Wrong Date.");
                                            break;
                                        }
                                        db.searchUserForDiscount(date2.getTime(), date1.getTime());
                                        System.out.println();
                                        break;
                                    case 4:
                                        System.out.println("Insert the year in which you want to perform statistic: ");
                                        String yearString = sc.nextLine();
                                        Integer year = 2020;
                                        try {

                                            year = Integer.valueOf(yearString);
                                        } catch (Exception e){
                                            System.out.println("Wrong year");
                                            break;
                                        }
                                        db.mostUsedAccessories(year);
                                        break;
                                    default:
                                        System.out.println("Wrong choice");
                                }
                                System.out.println("");
                                break;

                            }
                            case 12:
                            {
                                ((Admin) u).addRemoveOffice(db);
                                System.out.println();
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
                /*db = new MongoDBConnection("RentNGO");
                db.insertUser(User.signin());
                db.closeConnection(); */
                User.signin();
            }
        } while(i!=0);

        System.out.println("Fine");
        ldb.closeDB();
        db.closeConnection();
    }




}
