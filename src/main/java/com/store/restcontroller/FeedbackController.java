package com.store.restcontroller;

import com.store.constants.FeedbackStatus;
import com.store.constants.Role;
import com.store.dto.feedbackDTOs.FeedbackCreateDTO;
import com.store.dto.feedbackDTOs.FeedbackDTO;
import com.store.dto.feedbackDTOs.FeedbackReplyDTO;
import com.store.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Operation(summary = "Create feedback", description = "Does not need authorization header, but will work if you have one")
    @PostMapping("/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackDTO createFeedback(@Validated @RequestBody FeedbackCreateDTO feedbackCreateDTO) {
        return feedbackService.createFeedback(feedbackCreateDTO);
    }


    @Operation(summary = "Get feedback by id", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/feedback/{id}")
    public FeedbackDTO getFeedback(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    @Operation(summary = "Get all feedbacks", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @GetMapping("/feedback")
    public Page<FeedbackDTO> getAllFeedbacks(Pageable pageable) {
        return feedbackService.getAllFeedbacks(pageable);
    }

    @Operation(summary = "Update status of feedback", description = "Needs authorization header with ROLE.ADMIN")
    @PutMapping("/feedback/{id}/status")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    public void updateFeedbackStatus(@PathVariable Long id, FeedbackStatus status) {
        feedbackService.updateStatus(id, status);
    }

    @Operation(summary = "Reply to feedback", description = "Needs authorization header with ROLE.ADMIN")
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @PostMapping("feedback/{id}/reply")
    public void replyToFeedback(@PathVariable Long id, @RequestBody FeedbackReplyDTO feedbackReplyDTO) {
        feedbackService.replyToFeedback(id, feedbackReplyDTO);
    }

    @Operation(summary = "Delete feedback by id", description = "Needs authorization header")
    @DeleteMapping("/feedback/{id}")
    public void deleteFeedbackById(@PathVariable Long id){
        feedbackService.deleteFeedback(id);
    }
}