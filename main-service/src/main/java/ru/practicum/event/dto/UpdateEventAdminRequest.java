package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    private String title;
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Long category;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;

    public enum AdminStateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }
}

