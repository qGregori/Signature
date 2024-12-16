import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignatureDatabase {

    private List<File> signatureFiles;

    public SignatureDatabase(String directoryPath) {
        signatureFiles = new ArrayList<>();
        loadSignatures(directoryPath);
    }

    private void loadSignatures(String directoryPath) {
        File folder = new File(directoryPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        signatureFiles.add(file);
                    }
                }
            }
        }
    }

    public List<File> getSignatureFiles() {
        return signatureFiles;
    }
}
