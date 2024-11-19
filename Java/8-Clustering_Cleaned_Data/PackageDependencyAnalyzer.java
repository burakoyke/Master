import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PackageDependencyAnalyzer {
    public static void main(String[] args) {
        String filePath = "packages_packages_dependencies.rsf";
        String outputFilePath = "output.txt"; // Sonuçların yazılacağı dosya
        
        // Bağımlılıkları saklayacağımız veri yapısını oluşturuyoruz
        Map<String, Set<String>> paket1ToPaket2s = new HashMap<>();
        
        // Dosyayı okuyup bağımlılık verilerini işliyoruz
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3 && "depends".equals(parts[0])) {
                    String paket1 = parts[1];
                    String paket2 = parts[2];
                    
                    // paket1 ve paket2 ilişkilerini ekleyin
                    paket1ToPaket2s.putIfAbsent(paket1, new HashSet<>());
                    paket1ToPaket2s.get(paket1).add(paket2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Tüm paket1'lerin listesi
        List<String> paket1List = new ArrayList<>(paket1ToPaket2s.keySet());
        int combinationCount = 0;

        // Sonuçları yazmak için BufferedWriter kullanıyoruz
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int i = 0; i < paket1List.size(); i++) {
                for (int j = i + 1; j < paket1List.size(); j++) {
                    String paket1_A = paket1List.get(i);
                    String paket1_B = paket1List.get(j);
                    
                    // İki paket için ortak paket2 bağımlılıklarını sayıyoruz
                    Set<String> paket2_A = paket1ToPaket2s.get(paket1_A);
                    Set<String> paket2_B = paket1ToPaket2s.get(paket1_B);
                    
                    // Ortak paket2'leri buluyoruz
                    if (paket2_A != null && paket2_B != null) {
                        Set<String> ortakPaket2ler = new HashSet<>(paket2_A);
                        ortakPaket2ler.retainAll(paket2_B); // Ortak elemanları bul
                        
                        // Sonucu belirtilen formatta ekrana ve dosyaya yazdırıyoruz
                        String result = paket1_A + " " + paket1_B + " " + ortakPaket2ler.size();
                        System.out.println(result);
                        writer.write(result);
                        writer.newLine(); // Yeni satıra geç
                        combinationCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Toplam analiz edilen kombinasyon sayısı: " + combinationCount);
    }
}

