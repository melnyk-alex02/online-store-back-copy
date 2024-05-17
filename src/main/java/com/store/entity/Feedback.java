package com.store.entity;

import com.store.constants.FeedbackStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String userName;
    private String content;
    private String imageSrc;
    @Enumerated(EnumType.STRING)
    private FeedbackStatus feedbackStatus;
    private String replyOfManager;
    private ZonedDateTime sentAt;
    private ZonedDateTime resolvedAt;
}