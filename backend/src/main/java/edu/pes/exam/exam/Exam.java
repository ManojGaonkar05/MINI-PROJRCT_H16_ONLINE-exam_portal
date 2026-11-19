package edu.pes.exam.exam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 120)
    private String subject;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Column(name = "total_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMarks;

    @Column(name = "passing_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal passingMarks;

    protected Exam() {
    }

    public Exam(UUID createdBy, String title, String subject, int durationMinutes,
                OffsetDateTime startsAt, OffsetDateTime endsAt,
                BigDecimal totalMarks, BigDecimal passingMarks) {
        this.createdBy = createdBy;
        this.title = title;
        this.subject = subject;
        this.durationMinutes = durationMinutes;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
    }

    public UUID getId() { return id; }
    public UUID getCreatedBy() { return createdBy; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
    public int getDurationMinutes() { return durationMinutes; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public BigDecimal getTotalMarks() { return totalMarks; }
    public BigDecimal getPassingMarks() { return passingMarks; }
}
