import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vad on 22.10.2015 22:45.
 * https://sites.google.com/site/libvmr/
 * http://sourceforge.net/p/libvmr/code/HEAD/tree/trunk/
 */
public class MedicExample {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //File myPicFile = new ClassPathResource("/pix/my.jpg").getFile();
        /*Получаем URL CODEBASE (проще говоря - полный путь к хранилищу кода класса) */
        //URL aa = MedicExample.class.getProtectionDomain().getCodeSource().getLocation();

        URL aa = MedicExample.class.getResource("diagnosis.data");
        List<String> lines = Files.readAllLines(Paths.get(aa.toURI()));


        for (String l : lines) {
            String[] a = l.split("\\t");

            double[] dd = new double[8];
            dd[0] = Double.parseDouble(a[0].replace(",", "."));
            for (int i = 1; i < dd.length; i++) {
                dd[i] = yn(a[i]);

            }
            System.out.println(Arrays.toString(dd));
        }
    }

    static double yn(String val) {
        if (val.equals("yes")) return 1;
        if (val.equals("no")) return -1;
        return 0;
    }
}
