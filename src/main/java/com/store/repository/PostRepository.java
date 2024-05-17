package com.store.repository;

import com.store.entity.Post;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsById(@NotNull Long id);

    Page<Post> findAllByPostStatus(String postStatus, Pageable pageable);

    Page<Post> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Post> getPostByHeroIsTrue();
}