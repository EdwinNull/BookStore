package com.weblearning.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.SupplierBooks;
import com.weblearning.bookstore.servcie.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookController 单元测试类
 * 使用 MockMvc 模拟 HTTP 请求，使用 @MockitoBean Mock BookService 服务层
 * 测试覆盖所有 Controller 接口方法
 *
 * 注意：Spring Boot 3.4.0+ 推荐使用 @MockitoBean 替代已废弃的 @MockBean
 * 注意：@ActiveProfiles("test") 激活 test profile，禁用 WebConfig 中的登录拦截器
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private BookService bookService;

    private Books testBook;

    @BeforeEach
    void setUp() {
        testBook = new Books();
        testBook.setBookId(1);
        testBook.setTitle("测试书籍");
        testBook.setAuthor1("作者1");
        testBook.setPublisher("测试出版社");
        testBook.setPrice(99.9);
    }

    @Test
    @DisplayName("添加书籍 - 成功")
    void testAddBook_Success() throws Exception {
        String bookJson = objectMapper.writeValueAsString(testBook);

        mockMvc.perform(post("/api/books/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));

        verify(bookService, times(1)).addBook(any(Books.class));
    }

    @Test
    @DisplayName("删除书籍 - 成功")
    void testDeleteBook_Success() throws Exception {
        int bookId = 1;

        mockMvc.perform(delete("/api/books/delete/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));

        verify(bookService, times(1)).deleteBook(bookId);
    }

    @Test
    @DisplayName("添加供应商书籍 - 成功")
    void testAddSupplierBooks_Success() throws Exception {
        SupplierBooks supplierBook = new SupplierBooks();
        supplierBook.setSupplierName(1);
        supplierBook.setBookName(1);
        supplierBook.setQuantity(100);

        List<SupplierBooks> supplierBooks = Arrays.asList(supplierBook);
        String json = objectMapper.writeValueAsString(supplierBooks);

        mockMvc.perform(post("/api/books/addSupplierBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));

        verify(bookService, times(1)).addSupplierBooks(anyList());
    }

    @Test
    @DisplayName("查询书籍 - 按标题查询成功")
    void testFindBook_ByTitle() throws Exception {
        List<Books> books = Arrays.asList(testBook);
        when(bookService.findBookByName(anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(books);

        mockMvc.perform(get("/api/books/findBook")
                        .param("title", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("测试书籍"));

        verify(bookService, times(1)).findBookByName(anyString(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("更新书籍 - 成功")
    void testUpdateBook_Success() throws Exception {
        String bookJson = objectMapper.writeValueAsString(testBook);

        mockMvc.perform(put("/api/books/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"));

        verify(bookService, times(1)).updateBook(any(Books.class));
    }

    @Test
    @DisplayName("获取书籍详情 - 成功")
    void testShowBook_Success() throws Exception {
        int bookId = 1;
        when(bookService.showBook(bookId)).thenReturn(testBook);

        mockMvc.perform(get("/api/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.bookId").value(1))
                .andExpect(jsonPath("$.data.title").value("测试书籍"));

        verify(bookService, times(1)).showBook(bookId);
    }

    @Test
    @DisplayName("获取所有书籍 - 分页查询成功")
    void testGetAllBooks_Success() throws Exception {
        PageBean<Books> pageBean = new PageBean<>();
        pageBean.setTotal(1L);
        pageBean.setItems(Arrays.asList(testBook));

        when(bookService.getAllBooks(1, 10, null, null)).thenReturn(pageBean);

        mockMvc.perform(get("/api/books")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items").isArray());

        verify(bookService, times(1)).getAllBooks(1, 10, null, null);
    }
}
