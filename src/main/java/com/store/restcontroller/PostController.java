package com.store.restcontroller;

import com.store.constants.PostStatus;
import com.store.constants.Role;
import com.store.dto.postDTOs.PostCreateDTO;
import com.store.dto.postDTOs.PostDTO;
import com.store.dto.postDTOs.PostUpdateDTO;
import com.store.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Get all blogs", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/posts")
    public Page<PostDTO> getAllPosts(Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    @Operation(summary = "Get all published blogs", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/posts/published")
    public Page<PostDTO> getPublishedPosts(Pageable pageable) {
        return postService.getPublishedPosts(pageable);
    }

    @Operation(summary = "Get blog by id", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/posts/{id}")
    public PostDTO getPostById(@PathVariable Long id) {
        return postService.getBlogById(id);
    }

    @Operation(summary = "Get hero blog", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("posts/hero")
    public PostDTO getHeroPost() {
        return postService.getHeroBlog();
    }

    @Operation(summary = "Get blogs by title", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/posts/search")
    public Page<PostDTO> searchPostByTitle(String title, Pageable pageable) {
        return postService.getPostsByTitle(title, pageable);
    }

    @Operation(summary = "Update hero blog", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("posts/hero/{id}")
    public void updateHeroBlog(@PathVariable Long id) {
        postService.updateHeroBlog(id);
    }

    @Operation(summary = "Get last five blogs", description = "Does not need authorization header, but will work if you have one")
    @GetMapping("/posts/last-five")
    public List<PostDTO> getLastFivePosts() {
        return postService.getLastFiveAddedPosts();
    }

    @Operation(summary = "Create Post", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO createPost(@Validated @RequestBody PostCreateDTO postCreateDTO) {
        return postService.createBlog(postCreateDTO);
    }

    @Operation(summary = "Update blog", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("/posts")
    public PostDTO updatePost(@Validated@RequestBody PostUpdateDTO postUpdateDTO) {
        return postService.updateBlog(postUpdateDTO);
    }

    @Operation(summary = "Update blog status {CREATED,REVIEWING, APPROVED, PUBLISHED}", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PutMapping("/posts/update-status")
    public PostDTO updateStatus(Long id, PostStatus postStatus) {
        return postService.updateStatus(id, postStatus);
    }

    @Operation(summary = "Delete blog by id", description = "Needs authorization header with ROLE.ADMIN")
    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlogById(@PathVariable Long id) {
        postService.deleteBlogById(id);
    }
}