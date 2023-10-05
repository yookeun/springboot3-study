package com.example.study.item.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.study.common.PageDescriptor;
import com.example.study.common.TestToken;
import com.example.study.item.domain.Item;
import com.example.study.item.dto.ItemDto.ItemRequestDto;
import com.example.study.item.repository.ItemRepository;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.ItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@Slf4j
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestToken testToken;

    @Autowired
    private ItemRepository itemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String contextPath;
    private final String prefixUrl;

    private Long itemId;

    public ItemControllerTest(@Value("${server.servlet.context-path}") String contextPath) {
        this.contextPath = contextPath;
        this.prefixUrl = contextPath + "/api/item";
    }

    private String getAccessToken() {
        return testToken.getAccessToken(Authority.ITEM);
    }

    @DisplayName("Get all Items")
    @Test
    void testGetAllItems() throws Exception {
        //given
        insertDb();

        int page = 0;
        int size = 10;
        String searchName = "item";
        String itemType = ItemType.FOOD.name();

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl
                + "?page={page}"
                + "&size={size}"
                + "&searchName={searchName}"
                + "&itemType={itemType}", page, size, searchName, itemType)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION,
                        String.format("%s%s", testToken.BEARER, getAccessToken()))
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$['content'].size()").isNotEmpty())
                .andReturn();


        //docs
        result.andDo(document("list-item",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(PageDescriptor.requestDescriptor)
                        .and(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page limit size").optional(),
                                parameterWithName("searchName").description("ITEM NAME"),
                                parameterWithName("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES\"")
                        ),
                responseFields(PageDescriptor.responseDescriptor)
                        .and(fieldWithPath("content").description("CONTENT"))
                        .andWithPrefix("content[].",
                                fieldWithPath("id").description("ID"),
                                fieldWithPath("itemName").description("ITEM NAME"),
                                fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                                fieldWithPath("price").description("ITEM PRICE")
                        )
        ));

    }


    @DisplayName("Save item")
    @Test
    void testSaveItem() throws Exception {
        //given
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .itemName("testItem")
                .itemType(ItemType.FOOD)
                .price(10L)
                .build();

        //when
        ResultActions result = mockMvc.perform(post(prefixUrl).contextPath(contextPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION,
                        String.format("%s%s", testToken.BEARER, getAccessToken()))
                .content(objectMapper.writeValueAsString(requestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("testItem"))
                .andReturn();

        //docs
        result.andDo(document("save-item",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("itemName").description("ITEM NAME"),
                        fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("price").description("ITEM PRICE")
                ),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("itemName").description("ITEM NAME"),
                        fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("price").description("ITEM PRICE")
                )
        ));

    }

    @DisplayName("Get one Item")
    @Test
    void testGetItem() throws Exception {
        //given
        insertDb();

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl+"/{id}", itemId)
                        .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                        .header(testToken.AUTHORIZATION, String.format("%s%s", testToken.BEARER, getAccessToken()))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("item"))
                .andReturn();

        //docs
        result.andDo(document("get-item",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("ITEM ID")),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("itemName").description("ITEM NAME"),
                        fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("price").description("ITEM PRICE")
                )
        ));

    }

    @DisplayName("Update Item")
    @Test
    void testUpdateItem() throws Exception {
        //given
        insertDb();

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .itemName("item2")
                .itemType(ItemType.FOOD)
                .price(10L)
                .build();

        //when
        ResultActions result = mockMvc.perform(patch(prefixUrl+"/{id}", itemId)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION, String.format("%s%s", testToken.BEARER, getAccessToken()))
                .content(objectMapper.writeValueAsString(requestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("item2"))
                .andReturn();

        //docs
        result.andDo(document("update-item",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("ITEM ID")),
                requestFields(
                        fieldWithPath("itemName").description("ITEM NAME"),
                        fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("price").description("ITEM PRICE")
                ),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("itemName").description("ITEM NAME"),
                        fieldWithPath("itemType").description("ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("price").description("ITEM PRICE")
                )
        ));

    }

    void insertDb() {
        Item item = Item.builder()
                .itemName("item")
                .price(10L)
                .itemType(ItemType.FOOD)
                .build();
        itemRepository.save(item);
        itemId = item.getId();
    }

}