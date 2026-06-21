package com.mmcafe.board.service;
import com.mmcafe.board.dto.*;
import com.mmcafe.board.repository.BoardRepository;
import com.mmcafe.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class BoardService {
    private final BoardRepository repository;
    public BoardService(BoardRepository repository) { this.repository = repository; }
    @Transactional
    public BoardResponse createBoard(BoardRequest request) { return repository.save(request.title(), request.content()); }
    public BoardResponse getBoard(long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board not found: " + id)); }

    public PageResponse<BoardResponse> getBoards(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.max(size, 1);
        long total = repository.count();
        int totalPages = (int) Math.ceil((double) total / normalizedSize);
        return new PageResponse<>(repository.findAll(normalizedPage, normalizedSize), normalizedPage, normalizedSize, total, totalPages);
    }



}
