package com.web.stard.domain.board.global.application;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.dto.request.PostRequestDto;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.Member;

public interface PostService {

    PostResponseDto.PostDto createPost(PostRequestDto.CreatePostDto requestDto, Member member, PostType postType);

    PostResponseDto.PostDto createCommPost(Member member, PostRequestDto.CreateCommPostDto requestDto);

    PostResponseDto.PostDto updatePost(Long postId, PostRequestDto.CreatePostDto requestDto, Member member, PostType postType);

    PostResponseDto.PostDto updateCommPost(Member member, Long commPostId, PostRequestDto.CreateCommPostDto requestDto);

    Long deletePost(Long postId, Member member, PostType postType);

    PostResponseDto.PostListDto getPostList(int page, PostType postType);

    PostResponseDto.PostDto getPostDetail(Long postId, Member member, PostType postType);

    PostResponseDto.PostListDto searchPost(String keyword, int page, PostType postType);

    PostResponseDto.PostListDto getAllFaqsAndQnas(int page);

    PostResponseDto.PostListDto searchFaqsAndQnas(String keyword, int page);

    PostResponseDto.PostListDto getCommPostListByCategory(String category, int page);

    PostResponseDto.PostListDto searchCommPostWithCategory(String keyword, String category, int page);

    PostResponseDto.PostListDto getCommPostListByMember(Member member, int page);
}
