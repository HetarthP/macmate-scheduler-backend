package com.mcmaster.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedule_items")
public class ScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // "MATH 1B03 Midterm"
    private String type;           // "exam" or "assignment"
    private String courseCode;     // "MATH 1B03"

    @JsonFormat(pattern = "yyyy-MM-dd") //  This fixes JSON parsing
    private LocalDate dueDate;          // 2025-06-15

    private String notes;          // optional
    private String ownerEmail;     // linked to user

    public ScheduleItem() {}

    public ScheduleItem(String title, String type, String courseCode, LocalDate dueDate, String notes, String ownerEmail) {
        this.title = title;
        this.type = type;
        this.courseCode = courseCode;
        this.dueDate = dueDate;
        this.notes = notes;
        this.ownerEmail = ownerEmail;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
