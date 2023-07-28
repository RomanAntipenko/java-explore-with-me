package ru.practicum.ewm.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events = new HashSet<>();
    private Boolean pinned = false;
    @Size(max = 50, min = 1)
    @NotBlank
    private String title;
}
