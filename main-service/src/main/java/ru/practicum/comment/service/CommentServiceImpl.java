package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.enums.CommentStatusAction;
import ru.practicum.comment.model.enums.CommentStatus;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto createCommentPrivate(NewCommentDto newCommentDto, Long authorId, Long eventId) {
        User author = getUserById(authorId);
        Event event = getEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Можно создавать комментарии только к опубликованным событиям.");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, author, event);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentPrivate(UpdateCommentDto updateComment, Long commentId, Long authorId) {
        existsCommentByIdAndAuthorId(commentId, authorId);
        Comment comment = getCommentById(commentId);
        CommentStatus commentStatus = getCommentStatus(updateComment.getStatusAction());
        if (commentStatus != null) {
            if (CommentStatus.CANCELED.equals(commentStatus)) {
                comment.setStatus(commentStatus);
            } else {
                throw new ValidationException(String.format("Не допустимое действие в статусе комментария: %s",
                        updateComment.getStatusAction()));
            }
        }
        String newText = updateComment.getText();
        if (newText != null && !newText.isBlank()) {
            comment.setText(updateComment.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentPrivate(Long commentId, Long authorId) {
        existsCommentByIdAndAuthorId(commentId, authorId);
        return CommentMapper.toCommentDto(getCommentById(commentId));
    }

    @Override
    @Transactional
    public void deleteCommentPrivate(Long commentId, Long authorId) {
        existsCommentByIdAndAuthorId(commentId, authorId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentAdmin(Long commentId, UpdateCommentDto updateComment) {
        Comment comment = getCommentById(commentId);
        CommentStatus commentStatus = getCommentStatus(updateComment.getStatusAction());
        if (commentStatus != null) {
            comment.setStatus(commentStatus);
        }
        String newText = updateComment.getText();
        if (newText != null && !newText.isBlank()) {
            comment.setText(updateComment.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        commentRepository.delete(getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllAdmin(Long eventId, int from, int size) {
        getEventById(eventId);
        PageRequest page = getPageRequest(from, size).withSort(Sort.by("created").descending());
        return CommentMapper.toListCommentDto(commentRepository.findAllByEventId(eventId, page));
    }

    @Override
    public List<CommentShortDto> getAllCommentEventPublic(Long eventId, int from, int size) {
        getEventById(eventId);
        PageRequest page = getPageRequest(from, size).withSort(Sort.by("created").descending());
        return CommentMapper.toListCommentShortDto(commentRepository
                .findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, page));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с  id: %d не существует", id)));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %s не существует", id)));
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Комментарий с id: %d не существует", id)));
    }

    private void existsCommentByIdAndAuthorId(Long commentId, Long authorId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, authorId)) {
            throw new NotFoundException(String.format("Комментарий с id:%d и автор с id:%s не существуют",
                    commentId, authorId));
        }
    }

    private CommentStatus getCommentStatus(CommentStatusAction statusAction) {
        if (statusAction == null) {
            return null;
        }
        if (CommentStatusAction.PUBLISH.equals(statusAction)) {
            return CommentStatus.PUBLISHED;
        }
        if (CommentStatusAction.REJECT.equals(statusAction)) {
            return CommentStatus.CANCELED;
        }
        throw new ValidationException(String.format("Не допустимое действие в статусе комментария: %s", statusAction));
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}