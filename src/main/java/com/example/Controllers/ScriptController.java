package com.example.Controllers;



import com.example.Entity.Movies;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.ScriptService;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/Script")
public class ScriptController {

    private final ScriptService service;
    private final UsersRepo usersRepo;
    private final Storage storage;
    @Value("${google.cloud.storage.bucket.name}")
    private String bucketName;



    // Добавить в контролеры проверку что юзер может изменять текущие
    // сценарии к фильму и проверять что авторизованный юзер именно тот за кого себя выдает

    @Autowired
    public ScriptController(ScriptService service, UsersRepo usersRepo, Storage storage) {
        this.service = service;
        this.usersRepo = usersRepo;
        this.storage = storage;
    }


    // ▶️ Новая часть
    // контроллер для загрузки сценария в хранилище
    @Loggable
    @PostMapping("/uploadScriptMovie/{movieId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public CompletableFuture<ResponseEntity<String>> uploadScript(@RequestParam("file") MultipartFile file,
                                                  Principal principal,
                                                  @PathVariable("movieId") Long movieId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        String username = authentication.getName(); // Получаем имя пользователя

        try {
            return service.uploadFile(userId,movieId,file)
                    .thenApply( path -> ResponseEntity.ok(path))
                    .exceptionally( ex-> {
                        String msg = ex.getCause() != null ? ex.getCause().getMessage(): ex.getMessage();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed " + msg);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

 // контроллер для скачивания сценария
    @Loggable
    @GetMapping("/downloadScript/{movieId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> downloadScriptFile(@PathVariable Long movieId,
                                  Principal principal,
                                   HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        service.downloadScript(movieId,userId,response);

        return ResponseEntity.ok("Script successfully deleted.");
    }
        //контроллер для удаления сценария
        @Loggable
        @DeleteMapping("/deleteScript/{movieId}")
        @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
        public void deleteScript(@PathVariable Long movieId,
                                       Principal principal) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

            service.deleteScript(movieId,userId);

        }

        // 1.добавить контроллер для изменения сценария , просто заменить файл

 // или первую идею реализовать или вторую

    // 2.добавить возможность редактирования файла напрямую в приложении,
    // а после просто сохранить изменения в хранилище для безопасного хранения ифонмации

    @Loggable
    @GetMapping("/download/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
        try (InputStream inputStream = downloadFileFromGCS(fileName);
             OutputStream outputStream = response.getOutputStream()) {

            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            String contentType = blob.getContentType(); // Отримуємо Content-Type з GCS
            if (contentType == null) {
                contentType = "application/octet-stream"; // Встановлюємо за замовчуванням
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error downloading file", e);
        }
    }



    private InputStream downloadFileFromGCS(String objectName) throws IOException {
        Blob blob = storage.get(bucketName, objectName);
        if (blob == null) {
            throw new FileNotFoundException("File not found in GCS.");
        }

        ReadableByteChannel channel = blob.reader();
        return Channels.newInputStream(channel);
    }

    @Loggable
    @DeleteMapping("/delete/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<String> deleteScript(@PathVariable String fileName) {
        boolean deleted = service.deleteFile(fileName);
        if (deleted) {
            return ResponseEntity.ok("Script deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Script not found.");
        }
    }
    @Loggable
    @PostMapping("/update/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<String> updateFile(@PathVariable String fileName, MultipartFile file){
        try {
            String fileUrl = service.updateFile(fileName,file);
            return ResponseEntity.ok("File update:"+fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Loggable
    @GetMapping("/files")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = service.listFiles();
        return ResponseEntity.ok(files);
    }

    @Loggable
    @GetMapping("/information/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String,Object>> getFileInformation(@PathVariable String fileName) {
        Map<String,Object> fileInfo = service.getInfoFile(fileName);
        return ResponseEntity.ok(fileInfo);
    }

    @Loggable
    @PostMapping("/create-folder/{folderName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createFolder(@PathVariable String folderName) {
        boolean created = service.createFolder(folderName);
        if (created) {
            return ResponseEntity.ok("Folder created.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create folder.");
        }
    }



    @Loggable
    @PostMapping("/upload/{folder}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>uploadInFolder(@PathVariable String folder, @RequestParam("file") MultipartFile file){
       try {
           String url = service.uploadFileInFolder(folder, file);
           return ResponseEntity.ok("File upload:" + url);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }

    @Loggable
    @GetMapping("/search/{prefix}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> searchFilesByPrefix(@PathVariable String prefix){
        return ResponseEntity.ok(service.searchListByPrefix(prefix));
    }

    @Loggable
    @PostMapping("/copy")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> copyFile(@RequestParam String source, @RequestParam String target){
        boolean success = service.copyFile(source,target);
        return success ? ResponseEntity.ok("File copied successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to copy file.");
    }

    @Loggable
    @PostMapping("/move")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> moveFile(@RequestParam String source, @RequestParam String target){
        boolean success = service.moveFile(source,target);
        return success? ResponseEntity.ok("File moved successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to move file.");
    }

    @Loggable
    @GetMapping("/stream/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public void streamFile(@PathVariable String fileName, HttpServletResponse response) {
        try(InputStream inputStream = service.streamFile(fileName);
        OutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Loggable
    @GetMapping("/read-docx/{fileName}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<String> readWordFile(@PathVariable String fileName) {
        try {
            String text = service.readDocxFile(fileName);
            return ResponseEntity.ok(text);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        }
    }


}







