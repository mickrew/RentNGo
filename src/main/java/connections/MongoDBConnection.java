package main.java.connections;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.function.Consumer;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import main.java.actors.Admin;
import main.java.actors.UnregisteredUser;
import main.java.actors.User;
import main.java.actors.Worker;
import main.java.entities.Car;
import main.java.entities.Office;
import main.java.entities.Order;
import main.java.entities.Service;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import javax.print.Doc;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;

public class MongoDBConnection
{

    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson());
    }
    private static Consumer<Document> printFormattedDocuments() {
            return doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
    }

    //Consumer<Document> printFormattedDocuments;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private ArrayList<Office> offices = new ArrayList<Office>();
    private ArrayList<Service> services = new ArrayList<Service>();
    private ArrayList<Service> servicesWorker = new ArrayList<Service>();

    public String pattern = "dd/MM/yyyy";
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public MongoDBConnection(String database){
        //mongoClient = MongoClients.create();
         mongoClient = MongoClients.create(
                "mongodb://172.16.3.134:27022, 172.16.3.135:27022, 172.16.3.137:27022/");

        db = mongoClient.getDatabase("RentNGO").withReadPreference(ReadPreference.nearest()).withWriteConcern(WriteConcern.W2);

        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        }; /*
        MongoCollection<Document> myColl = db.getCollection("offices");
        MongoCursor<Document> cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Office o = new Office();
            //o.setCapacity(Integer.valueOf(d.getString("Capacity")));
            o.setCity(d.getString("City"));
            o.setId(d.getString("ID"));
            o.setName(d.getString("Name"));
            o.setRegion(d.getString("Region"));
            o.setPosition(Integer.valueOf(d.getString("Position")));
            offices.add(o);
        }

        myColl = db.getCollection("services");
        cursor  = myColl.find().iterator();
        int i=0;
        while(cursor.hasNext()){
            Document d = cursor.next();
            Service s = new Service();
            s.setMultiplicator(d.getString("MULTIPLICATOR"));
            s.setPrice(Double.valueOf(d.getString("PRICE VAT INCLUDED ")));
            s.setNameService(d.getString("SERVICES"));
            switch (s.getNameService()){
                case "Deductible for insolvency or passive claim / car accident":
                    servicesWorker.add(s);
                    break;
                case "Administrative expenses for fines/tolls/parking":
                    servicesWorker.add(s);
                    break;
                case "Special vehicle clean":
                    servicesWorker.add(s);
                    break;
                case "Nav system loss":
                    servicesWorker.add(s);
                    break;
                case "Refective Jacket Loss":
                    servicesWorker.add(s);
                    break;
                case "Administrative expenses for damages":
                    servicesWorker.add(s);
                    break;
                case "One Way Same Area":
                    servicesWorker.add(s);
                    break;
                case "One Way Mainland":
                    servicesWorker.add(s);
                    break;
                case "One Way Mainland-Island":
                    servicesWorker.add(s);
                    break;
                case "Plate loss":
                    servicesWorker.add(s);
                    break;
                case "Keys Loss":
                    servicesWorker.add(s);
                    break;
                case "Car documents loss":
                    servicesWorker.add(s);
                    break;
                case "Truck Service":
                    servicesWorker.add(s);
                    break;
                default:
                    if(!s.getNameService().equals("Young Driver 19/20") && !s.getNameService().equals("Young Driver 21/24"))
                        services.add(s);
            }
            i++;
            //services.add(s);
        }
       */
    }

    // String

    public ArrayList<Service> getServicesWorker(){
        ArrayList<Service> servicesUser = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("services");
        MongoCursor<Document> cursor  = myColl.find(eq("PAYER", "worker")).iterator();
        while(cursor.hasNext()){
            Service s = new Service();
            Document d = cursor.next();
            s.setName(d.getString("SERVICES"));
            s.setPrice(d.getDouble("PRICE VAT INCLUDED "));
            s.setMultiplicator(d.getString("MULTIPLICATOR"));
            servicesUser.add(s);
        }
        return servicesUser;
    }

    public void closeConnection(){
        mongoClient.close();
    }


    public User findUser(String email){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(and(eq("Email", email), exists("DateOfHiring", false))).iterator();
        User u;

        if (!cursor.hasNext()) {

            return null;
        } else {
            Document d = cursor.next();
            u = createUser(d);
        }
        //u.printUser();

        return u;
    }

    public Worker findWorker(String email) throws ParseException {
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(and(eq("Email", email), exists("DateOfHiring", true))).iterator();
        Worker w;

        if (!cursor.hasNext()) {
            return null;
        } else {
            Document d = cursor.next();
            w = createWorker(d);
        }

        return w;
    }



    private Worker createWorker(Document d) throws ParseException {
        //String surname, String name, String email, String password, Date dateOfBirth
        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfBirth"));
        Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfHiring"));

        Worker w = new Worker(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date1, Integer.valueOf(d.getString("Salary")),date2, d.getString("Office"));
        return w;
    }

    public void deleteWorker(String email) {
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        myColl.deleteOne(eq("Email", email));

    }

    public void deleteOffice(String name) {
        MongoCollection<Document> myColl = db.getCollection("offices");
        MongoCursor<Document> cursor  = myColl.find(eq("Name", name)).iterator();
        myColl.deleteOne(eq("Name", name));
        //System.out.println("Office deleted successfully");
    }

    private Admin createAdmin(Document d) throws ParseException {
        //String surname, String name, String email, String password, Date dateOfBirth
        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfBirth"));
        Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfHiring"));
        Date date3=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateWorkerToAdmin"));
        Admin a = new Admin(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date1, Integer.valueOf(d.getString("Salary")), date2, "",date3);
        return a;
    }

    public Admin findAdmin(String email) throws ParseException {
        MongoCollection<Document> myColl = db.getCollection("admins");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        Admin a;

        if (!cursor.hasNext()) {
            //System.out.println("Admin not found");
            return null;
        } else {
            Document d = cursor.next();
            a = createAdmin(d);
        }

        return a;
    }

    public void deleteAdmin(String email) {
        MongoCollection<Document> myColl = db.getCollection("admins");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        myColl.deleteOne(eq("Email", email));
        //System.out.println("User deleted successfully");
    }

    public void deleteUser(String email) {
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        myColl.deleteOne(eq("Email", email));
        //System.out.println("User deleted successfully");
    }


    private User createUser(Document d) {
        ////////
        //////// errore d.getDate("DateOfBirth")
        ////////
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = formatter.parse(d.getString("DateOfBirth"));
        } catch (ParseException e){
            System.out.println("Error");
            return null;
        }
        User u = new User(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date);
        return u;
    }

    public boolean insertAdmin(Admin admin)  {
        MongoCollection<Document> myColl = db.getCollection("users");

        //check email
        MongoCursor<Document> cursor = myColl.find(eq("Email", admin.getEmail())).iterator();
        if(cursor.hasNext()){
            System.out.println("Worker already present in the database");
            return false;
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Document adminMongo = new Document("Name", admin.getName())
                .append("Surname", admin.getSurname())
                .append("Email", admin.getEmail())
                .append("Password", admin.getPassword())
                .append("DateOfBirth",formatter.format(admin.getDateOfBirth()))
                .append("Salary", String.valueOf(admin.getSalary()))
                .append("Date of hiring", formatter.format(admin.getHiringDate()))
                .append("Date WtoA", formatter.format(admin.getWorkertoAdmin()));

        myColl.insertOne(adminMongo);

        return true;
    }

    public boolean insertWorker(Worker worker)  {
        MongoCollection<Document> myColl = db.getCollection("users");

        //check email
        MongoCursor<Document> cursor = myColl.find(eq("Email", worker.getEmail())).iterator();
        if(cursor.hasNext()){
            System.out.println("Worker already present in the database");
            return false;
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Document workerMongo = new Document("Name", worker.getName())
                .append("Surname", worker.getSurname())
                .append("Email", worker.getEmail())
                .append("Password", worker.getPassword())
                .append("DateOfBirth",formatter.format(worker.getDateOfBirth()))
                .append("Salary", String.valueOf(worker.getSalary()))
                .append("DateOfHiring", formatter.format(worker.getHiringDate()))
                .append("Office", String.valueOf(worker.getOffice()));

        myColl.insertOne(workerMongo);

        return true;
    }

    public boolean insertOffice(Office office)  {
        MongoCollection<Document> myColl = db.getCollection("offices");

        //check email
        MongoCursor<Document> cursor = myColl.find(eq("Name", office.getName())).iterator();
        if(cursor.hasNext()){
            System.out.println("Office already present in the database");
            return false;
        }

        Document officeMongo = new Document("City", office.getCity())
                .append("Region", office.getRegion())
                .append("ID", office.getId())
                .append("Name", office.getName())
                .append("Capacity",office.getCapacity());

        myColl.insertOne(officeMongo);

        return true;
    }

    public boolean insertUser(User u)  {
        //

        //check email

        /*
        MongoCursor<Document> cursor = myColl.find(eq("Email", u.getEmail())).iterator();

        if(cursor.hasNext()){
            System.out.println("User already present in the database");
            return false;
        }
        */

        User user1 = findUser(u.getEmail());
        if (user1 != null){
            System.out.println("User already present in the database");
            return false;
        }
        MongoCollection<Document> myColl = db.getCollection("users");
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Document user = new Document("Name", u.getName())
                    .append("Surname", u.getSurname())
                    .append("Email", u.getEmail())
                    .append("Password", u.getPassword())
                    .append("DateOfBirth",formatter.format(u.getDateOfBirth()));
        myColl.insertOne(user);
        return true;
    }

    public User logInUser(String email, String password){
        User u = new User();
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(and(eq("Email", email),
                eq("Password", password))).iterator();
        if(!cursor.hasNext()) {
            System.out.println("Email or Password wrong, try again");
        } else{
            Document d = cursor.next();
            u = createUser(d);
        }
        return u;
    }

    public ArrayList<Office> listOffices(){
        ArrayList<Office> offices = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("offices");
        MongoCursor<Document> cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Office o = new Office();
            o.setCapacity(d.getString("Capacity"));
            o.setCity(d.getString("City"));
            o.setId(d.getString("ID"));
            o.setName(d.getString("Name"));
            o.setRegion(d.getString("Region"));
            offices.add(o);
        }
        return offices;
    }

    public ArrayList<Service> listServices(){
        return services;
    }

    public void deleteOrder(String email, int choice) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(eq("Email", email)).iterator();
        int i=0;
        while(cursor.hasNext()){



            Document d = cursor.next();
            if(choice == i){
                myColl.deleteOne(d);
            }
            i++;
        }

    }

    public void insertOrder(Order o, String status) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Document order = new Document("CarPlate", o.getCar())
                .append("Email", o.getUser())
                .append("CarPrice",o.getPriceCar())
                .append("StartOffice", o.getpickOffice())
                .append("PickDate", o.getPickDate().getTime())
                .append("EndOffice", o.getDeliveryOffice())
                .append("DeliveryDate", o.getDeliveryDate().getTime())
                .append("PriceAccessories", o.getPriceAccessories())
                .append("ListAccessories", o.getAccessories())
                .append("Status", status);
        myColl.insertOne(order);
    }


    public void findCarByBrand(String brand){
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq("Brand", brand)).iterator();
        Car c;
        int i = 0;
        int choice=0;
        Scanner sc = new Scanner(System.in);
        Document d=null;
        List<Document> plates = null;

        if (cursor.hasNext()){
            d = cursor.next();
            plates = d.get("cars", List.class);
        }

        for(int j=0; i<plates.size(); j++) {

            System.out.print(i + ") ");
            Document car = plates.get(i);

            c = getCarFromDocument(car.getString("CarPlate"), d);


            c.printCar();

            if ((i + 1) % 10 == 0) {
                    System.out.println("(Press -2 to exit, -1 to continue)");
                        try {
                            choice = Integer.valueOf(sc.nextLine());
                        } catch(Exception e){
                            System.out.println("Didn't insert and integer");
                            choice = -2;
                        }


                }
                if (choice == -2) {
                    break;
                }
                i++;
        }

        System.out.println();
    }

    public Car findCar(String plate){
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq("cars.CarPlate", plate)).iterator();
        Car c;

        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return null;
        } else {
            Document d = cursor.next();
            c = getCarFromDocument(plate, d);
        }

        //c.printCar();
        System.out.println();

        return c;
    }

    public void deleteCar(String plate) {
        /*
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq("cars.CarPlate", plate)).iterator();
        Car c;
        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return ;
        } else {
            myColl.deleteOne(eq("CarPlate", plate));
            System.out.println("Car deleted successfully");
        }*/
        Car c = findCar(plate);
        if (c==null){
            System.out.println("Car not found!");
        }
        MongoCollection<Document> myColl = db.getCollection("cars");

        Bson filter = Filters.and( eq("Brand", c.getBrand()), eq("Vehicle", c.getVehicle())); //get the parent-document
        Bson delete = Updates.pull("cars", new Document("CarPlate", c.getPlate()).append("RegistrationYear", c.getRegistrationYear()).append("Office", c.getOffice()));
        myColl.updateOne(filter, delete);

    }



   /* public void deleteOrders(){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            if(d.getString("CarPlate").contains("@")){
                myColl.deleteOne(eq("CarPlate", d.getString("CarPlate")));
            }
        }
    } */

    public void insertNewCar(Car c) {

        MongoCollection<Document> myColl = db.getCollection("cars");

        try (MongoCursor<Document> cursor = myColl.find(eq("cars.CarPlate", c.getPlate())).iterator()) {
            while (cursor.hasNext()) {
                System.out.println("Car's plate already present in the database");
                return;
            }
        }
        Document carEmbedded = new Document("CarPlate", c.getPlate())
                .append("RegistrationYear", c.getRegistrationYear())
                .append("Office", c.getOffice());

        MongoCursor<Document> cursor = myColl.find(and(eq("Brand", c.getBrand()), eq("Vehicle", c.getVehicle()))).iterator();

        if (!cursor.hasNext()) {
            Document car = new Document("Brand", c.getBrand().trim())
                    .append("Vehicle", c.getVehicle().trim())
                    .append("Engine", c.getEngine().trim())
                    .append("Power (hp - kW /rpm)", c.getPower().trim())
                    .append("AverageFuelConsumption", Double.valueOf(c.getAvgFuelCons()))
                    .append("CO2", Double.valueOf(c.getCo2()))
                    .append("Weight(3p/5p) kg", c.getWeight().trim())
                    .append("GearBox type", c.getGearBoxType().trim())
                    .append("Tyre", c.getTyre().trim())
                    .append("Traction type", c.getTractionType().trim());

            myColl.insertOne(car);
            Bson filter = Filters.and(eq("Brand", c.getBrand()), eq("Engine", c.getEngine()), eq("Vehicle", c.getVehicle())); //get the parent-document
            Bson setUpdate = Updates.push("cars", carEmbedded);

            myColl.updateOne(filter, setUpdate);
        } else {
            Bson filter = Filters.and( eq("Brand", c.getBrand()), eq("Engine", c.getEngine()), eq("Vehicle", c.getVehicle())); //get the parent-document
            Bson setUpdate = Updates.push("cars", carEmbedded);

            myColl.updateOne(filter, setUpdate);
        }
        System.out.println();
    }

    public void getMostUsedCars(int i) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Bson b1 = sort(descending("nUsed"));
        Bson b2 = group("$CarPlate", sum("nUsed", 1));
        Bson project = project(fields(include("CarPlate", "nUsed")));
        Bson b3 = limit(i);
        //myColl.aggregate(Arrays.asList(b2, project, b1, b3))
         //       .forEach(printFormattedDocuments);
    }

    public void getCarsOutOfDate(Long currentDate){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(and(lt("DeliveryDate", currentDate),
                                                        ne("Status", "Completed"))).iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Date date = new Date(d.getLong("DeliveryDate"));
            System.out.println("CarPlate: "+ d.getString("CarPlate") + " ,DateDelivery: "+ date + " ,Status: " + d.getString("Status"));
        }
    }


    public ArrayList<Car> getListOfCars(String office, int category,  long pickDate,long deliveryDate, String brand) {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor;
        if(brand.equals("")) {
            cursor = myColl.find(and(eq("cars.Office", office), exists("maintenance", false))).iterator();
        }else{
            cursor = myColl.find(and(eq("cars.Office", office), eq("Brand", brand), exists("maintenance", false))).iterator();
        }
        ArrayList<Car> carsAvail = new ArrayList<>();
        boolean check =true;
        while(cursor.hasNext()){
            Document d = cursor.next();
            if(checkIfCarRented(d,pickDate, deliveryDate)==true) {
                Car c = getCarFromDocument("", d);
                Double kw = c.getKw(this);
                switch (category) {
                    case 1:
                        if (kw < 75.0)
                            carsAvail.add(c);
                        break;
                    case 2:
                        if (kw > 75.0 && kw <= 120.0)
                            carsAvail.add(c);
                        break;
                    case 3:
                        if (kw > 120.0)
                            carsAvail.add(c);
                        break;
                    default:
                        carsAvail.add(c);
                }
            }



        }
        System.out.println(carsAvail.size());
        return carsAvail;
    }



    public void updateUser(){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            myColl.updateOne(eq("Email", d.get("Email")), set("Email", d.get("Email").toString().trim()));
            myColl.updateOne(eq("Name", d.get("Name")), set("Name", d.get("Name").toString().trim()));
            myColl.updateOne(eq("Password", d.get("Password")), set("Password", d.get("Password").toString().trim()));
            myColl.updateOne(eq("DateOfBirth", d.get("DateOfBirth")), set("DateOfBirth", d.get("DateOfBirth").toString().trim()));
        }
    }


    public Car getCarFromDocument(String plate, Document d){
        Car c = new Car("", d.getString("Brand"),
                d.getString("Vehicle"),
                d.getString("Engine"),
                String.valueOf(d.getDouble("AverageFuelConsumption")),
                String.valueOf(d.getDouble("CO2")),
                d.getString("Weight(3p/5p) kg"),
                d.getString("GearBox type"),
                d.getString("Tyre"),
                d.getString("Traction type"),
                d.getString("Power (hp - kW /rpm)"),
                0,
                "");

        List<Document> cars = d.get("cars", List.class);

        if (plate.equals("")){
            for(int i = 0; i< cars.size(); i++) {
                c.setPlate(cars.get(i).getString("CarPlate"));
                c.setOffice(cars.get(i).getString("Office"));
                c.setRegistrationYear(cars.get(i).getInteger("RegistrationYear"));
            }
        } else{
            for(int i = 0; i< cars.size(); i++){
                if (cars.get(i).getString("CarPlate").equals(plate)){
                    c.setPlate(cars.get(i).getString("CarPlate"));
                    c.setOffice(cars.get(i).getString("Office"));
                    c.setRegistrationYear(cars.get(i).getInteger("RegistrationYear"));
                    break;
                }
            }
        }
        /*
        for(int i = 0; i< cars.size(); i++) {
                if(!cars.get(i).getString("CarPlate").equals(plate) || !plate.equals(""))
                    c.setPlate(d.getString("CarPlate"));
                    c.setOffice(d.getString("Office"));
                    c.setRegistrationYear(d.getInteger("RegistrationYear"));
        }*/
        return c;
        }


    public User getUser(ArrayList<String> s) throws ParseException {
        if(s == null)
            return null;

        User us = findUser(s.get(0));
        if(us != null){
            if(s.get(1).equals(us.getPassword())) {
                us.printUser();
                return new User(us.getSurname(), us.getName(), us.getEmail(), us.getPassword(), us.getDateOfBirth());
            }
            return null;
            //u=us;
        }

        Worker w = findWorker(s.get(0));
        if(w != null){
            if(s.get(1).equals(w.getPassword())) {
                w.printUser();
                return new Worker(w.getSurname(), w.getName(), w.getEmail(), w.getPassword(), w.getDateOfBirth(), w.getSalary(), w.getHiringDate(), w.getOffice());
            }
        }

        Admin a = findAdmin(s.get(0));
        if(a != null){
            if(s.get(1).equals(a.getPassword())) {
                a.printUser();
                return new Admin(a.getSurname(), a.getName(), a.getEmail(), a.getPassword(), a.getDateOfBirth(), a.getSalary(), a.getHiringDate(), "",a.getWtoAdDate());
            }
        }
        //System.out.println("User not found");
        return null;
    }

    public void showListOrders(String email) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(eq("Email", email)).iterator();
        int i=0;
        while(cursor.hasNext()){
            String format = "%-40s%n";
            Document d = cursor.next();
            System.out.print(i + ") ");
            Document cars = d.get("CarPlate", Document.class); // if there are some cars (carPlates)
            System.out.printf(format, "CarPlate: " +cars.getString("CarPlate") + " ");
            System.out.printf(format, "Brand: " + cars.getString("Brand") + " ");
            System.out.printf(format, "Vehicle: " + cars.getString("Vehicle") + " ");

            Double carPrice = 0.0;
            try{
                carPrice = d.getDouble("CarPrice");
            } catch (Exception e){
                carPrice = Double.valueOf((d.getInteger("CarPrice")));
            }
            System.out.printf(format, "CarPrice: " + Math.ceil(carPrice) + "€ ");

            Long datePick = 0L;
            try{
                datePick = d.getLong("PickDate");
            } catch (Exception e){
                datePick = d.getDouble("PickDate").longValue();
            }
            Date pick = new Date(datePick);
            System.out.printf(format, "PickDate: " + simpleDateFormat.format(pick) + " ");

            Long dateDelivery = 0L;
            try{
                dateDelivery = d.getLong("DeliveryDate");
            } catch (Exception e){
                dateDelivery = d.getDouble("DeliveryDate").longValue();
            }
            Date delivery = new Date(dateDelivery);
            System.out.printf(format, "DateDelivery: " + simpleDateFormat.format(delivery) + " ");

            System.out.printf(format, "StartOffice: " + d.getString("StartOffice") + " ");
            System.out.printf(format, "EndOffice: " + d.getString("EndOffice") + " ");

            List<Document> accessories = d.get("Accessories", List.class);
            if(accessories!=null) {
                System.out.println("SERVICES: ");
                for (Document service : accessories) {
                    System.out.println("\tSERVICE NAME: " + service.getString("SERVICES"));
                    Double priceAccessories;

                    try{
                        priceAccessories = service.getDouble("PRICE VAT INCLUDED ");
                    } catch (Exception e){
                        priceAccessories = Double.valueOf((service.getString("PRICE VAT INCLUDED ")));
                    }

                    System.out.println("\tPRICE VAT INCLUDED: " + priceAccessories + "€");
                }
            }
            System.out.println();
            i++;
        }
        System.out.println();
    }

    public Office findOfficeByName(String name){
        MongoCollection<Document> myColl = db.getCollection("offices");
        MongoCursor<Document> cursor  = myColl.find(eq("Name", name.trim())).iterator();
        Office o;

        if (!cursor.hasNext()) {
            System.out.println("Office not found");
            return null;
        } else {
            Document d = cursor.next();
            o = getOfficeFromDocument(d);
        }


        System.out.println();

        return o;
    }


    public Office getOfficeFromDocument(Document d){
        Office o = new Office(d.getString("City"), d.getString("Region"), d.getString("ID"),
                d.getString("Name"), d.getString("Capacity"));
        return o;
    }

    public void updateWorkerSalary(int newSalary, String workerEmail){
        MongoCollection<Document> myColl = db.getCollection("users");
        myColl.updateOne(
                (eq("Email", workerEmail)), set("Salary", String.valueOf(newSalary)));
    }

    public void getMostUsedCarsPerOffice(String startOffice, long date) {
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        //Bson match1 = match();
        MongoCollection<Document> myColl = db.getCollection("orders");
        Bson sort = sort(descending(Arrays.asList("count")));
        Bson group = group(Arrays.asList("$StartOffice", "$CarPlate"), sum("count", 1));
        Bson match= match(and(eq("StartOffice", startOffice), ne("Status", "Maintenance"),
                gt("PickDate", date)));
        Bson project = project(fields(include("_id", "count")));
        Bson limit = limit(3);
        myColl.aggregate(Arrays.asList( match,group, sort, limit))
               .forEach(printFormattedDocuments);

    }


    public void getLessEcoFriendlyOffice(){ //BETTER LOWER ECO FRENDLY
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        MongoCollection<Document> myColl = db.getCollection("cars");
        Bson sort = sort(descending("AvgCO2"));
        Bson group = group("$Office", avg("AvgCO2", "$CO2"));
        //Bson project = project(fields(include( "AvgCO2")));
        Bson limit = limit(3);

        Bson lookup = lookup(
                "offices",
                "_id",
                "Position",
                "Office"
        );
        Bson project = project(fields(include("AvgCO2", "Office.City", "Office.Region", "Office.Name"), excludeId()));

        myColl.aggregate(Arrays.asList( group, sort, limit, lookup, project))
                .forEach(printFormattedDocuments);

    }



    public void searchUserForDiscount(long currentDate, long lastYearDate){
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        Bson match = match(ne("Status", "Maintenance"));
        Bson group = group("$Email", sum("countCurrent", 1));
        Bson group2 = group("$Email", sum("countPrev", 1));
        Bson matchCurrent = match(gt("PickDate", currentDate));
        Bson matchPrev = match(and(gt("PickDate", lastYearDate),
                lt("PickDate", currentDate)));

        MongoCollection<Document> collection = db.getCollection("orders");

        collection.aggregate(Arrays.asList(
                match,
                matchCurrent,
                group,
                out("currentYear"))).toCollection();

        collection.aggregate(Arrays.asList(
                match,
                matchPrev,
                group2,
                out("prevYear"))).toCollection();

        Bson merge = merge("prevYear");

        MongoCollection<Document> myColl = db.getCollection("currentYear");
        MongoCursor<Document> cursor = myColl.aggregate(Arrays.asList( merge)).cursor();
        while(cursor.hasNext()){
            Document doc = cursor.next();
            if(doc.getInteger("countPrev") != null && doc.getInteger("countCurrent")!=null
                    && (doc.getInteger("countPrev") - doc.getInteger("countCurrent")) > 4){
                System.out.println("User: "+ doc.getString("_id")
                 + ", Start Year Rent: "+ doc.getInteger("countCurrent") + ", End Year Rent: "
                        + doc.getInteger("countPrev"));
            }
        }
    }
    

    public void changeStatusOrder(String carPlate, String email, String field, Date d, String status, ArrayList<Service> damage, double damageCost){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(and(eq("CarPlate.CarPlate", carPlate), lt(field, d.getTime()+30*1000*60*60), gt(field, d.getTime()-30*1000*60*60), eq("Email", email))).iterator();
        if(cursor.hasNext()) {
            System.out.println("Order found");
            myColl.updateOne(
                    (and(eq("CarPlate.CarPlate", carPlate), lt(field, d.getTime() + 30 * 1000 * 60 * 60), gt(field, d.getTime() - 30 * 1000 * 60 * 60), eq("Email", email)))
                    , set("Status", status));
            if(damage!=null) {
                ArrayList<Document> documents = new ArrayList<>();
                List<Document> docum = cursor.next().get("Accessories", List.class);
                for(Document doc: docum){
                    documents.add(doc);
                }
                for(Service s: damage) {
                    Document doc = new Document("SERVICES", s.getNameService()).append("PRICE VAT INCLUDED ", s.getPrice())
                            .append("MULTIPLICATOR", s.getMultiplicator());
                    documents.add(doc);

                }
                    myColl.updateOne(
                            (and(eq("CarPlate.CarPlate", carPlate), lt(field, d.getTime() + 12 * 1000 * 60 * 60), gt(field, d.getTime() - 12 * 1000 * 60 * 60), eq("Email", email)))
                            , set("Accessories", documents));
                if(damageCost!=0) {
                    myColl.updateOne(
                            (and(eq("CarPlate.CarPlate", carPlate), lt(field, d.getTime() + 12 * 1000 * 60 * 60), gt(field, d.getTime() - 12 * 1000 * 60 * 60), eq("Email", email)))
                            , set("DelayCost", damageCost));
                }
            }
        }
        System.out.println("Operation completed");
    }

    public void showListOrdersByParameters(String carplate, String pickOffice, Long pickDate) {
        if (carplate != null){
            MongoCollection<Document> myColl = db.getCollection("orders");
            MongoCursor<Document> cursor = myColl.find(eq("CarPlate.CarPlate", carplate)).iterator();
            int j=0;
            while(cursor.hasNext()){
                Document d = cursor.next();
                String format = "%-40s%n";
                System.out.print(j + ") ");

                Document car = (Document) d.get("CarPlate");
                System.out.printf(format, "CarPlate: " + car.getString("CarPlate") + " ");

                System.out.printf(format, "Email: " + d.getString("Email") + " ");

                Double carPrice = 0.0;
                try{
                    carPrice = d.getDouble("CarPrice");
                } catch (Exception e){
                    carPrice = Double.valueOf((d.getInteger("CarPrice")));
                }
                System.out.printf(format, "CarPrice: " + Math.ceil(carPrice) + "€ ");

                Long datePick = 0L;
                try{
                    datePick = d.getLong("PickDate");
                } catch (Exception e){
                    datePick = d.getDouble("PickDate").longValue();
                }
                Date pick = new Date(datePick);
                System.out.printf(format, "PickDate: " + simpleDateFormat.format(pick) + " ");

                Long dateDelivery = 0L;
                try{
                    dateDelivery = d.getLong("DeliveryDate");
                } catch (Exception e){
                    dateDelivery = d.getDouble("DeliveryDate").longValue();
                }
                Date delivery = new Date(dateDelivery);

                System.out.printf(format,"DatePick: " + simpleDateFormat.format(delivery) + " ");


                System.out.printf(format,"StartOffice: " + d.getString("StartOffice") + " ");
                System.out.printf(format, "EndOffice: " + d.getString("EndOffice") + " ");
                //System.out.printf(format,"PriceAccessories: " + d.getInteger("PriceAccessories") + " ");
                Double priceAccessories = 0.0;
                try{
                    priceAccessories = d.getDouble("PriceAccessories");
                } catch (Exception e){
                    priceAccessories = Double.valueOf((d.getInteger("PriceAccessories")));
                }

                System.out.printf(format, "PriceAccessories: " + priceAccessories + "€ ");
                List<Document> accessories = d.get("Accessories", List.class);
                if(accessories!=null) {
                    System.out.println("SERVICES: ");
                    for (Document service : accessories) {
                        System.out.println("\tSERVICE NAME: " + service.getString("SERVICES"));

                    try{
                        priceAccessories = d.getDouble(service.getDouble("PRICE VAT INCLUDED "));
                    } catch (Exception e){
                        priceAccessories = Double.valueOf((service.getString("PRICE VAT INCLUDED ")));
                    }


                        System.out.println("\tPRICE VAT INCLUDED: " + priceAccessories + "€");
                    }
                }
                System.out.println();
                j++;
            }
        } else {
            MongoCollection<Document> myColl = db.getCollection("orders");
            MongoCursor<Document> cursor = myColl.find(and(eq("StartOffice", pickOffice.trim()), gt("PickDate", (pickDate-1000*60*60*12)), lt("PickDate", (pickDate+1000*60*60*12)), exists("CarPlate.CarPlate"))).iterator();
            int j = 0;
            while (cursor.hasNext()) {
                Document d = cursor.next();
                String format = "%-40s%n";
                System.out.print(j + ") ");

                Document car = (Document) d.get("CarPlate");
                System.out.printf(format, "CarPlate: " + car.getString("CarPlate") + " ");
                System.out.printf(format, "Email: " + d.getString("Email") + " ");

                Double carPrice = 0.0;
                try{
                    carPrice = d.getDouble("CarPrice");
                } catch (Exception e){
                    carPrice = Double.valueOf((d.getInteger("CarPrice")));
                }
                System.out.printf(format, "CarPrice: " + Math.ceil(carPrice) + "€ ");

                Long datePick = 0L;
                try{
                    datePick = d.getLong("PickDate");
                } catch (Exception e){
                    datePick = d.getDouble("PickDate").longValue();
                }
                Date pick = new Date(datePick);
                System.out.printf(format, "PickDate: " + simpleDateFormat.format(pick) + " ");

                Long dateDelivery = 0L;
                try{
                    dateDelivery = d.getLong("DeliveryDate");
                } catch (Exception e){
                    dateDelivery = d.getDouble("DeliveryDate").longValue();
                }
                Date delivery = new Date(dateDelivery);

                System.out.printf(format, "StartOffice: " + d.getString("StartOffice") + " ");
                System.out.printf(format,"EndOffice: " + d.getString("EndOffice") + " ");



                Double priceAccessories = 0.0;
                try{
                    priceAccessories = d.getDouble("PriceAccessories");
                } catch (Exception e){
                    priceAccessories = Double.valueOf((d.getInteger("PriceAccessories")));
                }
                System.out.printf(format, "PriceAccessories: " + priceAccessories + "€ ");

                List<Document> accessories = d.get("Accessories", List.class);
                if(accessories!=null) {
                    System.out.println("SERVICES: ");
                    for (Document service : accessories) {
                        System.out.println("\tSERVICE NAME: " + service.getString("SERVICES"));


                    try{
                        priceAccessories = d.getDouble(service.getDouble("PRICE VAT INCLUDED "));
                    } catch (Exception e){
                        priceAccessories = Double.valueOf((service.getString("PRICE VAT INCLUDED ")));
                    }

                        System.out.println("\tPRICE VAT INCLUDED: " + priceAccessories + "€");
                    }
                }
                System.out.println();
                j++;
            }
        }
        //System.out.println();
    }


    public ArrayList<Service> getServices() {
        ArrayList<Service> servicesUser = new ArrayList<>();
        MongoCollection<Document> myColl = db.getCollection("services");
        MongoCursor<Document> cursor  = myColl.find(exists("PAYER", false)).iterator();
        while(cursor.hasNext()){
            Service s = new Service();
            Document d = cursor.next();
            s.setName(d.getString("SERVICES"));
            s.setPrice(d.getDouble("PRICE VAT INCLUDED "));
            s.setMultiplicator(d.getString("MULTIPLICATOR"));
            servicesUser.add(s);
        }
        return servicesUser;
    }

    public ArrayList<Order> getListOfRecentOrders() {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Date d =new Date();
        MongoCursor<Document> cursor = myColl.find(gt("DeliveryDate", d.getTime())).iterator();
        ArrayList<Order> orders = new ArrayList<>();
        while(cursor.hasNext()){
            Order o =new Order();
            Document doc = cursor.next();
            o.setCar(doc.getString("CarPlate"));
            Date dPick = new Date(Long.valueOf(doc.getLong("PickDate")));
            o.setPickDate(dPick);
            Date dDelivery = new Date(Long.valueOf(doc.getLong("DeliveryDate")));
            o.setDeliveryDate(dDelivery);
            orders.add(o);
        }
        return orders;
    }

    public void updateWorkerOffice(String emailWorker,String office) {
        MongoCollection<Document> myColl = db.getCollection("users");
        myColl.updateOne(
                (eq("Email", emailWorker)), set("Office", office));
    }

    public void updateCarOffice(String carPlate, String office) {
        MongoCollection<Document> myColl = db.getCollection("cars");
        myColl.updateOne(
                (eq("cars.CarPlate", carPlate)), set("cars.$.Office", office));
    }

    public void showUsersOrdersForDate(String email, String plate, String office) {
        Car c = findCar(plate);
        if(c == null)
            return;
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(and(
                                                            eq("CarPlate.CarPlate", plate),
                                                            gte("DeliveryDate", new Date().getTime())
                                                      )
                                    ).iterator();

        while(cursor.hasNext()){
            Document d = cursor.next();
            Date d1 = new Date(d.getLong("PickDate"));
            Date d2 = new Date(d.getLong("DeliveryDate"));
            System.out.println("Email: " + d.getString("Email") + " ,date pick: " + d1 + ", date delivery: "+d2 );
            myColl.updateOne(and(eq("Email",d.getString("Email")),
                                 eq("CarPlate.CarPlate", plate),
                                eq("PickDate", d.getLong("PickDate"))), set("Status", "Deleted"));
        }
        // Add maintenance field
        myColl = db.getCollection("cars");
        Bson filter = Filters.eq("cars.CarPlate", plate);
        myColl.updateOne(filter, set("cars.$.maintenance", 1));
        //insertOrder(new Order(plate, email, 0.0, nameOffice, start,nameOffice, stop,  0.0, ""), "Maintenance");
    }


    public ArrayList<Car> getListOfAllCars() {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor = myColl.find().iterator();
        ArrayList<Car> cars = new ArrayList<>();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Car c = getCarFromDocument("",d);
            cars.add(c);
        }
        return cars;
    }

    public void showAllCarsInMaintenance() {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor = myColl.find(exists( "cars.maintenance", true)
        ).iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Car c = getCarFromDocument("", d);
            List<Document> documents = d.get("cars", List.class);
            for(Document car: documents){
                if(car.getInteger("maintenance")!=null){
                    c.setPlate(car.getString("CarPlate"));
                    c.printCar();
                }
            }
        }
        System.out.println("");
    }

    public boolean checkIfCarRented(Document d, long pickDate, long deliveryDate)  {
        boolean check = true;
        List<Document> cars = d.get("cars", List.class); // if there are some cars (carPlates)
        for(Document doc: cars){
            List<Document> availability = doc.get("availability", List.class);
            if (availability != null && !availability.isEmpty()) {
                for (Document avail : availability) { // check if car is available in that period
                    Long dateP = avail.getLong("pickDate");
                    Long dateD = avail.getLong("deliveryDate");
                    if ((
                            (dateP <= pickDate) && (dateD >= pickDate)
                    ) ||
                            (
                                    (dateD >= deliveryDate) && (dateP <= deliveryDate)
                            ) ||
                            (
                                    (pickDate <= dateP) && (deliveryDate >= dateD)
                            )
                    ) {
                        check = false;
                    }
                }
            }
            if (check == true) {
                return true;
            }
            check= true;
        }
        return false;
    }

    public User login(String email, String password) throws ParseException {
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor = myColl.find(and(
                eq("Email", email),
                eq("Password", password)
        )).iterator();
        if(cursor.hasNext()){
            Document d = cursor.next();
            if(d.getString("DateOfHiring") == null){
                return createUser(d);
            }
            else if(d.getString("DateWorkerToAdmin") == null){
                return createWorker(d);
            } else {
                return createAdmin(d);
            }
        }
        return null;
    }

    public void procedeWithOrder(Car c, Long dateOfPick,Long dateOfDelivery, String email, String pickOffice, String deliveryOffice, ArrayList<Service> services) {
        if(c.getBrand() == null || c.getVehicle()== null) {
            System.out.println("Error");
            return;
        }
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(and(
                eq("Brand", c.getBrand()),
                eq("Vehicle", c.getVehicle()),
                eq("Power (hp - kW /rpm)", c.getPower())
                )
        ).iterator();
        Boolean check= true;
        if(cursor.hasNext()) { // if the car with that brand and that vehicle exists
            Document d = cursor.next();
            List<Document> cars = d.get("cars", List.class); // if there are some cars (carPlates)
            for(Document doc: cars){
                if(doc.getString("Office").equals(pickOffice)) {
                    List<Document> availability = doc.get("availability", List.class);
                    if (availability != null && !availability.isEmpty()) {
                        for (Document avail : availability) { // check if car is available in that period
                            Long dateP = avail.getLong("pickDate");
                            Long dateD = avail.getLong("deliveryDate");
                            if ((
                                    (dateP <= dateOfPick) && (dateD >= dateOfPick)
                            ) ||
                                    (
                                            (dateD >= dateOfDelivery) && (dateP <= dateOfDelivery)
                                    ) ||
                                    (
                                            (dateOfPick <= dateP) && (dateOfDelivery >= dateD)
                                    )
                            ) {
                                check = false;
                            }
                        }
                    }
                    if (check == true) {
                        String plate = doc.getString("CarPlate");
                        //add new dates
                        Bson filter = Filters.eq("cars.CarPlate", plate); //get the parent-document
                        if (availability != null && !availability.isEmpty()) {
                            Bson setUpdate = Updates.push("cars.$.availability", new Document("pickDate", dateOfPick).append(
                                    "deliveryDate", dateOfDelivery
                            ));
                            myColl.updateOne(filter, setUpdate);
                        } else {
                            ArrayList<Document> documents = new ArrayList<>();
                            documents.add(new Document("pickDate", dateOfPick).append(
                                    "deliveryDate", dateOfDelivery
                            ));
                            myColl.updateOne(filter, set("cars.$.availability", documents));
                        }

                        insertNewOrder(plate, c.getBrand(), c.getVehicle(), email, dateOfPick, dateOfDelivery, pickOffice, deliveryOffice,
                                "Booked", Math.ceil(c.calcolatePrice()), services);
                        return;
                    }
                }
                check= true;
            }
        }
        System.out.println("Car Already Rented. Please choose another one or select different dates");
    }

    public void insertNewOrder(String plate, String brand, String vehicle, String email, Long dateOfPick, Long dateOfDelivery, String pickOffice, String deliveryOffice,
    String status, Double price, ArrayList<Service> services){
        MongoCollection<Document> myColl = db.getCollection("orders");
        ArrayList<Document> documents = new ArrayList<>();
        Double priceAccessories = 0.0;
        for(Service s: services){
            Document d = new Document("SERVICES", s.getNameService()).append("PRICE VAT INCLUDED ", s.getPrice())
                    .append("MULTIPLICATOR", s.getMultiplicator());
            priceAccessories += s.getPrice();
            documents.add(d);
        }
        Document order = new Document("CarPlate", new Document("CarPlate", plate).append("Brand", brand).append("Vehicle", vehicle))
                .append("Email", email)
                .append("CarPrice", price)
                .append("StartOffice", pickOffice)
                .append("PickDate", dateOfPick)
                .append("EndOffice", deliveryOffice)
                .append("DeliveryDate", dateOfDelivery)
                .append("Status", status);
        if(documents!=null || !documents.isEmpty()){
            order.append("Accessories",documents);
            order.append("PriceAccessories", priceAccessories);
        }

        myColl.insertOne(order);
        System.out.println("Order completed successfully");
    }

    public void promoteWorker(Admin a) {
        MongoCollection<Document> listUsers = db.getCollection("users");
        Bson filter = Filters.and( eq("Email", a.getEmail()));
        Bson update1 = Updates.combine(
                Updates.set("Salary", String.valueOf(a.getSalary())),
                Updates.set("DateWorkerToAdmin", simpleDateFormat.format(a.getWorkertoAdmin()))
        );
        listUsers.updateOne(filter, update1);
    }


/*
    public User logIn(ArrayList<String> parameters){
        String email = parameters.get(0);
        User u = findUser(email);
        if (u==null) {
            u = findWorker(email);
            if (u == null) {
                u = findAdmin(email);
            }
        }
        return u;
    }




    public void createWorker(){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCollection<Document> myColl1 = db.getCollection("workers");


        MongoCursor<Document> cursor  = myColl.find().limit(50).iterator();

        while (cursor.hasNext()) {

            Document d = cursor.next();
            d.remove("_id");
            d.append("Salary", 1200);
            d.append("Date of hiring", "01/01/2000");

            myColl1.insertOne(d);
            deleteUser(d.getString("Email"));
        }

    }

    public void createAdmin(){
    MongoCollection<Document> myColl = db.getCollection("users");
    MongoCollection<Document> myColl1 = db.getCollection("admins");


    MongoCursor<Document> cursor  = myColl.find().limit(20).iterator();

    while (cursor.hasNext()) {

        Document d = cursor.next();
        d.remove("_id");
        d.append("Salary", 2000);
        d.append("Date of hiring", "01/01/2000");
        d.append("Date WtoA", "01/01/2010");

        myColl1.insertOne(d);
        deleteUser(d.getString("Email"));
    }

    }
*/



}
