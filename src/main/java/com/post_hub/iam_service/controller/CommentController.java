package com.post_hub.iam_service.controller;

import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.comment.CommentDTO;
import com.post_hub.iam_service.model.entity.Comment;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.service.CommentService;
import com.post_hub.iam_service.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${end-points.comments}")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("${end-points.id}")
    public ResponseEntity<IamResponse<CommentDTO>> getCommentById(@PathVariable(name = "id") Integer commentId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        IamResponse<CommentDTO> result = commentService.getCommentById(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }






}
