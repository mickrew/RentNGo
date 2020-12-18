package main.java;

public class RentNGo {
    static private MongoDBConnection db;
    static private LevelDBConnection ldb;

    public static void main(String args[]){
        db = new MongoDBConnection("local");
        //ldb.openDB();

        System.out.println("Add car");
        //db.insertNewCar();
        db.deleteCar("AA111AA");
    }
}
