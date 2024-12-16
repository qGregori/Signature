import java.io.File;
import java.text.SimpleDateFormat;

public class FileMetadata {

    public static String getMetadata(File file) {
        long fileSize = file.length();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastModified = sdf.format(file.lastModified());

        return String.format("Размер: %d байт\nДата изменения: %s", fileSize, lastModified);
    }
}
