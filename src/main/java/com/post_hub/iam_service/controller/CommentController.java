package com.post_hub.iam_service.controller;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.comment.CommentDTO;
import com.post_hub.iam_service.model.request.comment.CommentRequest;
import com.post_hub.iam_service.model.request.comment.UpdateCommentRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.service.CommentService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("${end-points.create}")
    public ResponseEntity<IamResponse<CommentDTO>> createComment(@RequestBody @Valid CommentRequest commentRequest) {


        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());
        IamResponse<CommentDTO> result = commentService.createComment(commentRequest);
        return ResponseEntity.ok(result);
    }

    @PutMapping("${end-points.id}")
    public ResponseEntity<IamResponse<CommentDTO>> updateComment(@PathVariable("id") Integer commentId,
                                                                 @RequestBody @Valid UpdateCommentRequest updateCommentRequest) {

        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<CommentDTO> result = commentService.updateComment(commentId, updateCommentRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("${end-points.id}")
    public ResponseEntity<Void> softDeleteComment(@PathVariable("id") Integer commentId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        commentService.softDeleteComment(commentId);
        return ResponseEntity.noContent().build();
    }





}
