package main.java;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDBConnection {

    private DB db = null;

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

    public DBIterator iterator() {
        return db.iterator();
    }

    public ArrayList<Car> getListOfCarsInCart(String email){
        String key = email + ":cart";
        String s = getValue(key);
        if(s==null){
            System.out.println("Cart is empty");
            return new ArrayList<>();
        }
        Iterator<String> c = Arrays.stream(s.split("<<==>>")).iterator(); // es. AAA11AA,Brand,Engine,Power;BBB11BB;..
        ArrayList<Car> cars = new ArrayList<>();
        while(c.hasNext()){
            String car = c.next();
            Iterator<String> carInfo = Arrays.stream(car.split("~")).iterator();
            Car ca = null;
            while(carInfo.hasNext()){ // for every car, in the cart, there are these attribute
                ca = new Car();
                if(carInfo.hasNext())
                    ca.setPlate(carInfo.next());
                else
                    ca.setPlate("No info");

                if(carInfo.hasNext())
                    ca.setBrand(carInfo.next());
                else
                    ca.setBrand("No info");

                if(carInfo.hasNext())
                    ca.setVehicle(carInfo.next());
                else
                    ca.setVehicle("No info");

                if(carInfo.hasNext())
                    ca.setEngine(carInfo.next());
                else
                    ca.setEngine("No info");

                if(carInfo.hasNext())
                    ca.setPower(carInfo.next());
                else
                    ca.setPower("No info");
            }
            cars.add(ca);
        }
        return cars;
    }

    public void elementInDatabase(){
       String key = "Cart:cars";
       String value = getValue(key);
       System.out.println("The value is: " + value);
    }

    public boolean addCarInCart(String email, String plate, String brand, String engine, String power, String vehicle){
        String key = email + ":cart"; // KEY:= andrea@live.it:cart   VALUE:= AAA11AA~Renault~Megane 2 (2003)~ 1.4L 100 hp~98-72/6000<<==>>BBB11BB~ ...
        String s = getValue(key);
        if(s!=null)
            s += "<<==>>" + plate + "~" + brand + "~" + vehicle + "~" + engine + "~" + power;
        else
            s = plate + "~" + brand + "~" + vehicle + "~" + engine + "~" + power;
        putValue(key, s);
        return false;
    }

    public ArrayList<Car> getNotAvailableCars(Date pick, String officePick, Date delivery, String type){
        ArrayList<Car> cars = new ArrayList<>();
        //Cars that are available in that period for a specif office
        return cars;
    }

    public void searchCar(String getpickOffice,String deliveryOffice, Date pickDate, Date deliveryDate, ArrayList<Car> cars, String email) {
        System.out.println("Choose the best cars");
        int i = 0;
        int choice = 0;
        ArrayList<Car> listCars = new ArrayList<Car>();
        Scanner sc =new Scanner(System.in);
        Long dPick = pickDate.getTime();
        Long dDelivery = deliveryDate.getTime();
        String key;
        String value = "";
        int j = 0;
        while(!cars.isEmpty()){
            //check Car availability
            Car c = cars.get(i);
            //key= c.getPlate() + ":availability";
            //value = getValue(key);
            key= c.getPlate() + ":availability";
            value = getValue(key);
            if(value!=null){
                Iterator<String> dates = Arrays.stream(value.split("~")).iterator(); // es. AAA11AA,Brand,Engine,Power;BBB11BB;..
                while (dates.hasNext()) {
                    String dateInfo = dates.next();
                    Iterator<String> s = Arrays.stream(dateInfo.split(",")).iterator();
                    long dPickCart = Long.valueOf(s.next());
                    long dDeliveryCart = Long.valueOf(s.next());
                    if ((dPick >= dDeliveryCart && dDelivery >= dDeliveryCart)|| (dPick<= dPickCart && dDelivery <= dPickCart)){
                        System.out.println("Ok");
                    } else{
                        cars.remove(i);
                        j = 1;
                    }
                }
            }
            if(j == 0) {
                System.out.print(i + ") ");
                cars.get(i).printCar();
                if ((i + 1) % 10 == 0) {
                    do {
                        System.out.println("Whick cars do you want to add on the cart? (Press -2 to exit, -1 to continue)");
                        choice = sc.nextInt();
                        if (choice > (i - 10) && choice < i) {
                            c = cars.get(choice);
                            addCarInCart(email, c.getPlate(), c.getBrand(), c.getEngine(), c.getPower(), c.getVehicle());
                        }
                    } while (choice != -1 && choice != -2);
                }
                if (choice == -2) {
                    break;
                }
                i++;
                //
            }
            j=0;
            //
        }
        key= email + ":order";
        value = getpickOffice + "~" + pickDate.getTime() + "~" + deliveryDate.getTime() + "~" + deliveryOffice;
        putValue(key, value);
    }

    public Order payment(String email, Car car) {
        Order o =new Order();
        if(car == null){
            return null;
        }
        o.setUser(email);
        o.setCar(car.getPlate());

        String key= email + ":order";
        String value = getValue(key);
        if(value==null){
            return null;
        }
        Iterator<String> c = Arrays.stream(value.split("~")).iterator();
        o.setpickOffice(c.next());
        Date dPick =new Date(Long.valueOf(c.next()));
        o.setPickDate(dPick);
        Date dDelivery =new Date(Long.valueOf(c.next()));
        o.setDeliveryDate(dDelivery);
        o.setDeliveryOffice(c.next());


        key= email + ":accessories";
        value = getValue(key);
        if(value == null){
            o.setAccessories("");
            o.setPriceAccessories(0.0);
        } else {
            double cost = 0.0;
            o.setAccessories(value);
            key = email + ":accessoriesPriceDay";
            value = getValue(key);
            if(value!=null){
                cost = Double.valueOf(value);
                cost = cost * Math.ceil(dDelivery.getTime() - dPick.getTime())/(60*1000*60*24);
            }
            key = email + ":accessoriesPriceOneTime";
            value = getValue(key);
            if(value!=null){
                cost += Double.valueOf(value);
            }
            o.setPriceAccessories(cost);
        }
        o.setPriceCar(Math.ceil(car.calcolatePrice(car)));

        key= car.getPlate() + ":availability";
        value = getValue(key);
        if(value!=null){
            Iterator<String> dates = Arrays.stream(value.split("~")).iterator(); // es. AAA11AA,Brand,Engine,Power;BBB11BB;..
            while (dates.hasNext()) {
                String dateInfo = dates.next();
                Iterator<String> s = Arrays.stream(dateInfo.split(",")).iterator();
                long dPickCart = Long.valueOf(s.next());
                long dDeliveryCart = Long.valueOf(s.next());
                if ((dPick.getTime() >= dDeliveryCart && dDelivery.getTime() >= dDeliveryCart)|| (dPick.getTime()<= dPickCart && dDelivery.getTime() <= dPickCart)){
                    System.out.println("Ok");
                } else{
                    return null;
                }
            }
        } else {
            value = dPick.getTime() + "," + dDelivery.getTime() + "~";
            putValue(key, value);
            deleteUserCart(email);
            return o;
        }
        value = value + dPick.getTime() + "," + dDelivery.getTime() + "~";
        putValue(key, value);
        deleteUserCart(email);

        return o;
        //Car c =cars.get(i);

    }

    public void deleteUserCart(String email) {
        String key = email + ":cart";
        deleteValue(key);
        key = email + ":order";
        deleteValue(key);
        key = email + ":accessories";
        deleteValue(key);
        key = email + ":accessoriesPriceDay";
        deleteValue(key);
        key = email + ":accessoriesPriceOneTime";
        deleteValue(key);
    }

    public void addAccessories(String email, String service, double price, String type) {
        String key = email + ":accessories";
        String value = getValue(key);
        if(value!=null){
            value += service + ",";
        } else {
            value = service + ",";
        }
        putValue(key, value);
        String accessories = "";
        if(type.equals("day")){
            accessories =":accessoriesPriceDay";
        } else {
            accessories =":accessoriesPriceOneTime";
        }
        key = email + accessories;
        value = getValue(key);
        Double costs = 0.0;
        if(value == null){
            costs = price;
        } else {
            costs = Double.valueOf(value) + price;
        }
        putValue(key, String.valueOf(costs));
    }

    public void showOrderInfo(String email) {
        String key = email + ":order";
        String value = getValue(key);
        if(value == null){
            return;
        }
        Iterator<String> c = Arrays.stream(value.split("~")).iterator();
        System.out.print("Pick Office: " + c.next() + ", ");
        Date dPick =new Date(Long.valueOf(c.next()));
        System.out.print("Pick Date: "+ dPick + ", ");
        Date dDelivery =new Date(Long.valueOf(c.next()));
        System.out.print("Date Delivery: " + dDelivery);
        System.out.print(", Delivery Office: " + c.next());

        key = email + ":accessories";
        value = getValue(key);
        System.out.println(", Accessories: "+value);
        key= email + ":accessoriesPriceDay";
        value = getValue(key);
        System.out.print("price accessories per day: "+ value +"€, ");
        key= email + ":accessoriesPriceOneTime";
        value = getValue(key);
        System.out.println("price accessories one time: "+ value + "€");


    }

    public void deleteAccessories(String email, String nameService, Double price, String type) {
        String key = email + ":accessories";
        String value = getValue(key);
        if(value.contains(nameService)){
            String newValue ="";
            Iterator<String> c = Arrays.stream(value.split(",")).iterator();
            while(c.hasNext()){
                value = c.next();
                if(!value.contains(nameService)){
                    newValue += value + ",";
                }
            }
            putValue(key, newValue);
            String accessories = "";
            if(type.equals("day")){
                accessories =":accessoriesPriceDay";
            } else {
                accessories =":accessoriesPriceOneTime";
            }
            key =email + accessories;
            Double cost = Double.valueOf(getValue(key));
            cost = cost - price;
            putValue(key, String.valueOf(cost));
        }
    }

}
