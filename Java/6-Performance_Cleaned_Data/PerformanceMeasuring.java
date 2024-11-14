import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

class PerformanceMeasuring {
    public static void main(String[] args) {
    String filePath = "packages_packages_dependencies.rsf";
        Set<String> uniquePackage = new LinkedHashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 2) { // Check if the line has at least 3 elements
                    uniquePackage.add(parts[1]);
                    uniquePackage.add(parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print unique packages
        //uniquePackage.forEach(System.out::println);
    
       
        ArrayList<String> package_package = package_convert_list("packages_packages_dependencies.rsf");
        ArrayList<String> module_package = package_convert_list("modules_packages_dependencies.rsf");
        writeHeader();

        for (String item : uniquePackage) {

            // item : com.sun.org.apache.xalan.internal

















            ArrayList<String> packOfPack = new ArrayList<>();  
            for (String i : package_package) {
                String[] splitPackage2 = i.split(" ");
                if (splitPackage2[1].equals(item)) {
                    if (splitPackage2[1].equals("com.sun.java.swing")) {
                        //System.out.println("Contain: " +i);
                    }
                    packOfPack.add(i);
                }
                
            }

            if (item.equals("com.sun.java.swing")) {
                //System.out.println("com.sun.java.swing: " +packOfPack.size()); 
            }

            HashSet<String> module_hash = new HashSet<>();

            int c = 0;

            for (String i : packOfPack) {
                String[] splitPackage = i.split(" ");
                
                    for (String modPkg : module_package) {
                        String[] splitModule = modPkg.split(" ");
                        if (splitModule.length > 2 && splitModule[2].equals(splitPackage[2])) {
                            module_hash.add(splitModule[1]);
                            
                           //System.out.println("Module: " + splitModule[1] + " -> Package: " + splitPackage[2]);
                        }
                    }

                    
                
            }

            //System.err.println("Module_Hash Size: " +module_hash.size());

            for (String mod_hash : module_hash) {

                //System.err.println("Module: " +mod_hash);

                for (String modPkg : module_package) {
                    //System.out.println("modPkg: " + modPkg);
                    String[] splitModule = modPkg.split(" ");
                    if (splitModule[1].equals(mod_hash)) {
                        //System.out.println("splitModule[1]: " + splitModule[1]);
                        //System.out.println("mod_hash: " + mod_hash);

                        c++;

                    }
                }

                

            }




            //System.out.println("Item: " + item);
            //System.out.println("Package Size: " + packOfPack.size() + 
            //" | Module Size: " + module_hash.size() + 
           //// " | Redundant: " +(packOfPack.size() - module_hash.size()) +
            //" | Extra Load " +(c - packOfPack.size()));
            //System.err.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
           // System.err.println(" ");
           if ((c - packOfPack.size()) < 0) {
            System.out.println(item);
           }
            logToFile(item, packOfPack.size(), module_hash.size(),c);
            logToFileCSV(item, packOfPack.size(), module_hash.size(),c);
            module_hash.clear();
            c =0;
            
        }
    }

    public static ArrayList<String> package_convert_list(String file) {
        ArrayList<String> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void logToFile(String item, int packSize, int moduleSize, int c) {
        String fileName = "performance.txt"; 
        
        try (FileWriter fileWriter = new FileWriter(fileName, true); 
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
             
            // Logları dosyaya yazdır
            printWriter.println("Item: " + item);
            printWriter.println("Package Size: " + packSize + 
                                " | Module Size: " + moduleSize + 
                                " | Redundant " + (c - packSize));
            printWriter.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            printWriter.println(" ");
            
        } catch (IOException e) {
            // Hata durumunda konsola hata mesajı yazdır
            System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
        }
    }

    public static void logToFileCSV(String item, int packSize, int moduleSize, int c) {
        String fileName = "performance_csv.txt"; 
        
        try (FileWriter fileWriter = new FileWriter(fileName, true); 
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
             
            // İstediğin formatta logları dosyaya yazdır
            printWriter.println(item + "," + packSize + "," + moduleSize + "," + (c - packSize));
            
        } catch (IOException e) {
            // Hata durumunda konsola hata mesajı yazdır
            System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
        }
    }

    public static void writeHeader() {
        String fileName = "performance_csv.txt"; 
        try (FileWriter fileWriter = new FileWriter(fileName, false); // 'false' bayrağı dosyanın üzerine yazmayı sağlar
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
             
            // Başlık satırını yazdır
            printWriter.println("Item,Package Size,Module Size,Redundant Count");
            
        } catch (IOException e) {
            System.err.println("Başlık yazılamadı: " + e.getMessage());
        }
    }
    
}

