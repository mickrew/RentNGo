package main.java.connections;

import main.java.entities.Car;
import main.java.entities.Order;
import main.java.actors.User;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import java.util.*;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDBConnection {

    private DB db = null;
    private Integer Max_N_Cars = 5;
    public void openDB(){
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File("rentService"), options);
        }
        catch (IOException ioe) { closeDB(); }
    }

    public void closeDB() {
        try {
            if (db != null) db.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void putValue(String key, String value){
        db.put(bytes(key), bytes(value));
    }

    public void deleteValue(String key){
        db.delete(bytes(key));
    }

    public String getValue(String key){
        return asString(db.get(bytes(key)));
    }

    private DBIterator iterator() {
        return db.iterator();
    }


    public ArrayList<Car> getListOfCarsInCart(String email){
        ArrayList<Car> cars = new ArrayList<>();
        String key ;
        String value;
        int i = 0;
        do {
            key = email + ":cart:"+i+":brand";
            value = getValue(key);
            if(value != null){
                Car c = new Car();
                c.setBrand(value);
                key = email + ":cart:"+i+":vehicle";
                value = getValue(key);
                c.setVehicle(value);
                key = email + ":cart:"+i+":power";
                value = getValue(key);
                c.setPower(value);
                cars.add(c);
            }
            i++;
        } while(i != Max_N_Cars);

        return cars;


    }


    private void addCarInCart(String email, String brand, String vehicle, String power) {

        int i = 0;
        do {
            String key = email + ":cart:"+i+":brand";
            if(getValue(key) == null){
                putValue(key, brand);
                key = email + ":cart:"+i+":vehicle";
                putValue(key, vehicle);
                key = email + ":cart:"+i+":power";
                putValue(key, power);
                return ;
            }
            i++;
        } while(i != Max_N_Cars);

    }



    private void deleteCars(String email){
        //String key = email + ":cart:";
        //deleteValue(key);
        int i = 0;
        do {
            String key = email + ":cart:"+i+":brand";
            if(getValue(key) != null){
                deleteValue(key);
            }
            key = email + ":cart:"+i+":vehicle";
            if(getValue(key)!=null){
                deleteValue(key);
            }
            key = email + ":cart:"+i+":power";
            if(getValue(key)!=null){
                deleteValue(key);
            }
            i++;
        } while(i != Max_N_Cars);
    }

    public void searchCar(String pickOffice,String deliveryOffice, Date pickDate, Date deliveryDate, ArrayList<Car> cars, User u) {
        System.out.println("Choose the best cars");
        int i = 0;
        int choice = 0;

        Scanner sc = new Scanner(System.in);
        Long dPick = pickDate.getTime();
        Long dDelivery = deliveryDate.getTime();

        String key= u.getEmail() + ":pickOffice"; //set the orders information
        String value = getValue(key);
        if(value!=null){  // checks if
            if(!value.equals(pickOffice)) {
                deleteCars(u.getEmail());
            }
        }

        createCart(pickOffice, dPick, dDelivery, deliveryOffice, u.getEmail());
        while(!cars.isEmpty()){
            System.out.print(i + ") ");
            cars.get(i).printCar();
            System.out.println("- Price per day:"+Math.ceil(cars.get(i).calcolatePrice())+"\n");
            if ((i + 1) % 10 == 0) {
                do {
                        System.out.println("Which cars do you want to add on the cart? (Press -2 to exit, -1 to continue)");
                        try {
                            choice = Integer.valueOf(sc.nextLine());
                        } catch(Exception e){
                            System.out.println("Didn't insert and integer");
                            choice = -2;
                        }
                        if (choice > (i - 10) && choice <= i) {
                            Car c = cars.get(choice);
                            addCarInCart(u.getEmail(), c.getBrand(), c.getVehicle(), c.getPower());
                        }
                    } while (choice != -1 && choice != -2);
                }
                if (choice == -2) {
                    return;
                }
                i++;
        }
    }




    private void createCart(String pickOffice, Long dPick, Long dDelivery, String deliveryOffice,String email) {
        String key= email + ":pickOffice"; //set the orders information
        putValue(key, pickOffice);
        key =email + ":pickDate";
        putValue(key, String.valueOf(dPick));
        key =email + ":deliveryOffice";
        putValue(key, deliveryOffice);
        key =email + ":deliveryDate";
        putValue(key, String.valueOf(dDelivery));
    }

    private void deleteCart(String email){
        String key= email + ":pickOffice"; //set the orders information
        deleteValue(key);
        key =email + ":pickDate";
        deleteValue(key);
        key =email + ":deliveryOffice";
        deleteValue(key);
        key =email + ":deliveryDate";
        deleteValue(key);
    }


    public void deleteUserCart(String email) {

        deleteCars(email);
        deleteCart(email);
    }


    public Order showOrderInfo(String email) {

        String key= email + ":pickOffice"; //set the orders information
        String pickOffice = getValue(key);
        key =email + ":pickDate";
        Date pickDate = new Date(Long.valueOf(getValue(key)));
        key =email + ":deliveryOffice";
        String deliveryOffice = getValue(key);
        key =email + ":deliveryDate";
        Date dDelivery = new Date(Long.valueOf(getValue(key)));
        Order o = new Order();
        o.setpickOffice(pickOffice);
        o.setDeliveryOffice(deliveryOffice);
        o.setDeliveryDate(dDelivery);
        o.setPickDate(pickDate);
        return o;
    }





    public void deleteCarFromCart(String email, String brand, String vehicle, String power) {
        int i = 0;
        do {
            String key = email + ":cart:"+i+":brand";
            String cartBrand = getValue(key);
            if(cartBrand!=null){
                key = email + ":cart:" + i + ":vehicle";
                String cartVehicle = getValue(key);
                if (cartVehicle != null){
                    key = email + ":cart:" + i + ":power";
                    String cartPower = getValue(key);
                    if (cartBrand!= null && cartBrand.equals(brand) && cartVehicle.equals(vehicle) && cartPower.equals(power)) {
                        key = email + ":cart:" + i + ":brand";
                        deleteValue(key);

                        key = email + ":cart:" + i + ":vehicle";
                        deleteValue(key);

                        key = email + ":cart:" + i + ":power";
                        deleteValue(key);
                    }
                }
            }
            i++;
        } while(i != Max_N_Cars);
    }
}
