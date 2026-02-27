package com.pipeline.repository;

import com.pipeline.enums.ApprovalStatus;
import com.pipeline.model.DEApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DEApprovalRequestRepository extends JpaRepository<DEApprovalRequest, Long> {

    List<DEApprovalRequest> findByStatus(ApprovalStatus status);

    long countByStatus(ApprovalStatus status);

    Optional<DEApprovalRequest> findFirstByPipelineIdAndStatusOrderByCreatedAtDesc(String pipelineId, ApprovalStatus status);

    @Query("SELECT d FROM DEApprovalRequest d WHERE d.status IN :statuses ORDER BY d.createdAt DESC")
    List<DEApprovalRequest> findRecentDecisions(List<ApprovalStatus> statuses);

    @Query("SELECT d FROM DEApprovalRequest d WHERE d.status IN :statuses AND d.createdAt >= :startTime ORDER BY d.createdAt DESC")
    List<DEApprovalRequest> findRecentDecisionsByTimePeriod(
            @Param("statuses") List<ApprovalStatus> statuses,
            @Param("startTime") Instant startTime
    );
}
