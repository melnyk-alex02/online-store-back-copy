package com.store.repository;

import com.store.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select count(p) from Product p where p.category.id =:id")
    long countItemsByCategory(Long id);

    @Query("select new Category(c.id, c.name, c.title, c.path, c.description, c.overview, c.imgSrc, c.createdAt, c.updatedAt, c.coordinateOnBannerX, c.coordinateOnBannerY, count(p)) from Category c left join Product p on  c.id =p.category.id group by c.id")
    Page<Category> findAll(Pageable pageable);
}