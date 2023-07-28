package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;
    private LocationDto location;
    private Boolean paid = false;
    @PositiveOrZero
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}
