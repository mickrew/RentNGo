package main.java;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import main.java.connections.MongoDBConnection;
import main.java.entities.Car;
import main.java.entities.Order;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonReader;

import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

public class DBcreator {

    public static void modifyOrder(MongoDatabase db) {
        MongoCollection<Document> orders = db.getCollection("newOrders");
        MongoCollection<Document> cars = db.getCollection("cars");
        MongoCursor<Document> cursor = orders.find().iterator();
        int i = 0;
        while (cursor.hasNext()){
            try {

                Document order = cursor.next();
                String plate="";
                try{
                    plate = order.getString("CarPlate");
                }catch (Exception e){
                }

                MongoCursor<Document> cursorCars = cars.find(eq("CarPlate", plate)).iterator();
                Document car = cursorCars.next();
                String brand = car.getString("Brand");
                String vehicle = car.getString("Vehicle").trim();
                Document newCar = new Document("CarPlate", plate).append("Brand", brand).append("Vehicle", vehicle);

                orders.updateOne(and(eq("CarPlate", plate), eq("PickDate", order.getLong("PickDate"))), set("CarPlate", newCar));
            }catch (Exception e){}
            i++;
            if(i%100==0)
                System.out.println(i);
        }

    }

    public static  void fillUsers(MongoDatabase db) {
        MongoCollection<Document> tmpUsers = db.getCollection("workers");
        MongoCollection<Document> offices = db.getCollection("offices");
        MongoCursor<Document> cursor = tmpUsers.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Integer office = -1;
            try {
                office = Integer.valueOf(d.getString("Office"));
            }catch (Exception e){}

            if (office!=-1){
                MongoCursor<Document> cursorService = offices.find(eq("Position", String.valueOf(office))).iterator();
                Document d1 = cursorService.next();
                String name = d1.getString("Name");
                tmpUsers.updateOne(eq("Email", d.get("Email")), set("Office", name));
            }
        }
    }

    public static  void fillOrdersWithInfo(MongoDatabase db){
        MongoCollection<Document> orders = db.getCollection("tmpOrders");
        MongoCollection<Document> offices1 = db.getCollection("OfficeNameCityReg");
        ArrayList<Document> offices = offices1.find().into(new ArrayList<Document>());
        MongoCursor<Document> cursor = orders.find().iterator();
        while(cursor.hasNext()){
            try {
                Document d = cursor.next();
                String officeName = d.getString("PickOffice");
                for (Document office: offices){
                    if (office.getString("PickOffice") == officeName){
                        orders.updateOne(
                                (and(eq("Email", d.getString("Email")), eq("PickDate", d.getLong("PickDate"))))
                                , set("StartOffice", office));
                    }
                    if (office.getString("DeliveryOffice") == officeName){

                        orders.updateOne(
                                (and(eq("Email", d.getString("Email")), eq("PickDate", d.getLong("PickDate"))))
                                , set("EndOffice", office));
                    }
                }
            }catch (Exception e){}

         }
    }

    public static void InsertEmbedded(MongoDatabase db){
        MongoCollection<Document> carsCollection = db.getCollection("newCars1");
        MongoCursor<Document> cursor = carsCollection.find(and(eq("Brand", "Volkswagen"),eq("Vehicle", " Tiguan") )).iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            List<Document> cars = d.get("cars", List.class); // if there are some cars (carPlates)
            for(Document doc: cars){
                List<Document> availability = doc.get("availability", List.class);
                String plate ="AA016AA";
                if(availability != null) {
                     ;
                    //add new dates
                     //get the parent-document
                    Bson filter = Filters.and( eq("cars.CarPlate", plate));
                    Bson setUpdate = Updates.push("availability", new Document("pickDate", "prova").append(
                                "deliveryDate", "prova"));
                        carsCollection.updateOne(filter, setUpdate);

                } else {
                    Bson filter = Filters.and( eq("cars.CarPlate", plate));
                    carsCollection.updateOne(filter, set("cars.$.availability",  new Document("pickDate", "prova").append(
                            "deliveryDate", "prova"
                    )));
                }
            }

        }
    }


    public static  void fillOrders(MongoDatabase db){
        MongoCollection<Document> tmpOrders = db.getCollection("orders");
        MongoCollection<Document> offices = db.getCollection("offices");
        MongoCollection<Document> services = db.getCollection("services");
        MongoCollection<Document> cars = db.getCollection("cars");
        MongoCollection<Document> orders = db.getCollection("newOrders1");


        MongoCursor<Document> cursor = tmpOrders.find().iterator();
        int step = 0;
        while(cursor.hasNext()){
            try {

                Document d = cursor.next();
                List<String> accessoriesOrder = Arrays.asList(d.getString("ListAccessories").split("-"));
                Double price = 0.0;
                try {
                    price = d.getDouble("PriceAccessories");
                } catch (Exception e) {
                    price = Double.valueOf(d.getInteger("PriceAccessories"));
                }



                
                Document order = new Document("CarPlate", d.getString("CarPlate"))
                        .append("Email", d.getString("Email"))
                        .append("CarPrice", price)
                        .append("StartOffice", d.getString("StartOffice"))
                        .append("PickDate", d.getLong("PickDate"))
                        .append("EndOffice", d.getString("EndOffice"))
                        .append("DeliveryDate", d.getLong("DeliveryDate"))
                        .append("PriceAccessories", price);


                ArrayList<Document> accessories = new ArrayList<>();
                for (int i = 0; i < accessoriesOrder.size(); i++) {
                    MongoCursor<Document> cursor1 = services.find(eq("SERVICES", accessoriesOrder.get(i))).iterator();
                    if (cursor1.hasNext()) {
                        Document service = cursor1.next();
                        accessories.add(service);
                    }
                }
                order.append("Accessories", accessories).append("Status", "Completed");

                orders.insertOne(order);
                step++;
                if (step % 100 == 0)
                    System.out.println(step);
            }catch (Exception e){}
        }



    }

    private static void prova(MongoDatabase db) {
        MongoCollection<Document> myColl = db.getCollection("newCars1");
        MongoCursor<Document> cursor = myColl.find(and(eq("Brand", "Volkswagen"), eq("Vehicle", " Tiguan"))).iterator();

        while(cursor.hasNext()) {
            Document d = cursor.next();
            List<Document> lineaCompra = d.get("cars", List.class);
            System.out.println("fine");
        }
    }



    public static void fillCar (MongoDatabase db){
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCollection<Document> newColl = db.getCollection("newCars2");
        MongoCollection<Document> offices = db.getCollection("offices");
        MongoCursor<Document> cursor = myColl.find().iterator();
        ArrayList<Car> cars = new ArrayList<>();
        int i = 0;
        while(cursor.hasNext()) {
            Document d = cursor.next();
            String co2 = "";
            String AverageFuelConsumption = "";
            try{
                AverageFuelConsumption = String.valueOf(d.getDouble("AverageFuelConsumption"));
            }catch (Exception e){
                AverageFuelConsumption = String.valueOf(d.getInteger("AverageFuelConsumption"));
            }
            try{
                co2 = String.valueOf(d.getDouble("CO2"));
            }catch (Exception e){
                co2 = String.valueOf(d.getInteger("CO2"));
            }

            Car c = new Car(d.getString("CarPlate"), d.getString("Brand"),
                    d.getString("Vehicle").trim(),
                    d.getString("Engine").trim(),
                    AverageFuelConsumption,
                    co2,
                    d.getString("Weight(3p/5p) kg").trim(),
                    d.getString("GearBox type").trim(),
                    d.getString("Tyre").trim(),
                    d.getString("Traction type").trim(),
                    d.getString("Power").trim(),
                    d.getInteger("RegistrationYear"),
                    (d.getString("Office")));

            MongoCursor<Document> searchCar  = newColl.find(and( eq("Brand", c.getBrand()), eq("Engine", c.getEngine()), eq("Vehicle", c.getVehicle()))).iterator();

            MongoCursor<Document> cursorService = offices.find(eq("Position", String.valueOf(c.getOffice()))).iterator();
            Document d1 = cursorService.next();
            String name = d1.getString("Name");

            Document carEmbedded = new Document("CarPlate", c.getPlate())
                    .append("RegistrationYear", c.getRegistrationYear())
                    .append("Office", name);

            if (!searchCar.hasNext()) {

                Document car = new Document("Brand", c.getBrand().trim())
                        .append("Vehicle", c.getVehicle().trim())
                        .append("Engine", c.getEngine().trim())
                        .append("Power", c.getPower().trim())
                        .append("AverageFuelConsumption", Double.valueOf(c.getAvgFuelCons()))
                        .append("CO2", Double.valueOf(c.getCo2()))
                        .append("Weight(3p/5p) kg", c.getWeight().trim())
                        .append("GearBox type", c.getGearBoxType().trim())
                        .append("Tyre", c.getTyre().trim())
                        .append("Traction type", c.getTractionType().trim())
                        ;

                newColl.insertOne(car);
                Bson filter = Filters.and( eq("Brand", c.getBrand()), eq("Engine", c.getEngine()), eq("Vehicle", c.getVehicle())); //get the parent-document
                Bson setUpdate = Updates.push("cars", carEmbedded);

                newColl.updateOne(filter, setUpdate);
            } else {

                Bson filter = Filters.and( eq("Brand", c.getBrand()), eq("Engine", c.getEngine()), eq("Vehicle", c.getVehicle())); //get the parent-document
                Bson setUpdate = Updates.push("cars", carEmbedded);

                newColl.updateOne(filter, setUpdate);
/*
                myColl.updateOne(
                        (and(eq("Brand", c.getBrand()), eq("Vehicle", c.getVehicle())))
                        , set("cars", carEmbedded));

 */
            }

            i++;
            if (i%100 == 0){
                System.out.println(i);
            }

        }

    }

    public static void FindCar(String Plate, MongoDatabase db){
            MongoCollection<Document> myColl = db.getCollection("newCars2");
            MongoCursor<Document> cursor  = myColl.find(eq("cars.CarPlate", Plate)).iterator();


            if (!cursor.hasNext()) {
                System.out.println("Car not found");
                return;
            } else {
                Document d = cursor.next();
                getCarFromDocument1(Plate, d);
            }

            //c.printCar();
            System.out.println();

        }


    public static void getCarFromDocument1(String plate, Document d){
        List<Document> cars = d.get("cars", List.class);
        for(int i = 0; i< cars.size(); i++){
            if (cars.get(i).getString("CarPlate").equals(plate)){
                System.out.println(cars.get(i));
            }
        }

        System.out.println();
    }


    public static void removeCar(String plate, MongoDatabase db){
        MongoCollection<Document> myColl = db.getCollection("newCars2");
        MongoCursor<Document> cursor  = myColl.find(eq("cars.CarPlate", plate)).iterator();


        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return;
        } else {
            Document d = cursor.next();
            Bson filter = Filters.and( eq("Brand", "Volkswagen"), eq("Vehicle", "Tiguan")); //get the parent-document

            //Bson setUpdate = Updates.pull("cars", plate);
            //Bson setUpdate = Updates.pull("cars.CarPlate", plate);
            Bson delete = Updates.pull("cars", new Document("CarPlate", plate).append("RegistrationYear", 2008).append("Office", "Fiumicino"));
            myColl.updateOne(filter, delete);
        }
    }




    public static void setCarPrice(MongoDatabase db){
        MongoCollection<Document> cars = db.getCollection("cars");
        MongoCollection<Document> listOrders = db.getCollection("newOrders1");
        MongoCursor<Document> orders  = listOrders.find().iterator();
        int i=0;
        while(orders.hasNext()) {
            Document d = orders.next();
            MongoCursor<Document> cursor1  = cars.find(and(eq("CarPlate",d.getString("CarPlate")))).iterator();
            i++;
            if(i%100==0)
                System.out.println(i);
            if (cursor1.hasNext()){

                Document d1 = cursor1.next();

                Document carEmbedded = new Document("CarPlate", d.getString("CarPlate"))
                        .append("Brand", d1.getString("Brand"))
                        .append("Vehicle", d1.getString("Vehicle").trim());

                listOrders.updateOne(and(eq("PickDate",d.getLong("PickDate")), eq("CarPlate", d.getString("CarPlate"))), set("CarPlate", carEmbedded));
            } else
                continue;
        }
    }


    private static String getPassword(String prompt) {

        String password = "";
        ConsoleEraser consoleEraser = new ConsoleEraser();
        System.out.print(prompt);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        consoleEraser.start();
        try {
            password = in.readLine();
        }
        catch (IOException e){
            System.out.println("Error trying to read your password!");
            System.exit(1);
        }

        consoleEraser.halt();
        System.out.print("\b");

        return password;
    }

    static class EraserThread implements Runnable {
        private boolean stop;

        public EraserThread(String prompt) {
            System.out.print(prompt);
        }

        public void run () {
            stop = true;
            while (stop) {
                System.out.print("\010*");
                try {
                    Thread.currentThread().sleep(1);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }


        public void stopMasking() {
            this.stop = false;
        }
    }

    public static String readPassword2 (String prompt) {
        EraserThread et = new EraserThread(prompt);
        Thread mask = new Thread(et);
        mask.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String password = "";

        try {
            password = in.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        // stop masking
        et.stopMasking();
        // return the password entered by the user
        return password;
    }

    public static class ConsoleEraser extends Thread {
        private boolean running = true;
        public void run() {
            while (running) {
                System.out.print("\b ");
                try {
                    Thread.currentThread().sleep(1);
                }
                catch(InterruptedException e) {
                    break;
                }
            }
        }
        public synchronized void halt() {
            running = false;
        }
    }

    public static String getPasswordWithinEclipse(String msg)
            throws IOException
    {
        // In Eclipse IDE
        System.out.print(msg);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        String password = reader.readLine();
        if (password != null) {
            if (password.length() <= 0) {
                System.out.println("Invalid input\n");
                throw new IOException("Error reading in password");
            }
        }
        return password;
    }


    public static void main(String args[]) throws ParseException, IOException {

        ConnectionString uri = new ConnectionString(
                "mongodb://localhosts:27017");

        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase db = mongoClient.getDatabase("RentNGOLocal");

        //InsertEmbedded(db);
        //fillOrders(db);
        //fillUsers(db);
        //modifyOrder(db);
        //fillOrdersWithInfo(db);
        //FindCar("AA016AA", db);
        //removeCar("AA272AB", db);
        //setCarPrice(db);
        System.out.println("Start");

        //String password = getPasswordWithinEclipse("Password: ");
        Scanner sc = new Scanner(System.in);
        String password = sc.next();
        Integer lenght = password.length();

        for(int i=0; i< lenght; i++ ){
            System.out.print("\b");
        }
        System.out.flush();
        System.out.println("fine");


    }




}
