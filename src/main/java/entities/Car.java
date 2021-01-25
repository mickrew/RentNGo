package main.java.entities;

import main.java.connections.MongoDBConnection;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Car {
    private
        String plate;
        String brand;
        String vehicle;
        String engine;
        String power;
        String avgFuelCons;
        String co2;
        String weight;
        String gearBoxType;
        String tyre;
        String tractionType;
        Integer registrationYear;
        Integer office;



    public
        Car(){
        }

        public Car(String plate, String brand, String vehicle, String engine, String avgFuelCons, String co2, String weight
                , String gearBoxType, String tyre, String tractionType, String power, Integer registrationYear, Integer office){
            this.plate = plate;
            this.brand = brand;
            this.vehicle = vehicle;
            this.engine = engine;
            this.power = power;
            this.avgFuelCons = avgFuelCons;
            this.co2 = co2;
            this.weight = weight;
            this.gearBoxType = gearBoxType;
            this.tyre = tyre;
            this.tractionType = tractionType;
            this.power = power;
            this.registrationYear = registrationYear;
            this.office = office;
        }

    public Integer getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(Integer registrationYear) {
        this.registrationYear = registrationYear;
    }

    public String getAvgFuelCons() {
            return avgFuelCons;
        }

        public String getBrand() {
            return brand;
        }

        public String getCo2() {
            return co2;
        }

        public String getEngine() {
            return engine;
        }

        public String getGearBoxType() {
            return gearBoxType;
        }

        public String getPlate() {
            return plate;
        }

        public String getPower() {
            return power;
        }

        public String getTractionType() {
            return tractionType;
        }

        public String getTyre() {
            return tyre;
        }

        public String getVehicle() {
            return vehicle;
        }

        public String getWeight() {
            return weight;
        }

        public void setGearBoxType(String gearBoxType) {
            this.gearBoxType = gearBoxType;
        }

        public void setAvgFuelCons(String avgFuelCons) {
            this.avgFuelCons = avgFuelCons;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public void setCo2(String co2) {
            this.co2 = co2;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTractionType(String tractionType) {
            this.tractionType = tractionType;
        }

        public void setTyre(String tyre) {
            this.tyre = tyre;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public void printCar(){
            String out = "Car Plate: " + plate;
            if(brand!=null)
                out += ", Brand: " + brand ;
            if(vehicle!=null)
                out += ", Vehicle: " + vehicle ;
            if(engine!=null)
                out += ", Engine: " + engine ;
            if(avgFuelCons!=null)
                out += ", Average Fuel Consumption: " + avgFuelCons ;
            if(co2!=null)
                out += ", CO2: " + co2 ;
            if(weight!=null)
                out += ", Weight: " + weight ;
            if(gearBoxType!=null)
                out += ", Gear Box Type: " + gearBoxType ;
            if(tyre!=null)
                out += ", Tyre: " + tyre;
            if(tractionType!=null)
                out += ", Traction Type: " + tractionType ;
            if(power!=null)
                out += ", Power: " + power ;
            if(registrationYear!=null)
                out += ", RegistrationYear: " + registrationYear ;
            if(office!=null)
                out += ", PositionOffice: " + office ;
           /* System.out.println("Car Plate: " + getPlate() + ", Brand: "+ getBrand() + ", Vehicle: " + getVehicle() + ", Engine: "
            + getEngine() + ", Average Fuel Consumption: " + getAvgFuelCons() + ", CO2: "+ getCo2()+ ", Weight: " + getWeight() +
                    ", GearBoxType: " + getGearBoxType() + ", Tyre: " + getTyre() + ", TractionType: " + getTractionType() + ", Power: " + getPower() );*/
            System.out.println(out);
        }

    public Double calcolatePrice() {
        Double kW = getKw(null);
        Double multiplicatoPrice = 0.4;
        Double priceCar = kW.doubleValue() * multiplicatoPrice;
        return priceCar;

    }

    public Double getKw(MongoDBConnection db){
        List<String> power = Arrays.asList(this.getPower().split("/"));
        List<String> power1 = Arrays.asList(power.get(0).split("-"));
        Double kW = 0.0;
        try {
             kW = Double.valueOf(power1.get(1));
        } catch (Exception e){
            //System.out.println("Plate: " + plate);
            kW = 70.0;
        }
        return kW;
    }
}
