package inzer.google.api.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@PropertySource("classpath:cloud-storage-secrets.properties")
public class CloudStorageService implements ICloudStorageService {
    private static final String PROJECT_ID_PROPERTY       = "project.id";
    private static final String ACCOUNT_ID_PROPERTY       = "account.id";
    private static final String PRIVATE_KEY_PATH_PROPERTY = "private.key.path";

    @Autowired
    private Environment env;

    @Autowired
    private ServletContext servletContext;

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private HttpTransport httpTransport;
    private Storage storage;

    private Storage getStorage() throws Exception {
        if (storage == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Set<String> scopes = new HashSet<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(env.getRequiredProperty(ACCOUNT_ID_PROPERTY))
                    .setServiceAccountPrivateKeyFromP12File(new File(servletContext.getRealPath(env.getRequiredProperty(PRIVATE_KEY_PATH_PROPERTY))))
                    .setServiceAccountScopes(scopes)
                    .build();

            storage = new Storage.Builder(httpTransport, JSON_FACTORY, credential).build();
        }

        return storage;
    }

    @Override
    public List<Bucket> getBucketsList() throws Exception {
        return getStorage().buckets().list(env.getRequiredProperty(PROJECT_ID_PROPERTY)).execute().getItems();
    }

    @Override
    public void createBucket(String bucketName) throws Exception {
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);

        getStorage().buckets().insert(env.getRequiredProperty(PROJECT_ID_PROPERTY), bucket).execute();
    }

    @Override
    public void deleteBucket(String bucketName) throws Exception {
        getStorage().buckets().delete(bucketName).execute();
    }

    @Override
    public Bucket getBucket(String bucketName) throws Exception {
        return getStorage().buckets().get(bucketName).execute();
    }

    @Override
    public List<StorageObject> getFilesList(String bucketName) throws Exception {
        return getStorage().objects().list(bucketName).execute().getItems();
    }

    @Override
    public void uploadFile(String bucketName, String filePath) throws Exception {
        StorageObject object = new StorageObject();
        object.setBucket(bucketName);

        File file = new File(filePath);
        InputStream stream = new FileInputStream(file);

        try {
            String contentType = URLConnection.guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType, stream);

            getStorage().objects().insert(bucketName, null, content).setName(file.getName()).execute();
        }
        finally {
            stream.close();
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws Exception {
        getStorage().objects().delete(bucketName, fileName).execute();
    }

    @Override
    public void downloadFile(String bucketName, String fileName, String destinationDirectory) throws Exception {
        File directory = new File(destinationDirectory);
        if (!directory.isDirectory()) {
            throw new Exception("Provided destinationDirectory path is not a directory");
        }

        File file = new File(directory.getAbsolutePath() + "/" + fileName);
        FileOutputStream stream = new FileOutputStream(file);

        try {
            getStorage().objects().get(bucketName, fileName).executeAndDownloadTo(stream);
        }
        finally {
            stream.close();
        }
    }
}
