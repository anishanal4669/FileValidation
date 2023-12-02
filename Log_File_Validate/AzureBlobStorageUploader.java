// Import the required packages
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

// Create a BlobServiceClient object by using the storage account name and access key
String accountName = "your-storage-account-name";
String accountKey = "your-storage-account-key";
String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();

// Get a reference to a BlobContainerClient object by using the container name
String containerName = "your-container-name";
BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

// Create the container if it does not exist
if (!blobContainerClient.exists()) {
    blobContainerClient.create();
}

// Get a reference to a BlobClient object by using the blob name
String blobName = "your-blob-name";
BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

// Upload a file from a local path to the blob storage
String localPath = "your-local-file-path";
blobClient.uploadFromFile(localPath);
