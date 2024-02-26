package rendeles;

import java.io.*;
import java.util.*;

public class Rendeles_Model {
    private String etel;
    private int ar;
    private String asztal;

    public Rendeles_Model() {
    }

    public Rendeles_Model(String etel, int ar, String asztal) {
        this.etel = etel;
        this.ar = ar;
        this.asztal = asztal;
    }

    public String getEtel() {
        return etel;
    }

    public void setEtel(String etel) {
        this.etel = etel;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public String getAsztal() {
        return asztal;
    }

    public void setAsztal(String asztal) {
        this.asztal = asztal;
    }

    public Map<String, Integer> readPricesFromFile(String fileName) {
        Map<String, Integer> prices = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String etel = parts[0].trim();
                    int ar = Integer.parseInt(parts[1].trim());
                    prices.put(etel, ar);
                } else {
                    System.err.println("Invalid line in file " + fileName + ": " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prices;
    }

    public Map<String, List<Rendeles_Model>> readOrdersFromFile(String fileName, Map<String, Integer> prices) {
        Map<String, List<Rendeles_Model>> asztalok = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            String currentAsztal = null;
            List<Rendeles_Model> rendelesek = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    if (currentAsztal != null) {
                        asztalok.put(currentAsztal, new ArrayList<>(rendelesek));
                        rendelesek.clear();
                        currentAsztal = null;
                    }
                } else {
                    if (currentAsztal == null) {
                        currentAsztal = line.trim();
                    } else {
                        Integer ar = prices.get(line.trim());
                        if (ar != null) {
                            rendelesek.add(new Rendeles_Model(line.trim(), ar, currentAsztal));
                        } else {
                            System.err.println("árat nem találtuk meg az ételhez: " + line.trim());
                        }
                    }
                }
            }

            if (currentAsztal != null && !rendelesek.isEmpty()) {
                asztalok.put(currentAsztal, rendelesek);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return asztalok;
    }

    public Map<String, List<Rendeles_Model>> groupOrdersByTable(List<Rendeles_Model> rendelesek) {
        Map<String, List<Rendeles_Model>> asztalok = new HashMap<>();

        for (Rendeles_Model rendeles : rendelesek) {
            String asztal = rendeles.getAsztal();
            List<Rendeles_Model> orders = asztalok.getOrDefault(asztal, new ArrayList<>());
            orders.add(rendeles);
            asztalok.put(asztal, orders);
        }

        return asztalok;
    }

      public static void kiirFajlba(Map<String, List<Rendeles_Model>> asztalok, String fajlnev) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fajlnev))) {
            for (Map.Entry<String, List<Rendeles_Model>> entry : asztalok.entrySet()) {
                String asztal = entry.getKey();
                List<Rendeles_Model> rendelesek = entry.getValue();

                writer.write(asztal + " asztal:");
                writer.newLine();
                writer.write("=".repeat(20 + asztal.length()));  // Assuming a maximum length for asztal
                writer.newLine();

                Collections.sort(rendelesek, Comparator.comparingInt(Rendeles_Model::getAr));

                int maxEtelLength = 0;
                for (Rendeles_Model rendeles : rendelesek) {
                    maxEtelLength = Math.max(maxEtelLength, rendeles.getEtel().length());
                }

                int maxPriceLength = Integer.MIN_VALUE;
                for (Rendeles_Model rendeles : rendelesek) {
                    maxPriceLength = Math.max(maxPriceLength, String.valueOf(rendeles.getAr()).length());
                }

                for (Rendeles_Model rendeles : rendelesek) {
                    String etel = rendeles.getEtel();
                    String price = rendeles.getAr() + " Ft";
                    int spacesForEtel = maxEtelLength - etel.length() + 5;  // Adjusting spaces for etel
                    int spacesForPrice = maxPriceLength - price.length();  // Adjusting spaces for price
                    writer.write(String.format("%s %s %s%n", etel, " ".repeat(spacesForEtel), price));
                }

                writer.write("-".repeat(maxEtelLength + maxPriceLength + 13)); // Assuming a maximum length for etel and price
                writer.newLine();

                int osszeg = rendelesek.stream().mapToInt(Rendeles_Model::getAr).sum();
                int spacesForSum = maxEtelLength + 5;  // Adjusting spaces for sum
                writer.write(String.format("Összesen : %s %s  Ft%n%n", " ".repeat(spacesForSum - 10),   osszeg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void kiirKonzolra(Map<String, List<Rendeles_Model>> asztalok) {
        for (Map.Entry<String, List<Rendeles_Model>> entry : asztalok.entrySet()) {
            String asztal = entry.getKey();
            List<Rendeles_Model> rendelesek = entry.getValue();

            System.out.println(asztal + " asztal:");
            System.out.println("=".repeat(20 + asztal.length()));  // Assuming a maximum length for asztal

            Collections.sort(rendelesek, Comparator.comparingInt(Rendeles_Model::getAr));

            int maxEtelLength = 0;
            for (Rendeles_Model rendeles : rendelesek) {
                maxEtelLength = Math.max(maxEtelLength, rendeles.getEtel().length());
            }

            int maxPriceLength = Integer.MIN_VALUE;
            for (Rendeles_Model rendeles : rendelesek) {
                maxPriceLength = Math.max(maxPriceLength, String.valueOf(rendeles.getAr()).length());
            }

            for (Rendeles_Model rendeles : rendelesek) {
                String etel = rendeles.getEtel();
                String price = rendeles.getAr() + " Ft";
                int spacesForEtel = maxEtelLength - etel.length() + 5;  // Adjusting spaces for etel
                int spacesForPrice = maxPriceLength - price.length();  // Adjusting spaces for price
                System.out.printf("%s %s %s%n", etel, " ".repeat(spacesForEtel), price);
            }

            System.out.println("-".repeat(maxEtelLength + maxPriceLength + 13)); // Assuming a maximum length for etel and price

            int osszeg = rendelesek.stream().mapToInt(Rendeles_Model::getAr).sum();
            int spacesForSum = maxEtelLength + 5;  // Adjusting spaces for sum
            System.out.printf("Összesen : %s %s  Ft%n%n", " ".repeat(spacesForSum - 10), osszeg);
        }
    }
}

