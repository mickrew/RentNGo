package main.java.connections;

import main.java.entities.Car;
import main.java.entities.Order;
import main.java.actors.User;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            return null;
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


    public void deleteCars(String email){
        String key = email + ":cart";
        deleteValue(key);
    }

    public void searchCar(String getpickOffice,String deliveryOffice, Date pickDate, Date deliveryDate, ArrayList<Car> cars, User u) {
        System.out.println("Choose the best cars");
        int i = 0;
        int choice = 0;

        Scanner sc = new Scanner(System.in);
        Long dPick = pickDate.getTime();
        Long dDelivery = deliveryDate.getTime();

        String key= u.getEmail() + ":order"; //set the orders information
        String value = getValue(key);
        if(value!=null){  // checks if
           Iterator<String> c = Arrays.stream(value.split("~")).iterator();
           if(!getpickOffice.equals(c.next())) {
               deleteCars(u.getEmail());
           }
        }

        value = getpickOffice + "~" + pickDate.getTime() + "~" + deliveryDate.getTime() + "~" + deliveryOffice;
        putValue(key, value);

        key = u.getEmail() + ":cart";
        value = "";
        String carsInCart = getValue(key);
        int j = 0;
        while(!cars.isEmpty()){
            Car c = cars.get(i);
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
                        //OK
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
                        System.out.println("Which cars do you want to add on the cart? (Press -2 to exit, -1 to continue)");
                        try {
                            choice = Integer.valueOf(sc.nextLine());
                        } catch(Exception e){
                            System.out.println("Didn't insert and integer");
                            choice = -2;
                        }
                        if (choice > (i - 10) && choice <= i) {
                            c = cars.get(choice);
                            if(carsInCart==null || !carsInCart.contains(c.getPlate()))
                                addCarInCart(u.getEmail(), c.getPlate(), c.getBrand(), c.getEngine(), c.getPower(), c.getVehicle());
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
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d = new Date();
        key= u.getEmail() + ":accessories";
        value = getValue(key);
        if((u.getDateOfBirth().getYear() + 21) >  d.getYear() ){ // if he has less than 20 years
            if(value==null || !value.contains("Young Driver 19/20"))
                addAccessories(u.getEmail(),"Young Driver 19/20",19.0, "day" );
        } else if((u.getDateOfBirth().getYear() + 25) >  d.getYear()){
            if(value==null || !value.contains("Young Driver 21/24"))
                addAccessories(u.getEmail(),"Young Driver 21/24",6.0, "day" );
        }

        if(!getpickOffice.equals(deliveryOffice)){
            if(value==null || !value.contains("One Way Same Area"))
                addAccessories(u.getEmail(),"One Way Same Area",75.0, "per rent" );
        } else {
            if(value!=null && value.contains("One Way Same Area")){
                deleteAccessories(u.getEmail(), "One Way Same Area", 75.0, "per rent");
            }
        }
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
            Long millisDay = 86400000L;
            if(value!=null){
                cost = Double.valueOf(value);
                cost = cost * Math.ceil(dDelivery.getTime() - dPick.getTime())/(millisDay);
            }
            key = email + ":accessoriesPriceOneTime";
            value = getValue(key);
            if(value!=null){
                cost += Double.valueOf(value);
            }
            o.setPriceAccessories(cost);
        }
        o.setPriceCar(Math.ceil(car.calcolatePrice()));

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
        if(value!=null && value.contains(service))
            return;
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
        if(value == null)
            value= "0.0";
        System.out.print("price accessories per day: "+ value +"€, ");
        key= email + ":accessoriesPriceOneTime";
        value = getValue(key);
        if(value == null)
            value ="0.0";
        System.out.println("price accessories one time: "+ value + "€");


    }

    public void deleteAccessories(String email, String nameService, Double price, String type) {
        String key = email + ":accessories";
        String value = getValue(key);
        if(value!=null) {
            if (value.contains(nameService)) {
                String newValue = "";
                Iterator<String> c = Arrays.stream(value.split(",")).iterator();
                while (c.hasNext()) {
                    value = c.next();
                    if (!value.contains(nameService)) {
                        newValue += value + ",";
                    }
                }
                putValue(key, newValue);
                String accessories = "";
                if (type.equals("day")) {
                    accessories = ":accessoriesPriceDay";
                } else {
                    accessories = ":accessoriesPriceOneTime";
                }
                key = email + accessories;
                Double cost = Double.valueOf(getValue(key));
                cost = cost - price;
                putValue(key, String.valueOf(cost));
            }
        }
    }

    //"AAA01AA:availability  VALUE: 10000000000,10000002222~20000000000,20000002222"

    public void updateLDB(ArrayList<Order> orders) {
        if(orders.isEmpty())
            return ;
        Date dPick = new Date();
        Date dDelivery = new Date();
        for (Order order : orders) {
            String key = order.getCar() + ":availability";
            String value = getValue(key);
            dPick = order.getPickDate();
            dDelivery = order.getDeliveryDate();
            if (value != null) {
                value = value + dPick.getTime() + "," + dDelivery.getTime() + "~";
                putValue(key, value);
            } else {
                value = dPick.getTime() + "," + dDelivery.getTime() + "~";
                putValue(key, value);
            }
        }
    }



}
