package rendeles;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String pricesFile = "Etelek.txt";
        String ordersFile = "Rendelesek.txt";
        String outputFile = "szamla.txt";

        Rendeles_Model rendelesModel = new Rendeles_Model();

       
        Map<String, Integer> prices = rendelesModel.readPricesFromFile(pricesFile);

        
        Map<String, List<Rendeles_Model>> ordersByTable = rendelesModel.readOrdersFromFile(ordersFile, prices);

        
        rendelesModel.kiirFajlba(ordersByTable, outputFile);
        
        rendelesModel.kiirKonzolra(ordersByTable);

        System.out.println("A rendelések feldolgozva és kiírva a következő fájlba: " + outputFile);
    }
}