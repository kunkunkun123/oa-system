package com.office.service.impl;

import com.office.entity.Announcement;
import com.office.mapper.AnnouncementMapper;
import com.office.mapper.UserMapper;
import com.office.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<Announcement> getActiveAnnouncements() {
        List<Announcement> announcements = announcementMapper.findAllActive();
        announcements.forEach(this::setCreator);
        return announcements;
    }
    
    @Override
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = announcementMapper.findAll();
        announcements.forEach(this::setCreator);
        return announcements;
    }
    
    private void setCreator(Announcement announcement) {
        if (announcement.getCreatorId() != null) {
            announcement.setCreator(userMapper.findById(announcement.getCreatorId()));
        }
    }
    
    @Override
    public void createAnnouncement(Announcement announcement) {
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setStatus(1);
        announcementMapper.insert(announcement);
    }
    
    @Override
    public void updateStatus(Integer announcementId, Integer status) {
        announcementMapper.updateStatus(announcementId, status);
    }
    
    @Override
    public void deleteAnnouncement(Integer announcementId) {
        announcementMapper.deleteById(announcementId);
    }
} 