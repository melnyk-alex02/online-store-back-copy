package com.store.service;

import com.store.constants.FeedbackStatus;
import com.store.dto.emailDTOs.EmailDTO;
import com.store.dto.feedbackDTOs.FeedbackCreateDTO;
import com.store.dto.feedbackDTOs.FeedbackDTO;
import com.store.dto.feedbackDTOs.FeedbackReplyDTO;
import com.store.entity.Feedback;
import com.store.exception.DataNotFoundException;
import com.store.mapper.FeedbackMapper;
import com.store.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final EmailService emailService;

    public Page<FeedbackDTO> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable).map(feedbackMapper::toDto);
    }

    public FeedbackDTO getFeedbackById(Long id) {
        return feedbackMapper.toDto(feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback with id " + id + " was not found")));
    }

    public FeedbackDTO createFeedback(FeedbackCreateDTO feedbackCreateDTO) {
        Feedback feedback = feedbackMapper.toEntity(feedbackCreateDTO);

        feedback.setFeedbackStatus(FeedbackStatus.NEW);
        feedback.setSentAt(ZonedDateTime.now());

        return feedbackMapper.toDto(feedbackRepository.save(feedback));
    }

    public void updateStatus(Long id, FeedbackStatus feedbackStatus) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback with id " + id + " was not found"));

        if (feedbackStatus.equals(FeedbackStatus.RESOLVED)) {
            feedback.setResolvedAt(ZonedDateTime.now());
        }

        feedback.setFeedbackStatus(feedbackStatus);
        feedbackRepository.save(feedback);
    }

    public void replyToFeedback(Long id, FeedbackReplyDTO feedbackReplyDTO) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback with id " + id + " was not found"));

        EmailDTO emailDTO = new EmailDTO(feedback.getUserEmail(), "Reply to your feedback", feedbackReplyDTO.getContent());

        emailService.sendEmail(emailDTO);

        feedback.setReplyOfManager(feedbackReplyDTO.getContent());
        feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new DataNotFoundException("Feedback with id " + id + " was not found");
        }

        feedbackRepository.deleteById(id);
    }
}