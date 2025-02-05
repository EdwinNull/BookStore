package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.PageBean;
import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Suppliers;
import com.weblearning.bookstore.servcie.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/add")
    public Result addSupplier(@RequestBody Suppliers supplier) {
        supplierService.addSupplier(supplier);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result updateSupplier(@RequestBody Suppliers supplier) {
        supplierService.updateSupplier(supplier);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteSupplier(@PathVariable("id") Integer id) {
        supplierService.deleteSupplier(id);
        return Result.success("删除成功");
    }

    @GetMapping("/findByName/{name}")
    public Result findByName(@PathVariable("name") String name) {
        Suppliers supplier = supplierService.findByName(name);
        return Result.success(supplier);
    }
}

