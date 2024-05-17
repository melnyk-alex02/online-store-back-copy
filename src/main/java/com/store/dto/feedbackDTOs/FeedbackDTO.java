package com.store.dto.feedbackDTOs;

import com.store.constants.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class FeedbackDTO {
    private Long id;
    private String userEmail;
    private String userName;
    private String content;
    private String imageSrc;
    private FeedbackStatus feedbackStatus;
    private String replyOfManager;
    private ZonedDateTime sentAt;
    private ZonedDateTime resolvedAt;
}