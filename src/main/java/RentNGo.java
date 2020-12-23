package main.java;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static void main(String args[]){
        db = new MongoDBConnection("CarRental");
        ldb = new LevelDBConnection();
        ldb.openDB();
        //        User(String surname, String name, String email, String password, Date dateOfBirth){
       /* Date d = new Date();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse("06/05/1993");
        } catch(Exception e){} */
        User u ; //"aaron", "billy r", "billyr.aaron@outlook.it", "Vxyy5cpIB5" , d); */
        Worker w ;
        Admin a ;
        ArrayList<String> parameters = setParameters();

        u = db.logIn(parameters);

        u.printUser();





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
