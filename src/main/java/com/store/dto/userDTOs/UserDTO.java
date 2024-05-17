package com.store.dto.userDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class UserDTO {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime registerDate;
    private List<String> roles;
    private Map<String, List<String>> attributes;
}