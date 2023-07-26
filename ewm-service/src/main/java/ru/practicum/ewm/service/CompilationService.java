package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationsById(Long compId);

    CompilationDto createCompilationAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(Long compId);

    CompilationDto updateCompilationAdmin(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
