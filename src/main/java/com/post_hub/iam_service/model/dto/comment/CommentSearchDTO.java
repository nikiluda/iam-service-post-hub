package com.post_hub.iam_service.model.dto.comment;

import com.post_hub.iam_service.model.dto.post.PostOwnerDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentSearchDTO {

    private Integer id;
    private String message;
    private PostOwnerDTO owner;
    private Integer postId;
    private LocalDateTime created;
    private LocalDateTime update;
    private Boolean isDeleted;
}
