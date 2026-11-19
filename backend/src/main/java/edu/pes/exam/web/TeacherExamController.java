package edu.pes.exam.web;

import edu.pes.exam.exam.Exam;
import edu.pes.exam.exam.ExamRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/teacher/exams")
public class TeacherExamController {
    private final ExamRepository exams;
    private final JdbcTemplate jdbc;

    public TeacherExamController(ExamRepository exams, JdbcTemplate jdbc) {
        this.exams = exams;
        this.jdbc = jdbc;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ExamResponse> create(@Valid @RequestBody CreateExamRequest request) {
        String role;
        try {
            role = jdbc.queryForObject(
                    "SELECT role FROM users WHERE id = ? AND is_active = TRUE",
                    String.class, request.createdById());
        } catch (EmptyResultDataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "An active teacher account is required to create an exam");
        }

        if (!"TEACHER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only teachers can create exams");
        }
        if (!request.startsAt().isBefore(request.endsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The exam end time must be after its start time");
        }
        if (request.passingMarks().compareTo(request.totalMarks()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passing marks cannot exceed total marks");
        }

        Exam saved = exams.save(new Exam(
                request.createdById(), request.title().trim(), request.subject().trim(),
                request.durationMinutes(), request.startsAt(), request.endsAt(),
                request.totalMarks(), request.passingMarks()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ExamResponse.from(saved));
    }

    public record CreateExamRequest(
            @NotNull UUID createdById,
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 120) String subject,
            @Min(5) @Max(240) int durationMinutes,
            @NotNull OffsetDateTime startsAt,
            @NotNull OffsetDateTime endsAt,
            @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal totalMarks,
            @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal passingMarks) {
    }

    public record ExamResponse(
            UUID id, UUID createdById, String title, String subject, String status,
            int durationMinutes, OffsetDateTime startsAt, OffsetDateTime endsAt,
            BigDecimal totalMarks, BigDecimal passingMarks) {
        static ExamResponse from(Exam exam) {
            return new ExamResponse(exam.getId(), exam.getCreatedBy(), exam.getTitle(),
                    exam.getSubject(), exam.getStatus(), exam.getDurationMinutes(),
                    exam.getStartsAt(), exam.getEndsAt(), exam.getTotalMarks(),
                    exam.getPassingMarks());
        }
    }
}
