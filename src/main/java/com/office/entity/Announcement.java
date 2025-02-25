package com.office.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Integer announcementId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String content;
    
    @Column(name = "creator_id")
    private Integer creatorId;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column(nullable = false)
    private Integer status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;
} 