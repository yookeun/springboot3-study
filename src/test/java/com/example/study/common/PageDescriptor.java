package com.example.study.common;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageDescriptor {

    public static final ParameterDescriptor[] requestDescriptor = {
            parameterWithName("page").description("Page number").optional(),
            parameterWithName("size").description("Page size").optional()
    };

    public static final FieldDescriptor[] responseDescriptor = {
            fieldWithPath("pageable.sort.empty").ignored(),
            fieldWithPath("pageable.sort.unsorted").ignored(),
            fieldWithPath("pageable.sort.sorted").ignored(),
            fieldWithPath("pageable.offset").ignored(),
            fieldWithPath("pageable.pageNumber").ignored(),
            fieldWithPath("pageable.pageSize").ignored(),
            fieldWithPath("pageable.paged").ignored(),
            fieldWithPath("pageable.unpaged").ignored(),
            fieldWithPath("totalElements").description("Number of elements in the entire list"),
            fieldWithPath("totalPages").description("Total number of pages"),
            fieldWithPath("size").description("Number of elements shown on one page"),
            fieldWithPath("number").description("Number of the current page (starting from 0)"),
            fieldWithPath("numberOfElements").description("Number of elements on this page"),
            fieldWithPath("sort.sorted").ignored(),
            fieldWithPath("sort.empty").ignored(),
            fieldWithPath("sort.unsorted").ignored(),
            fieldWithPath("first").description("First page"),
            fieldWithPath("last").description("Last page"),
            fieldWithPath("empty").description("Empty page")
    };
}
