package com.doinner.csys;

import com.doinner.common.core.context.SecurityContextHolder;
import com.doinner.csys.permission.CoursePermissionException;
import com.doinner.csys.permission.CoursePermissionExceptionHandler;
import com.doinner.csys.controller.WorkOrderController;
import com.doinner.csys.service.WorkOrderService;
import com.doinner.common.core.domain.DataSet;
import com.doinner.file.api.domain.FileInfo;
import com.doinner.file.api.service.RemoteFileInfoService;
import com.doinner.system.domain.entity.SysUser;
import com.doinner.system.service.DoinnerUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkOrderServiceTest {
    private JdbcTemplate jdbc;
    private WorkOrderService service;
    private RemoteFileInfoService files;
    private DoinnerUserService users;
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setup() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE t_csys_work_order(id BIGINT AUTO_INCREMENT PRIMARY KEY, creator_id BIGINT NOT NULL, description VARCHAR(2000) NOT NULL, reply VARCHAR(2000), reply_description VARCHAR(2000), reply_time TIMESTAMP, reply_version BIGINT DEFAULT 0 NOT NULL, read_version BIGINT DEFAULT 0 NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        jdbc.execute("CREATE TABLE t_csys_work_order_image(file_id VARCHAR(255) PRIMARY KEY, creator_id BIGINT NOT NULL, work_order_id BIGINT, file_name VARCHAR(255) NOT NULL, content_type VARCHAR(32) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        files = mock(RemoteFileInfoService.class);
        users = mock(DoinnerUserService.class);
        context = new AnnotationConfigApplicationContext();
        context.register(TransactionConfig.class);
        context.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(dataSource));
        context.registerBean(WorkOrderService.class, () -> new WorkOrderService(jdbc, files, users));
        context.refresh();
        service = context.getBean(WorkOrderService.class);
        login(11);
    }

    @AfterEach
    void cleanup() { SecurityContextHolder.remove(); context.close(); }

    @Test
    void anyLoggedInUserCanManageOrdersButOnlyOwnerCanEditOrDelete() {
        long id = ((Number) service.create("problem", Collections.emptyList()).get("id")).longValue();
        login(22);
        assertEquals(id, ((Number) service.detail(id).get("id")).longValue());
        assertStatus(403, () -> service.update(id, "changed", Collections.emptyList()));
        assertStatus(403, () -> service.delete(id));
        assertDoesNotThrow(() -> service.reply(id, "answer"));
        assertEquals(0L, service.list(false, 1, 10, null, null).get("total"));
        assertEquals(1L, service.list(true, 1, 10, null, null).get("total"));
        login(1);
        assertEquals(1L, service.list(true, 1, 10, null, null).get("total"));
        assertEquals(id, ((Number) service.detail(id).get("id")).longValue());
    }

    @Test
    void newReplyCannotBeMarkedReadByAnOldVersion() {
        long id = ((Number) service.create("problem", Collections.emptyList()).get("id")).longValue();
        login(1);
        service.reply(id, "first");
        login(11);
        assertEquals(1L, service.unreadCount());
        long version = ((Number) service.detail(id).get("replyVersion")).longValue();
        login(1);
        service.reply(id, "second");
        login(11);
        assertStatus(409, () -> service.read(id, version));
        assertEquals(1L, service.unreadCount());
        service.update(id, "changed", Collections.emptyList());
        assertEquals(true, service.detail(id).get("unread"));
        assertEquals("problem", service.detail(id).get("replyDescription"));
        service.read(id, version + 1);
        assertEquals(0L, service.unreadCount());
        assertEquals("second", service.detail(id).get("reply"));
        assertEquals("problem", service.detail(id).get("replyDescription"));
        service.delete(id);
        assertEquals(0L, service.list(false, 1, 10, null, null).get("total"));
    }

    @Test
    void foreignOrAlreadyAttachedImagesCannotBeClaimed() {
        jdbc.update("INSERT INTO t_csys_work_order_image(file_id,creator_id,file_name,content_type) VALUES('mine',11,'a.png','image/png'),('foreign',22,'b.png','image/png')");
        assertStatus(400, () -> service.create("problem", Collections.singletonList("foreign")));
        Map<String, Object> first = service.create("problem", Collections.singletonList("mine"));
        assertEquals(Collections.singletonList("mine"), first.get("imageIds"));
        assertStatus(400, () -> service.create("problem", Collections.singletonList("mine")));
        login(22);
        assertStatus(404, () -> service.image(((Number) first.get("id")).longValue(), "missing"));
    }

    @Test
    void uploadAndImageReadUseTheRegisteredOwnerAndOrder() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", out);
        byte[] bytes = out.toByteArray();
        FileInfo info = new FileInfo();
        info.setFileId("file123");
        when(files.upload(any(MultipartFile.class), anyString())).thenReturn(DataSet.success(info));
        when(files.getFileById("file123")).thenReturn(DataSet.success(bytes));
        assertEquals("file123", service.upload(new MockMultipartFile("file", "test.png", "image/png", bytes)).get("fileId"));
        long id = ((Number) service.create("problem", Collections.singletonList("file123")).get("id")).longValue();
        assertArrayEquals(bytes, (byte[]) service.image(id, "file123").get("bytes"));
        login(22);
        assertArrayEquals(bytes, (byte[]) service.image(id, "file123").get("bytes"));
        login(1);
        assertArrayEquals(bytes, (byte[]) service.image(id, "file123").get("bytes"));
        login(11);
        assertStatus(400, () -> service.upload(new MockMultipartFile("file", "fake.png", "image/png", new byte[]{1, 2})));
        verify(files, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    void documentAttachmentsKeepTheirNameAndRemainPrivate() throws Exception {
        byte[] bytes = "document".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        FileInfo info = new FileInfo();
        info.setFileId("document123");
        when(files.upload(any(MultipartFile.class), anyString())).thenReturn(DataSet.success(info));
        when(files.getFileById("document123")).thenReturn(DataSet.success(bytes));
        Map<String, Object> uploaded = service.upload(new MockMultipartFile("file", "问题说明.pdf", "application/pdf", bytes));
        assertEquals("问题说明.pdf", uploaded.get("fileName"));
        Map<String, Object> order = service.create("problem", Collections.singletonList("document123"));
        Map<?, ?> attachment = (Map<?, ?>) ((java.util.List<?>) order.get("attachments")).get(0);
        assertEquals("问题说明.pdf", attachment.get("fileName"));
        assertEquals("application/octet-stream", attachment.get("contentType"));
        long id = ((Number) order.get("id")).longValue();
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new WorkOrderController(service))
                .setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(get("/workOrder/" + id + "/images/document123"))
                .andExpect(status().isOk()).andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().bytes(bytes));
        login(22);
        assertArrayEquals(bytes, (byte[]) service.image(id, "document123").get("bytes"));
        login(11);
        assertStatus(400, () -> service.upload(new MockMultipartFile("file", "malware.exe", "application/octet-stream", bytes)));
    }

    @Test
    void httpContractExposesSeparateAdminListAndVersionedRead() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new WorkOrderController(service))
                .setControllerAdvice(new CoursePermissionExceptionHandler()).build();
        mvc.perform(post("/workOrder").contentType("application/json")
                        .content("{\"description\":\"problem\",\"imageIds\":[]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("0"))
                .andExpect(jsonPath("$.data.replyVersion").value(0));
        mvc.perform(get("/workOrder/list"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(1));
        mvc.perform(get("/workOrder/list").param("description", "missing").param("status", "0"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(0));
        mvc.perform(get("/workOrder/admin/list")).andExpect(status().isOk());
        login(1);
        mvc.perform(put("/workOrder/1/feedback").contentType("application/json").content("{\"reply\":\"done\"}"))
                .andExpect(status().isOk());
        login(11);
        mvc.perform(get("/workOrder/unreadCount")).andExpect(jsonPath("$.data").value(1));
        mvc.perform(put("/workOrder/1/read").contentType("application/json").content("{\"replyVersion\":1}"))
                .andExpect(status().isOk());
        mvc.perform(get("/workOrder/unreadCount")).andExpect(jsonPath("$.data").value(0));
    }

    @Test
    void failedImageAssociationRollsBackOrderCreation() {
        jdbc.update("INSERT INTO t_csys_work_order_image(file_id,creator_id,file_name,content_type) VALUES('mine',11,'a.png','image/png')");
        jdbc.execute("ALTER TABLE t_csys_work_order_image ADD CONSTRAINT reject_attachment CHECK(work_order_id IS NULL)");
        assertThrows(org.springframework.dao.DataAccessException.class,
                () -> service.create("problem", Collections.singletonList("mine")));
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_work_order", Long.class));
    }

    @Test
    void filtersApplyBeforePaginationAndAdminGetsSystemDisplayName() {
        service.create("alpha", Collections.emptyList());
        service.create("beta", Collections.emptyList());
        service.create("alpha again", Collections.emptyList());
        login(1);
        service.reply(1, "answer");
        SysUser user = new SysUser();
        user.setNickName("Display Name");
        when(users.getUserById(11L)).thenReturn(DataSet.success(user));
        Map<String, Object> result = service.list(true, 1, 1, "alpha", "1");
        assertEquals(1L, result.get("total"));
        Map<?, ?> row = (Map<?, ?>) ((java.util.List<?>) result.get("rows")).get(0);
        assertEquals("Display Name", row.get("creatorName"));
        assertEquals("alpha", row.get("description"));
        assertEquals(1L, service.list(true, 1, 1, "alpha", "0").get("total"));
        assertStatus(400, () -> service.list(true, 1, 10, null, "2"));
    }

    @Test
    void adminListPrioritizesPendingBeforePaginatingAndSortsByCreationTime() {
        for (int i = 0; i < 4; i++) service.create("order " + i, Collections.emptyList());
        jdbc.update("UPDATE t_csys_work_order SET created_at=TIMESTAMP '2026-01-01 00:00:00' WHERE id=3");
        jdbc.update("UPDATE t_csys_work_order SET created_at=TIMESTAMP '2026-02-01 00:00:00' WHERE id=1");
        jdbc.update("UPDATE t_csys_work_order SET created_at=TIMESTAMP '2026-03-01 00:00:00' WHERE id=2");
        jdbc.update("UPDATE t_csys_work_order SET created_at=TIMESTAMP '2026-04-01 00:00:00' WHERE id=4");
        login(1);
        service.reply(4, "done");
        long[] expected = {2, 1, 3, 4};
        for (int page = 1; page <= expected.length; page++) {
            Map<?, ?> row = (Map<?, ?>) ((java.util.List<?>) service.list(true, page, 1, null, null).get("rows")).get(0);
            assertEquals(expected[page - 1], ((Number) row.get("id")).longValue());
        }
    }

    @Test
    void concurrentOrdersCannotClaimTheSameUploadedImage() throws Exception {
        jdbc.update("INSERT INTO t_csys_work_order_image(file_id,creator_id,file_name,content_type) VALUES('mine',11,'a.png','image/png')");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Boolean> create = () -> {
            login(11);
            try {
                start.await();
                service.create("problem", Collections.singletonList("mine"));
                return true;
            } catch (CoursePermissionException e) {
                assertEquals(400, e.getStatus());
                return false;
            } finally {
                SecurityContextHolder.remove();
            }
        };
        try {
            Future<Boolean> first = pool.submit(create);
            Future<Boolean> second = pool.submit(create);
            start.countDown();
            assertNotEquals(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS));
            assertEquals(1L, jdbc.queryForObject("SELECT COUNT(*) FROM t_csys_work_order", Long.class));
        } finally {
            pool.shutdownNow();
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class TransactionConfig {}

    private static void login(long id) {
        SecurityContextHolder.remove();
        SecurityContextHolder.setUserId(String.valueOf(id));
    }

    private static void assertStatus(int status, Runnable action) {
        assertEquals(status, assertThrows(CoursePermissionException.class, action::run).getStatus());
    }
}
