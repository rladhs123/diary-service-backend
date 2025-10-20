package com.team8.diary.repository;

import com.team8.diary.domain.Diary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.awt.print.Pageable;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Page<Diary> findByMember_MemberId(Long memberMemberId, Pageable pageable);

    Optional<Diary>     
}
