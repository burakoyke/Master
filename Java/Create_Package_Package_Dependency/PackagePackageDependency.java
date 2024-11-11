import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PackagePackageDependency {
    public static void main(String[] args) {
        try {
            // jdeps komutunu çalıştır
            Process process = new ProcessBuilder(
                    "jdeps",
                    "-recursive", // Komut seçenekleri burada belirlenir
                    "-dotoutput",
                    "./",
                    "jre-8u202-linux-x64/jre1.8.0_202/lib/rt.jar"
            ).start();

            // Komutun tamamlanmasını bekle
            process.waitFor();

            // rt.jar.dot dosyasını oku
            Path dotFilePath = Paths.get("rt.jar.dot");
            List<String> lines = Files.readAllLines(dotFilePath);

            // İstenen formatta çıktıyı oluştur
            List<String> formattedOutput = new ArrayList<>();
            for (String line : lines) {
                if (line.contains("->")) {
                    String[] parts = line.split("->");
                    String fromPackage = parts[0].trim().replaceAll("\"", "");
                    String toPackage = parts[1].trim().split(" ")[0].replaceAll("\"", "");
                    formattedOutput.add("depends " + fromPackage + " " + toPackage);
                }
            }

            // Çıktıyı dosyaya kaydet
            Path outputFilePath = Paths.get("packages_packages_dependencies.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
                for (String output : formattedOutput) {
                    writer.write(output);
                    writer.newLine();
                }
            }

            System.out.println("Bağımlılık analizi 'packages_packages_dependencies.txt' dosyasına kaydedildi.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Fark Açıklaması:
 * 1. `jdeps -verbose:package -dotoutput ~/Documents/TEZ2/ Desktop/rt.jar`
 *    - Bu komut, her bir paketin hangi paketlere bağımlı olduğunu paket düzeyinde (package-level) ayrıntılı olarak gösterir.
 *    - Çıktıda, paketler arası bağımlılıklar özetlenir ve yalnızca doğrudan bağımlılıklar listelenir.
 * 
 * 2. `jdeps -recursive -dotoutput ~/Documents/TEZ2/ Desktop/rt.jar`
 *    - Bu komut, bağımlılıkların hiyerarşisini veya ağaç yapısını oluşturur.
 *    - Paketlerin tüm transitif bağımlılıklarını (yani, dolaylı bağımlılıkları) içerecek şekilde daha kapsamlı bir analiz yapar.
 *    - Sonuçta, tüm paketler ve onların bağımlı olduğu diğer tüm paketler liste halinde ve daha geniş kapsamlı şekilde raporlanır.
 */

