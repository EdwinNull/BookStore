package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.SupplierBooks;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    void addBook(Books book);

    void deleteBook(int id);


    void addSupplierBooks(List<SupplierBooks> supplierBooks);
    

    void updateBook(Books book);

    Books findById(Integer bookId);

    void updateQuantity(Integer stockQuantity,Integer bookId);

    List<Books> getAllBooks(Integer supplierId, String title);

    List<Books> findBookByName(String title, String keywords, String author1, String author2, String author3, String author4, String publisher);

    void increaseStock(Integer bookId, Integer quantity);
}
