package kr.adapterz.springboot.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    Long countByPost_Id(Long postId);
}
