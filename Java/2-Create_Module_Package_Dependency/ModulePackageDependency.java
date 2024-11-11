import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModulePackageDependency {

    // Modüllerin listesi
    List<String> moduleList = new ArrayList<>();

    public static void main(String[] args) {
        ModulePackageDependency modulePackageDependency = new ModulePackageDependency();
        try {
            // "java --list-modules" komutunu çalıştır
            modulePackageDependency.getModuleList();

            // Her modül için "java --describe-module" komutunu çalıştır
            modulePackageDependency.describeModulesAndWriteToFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Modül listesini al
    public void getModuleList() {
        try {
            Process process = new ProcessBuilder("java", "--list-modules").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            // Çıktıyı satır satır oku
            while ((line = reader.readLine()) != null) {
                // "@" işaretinden önceki kısmı al
                if (line.contains("@")) {
                    String moduleName = line.split("@")[0];
                    moduleList.add(moduleName);
                }
            }
            // Çalışma tamamlanana kadar bekle
            process.waitFor();

            // Modülleri yazdır (debug amaçlı)
            for (String module : moduleList) {
                System.out.println(module);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Her modül için "java --describe-module" komutunu çalıştır ve çıktılarını txt dosyasına yaz
    public void describeModulesAndWriteToFile() {
        try {
            // Txt dosyasını oluştur
            File file = new File("module_package_dependencies.txt");
            FileWriter writer = new FileWriter(file);

            // Her modül için "java --describe-module" komutunu çalıştır
            for (String module : moduleList) {
                System.out.println("\nDescribing module: " + module);

                // "java --describe-module" komutunu her modül için çalıştır
                Process process = new ProcessBuilder("java", "--describe-module", module).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                boolean isExportSection = false;
                boolean isContainsSection = false;

                // Satırları oku ve ilgili paket isimlerini al
                while ((line = reader.readLine()) != null) {
                    // "exports", "qualified exports" ve "contains" kelimeleri ile başlayan satırları kontrol et
                    if (line.startsWith("exports")) {
                        // Paketi al ve dosyaya yaz
                        String[] parts = line.split(" ");
                        if (parts.length > 1) {
                            String packageName = parts[1];
                            writer.write("contain " + module + " " + packageName + "\n");
                        }
                    } else if (line.startsWith("contains")) {
                        // "contains" satırları için aynı işlemi yap
                        String[] parts = line.split(" ");
                        if (parts.length > 1) {
                            String packageName = parts[1];
                            writer.write("contain " + module + " " + packageName + "\n");
                        }
                    } else if (line.startsWith("qualified exports")) {
                        String[] parts = line.split(" ");
                        if (parts.length > 1) {
                            String packageName = parts[2];
                            writer.write("contain " + module + " " + packageName + "\n");
                        }
                    }
                }
                // Çalışma tamamlanana kadar bekle
                process.waitFor();
            }
            // Dosyayı kapat
            writer.close();

            System.out.println("Dosya başarıyla oluşturuldu: module_package_dependencies.txt");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
