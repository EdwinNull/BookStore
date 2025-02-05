package com.weblearning.bookstore.controller;

import com.weblearning.bookstore.pojo.Result;
import com.weblearning.bookstore.pojo.Series;
import com.weblearning.bookstore.servcie.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @PostMapping("/add")
    public Result addSeries(@RequestBody Series series) {
        seriesService.addSeries(series);
        return Result.success("Series added");
    }

    @GetMapping("/find/{name}")
    public Series findSeriesByName(@PathVariable("name") String name) {
        Series series = seriesService.findSeriesByName(name);
        return series;
    }

    @PutMapping("/update")
    public Result updateSeries(@RequestBody Series series) {
        seriesService.updateSeries(series);
        return Result.success("Series updated");
    }
}
