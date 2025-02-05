package com.weblearning.bookstore.servcie.impl;

import com.weblearning.bookstore.mapper.SeriesMapper;
import com.weblearning.bookstore.pojo.Series;
import com.weblearning.bookstore.servcie.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeriesServiceImpl implements SeriesService {

    @Autowired
    private SeriesMapper seriesMapper;

    @Override
    public void addSeries(Series series) {
        seriesMapper.addSeries(series);
    }

    @Override
    public Series findSeriesByName(String name) {
        Series series = seriesMapper.findSeriesByName(name);
        return series;
    }

    @Override
    public void updateSeries(Series series) {
        seriesMapper.updateSeries(series);
    }
}
