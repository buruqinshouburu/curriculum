package com.doinner.csys.controller;

import com.doinner.common.core.domain.DataSet;
import com.doinner.common.core.domain.Message;
import com.doinner.csys.service.WorkOrderService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {
    private final WorkOrderService service;

    public WorkOrderController(WorkOrderService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public DataSet<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        return DataSet.success(service.upload(file));
    }

    @PostMapping
    public DataSet<Map<String, Object>> create(@RequestBody OrderRequest request) {
        return DataSet.success(service.create(request.description, request.imageIds));
    }

    @GetMapping("/list")
    public DataSet<Map<String, Object>> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "status", required = false) String status) {
        return DataSet.success(service.list(false, pageNum, pageSize, description, status));
    }

    @GetMapping("/admin/list")
    public DataSet<Map<String, Object>> adminList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam(value = "status", required = false) String status) {
        return DataSet.success(service.list(true, pageNum, pageSize, description, status));
    }

    @GetMapping("/{id}")
    public DataSet<Map<String, Object>> detail(@PathVariable("id") long id) {
        return DataSet.success(service.detail(id));
    }

    @PutMapping("/{id}")
    public Message update(@PathVariable("id") long id, @RequestBody OrderRequest request) {
        service.update(id, request.description, request.imageIds);
        return Message.success();
    }

    @DeleteMapping("/{id}")
    public Message delete(@PathVariable("id") long id) {
        service.delete(id);
        return Message.success();
    }

    @PutMapping("/{id}/feedback")
    public Message feedback(@PathVariable("id") long id, @RequestBody ReplyRequest request) {
        service.reply(id, request.reply);
        return Message.success();
    }

    @GetMapping("/unreadCount")
    public DataSet<Long> unreadCount() {
        return DataSet.success(service.unreadCount());
    }

    @PutMapping("/{id}/read")
    public Message read(@PathVariable("id") long id, @RequestBody ReadRequest request) {
        service.read(id, request.replyVersion == null ? 0 : request.replyVersion);
        return Message.success();
    }

    @GetMapping("/{id}/images/{fileId}")
    public ResponseEntity<byte[]> image(@PathVariable("id") long id, @PathVariable("fileId") String fileId) {
        Map<String, Object> image = service.image(id, fileId);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType((String) image.get("contentType")))
                .header("Content-Disposition", ContentDisposition.attachment().filename((String) image.get("fileName"), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body((byte[]) image.get("bytes"));
    }

    public static class OrderRequest {
        public String description;
        public List<String> imageIds;
    }

    public static class ReplyRequest {
        public String reply;
    }

    public static class ReadRequest {
        public Long replyVersion;
    }
}
