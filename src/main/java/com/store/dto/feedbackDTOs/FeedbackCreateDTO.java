package com.store.dto.feedbackDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCreateDTO {
    @Email
    @NotBlank
    private String userEmail;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name should contain only letters")
    private String userName;
    @NotBlank
    @Size(min = 2, message = "Content should contain at least 2 characters")
    private String content;
    private String imageSrc;
}