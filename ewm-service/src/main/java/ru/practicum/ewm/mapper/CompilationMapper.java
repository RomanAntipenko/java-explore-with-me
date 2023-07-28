package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilationFromNew(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toDtoFromCompilation(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream().map(EventMapper::toShortFromEvent).collect(Collectors.toList()))
                .build();
    }
}
