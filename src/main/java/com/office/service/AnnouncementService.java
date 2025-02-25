package com.office.service;

import com.office.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> getActiveAnnouncements();
    List<Announcement> getAllAnnouncements();
    void createAnnouncement(Announcement announcement);
    void updateStatus(Integer announcementId, Integer status);
    void deleteAnnouncement(Integer announcementId);
} 