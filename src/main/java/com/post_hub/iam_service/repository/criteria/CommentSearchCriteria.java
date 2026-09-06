package com.post_hub.iam_service.repository.criteria;

import com.post_hub.iam_service.model.entity.Comment;
import com.post_hub.iam_service.model.request.comment.CommentSearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@RequiredArgsConstructor
public class CommentSearchCriteria implements Specification<Comment> {

    private final CommentSearchRequest request;


    @Override
    public @Nullable Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(request.getMessage())) {
            predicates.add(criteriaBuilder.like(root.get(Comment.MESSAGE_NAME_FIELD), "%" + request.getMessage() + "%"));
        }

        if (request.getCreatedBy()!= null) {
            predicates.add(criteriaBuilder.like(root.get(Comment.CREATED_BY_FIELD), "%" + request.getCreatedBy() + "%"));
        }

        if (request.getDeleted()!=null) {
            predicates.add(criteriaBuilder.equal(root.get(Comment.DELETED_FIELD), request.getDeleted()));
        }

        if (request.getPostId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("post").get("id"), request.getPostId()));
        }

        if (request.getKeyword()!=null) {
            Predicate keywoordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Comment.MESSAGE_NAME_FIELD), "%" + request.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Comment.CREATED_BY_FIELD), "%" + request.getKeyword() + "%")
            );
            predicates.add(keywoordPredicate);
        }

        sort(root, criteriaBuilder, query);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));


    }

    private void sort(Root<Comment> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query) {
        if (request.getSortField() != null) {
            switch (request.getSortField()) {
                case MESSAGE -> query.orderBy(criteriaBuilder.desc(root.get(Comment.MESSAGE_NAME_FIELD)));
                case CREATED_BY -> query.orderBy(criteriaBuilder.desc(root.get(Comment.CREATED_BY_FIELD)));
                default -> query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD)));
        }
    }
}
