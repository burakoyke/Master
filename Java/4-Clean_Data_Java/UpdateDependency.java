import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.FileWriter;

public class UpdateDependency {

    public static void main(String[] args) {
        String filePath = "modules_packages_dependencies.rsf";
        String filePath_pack = "packages_packages_dependencies.rsf";
        ArrayList<String> packOfModules = readAndSplitModulesFile(filePath);
        ArrayList<String> packOfPackages = readAndSplitPackagesFile(filePath_pack);

        List<String> updatedPackOfPackages = new ArrayList<>();

        for (String entry : packOfPackages) {
            String[] parts = entry.split(" ");
            if (parts.length < 3) {
                continue; // Dizinin uzunluğu 3'ten azsa, geçersiz girdiyi atla
            }


            String element1 = parts[1];
            String element2 = parts[2];

            //System.out.println("Element1: " +element1);
            //System.out.println("Element2: " +element2);

            boolean element1Exists = elementExistsInList(element1, packOfModules);
            boolean element2Exists = elementExistsInList(element2, packOfModules);

            if (element1Exists && element2Exists) {
                updatedPackOfPackages.add(entry); // Her iki eleman da varsa, satırı koru
            } else {
                // Kısaltma işlemi
                element1 = adjustElementIfNecessary(element1, packOfModules);
                element2 = adjustElementIfNecessary(element2, packOfModules);

                // Güncellenmiş veya "dummy" olarak değiştirilmiş satırı ekle
                updatedPackOfPackages.add("depends " + element1 + " " + element2);
            }
        }

                // "dummy" kelimesini içeren elemanları listeden kaldır
        Iterator<String> iterator = updatedPackOfPackages.iterator();
            while (iterator.hasNext()) {
                String entry = iterator.next();
                if (entry.contains("dummy")) {
                    iterator.remove(); // "dummy" kelimesini içeren elemanları sil
                }
            }

        // Güncellenmiş listeyi dosyaya kaydet
        saveToFile("packages_packages_dependencies_new.rsf", updatedPackOfPackages);

        List<String> missingModules = findMissingModules(packOfModules, updatedPackOfPackages);

        List<String> filteredModules = filterModulesFile(filePath, missingModules);
        saveToFile("modules_packages_dependencies_new.rsf", filteredModules);

    }
    private static List<String> filterModulesFile(String filePath, List<String> missingModules) {
        List<String> filteredLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 2 && missingModules.contains(parts[2])) {
                    // Satırdaki 2. index missingModules içerisinde varsa, satırı atla
                    continue;
                }
                filteredLines.add(line); // Diğer satırları listeye ekle
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredLines;
    }
    private static List<String> findMissingModules(List<String> packOfModules, List<String> updatedPackOfPackages) {
        List<String> missingModules = new ArrayList<>();
        for (String module : packOfModules) {
            boolean found = false;
            for (String updatedEntry : updatedPackOfPackages) {
                if (updatedEntry.contains(module)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingModules.add(module);
            }
        }
        return missingModules;
    }

    private static boolean elementExistsInList(String element, List<String> list) {
        for (String item : list) {
            if (item.equals(element)) {
                return true;
            }
        }
        return false;
    }

    private static String adjustElementIfNecessary(String element, List<String> packOfModules) {
        while (!packOfModules.contains(element) && element.length() > 0) {
            element = element.substring(0, element.length() - 1); // Son harfi sil
        }
        return element.isEmpty() ? "dummy" : element; // Boş kaldıysa "dummy" olarak değiştir
    }

    public static ArrayList<String> readAndSplitModulesFile(String filePath) {
        ArrayList<String> linesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(" ");
                if (splitLine.length > 2) { // Dizinin boyutunu kontrol et
                    linesList.add(splitLine[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesList;
    }

    public static ArrayList<String> readAndSplitPackagesFile(String filePath) {
        ArrayList<String> linesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(" ");
                if (splitLine.length >= 3) { // Dizinin boyutunu kontrol et
                    linesList.add(line); // Tüm satırı ekle
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesList;
    }

    private static void saveToFile(String fileName, List<String> data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String line : data) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
