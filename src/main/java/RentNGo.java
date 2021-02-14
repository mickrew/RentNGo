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

                User u = User.login();

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
                                    ((Admin) u).addRemoveCars(db);
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
                                ((Admin) u).performAnalytics(db);
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

                User.signin();
            }
        } while(i!=0);

        System.out.println("Fine");
        ldb.closeDB();
        db.closeConnection();
    }




}
