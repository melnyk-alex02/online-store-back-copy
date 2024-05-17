package com.store.mapper;

import com.store.dto.feedbackDTOs.FeedbackCreateDTO;
import com.store.dto.feedbackDTOs.FeedbackDTO;
import com.store.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper
public interface FeedbackMapper {
    FeedbackDTO toDto(Feedback feedback);

    Feedback toEntity(FeedbackCreateDTO feedbackCreateDTO);

    Feedback toEntity(FeedbackDTO feedbackDTO);
}