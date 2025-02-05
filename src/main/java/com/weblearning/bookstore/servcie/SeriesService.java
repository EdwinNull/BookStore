package com.weblearning.bookstore.servcie;

import com.weblearning.bookstore.pojo.Series;

public interface SeriesService {
    void addSeries(Series series);

    Series findSeriesByName(String name);

    void updateSeries(Series series);
}
