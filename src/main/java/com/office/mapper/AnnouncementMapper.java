package com.office.mapper;

import com.office.entity.Announcement;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AnnouncementMapper {
    @Select("SELECT * FROM announcements WHERE status = 1 ORDER BY create_time DESC")
    @Results({
        @Result(property = "announcementId", column = "announcement_id"),
        @Result(property = "creatorId", column = "creator_id"),
        @Result(property = "createTime", column = "create_time"),
    })
    List<Announcement> findAllActive();
    
    @Select("SELECT * FROM announcements ORDER BY create_time DESC")
    @Results({
        @Result(property = "announcementId", column = "announcement_id"),
        @Result(property = "creatorId", column = "creator_id"),
        @Result(property = "createTime", column = "create_time"),
    })
    List<Announcement> findAll();
    
    @Insert("INSERT INTO announcements (title, content, creator_id, create_time, status) " +
            "VALUES (#{title}, #{content}, #{creatorId}, #{createTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "announcementId")
    void insert(Announcement announcement);
    
    @Update("UPDATE announcements SET status = #{status} WHERE announcement_id = #{announcementId}")
    void updateStatus(@Param("announcementId") Integer announcementId, @Param("status") Integer status);
    
    @Delete("DELETE FROM announcements WHERE announcement_id = #{announcementId}")
    void deleteById(@Param("announcementId") Integer announcementId);
} 