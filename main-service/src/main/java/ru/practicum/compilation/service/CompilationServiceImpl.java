package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(compilationDto, events));
        Set<EventShortDto> eventsShort = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toSet());
        return CompilationMapper.toCompilationDto(compilation, eventsShort);

    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto updateCompilation) {
        Compilation compilation = getCompilationById(compilationId);
        if (updateCompilation.getTitle() != null && !updateCompilation.getTitle().isBlank()) {
            compilation.setTitle(updateCompilation.getTitle());
        }
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilation.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public Compilation getCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Подборки с  id: %d не существует", compilationId)));
    }

    @Override
    public CompilationDto getCompilationDtoById(Long compilationId) {
        return CompilationMapper.toCompilationDto(getCompilationById(compilationId));
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }
        return CompilationMapper.toListCompilationDto(compilations);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compilationId) {
        compilationRepository.delete(getCompilationById(compilationId));
    }
}