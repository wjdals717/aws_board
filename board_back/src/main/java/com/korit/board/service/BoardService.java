package com.korit.board.service;

import com.korit.board.dto.BoardCategoryRespDto;
import com.korit.board.dto.BoardListRespDto;
import com.korit.board.dto.SearchBoardListReqDto;
import com.korit.board.dto.WriteBoardReqDto;
import com.korit.board.entity.Board;
import com.korit.board.entity.BoardCategory;
import com.korit.board.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public List<BoardCategoryRespDto> getBoardCategoriseAll(){
        List<BoardCategoryRespDto> boardCategoryRespDtos = new ArrayList<>();
        boardMapper.getBoardCategories().forEach(category -> {
            boardCategoryRespDtos.add(category.toCategoryDto());
        });
        return boardCategoryRespDtos;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean writeBoardContent(WriteBoardReqDto writeBoardReqDto) {
        BoardCategory boardCategory = null;
        if(writeBoardReqDto.getCategoryId() == 0) {
            boardCategory = BoardCategory.builder()
                    .boardCategoryName(writeBoardReqDto.getCategoryName())
                    .build();
            boardMapper.saveCategory(boardCategory);
            writeBoardReqDto.setCategoryId(boardCategory.getBoardCategoryId());
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Board board = writeBoardReqDto.toBoardEntity(email);
        return boardMapper.saveBoard(board) > 0;
    }

    public List<BoardListRespDto> getBoardList(String categoryName, int page, SearchBoardListReqDto searchBoardListReqDto) {
        int index = (page - 1) * 10;        //1번 페이지일 때 0, 2번 페이지일 때 10, 3번 페이지일 때 30?
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("index", index);
        paramsMap.put("categoryName", categoryName);
        paramsMap.put("optionName", searchBoardListReqDto.getOptionName());
        paramsMap.put("searchValue", searchBoardListReqDto.getSearchValue());
        List<BoardListRespDto> boardListRespDtos = new ArrayList<>();
        boardMapper.getBoardList(paramsMap).forEach(board -> {
            boardListRespDtos.add(board.toBoardListRespDto());
        });
//        System.out.println(boardMapper.getBoardList(paramsMap));        // 결과값 List
        return boardListRespDtos;
    }
}
