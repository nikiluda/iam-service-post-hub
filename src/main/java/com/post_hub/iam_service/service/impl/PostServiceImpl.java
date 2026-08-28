package com.post_hub.iam_service.service.impl;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.enteties.Post;
import com.post_hub.iam_service.model.exception.DataExistsException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.post.PostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.repositories.PostRepository;
import com.post_hub.iam_service.service.PostService;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Service;



@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }


    @Override
    public IamResponse<PostDTO> getById(@NotNull Integer postId) {
        Post post  = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        PostDTO postDTO = postMapper.toPostDTO(post);

        return IamResponse.createSuccessful(postDTO);
    }

    @Override
    public IamResponse<PostDTO> createPost(PostRequest postRequest) {
        if (postRepository.existsByTitle(postRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(postRequest.getTitle()));
        }

        Post post  = postMapper.createPost(postRequest);
        Post savedPost = postRepository.save(post);

        PostDTO postDTO = postMapper.toPostDTO(savedPost);

        return IamResponse.createSuccessful(postDTO);
    }


}
