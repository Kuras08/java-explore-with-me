package ru.practicum.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}


