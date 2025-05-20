//package com.example.Service;
//
//
//
//import com.example.Controllers.AddActorsControllers;
//import com.example.Entity.Director;
//import com.example.Entity.Movies;
//import com.example.Entity.Script;
//import com.example.Repository.DirectorRepo;
//import com.example.Repository.MoviesRepo;
//import com.example.Repository.ScriptRepo;
//import com.example.loger.Loggable;
//import com.google.api.gax.paging.Page;
//import com.google.cloud.ReadChannel;
//import com.google.cloud.storage.*;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.channels.Channels;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//@Service
//public class ScriptService {
//
//    private static final Logger log = LoggerFactory.getLogger(ScriptService.class);
//
//
//    @Value("${google.cloud.storage.bucket.name1}")
//    private String bucketName;
//
//    private final Bucket bucket;
//    private final Storage storage;
//    private final MoviesRepo moviesRepo;
//    private final ScriptRepo scriptRepo;
//    private final DirectorRepo directorRepo;
//
//    @Autowired
//    public ScriptService(Storage storage, MoviesRepo moviesRepo, ScriptRepo scriptRepo, DirectorRepo directorRepo, @Value("${google.cloud.storage.bucket.name1}") String bucketName) {
//
//        this.moviesRepo = moviesRepo;
//        this.directorRepo = directorRepo;
//        this.storage = StorageOptions.getDefaultInstance().getService();
//        this.scriptRepo = scriptRepo;
//        this.bucketName = bucketName;
//        // Получаем Bucket один раз при создании бина
//        this.bucket = storage.get(bucketName);
//        if (this.bucket == null) {
//            throw new IllegalStateException("GCS bucket not found: " + bucketName);
//        }
//    }
//
//    @Loggable
//    public Script findById(Long id) {
//        Optional<Script> scriptOptional = scriptRepo.findById(id);
//        return scriptOptional.orElseThrow(() -> new RuntimeException("Script with id " + id + " not found"));
//    }
//
//    @Loggable
//    public void update(Long id ,Script updatedScript) {
//        Script script = scriptRepo.findById(id).
//                orElseThrow(() -> new RuntimeException("Script with id "+ id + "not found"));
//        Script existingScript = findById(updatedScript.getId());
//        existingScript.setContent(updatedScript.getContent());
//        scriptRepo.save(existingScript);
//    }
//
//    @Async("fileUploadExecutor")
//    public CompletableFuture<String> uploadFile(Long userId, Long movieId, MultipartFile file) throws IOException {
//        Director director = directorRepo.findByUserUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Director with id " + userId + " not found"));
//
//        Movies movies = moviesRepo.findById(movieId)
//                .orElseThrow(() -> new RuntimeException("Movies with id " + movieId + " not found"));
//
//        Long directorUserId = movies.getDirector().getUsers().getUser_id();
//        if (!directorUserId.equals(userId)) {
//            throw new RuntimeException("User " + userId + " is not director of movie " + movieId);
//        }
//
//
//        String uniqueFileName = UUID.randomUUID() + file.getOriginalFilename();
//        String fullPath = "ScriptFilms/" + uniqueFileName;
//
//
//         bucket.create(fullPath, file.getInputStream(),file.getContentType());
//
//        Script script = new Script();
//        script.setContent(fullPath);
//        script.setMovie(movies);
//        scriptRepo.save(script);
//        return CompletableFuture.completedFuture(fullPath); // Возвращает ссылку на загруженный файл
//    }
//
//     //5. Добавление файла в выбранную папку
//    public String uploadFileInFolder(String folderName ,MultipartFile file ) throws IOException {
//        if(!folderName.endsWith("/")){
//            folderName += "/";
//        }
//
//        String filePath = folderName + file.getOriginalFilename();
//
//
//        BlobId blobId = BlobId.of(bucketName, filePath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
//        storage.create(blobInfo,file.getBytes());
//        return "https://storage.googleapis.com/"+bucketName+"/"+filePath;
//
//    }
//
//    //2. Поиск файлов по префиксу (например, все файлы в определенной папке)
//    public List<String> searchListByPrefix(String prefix) {
//        Iterable<Blob> blobs = storage.list(bucketName,Storage.BlobListOption.prefix(prefix)).iterateAll();
//
//        return StreamSupport.stream(blobs.spliterator(),false).
//                map(Blob::getName).
//                collect(Collectors.toList());
//    }
//
//    //3. Копирование файла в другую папку
//    public boolean copyFile(String sourcePath, String targetPath) {
//        BlobId sourceBlobId = BlobId.of(bucketName, sourcePath);
//        BlobId targetBlobId = BlobId.of(bucketName, targetPath);
//        Blob sourceBlob = storage.get(sourceBlobId);
//        if(!sourceBlob.exists()) {
//            return false;
//        }
//        storage.copy(Storage.CopyRequest.of(sourceBlobId,targetBlobId));
//        return true;
//    }
//
//    //4. Перемещение файла в другую папку (копируем + удаляем)
//    public boolean moveFile(String sourcePath, String targetPath) {
//        boolean copied = copyFile(sourcePath, targetPath);
//        if(copied){
//            return storage.get(BlobId.of(bucketName,sourcePath)).delete();
//        }
//        return false;
//    }
//
//    public InputStream streamFile(String fileName) {
//        Blob blob = storage.get(BlobId.of(bucketName,fileName));
//        if(!blob.exists()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
//        }
//        return Channels.newInputStream(blob.reader());
//    }
//
//    public String readDocxFile(String fileName) throws IOException {
//        Blob blob = storage.get(bucketName, fileName);
//        if (blob == null) {
//            throw new RuntimeException("File not found: " + fileName);
//        }
//
//        // Преобразуем byte[] в InputStream
//        try (InputStream inputStream = new ByteArrayInputStream(blob.getContent())) {
//            XWPFDocument document = new XWPFDocument(inputStream);
//            List<String> paragraphs = document.getParagraphs()
//                    .stream()
//                    .map(XWPFParagraph::getText)
//                    .collect(Collectors.toList());
//
//            return String.join("\n", paragraphs);
//        }
//    }
//   public boolean deleteFile(String fileName) {
//        Blob blob = storage.get(BlobId.of(bucketName, fileName));
//        if (blob != null) {
//            return blob.delete();
//        }
//        return false;
//   }
//
//   public List<String> listFiles() {
//        List<String> filesNames = new ArrayList<>();
//       Page<Blob> blobs = storage.list(bucketName);
//       for(Blob blob : blobs.iterateAll()){
//           filesNames.add(blob.getName());
//       }
//       return filesNames;
//   }
//
//   public Map<String,Object> getInfoFile(String fileName){
//        Blob blob = storage.get(bucketName, fileName);
//        if (blob == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"file not found");
//        }
//
//        Map<String,Object> info = new HashMap<>();
//        info.put("name",blob.getName());
//        info.put("size",blob.getSize());
//        info.put("contentType",blob.getContentType());
//        info.put("updated",blob.getUpdateTime());
//        info.put("mediaLink",blob.getMediaLink());
//
//       // ✅ Додаткові параметри
//       info.put("bucket", blob.getBucket()); // Назва бакету
//       info.put("generation", blob.getGeneration()); // Версія файлу
//       info.put("etag", blob.getEtag()); // Унікальний тег версії
//       info.put("crc32c", blob.getCrc32cToHexString()); // Контрольна сума CRC32C
//       info.put("md5Hash", blob.getMd5ToHexString()); // MD5-хеш файлу
//       info.put("storageClass", blob.getStorageClass().name()); // Клас зберігання (STANDARD, NEARLINE, COLDLINE тощо)
//       info.put("createTime", blob.getCreateTime()); // Час створення файлу
//       info.put("cacheControl", blob.getCacheControl()); // Політика кешування
//       info.put("customMetadata", blob.getMetadata()); // Додаткові мета-дані
//        return info;
//   }
//
//   public boolean createFolder(String folderName){
//        String folderPath = folderName.endsWith("/") ? folderName : folderName +"/";
//        BlobId blobId = BlobId.of(bucketName, folderPath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//        storage.create(blobInfo);
//        return true;
//   }
//
//   public String updateFile(String fileName, MultipartFile file) throws IOException {
//        BlobId blobId = BlobId.of(bucketName, fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
//        storage.create(blobInfo,file.getBytes());
//        return "https://storage.googleapis.com/"+bucketName+"/"+fileName;
//   }
//
//
//    public void downloadScript(Long movieId, Long userId, HttpServletResponse response) {
//        Director director = directorRepo.findByUserUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Director with id " + userId + " not found"));
//
//        Movies movies = moviesRepo.findById(movieId)
//                .orElseThrow(() -> new RuntimeException("Movies with id " + movieId + " not found"));
//
//        Long directorUserId = movies.getDirector().getUsers().getUser_id();
//        if(!directorUserId.equals(userId)) {
//            throw new RuntimeException("Movie with id " + movieId + " is not a director");
//        }
//
//        // Получаем путь к файлу из скрипта
//        Script script = scriptRepo.findByMovieId(movieId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
//
//        String filePath = script.getContent();
//
//        // Получаем blob из хранилища
//        Blob blob = storage.get(bucketName, filePath);
//        if (blob == null || !blob.exists()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found in storage");
//        }
//
//        // Настройка ответа
//        response.setContentType(blob.getContentType());
//        response.setHeader("Content-Disposition", "inline; filename=\"" + blob.getName() + "\"");
//
//        try (InputStream inputStream = Channels.newInputStream(blob.reader());
//             OutputStream outputStream = response.getOutputStream()) {
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//            outputStream.flush();
//        } catch (IOException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while streaming file", e);
//        }
//    }
//
//    @Transactional
//    public void deleteScript(Long movieId, Long userId) {
//
//        // Проверка режиссера
//        Director director = directorRepo.findByUserUserId(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Director not found"));
//
//        Movies movie = moviesRepo.findById(movieId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
//
//        if (!movie.getDirector().equals(director)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Not your movie");
//        }
//
//        // Поиск скрипта
//        Script script = scriptRepo.findByMovieId(movieId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
//
//        String filePath = script.getContent(); // https://storage.googleapis.com/your-bucket/ScriptFilms/UUIDfilename.jpg
//
//
//        // Извлекаем путь относительно bucket
//        log.info("File path from DB: {}", filePath);
//        String objectName;
//        if (filePath.startsWith("https://storage.googleapis.com/")) {
//            objectName = filePath.replace("https://storage.googleapis.com/" + bucketName + "/", "");
//        } else {
//            objectName = filePath; // если уже относительный путь
//        }
//        log.debug("Final object name: {}", objectName);
//
//        Blob blob = storage.get(bucketName, objectName);
//
//        if (blob == null) {
//            log.error("Blob is null for objectName: {}", objectName);
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found in Google Cloud Storage");
//        }
//
//        log.info("Bucket name: {}", bucketName);
//        log.info("Trying to delete from bucket: {}, object: {}", bucketName, objectName);
//
//
//
//        if (blob == null || !blob.exists()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found in storage");
//        }
//
//
//
//        boolean deleted = blob.delete();
//        if (deleted) {
//            log.info("Successfully deleted object: {}", objectName);
//        } else {
//            log.error("Failed to delete object: {}", objectName);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file from storage");
//        }
//
//        if (!deleted) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file from storage");
//        }
//
//        // Сброс связи у фильма
//        Movies moviee = script.getMovie();
//        if (moviee != null) {
//            moviee.setScript(null);
//        }
//
//        scriptRepo.delete(script); // Удаляем запись из базы (опционально)
//    }
//
//    }
//
