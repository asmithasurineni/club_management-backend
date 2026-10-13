
package com.clubmanagement.backend.membership;

import jakarta.persistence.*;

@Entity
@Table(
    name = "memberships",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"student_id", "club_id"}
        )
    }
)
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Column(length = 20)
    private String status = "PENDING";

    public Membership() {}

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getClubId() {
        return clubId;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

