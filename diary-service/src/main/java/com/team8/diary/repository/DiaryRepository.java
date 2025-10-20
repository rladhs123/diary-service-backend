package com.team8.diary.repository;

import com.team8.diary.domain.Diary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public interface DiaryRepository extends JpaRepository<Diary, > {

    @PersistenceContext
    private final EntityManager em;

    public void save(Diary diary) {
        em.persist(diary);
    }

    public List<Diary> findDiaryList() {

    }
}
