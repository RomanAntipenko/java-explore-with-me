package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.error.FieldUniqueException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }
        return compilations.stream()
                .map(CompilationMapper::toDtoFromCompilation)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation with id=\"%s\" was not found", compId)));
        return CompilationMapper.toDtoFromCompilation(compilation);
    }

    @Override
    public CompilationDto createCompilationAdmin(NewCompilationDto newCompilationDto) {
        List<Event> eventList;
        if (!newCompilationDto.getEvents().isEmpty()) {
            eventList = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        } else {
            eventList = new ArrayList<>();
        }
        Compilation compilation = CompilationMapper.toCompilationFromNew(newCompilationDto, eventList);
        try {
            Compilation savedCompilation = compilationRepository.save(compilation);
            return CompilationMapper.toDtoFromCompilation(savedCompilation);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }

    }

    @Override
    public void deleteCompilationAdmin(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ObjectNotFoundException(String.format("Compilation with id=\"%s\" was not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilationAdmin(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation with id=\"%s\" was not found", compId)));
        compilation.setTitle(Optional.ofNullable(updateCompilationRequest.getTitle()).orElse(compilation.getTitle()));
        compilation.setPinned(Optional.ofNullable(updateCompilationRequest.getPinned()).orElse(compilation.getPinned()));
        List<Event> eventList = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
        if (!eventList.isEmpty()) {
            compilation.setEvents(eventList);
        }
        try {
            Compilation savedCompilation = compilationRepository.save(compilation);
            return CompilationMapper.toDtoFromCompilation(savedCompilation);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }
}
