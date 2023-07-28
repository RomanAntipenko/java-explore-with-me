package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationsController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationAdmin(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Method of creating compilation was caused in AdminCompilationsController");
        return compilationService.createCompilationAdmin(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationAdmin(@PathVariable Long compId) {
        log.info("Removal method of compilation was caused in AdminCompilationsController");
        compilationService.deleteCompilationAdmin(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilationAdmin(@PathVariable Long compId,
                                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Method of getting compilationList was caused in AdminCompilationsController");
        return compilationService.updateCompilationAdmin(compId, updateCompilationRequest);
    }
}
