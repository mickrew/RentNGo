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
        db = new MongoDBConnection("RentNGO");
        ldb = new LevelDBConnection();
        ldb.openDB();

        Date date1 =new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020");
        //db.getCarsOutOfDate(date1.getTime());
        //QUERY 1
        //Date date1 =new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2016");
        //db.getMostUsedCarsPerOffice("Malpensa", date1.getTime());

        //QUERY 2
        //db.getLessEcoFriendlyOffice();

        //QUERY 3
        //Date currentDate =new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020");
        //Date lastYearDate =new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2019");
        //db.query4(currentDate.getTime(), lastYearDate.getTime());
        //ldb.updateLDB(db.getListOfRecentOrders());
        //        User(String surname, String name, String email, String password, Date dateOfBirth){
       /* Date d = new Date();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse("06/05/1993");
        } catch(Exception e){} */
   /*     User u ; //"aaron", "billy r", "billyr.aaron@outlook.it", "Vxyy5cpIB5" , d);
        Worker w ;
        Admin a ;
        ArrayList<String> parameters = setParameters();

        u = db.logIn(parameters);

        u.printUser();
  */
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
                UnregisteredUser u = db.getUser(UnregisteredUser.logIn());
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
                        }catch (Exception e){
                            System.out.println("Didn't insert an integer");
                            j=7;
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
                                // get the class (The cars are divided in classes by power

                                //CONTROLLA SPAZIO SU CARS
                                ldb.searchCar(o.getpickOffice(),o.getDeliveryOffice(), o.getPickDate(), o.getDeliveryDate(), db.getListOfCars(o.getOfficePickPosition(), category), (User)u);
                                break;
                            case 2:
                                //((User) u).showOrders();
                                db.showListOrders(u.getEmail());
                                //System.out.println("Do you want to Delete an order? Y/N");
                                //String a = sc.nextLine();
                                //if(a.equals("Y")){
                                    //db.deleteOrder();
                                //    System.out.println("Select which one:");
                                //    int choice = Integer.valueOf(sc.nextLine());
                                //    db.deleteOrder(u.getEmail(),choice);
                                //}
                                break;
                            case 3:
                                //((User) u).showCart();
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
                    }
                } else if (u instanceof Worker && u instanceof Admin == false) {
                    int j = 1;
                    while (j != 0) {
                        ((Worker) u).showMenu();
                        try{
                            j = Integer.valueOf(sc.nextLine());
                        } catch(Exception e){
                            j=6;
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
                                System.out.println("Insert the delivery date:");
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
                                        }
                                    }
                                }while(p!=-1);

                                db.changeStatusOrder(plate, email, "DeliveryDate",d, "Completed", damage, damageCost);
                                break;
                            default:
                                System.out.println("Try again.");
                        }
                    }
                    //u.showMenu
                } else if (u instanceof Admin) {
                    int j = 1;
                    while (j != 0) {
                        ((Admin) u).showMenu();
                        try{
                            j = Integer.valueOf(sc.nextLine());
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
                            default:
                                System.out.println("Try again. Wrong Choice !");
                                break;
                        }
                    }
                    //u.showMenu
                }
            }  else if (i == 2){
                db.insertUser(UnregisteredUser.signIn());
            }
        } while(i!=0);


/*
        Admin a = new Admin();
        a.insertNewCar(db);
        db.findCar("ZZ999ZZ");
        a.deleteCar(db);
        */

        /*
        System.out.println("1) Log in");
        System.out.println("2) Sign in");
        String email;
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while(i!=1 && i!=2) {
            i = sc.nextInt();
        }
        if(i==1)
            do {
                u.logIn(db);
                email = u.getEmail();
                u = db.logInUser(u.getEmail(), u.getPassword());
            } while(u.getEmail()==null || !u.getEmail().equals(email));

        else {
            do{
                u.signIn();
            } while(!db.insertUser(u));
        }
        u.printUser();

        Order o = new Order();
        o.setUser(u);
        o.chooseParameters(db.listOffices());
        String power;
        System.out.println("Type of car: ");
        power = sc.nextLine();
        ArrayList<Car> cars = ldb.getAvailableCars(o.getPickDate(), o.getpickOffice(), o.getDeliveryDate(), power);
        i=0;
        for(Car c:cars){ // all cars of that specific type (which depends on the power)
            System.out.print(i++ + ") ");
            c.printCar();
        }
        do {
            System.out.println("Select the cars (press -1 to stop)");
            i = Integer.valueOf(sc.nextLine());
            if(i>=0) {
                Car c = cars.get(i);
                ldb.addCarInCart(u.getEmail(), c.getPlate(), c.getBrand(), c.getEngine(), c.getPower(), c.getVehicle());
            }
        } while(i!=-1);
        ArrayList<Service> services = new ArrayList<>();
        services = db.listServices();
        for(Service s: services){
            System.out.print(i++ + ") ");
            s.printService();
        }
        ArrayList<Service> selectedServices = new ArrayList<>();
        do {
            System.out.println("Select the services (press -1 to stop)");
            i = Integer.valueOf(sc.nextLine());
            if(i>=0) {
                selectedServices.add(services.get(i));
            }
        } while(i!=-1);

        cars = ldb.getListOfCarsInCart(u.getEmail());
        for(Car c: cars){

        }
        //need to consider if the selected car is still available
        //after having proceed with the order, the selected car is inserted in the leveldb of the available cars
        //remove the list of cars in the cart.
        //System.out.println("Add User");
        //db.insertUser();
        //db.getInfo("andrea@live.it", "Email", "users");
        //db.insertNewCar();
        //db.deleteCar("AA111AA");

        //db.insertUser();
        //db.getInfo("andrea@live.it", "Email", "users");
        //db.deleteUser();
        //db.updateUser();
        //System.out.println("FINE");
      /*  Iterator<Car> cars = db.getListOfCars().iterator();
        Car c;
        i =0;
        while (cars.hasNext() && i!=100){
            c = cars.next();
            ldb.addCarInCart(u.getEmail(),c.getPlate(), c.getBrand(), c.getEngine(), c.getPower(), c.getVehicle());
            i++;
        }
        Iterator<Car> cars1 = ldb.getListOfCarsInCart(u.getEmail()).iterator();
        while (cars1.hasNext() ){
            c = cars1.next();
            c.printCar();
        }
        //ldb.elementInDatabase();
*/
        System.out.println("Fine");
        ldb.closeDB();
        db.closeConnection();
    }


}
