package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.request.post.PostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import org.antlr.v4.runtime.misc.NotNull;


public interface PostService {

    IamResponse<PostDTO> getById(@NotNull Integer postId);

    IamResponse<PostDTO> createPost(@NotNull PostRequest postRequest);
}
