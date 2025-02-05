package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.SupplierBooks;

import java.util.List;

public interface BookService {
    //添加书籍
    void addBook(Books book);

    void deleteBook(int id);


    void addSupplierBooks(List<SupplierBooks> supplierBooks);
    

    void updateBook(Books book);

    Books showBook(int bookId);

    PageBean<Books> getAllBooks(Integer pageNum, Integer pageSize, Integer supplierId, String title);

    List<Books> findBookByName(String title, String keywords, String author1, String author2, String author3, String author4, String publisher);
}
