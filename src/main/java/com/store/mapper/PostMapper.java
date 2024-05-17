package com.store.mapper;

import com.store.dto.postDTOs.PostCreateDTO;
import com.store.dto.postDTOs.PostDTO;
import com.store.dto.postDTOs.PostUpdateDTO;
import com.store.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PostMapper {
    PostDTO toDto(Post post);

    List<PostDTO> toDto(List<Post> postList);

    Post toEntity(PostCreateDTO postCreateDTO);

    Post toEntity(PostUpdateDTO postUpdateDTO);

    Post toEntity(PostDTO postDTO);
}
