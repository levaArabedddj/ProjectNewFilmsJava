package com.example.Service;



import com.example.Entity.Script;
import com.example.Repository.ScriptRepo;
import com.example.loger.Loggable;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ScriptService {



    @Value("${google.cloud.storage.bucket.name}")
    private String bucketName;

    @Autowired
    private final Storage storage;

    @Autowired
    private final ScriptRepo scriptRepo;

    @Autowired
    public ScriptService(Storage storage, ScriptRepo scriptRepo) {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.scriptRepo = scriptRepo;
    }

    @Loggable
    public Script findById(Long id) {
        Optional<Script> scriptOptional = scriptRepo.findById(id);
        return scriptOptional.orElseThrow(() -> new RuntimeException("Script with id " + id + " not found"));
    }

    @Loggable

    public void update(Long id ,Script updatedScript) {
        Script script = scriptRepo.findById(id).
                orElseThrow(() -> new RuntimeException("Script with id "+ id + "not found"));
        Script existingScript = findById(updatedScript.getId());
        existingScript.setContent(updatedScript.getContent());
        scriptRepo.save(existingScript);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        Blob blob = storage.create(blobInfo, inputStream);
        return blob.getMediaLink(); // Возвращает ссылку на загруженный файл
    }

     //5. Добавление файла в выбранную папку
    public String uploadFileInFolder(String folderName ,MultipartFile file ) throws IOException {
        if(!folderName.endsWith("/")){
            folderName += "/";
        }

        String filePath = folderName + file.getOriginalFilename();


        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo,file.getBytes());
        return "https://storage.googleapis.com/"+bucketName+"/"+filePath;

    }

    //2. Поиск файлов по префиксу (например, все файлы в определенной папке)
    public List<String> searchListByPrefix(String prefix) {
        Iterable<Blob> blobs = storage.list(bucketName,Storage.BlobListOption.prefix(prefix)).iterateAll();

        return StreamSupport.stream(blobs.spliterator(),false).
                map(Blob::getName).
                collect(Collectors.toList());
    }

    //3. Копирование файла в другую папку
    public boolean copyFile(String sourcePath, String targetPath) {
        BlobId sourceBlobId = BlobId.of(bucketName, sourcePath);
        BlobId targetBlobId = BlobId.of(bucketName, targetPath);
        Blob sourceBlob = storage.get(sourceBlobId);
        if(!sourceBlob.exists()) {
            return false;
        }
        storage.copy(Storage.CopyRequest.of(sourceBlobId,targetBlobId));
        return true;
    }

    //4. Перемещение файла в другую папку (копируем + удаляем)
    public boolean moveFile(String sourcePath, String targetPath) {
        boolean copied = copyFile(sourcePath, targetPath);
        if(copied){
            return storage.get(BlobId.of(bucketName,sourcePath)).delete();
        }
        return false;
    }

    public InputStream streamFile(String fileName) {
        Blob blob = storage.get(BlobId.of(bucketName,fileName));
        if(!blob.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return Channels.newInputStream(blob.reader());
    }

    public String readDocxFile(String fileName) throws IOException {
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new RuntimeException("File not found: " + fileName);
        }

        // Преобразуем byte[] в InputStream
        try (InputStream inputStream = new ByteArrayInputStream(blob.getContent())) {
            XWPFDocument document = new XWPFDocument(inputStream);
            List<String> paragraphs = document.getParagraphs()
                    .stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.toList());

            return String.join("\n", paragraphs);
        }
    }
   public boolean deleteFile(String fileName) {
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        if (blob != null) {
            return blob.delete();
        }
        return false;
   }

   public List<String> listFiles() {
        List<String> filesNames = new ArrayList<>();
       Page<Blob> blobs = storage.list(bucketName);
       for(Blob blob : blobs.iterateAll()){
           filesNames.add(blob.getName());
       }
       return filesNames;
   }

   public Map<String,Object> getInfoFile(String fileName){
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"file not found");
        }

        Map<String,Object> info = new HashMap<>();
        info.put("name",blob.getName());
        info.put("size",blob.getSize());
        info.put("contentType",blob.getContentType());
        info.put("updated",blob.getUpdateTime());
        info.put("mediaLink",blob.getMediaLink());

       // ✅ Додаткові параметри
       info.put("bucket", blob.getBucket()); // Назва бакету
       info.put("generation", blob.getGeneration()); // Версія файлу
       info.put("etag", blob.getEtag()); // Унікальний тег версії
       info.put("crc32c", blob.getCrc32cToHexString()); // Контрольна сума CRC32C
       info.put("md5Hash", blob.getMd5ToHexString()); // MD5-хеш файлу
       info.put("storageClass", blob.getStorageClass().name()); // Клас зберігання (STANDARD, NEARLINE, COLDLINE тощо)
       info.put("createTime", blob.getCreateTime()); // Час створення файлу
       info.put("cacheControl", blob.getCacheControl()); // Політика кешування
       info.put("customMetadata", blob.getMetadata()); // Додаткові мета-дані
        return info;
   }

   public boolean createFolder(String folderName){
        String folderPath = folderName.endsWith("/") ? folderName : folderName +"/";
        BlobId blobId = BlobId.of(bucketName, folderPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo);
        return true;
   }

   public String updateFile(String fileName, MultipartFile file) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo,file.getBytes());
        return "https://storage.googleapis.com/"+bucketName+"/"+fileName;
   }



}
