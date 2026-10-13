
package com.clubmanagement.backend.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipRepository
        extends JpaRepository<Membership, Long> {

    boolean existsByStudentIdAndClubId(
        Long studentId, Long clubId
    );

    List<Membership> findByStudentId(Long studentId);
}
