package com.korit.board.controller;

import com.korit.board.aop.annotation.ArgsAop;
import com.korit.board.dto.SearchBoardListReqDto;
import com.korit.board.dto.WriteBoardReqDto;
import com.korit.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(boardService.getBoardCategoriseAll());
    }

    @PostMapping("/board/content")
    public ResponseEntity<?> writeBoard(@Valid @RequestBody WriteBoardReqDto writeBoardReqDto, BindingResult bindingResult) {
        return ResponseEntity.ok(boardService.writeBoardContent(writeBoardReqDto));
    }

    @ArgsAop
    @GetMapping("/boards/{categoryName}/{page}")
    public ResponseEntity<?> getBoardList(@PathVariable String categoryName,
                                          @PathVariable int page,
                                          SearchBoardListReqDto searchBoardListReqDto) {
        return ResponseEntity.ok(boardService.getBoardList(categoryName, page, searchBoardListReqDto));
    }

    @GetMapping("/boards/{categoryName}/count")
    public ResponseEntity<?> getBoardCount(@PathVariable String categoryName, SearchBoardListReqDto searchBoardListReqDto) {
        //검색결과의 개수를 반영해서 들어와야 함
        return ResponseEntity.ok(boardService.getBoardCount(categoryName,searchBoardListReqDto));
    }

    //각 게시글 정보 가져오기
    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getBoard(@PathVariable int boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    //각 게시글의 좋아요 상태 가져오기
    @GetMapping("/board/like/{boardId}")
    public ResponseEntity<?> getLikeState(@PathVariable int boardId) {
        return ResponseEntity.ok(boardService.getLikeState(boardId));
    }

    //좋아요 추가
    @PostMapping("/board/like/{boardId}")
    public ResponseEntity<?> setLike(@PathVariable int boardId) {
        return ResponseEntity.ok(boardService.setLike(boardId));
    }

    //좋아요 취소
    @DeleteMapping("/board/like/{boardId}")
    public ResponseEntity<?> cancelLike(@PathVariable int boardId) {
        return ResponseEntity.ok(boardService.cancelLike(boardId));
    }


}
