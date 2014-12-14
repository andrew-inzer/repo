package inzer.google.api.controllers;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import inzer.google.api.services.ICloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/cloud-storage")
public class CloudStorageController {
    @Autowired
    private ICloudStorageService service;

    @RequestMapping(value = "/get-buckets-list", method = RequestMethod.GET)
    public List<Bucket> getBucketsList() throws Exception {
        return service.getBucketsList();
    }

    @RequestMapping(value = "/create-bucket", method = RequestMethod.GET)
    public void createBucket(@RequestParam(value = "bucketName", required = true) String bucketName) throws Exception {
        service.createBucket(bucketName);
    }

    @RequestMapping(value = "/delete-bucket", method = RequestMethod.GET)
    public void deleteBucket(@RequestParam(value = "bucketName", required = true) String bucketName) throws Exception {
        service.deleteBucket(bucketName);
    }

    @RequestMapping(value = "/get-bucket", method = RequestMethod.GET)
    public Bucket getBucket(@RequestParam(value = "bucketName", required = true) String bucketName) throws Exception {
        return service.getBucket(bucketName);
    }

    @RequestMapping(value = "/get-files-list", method = RequestMethod.GET)
    public List<StorageObject> getFilesList(@RequestParam(value = "bucketName", required = true) String bucketName) throws Exception {
        return service.getFilesList(bucketName);
    }

    @RequestMapping(value = "/upload-file", method = RequestMethod.GET)
    public void uploadFile(
            @RequestParam(value = "bucketName", required = true) String bucketName,
            @RequestParam(value = "filePath",   required = true) String filePath
    ) throws Exception {
        service.uploadFile(bucketName, filePath);
    }

    @RequestMapping(value = "/delete-file", method = RequestMethod.GET)
    public void deleteFile(
            @RequestParam(value = "bucketName", required = true) String bucketName,
            @RequestParam(value = "fileName",   required = true) String fileName
    ) throws Exception {
        service.deleteFile(bucketName, fileName);
    }

    @RequestMapping(value = "/download-file", method = RequestMethod.GET)
    public void downloadFile(
            @RequestParam(value = "bucketName",           required = true) String bucketName,
            @RequestParam(value = "fileName",             required = true) String fileName,
            @RequestParam(value = "destinationDirectory", required = true) String destinationDirectory
    ) throws Exception {
        service.downloadFile(bucketName, fileName, destinationDirectory);
    }
}
