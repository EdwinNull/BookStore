package com.weblearning.bookstore.mapper;

import com.weblearning.bookstore.pojo.Series;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeriesMapper {
    @Insert("INSERT INTO bookstore.series (series_id, series_name) VALUES (#{seriesId}, #{seriesName})")
    void addSeries(Series series);

    @Select("SELECT * FROM bookstore.series WHERE series_name = #{name}")
    Series findSeriesByName(String name);

    @Update("UPDATE bookstore.series SET series_name = #{seriesName} WHERE series_id = #{seriesId}")
    void updateSeries(Series series);
}
