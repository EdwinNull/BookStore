package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.SupplierBooks;
import com.weblearning.bookstore.servcie.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理接口
 * 提供图书的增删改查功能
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "图书管理", description = "图书的增删改查、搜索等相关接口")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 添加图书
     */
    @Operation(summary = "添加图书", description = "添加一本新图书到系统中")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/add")
    public Result addBook(
            @Parameter(description = "图书信息", required = true)
            @RequestBody Books book) {
        bookService.addBook(book);
        return Result.success("添加成功");
    }

    /**
     * 删除图书
     */
    @Operation(summary = "删除图书", description = "根据图书ID删除图书")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @DeleteMapping("/delete/{id}")
    public Result deleteBook(
            @Parameter(description = "图书ID", required = true, example = "1")
            @PathVariable int id) {
        bookService.deleteBook(id);
        return Result.success("删除成功");
    }

    /**
     * 添加供应商图书关联
     */
    @Operation(summary = "添加供应商图书关联", description = "批量添加供应商与图书的关联关系")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PostMapping("/addSupplierBooks")
    public Result addSupplierBooks(
            @Parameter(description = "供应商图书关联列表", required = true)
            @RequestBody List<SupplierBooks> supplierBooks) {
        bookService.addSupplierBooks(supplierBooks);
        return Result.success("添加成功");
    }

    /**
     * 搜索图书
     */
    @Operation(summary = "搜索图书", description = "根据书名、关键词、作者、出版社等条件搜索图书，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "搜索成功")
    })
    @GetMapping("/findBook")
    public Result<List<Books>> findBook(
            @Parameter(description = "书名")
            @RequestParam(required = false) String title,
            @Parameter(description = "关键词")
            @RequestParam(required = false) String keywords,
            @Parameter(description = "作者1")
            @RequestParam(required = false) String author1,
            @Parameter(description = "作者2")
            @RequestParam(required = false) String author2,
            @Parameter(description = "作者3")
            @RequestParam(required = false) String author3,
            @Parameter(description = "作者4")
            @RequestParam(required = false) String author4,
            @Parameter(description = "出版社")
            @RequestParam(required = false) String publisher
    ) {
        List<Books> books = bookService.findBookByName(title, keywords, author1, author2, author3, author4, publisher);
        return Result.success(books);
    }

    /**
     * 更新图书信息
     */
    @Operation(summary = "更新图书", description = "更新图书的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @PutMapping("/update")
    public Result updateBook(
            @Parameter(description = "图书信息", required = true)
            @RequestBody Books book) {
        bookService.updateBook(book);
        return Result.success("更新成功");
    }

    /**
     * 获取图书详情
     */
    @Operation(summary = "获取图书详情", description = "根据图书ID获取图书详细信息，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "图书不存在")
    })
    @GetMapping("/{bookId}")
    public Result showBook(
            @Parameter(description = "图书ID", required = true, example = "1")
            @PathVariable("bookId") int bookId) {
        Books book = bookService.showBook(bookId);
        return Result.success(book);
    }

    /**
     * 获取图书列表（分页）
     */
    @Operation(summary = "获取图书列表", description = "分页获取图书列表，支持按供应商和书名筛选，支持匿名访问")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping
    public Result<PageBean<Books>> getAllBooks(
            @Parameter(description = "页码", example = "1")
            Integer pageNum,
            @Parameter(description = "每页数量", example = "10")
            Integer pageSize,
            @Parameter(description = "供应商ID（可选）")
            @RequestParam(required = false) Integer supplierId,
            @Parameter(description = "书名（可选，模糊搜索）")
            @RequestParam(required = false) String title
    ) {
        PageBean<Books> pageBean = bookService.getAllBooks(pageNum, pageSize, supplierId, title);
        return Result.success(pageBean);
    }
}
