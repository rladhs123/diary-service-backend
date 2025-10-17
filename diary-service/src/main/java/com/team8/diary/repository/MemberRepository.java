package com.team8.diary.repository;

import com.team8.diary.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberEmail(String email);

    boolean existsByMemberEmail(String email);
}
