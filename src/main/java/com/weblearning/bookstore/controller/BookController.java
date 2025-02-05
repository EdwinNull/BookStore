package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.SupplierBooks;
import com.weblearning.bookstore.servcie.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public Result addBook(@RequestBody Books book){
        bookService.addBook(book);
        System.out.println("book="+book);
        return Result.success("添加成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteBook(@PathVariable int id){
        bookService.deleteBook(id);
        return Result.success("删除成功");
    }

    @PostMapping("/addSupplierBooks")
    public Result addSupplierBooks(@RequestBody List<SupplierBooks> supplierBooks){
        bookService.addSupplierBooks(supplierBooks);
        return Result.success("添加成功");
    }

    @GetMapping("/findBook")
    public Result<List<Books>> findBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String author1,
            @RequestParam(required = false) String author2,
            @RequestParam(required = false) String author3,
            @RequestParam(required = false) String author4,
            @RequestParam(required = false) String publisher
    ){
        List<Books> books = bookService.findBookByName(title, keywords, author1, author2, author3, author4, publisher);
        return Result.success(books);
    }

    @PutMapping("/update")
    public Result updateBook(@RequestBody Books book){
        bookService.updateBook(book);
        return Result.success("更新成功");
    }

    @GetMapping("/{bookId}")
    public Result showBook(@PathVariable("bookId") int bookId){
        Books book = bookService.showBook(bookId);
        return Result.success(book);
    }

    @GetMapping
    public Result<PageBean<Books>> getAllBooks(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String title
    ){
        PageBean<Books> pageBean = bookService.getAllBooks(pageNum, pageSize, supplierId, title);
        return Result.success(pageBean);
    }

}
