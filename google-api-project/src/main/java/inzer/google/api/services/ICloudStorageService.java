package inzer.google.api.services;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

import java.util.List;

public interface ICloudStorageService {
  public List<Bucket> getBucketsList() throws Exception;
  public void createBucket(String bucketName) throws Exception;
  public void deleteBucket(String bucketName) throws Exception;
  public Bucket getBucket(String bucketName) throws Exception;
  public List<StorageObject> getFilesList(String bucketName) throws Exception;
  public void uploadFile(String bucketName, String filePath) throws Exception;
  public void deleteFile(String bucketName, String fileName) throws Exception;
  public void downloadFile(String bucketName, String fileName, String destinationDirectory) throws Exception;
}
