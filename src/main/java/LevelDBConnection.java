package main.java;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

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

    public ArrayList<Car> getAvailableCars(Date pick, Office officePick, Date delivery, String type){
        ArrayList<Car> cars = new ArrayList<>();
        //Cars that are available in that period for a specif office
        return cars;
    }

}
