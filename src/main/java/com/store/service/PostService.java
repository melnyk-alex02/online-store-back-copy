package com.store.service;

import com.store.constants.PostStatus;
import com.store.dto.postDTOs.PostCreateDTO;
import com.store.dto.postDTOs.PostDTO;
import com.store.dto.postDTOs.PostUpdateDTO;
import com.store.entity.Post;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.mapper.PostMapper;
import com.store.repository.PostRepository;
import com.store.utils.SlugUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public Page<PostDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toDto);
    }

    public Page<PostDTO> getPublishedPosts(Pageable pageable) {
        return postRepository.findAllByPostStatus(PostStatus.PUBLISHED.name(), pageable).map(postMapper::toDto);
    }

    public Page<PostDTO> getPostsByTitle(String title, Pageable pageable) {
        return postRepository.findAllByTitleContainingIgnoreCase(title, pageable).map(postMapper::toDto);
    }

    public List<PostDTO> getLastFiveAddedPosts() {
        return postMapper.toDto(postRepository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, "id")).getContent());
    }

    public PostDTO getBlogById(Long id) {
        return postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("There is no blog with id: " + id)));
    }


    public PostDTO createBlog(PostCreateDTO postCreateDTO) {
        Post post = postMapper.toEntity(postCreateDTO);

        Post heroPost = postRepository.getPostByHeroIsTrue().orElse(null);

        if (heroPost != null && postCreateDTO.getPostStatus().equals(PostStatus.PUBLISHED)) {
            post.setHero(true);

            heroPost.setHero(false);
            postRepository.save(heroPost);
        }


        post.setCreatedAt(ZonedDateTime.now());
        if (postCreateDTO.getPostStatus().equals(PostStatus.PUBLISHED)) {
            post.setPublishedAt(ZonedDateTime.now());
        }

        try {
            return postMapper.toDto(postRepository.save(post));
        } catch (ConstraintViolationException e) {
            throw new InvalidDataException("Please, check entered fields for duplicates and required ones");
        }
    }

    public void updateHeroBlog(Long id) {
        Post heroPost = postRepository.getPostByHeroIsTrue().orElse(null);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("There is no blog with id: " + id));

        if (!post.getPostStatus().equals(PostStatus.PUBLISHED.name())) {
            throw new InvalidDataException("Post status should be PUBLISHED to be a hero post");
        }
        if (heroPost != null) {
            post.setHero(true);

            heroPost.setHero(false);
            postRepository.save(heroPost);
        }

        post.setHero(true);
        postRepository.save(post);
    }

    public PostDTO updateBlog(PostUpdateDTO postUpdateDTO) {
        PostDTO postDTO = postMapper.toDto(postRepository.findById(postUpdateDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("There is no blog with id: " + postUpdateDTO.getId())));

        postDTO.setTitle(postUpdateDTO.getTitle());
        postDTO.setAuthorName(postUpdateDTO.getAuthorName());
        postDTO.setPosterImgSrc(postUpdateDTO.getPosterImgSrc());
        postDTO.setContent(postUpdateDTO.getContent());
        postDTO.setUpdatedAt(ZonedDateTime.now());
        postDTO.setSlug(SlugUtils.generateSlug(postUpdateDTO.getTitle()));
        try {
            return postMapper.toDto(postRepository.save(postMapper.toEntity(postDTO)));
        } catch (ConstraintViolationException e) {
            throw new InvalidDataException("Please, check entered fields for duplicates and required ones");
        }
    }

    public PostDTO updateStatus(Long id, PostStatus postStatus) {
        PostDTO postDTO = postMapper.toDto(postRepository.findById(id).orElseThrow(() -> new DataNotFoundException("There is no blog with id: " + id)));
        if (postDTO.getPostStatus().equals(postStatus)) {
            throw new DataNotFoundException("Post status is already " + postStatus);
        }
        if (postStatus.equals(PostStatus.PUBLISHED)) {
            postDTO.setPublishedAt(ZonedDateTime.now());
        } else {
            postDTO.setUpdatedAt(ZonedDateTime.now());
        }
        postDTO.setPostStatus(postStatus);

        return postMapper.toDto(postRepository.save(postMapper.toEntity(postDTO)));
    }

    public void deleteBlogById(Long id) {
        if (!postRepository.existsById(id)) {
            throw new DataNotFoundException("There is no blog with id: " + id);
        }
        postRepository.deleteById(id);
    }

    public PostDTO getHeroBlog() {
        return postMapper.toDto(postRepository.getPostByHeroIsTrue()
                .orElseThrow(() -> new DataNotFoundException("There is no hero blog")));
    }
}