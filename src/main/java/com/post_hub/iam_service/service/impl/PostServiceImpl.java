package com.post_hub.iam_service.service.impl;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.dto.post.PostSearchDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistsException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.model.request.post.PostSearchRequest;
import com.post_hub.iam_service.model.request.post.UpdatePostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.model.response.PaginationResponse;
import com.post_hub.iam_service.repository.PostRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.repository.criteria.PostSearchCriteria;
import com.post_hub.iam_service.security.validation.AccessValidator;
import com.post_hub.iam_service.service.PostService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;


@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final AccessValidator accessValidator;
    private final ApiUtils apiUtils;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository, AccessValidator accessValidator, ApiUtils apiUtils) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
        this.accessValidator = accessValidator;
        this.apiUtils = apiUtils;
    }


    @Override
    public IamResponse<PostDTO> getById(@NotNull Integer postId) {
        Post post  = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));



        PostDTO postDTO = postMapper.toPostDTO(post);

        return IamResponse.createSuccessful(postDTO);
    }

    @Override
    public IamResponse<PostDTO> createPost(NewPostRequest newPostRequest) {
        if (postRepository.existsByTitle(newPostRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(newPostRequest.getTitle()));
        }

        Integer userId = apiUtils.getUserIdFromAuthentication();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(userId)));

        Post post  = postMapper.createPost(newPostRequest, user);
        Post savedPost = postRepository.save(post);

        PostDTO postDTO = postMapper.toPostDTO(savedPost);

        return IamResponse.createSuccessful(postDTO);
    }

    @Override
    public IamResponse<PostDTO> updatePost(@NotNull Integer postId, @NotNull UpdatePostRequest updatePostRequest) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));
        accessValidator.validateAdminOrOwnerAccess(post.getUser().getEmail(), post.getCreatedBy());

        if (postRepository.existsByTitle(updatePostRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(updatePostRequest.getTitle()));
        }

        postMapper.updatePost(post, updatePostRequest);
        post.setUpdated(LocalDateTime.now());
        postRepository.save(post);
        return IamResponse.createSuccessful(postMapper.toPostDTO(post));
    }

    @Override
    public void softDeletePost(@NotNull @PathVariable Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getEmail(), post.getCreatedBy());
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> findAllPosts(Pageable pageable) {
        Page<PostSearchDTO> posts = postRepository.findAll(pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        posts.getNumber() + 1,
                        posts.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> searchPosts(PostSearchRequest request, Pageable pageable) {
        Specification<Post> specification = new PostSearchCriteria(request);
        Page<PostSearchDTO> posts = postRepository.findAll(specification, pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> response = PaginationResponse.<PostSearchDTO>builder()
                .content(posts.getContent())
                .pagination(PaginationResponse.Pagination.builder()
                        .total(posts.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(posts.getNumber() + 1)
                        .pages(posts.getTotalPages())
                        .build())
                .build();

                return IamResponse.createSuccessful(response);
    }


}
