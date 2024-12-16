import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {

    // Поиск файлов с учетом маски сигнатуры
    public static List<File> searchFilesWithMask(String directoryPath, String signatureMask, List<File> signatureFiles) {
        List<File> matchedFiles = new ArrayList<>();
        // Создаем паттерн из маски сигнатуры
        String regexPattern = signatureMask.replace("*", ".*").replace("?", ".");

        // Проходим по всем файлам в директории
        for (File file : signatureFiles) {
            // Проверяем, соответствует ли имя файла маске
            if (file.getName().matches(regexPattern)) {
                matchedFiles.add(file);
            }
        }

        return matchedFiles;
    }

    // Поиск файлов с учетом HEX маски
    public static List<File> searchFilesWithHexSignature(String directoryPath, String hexSignatureMask, List<File> signatureFiles) {
        List<File> matchedFiles = new ArrayList<>();

        // Преобразуем HEX строку в массив байтов
        byte[] hexSignature = hexStringToByteArray(hexSignatureMask);

        // Проходим по всем файлам в директории
        for (File file : signatureFiles) {
            // Читаем первые байты файла и проверяем соответствие
            if (fileStartsWithHex(file, hexSignature)) {
                matchedFiles.add(file);
            }
        }

        return matchedFiles;
    }

    // Преобразование HEX строки в массив байтов
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // Проверка, начинается ли файл с заданной HEX сигнатуры
    private static boolean fileStartsWithHex(File file, byte[] hexSignature) {
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[hexSignature.length];
            int bytesRead = inputStream.read(fileBytes);
            if (bytesRead != -1 && bytesRead >= hexSignature.length) {
                for (int i = 0; i < hexSignature.length; i++) {
                    if (fileBytes[i] != hexSignature[i]) {
                        return false;
                    }
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
