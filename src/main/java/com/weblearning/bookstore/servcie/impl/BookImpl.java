package com.weblearning.bookstore.servcie.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.weblearning.bookstore.mapper.BookMapper;
import com.weblearning.bookstore.pojo.Books;
import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.SupplierBooks;
import com.weblearning.bookstore.servcie.BookService;
import com.weblearning.bookstore.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public void addBook(Books book) {
        bookMapper.addBook(book);
    }

    @Override
    public void deleteBook(int id) {
        bookMapper.deleteBook(id);
    }

    @Override
    public void addSupplierBooks(List<SupplierBooks> supplierBooks) {
        bookMapper.addSupplierBooks(supplierBooks);
    }


    @Override
    public void updateBook( Books book) {
        bookMapper.updateBook(book);
    }

    @Override
    public Books showBook(int bookId) {
        Books book = bookMapper.findById(bookId);
        return book;
    }

    @Override
    public PageBean<Books> getAllBooks(Integer pageNum, Integer pageSize, Integer supplierId, String title) {
        PageBean<Books> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List<Books> as = bookMapper.getAllBooks(supplierId,title);
        Page<Books> p = (Page<Books>) as;
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public List<Books> findBookByName(String title, String keywords, String author1, String author2, String author3, String author4, String publisher) {
        return bookMapper.findBookByName(title, keywords, author1, author2, author3, author4, publisher);
    }
}
