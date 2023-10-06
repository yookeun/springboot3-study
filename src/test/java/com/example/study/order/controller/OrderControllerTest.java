package com.example.study.order.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import com.example.study.item.repository.ItemRepository;
import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import com.example.study.member.enums.ItemType;
import com.example.study.member.enums.OrderStatus;
import com.example.study.member.respository.MemberRepository;
import com.example.study.order.domain.Order;
import com.example.study.order.dto.OrderDto.OrderRequestDto;
import com.example.study.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
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
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestToken testToken;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String contextPath;
    private final String prefixUrl;

    private Long orderId;
    private Long memberId;
    private Long itemId;

    public OrderControllerTest(@Value("${server.servlet.context-path}") String contextPath) {
        this.contextPath = contextPath;
        this.prefixUrl = contextPath + "/api/order";
    }

    private String getAccessToken() {
        return testToken.getAccessToken(Authority.ORDER);
    }



    @DisplayName("Get all Orders")
    @Test
    void testGetAllOrders() throws Exception {
        //given
        insertDb();

        int page = 0;
        int size = 10;
        String searchName = "item";
        String orderStatus = OrderStatus.WAITING.name();

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl
                + "?page={page}"
                + "&size={size}"
                + "&searchName={searchName}"
                + "&orderStatus={orderStatus}", page, size, searchName, orderStatus)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION, String.format("%s%s", testToken.BEARER, getAccessToken())
        ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$['content'].size()").isNotEmpty())
                .andReturn();

        //docs
        result.andDo(document("list-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(PageDescriptor.requestDescriptor)
                        .and(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page limit size").optional(),
                                parameterWithName("searchName").description("ITEM NAME, MEMBER NAME"),
                                parameterWithName("orderStatus").description("ORDER_STATUS = WAITING, DOING, DONE")
                        ),
                responseFields(PageDescriptor.responseDescriptor)
                        .and(fieldWithPath("content").description("CONTENT"))
                        .andWithPrefix("content[].",
                                fieldWithPath("id").description("ID"),
                                fieldWithPath("memberDto.id").description("MEMBER ID"),
                                fieldWithPath("memberDto.userId").description("MEMBER USER_ID"),
                                fieldWithPath("memberDto.name").description("MEMBER NAME"),
                                fieldWithPath("memberDto.gender").description("MEMBER GENDER"),
                                fieldWithPath("memberDto.authorities[].id").description("MEMBER AUTHORITIE ID"),
                                fieldWithPath("memberDto.authorities[].authority").description("MEMBER AUTHORITIE"),
                                fieldWithPath("itemDto.id").description("ITEM ID"),
                                fieldWithPath("itemDto.itemName").description("ITEM NAME"),
                                fieldWithPath("itemDto.price").description("ITEM PRICE"),
                                fieldWithPath("itemDto.itemType").description("ITEM ITEM TYPE(FOOD, BOOK, CLOTHES"),
                                fieldWithPath("orderCount").description("ORDER COUNT"),
                                fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                                fieldWithPath("orderDate").description("ORDER DATE(yyyy-MM-dd)")
                        )
                ));
    }


    @DisplayName("Save Order")
    @Test
    void testSaveOrder() throws Exception {
        //given
        insertDb();

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .itemId(itemId)
                .memberId(memberId)
                .orderCount(1)
                .orderStatus(OrderStatus.WAITING)
                .orderDate(LocalDate.now())
                .build();

        //when
        ResultActions result = mockMvc.perform(post(prefixUrl)
                .contextPath(contextPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION,
                        String.format("%s%s", testToken.BEARER, getAccessToken()))
                .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(requestDto))

        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCount").value(1))
                .andReturn();

        //docs
        result.andDo(document("save-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("memberId").description("MEMBER ID"),
                        fieldWithPath("itemId").description("ITEM ID"),
                        fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                        fieldWithPath("orderCount").description("ORDER COUNT"),
                        fieldWithPath("orderDate").description("ORDER DATE (yyy-MM-dd)")
                ),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("memberDto.id").description("MEMBER ID"),
                        fieldWithPath("memberDto.userId").description("MEMBER USER_ID"),
                        fieldWithPath("memberDto.name").description("MEMBER NAME"),
                        fieldWithPath("memberDto.gender").description("MEMBER GENDER"),
                        fieldWithPath("memberDto.authorities[].id").description("MEMBER AUTHORITIE ID"),
                        fieldWithPath("memberDto.authorities[].authority").description("MEMBER AUTHORITIE"),
                        fieldWithPath("itemDto.id").description("ITEM ID"),
                        fieldWithPath("itemDto.itemName").description("ITEM NAME"),
                        fieldWithPath("itemDto.price").description("ITEM PRICE"),
                        fieldWithPath("itemDto.itemType").description("ITEM ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("orderCount").description("ORDER COUNT"),
                        fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                        fieldWithPath("orderDate").description("ORDER DATE(yyyy-MM-dd)")
                )
        ));
    }


    @DisplayName("Update Order")
    @Test
    void testUpdateItem() throws Exception {
        //given
        insertDb();

        OrderRequestDto requestDto = OrderRequestDto.builder()
                .itemId(itemId)
                .memberId(memberId)
                .orderCount(2)
                .orderStatus(OrderStatus.WAITING)
                .orderDate(LocalDate.now())
                .build();

        //when
        ResultActions result = mockMvc.perform(patch(prefixUrl+"/{id}", orderId)
                .contextPath(contextPath)
                .contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION,
                        String.format("%s%s", testToken.BEARER, getAccessToken()))
                .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(requestDto))

        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCount").value(2))
                .andReturn();

        //docs
        result.andDo(document("update-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("ORDER ID")),
                requestFields(
                        fieldWithPath("memberId").description("MEMBER ID"),
                        fieldWithPath("itemId").description("ITEM ID"),
                        fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                        fieldWithPath("orderCount").description("ORDER COUNT"),
                        fieldWithPath("orderDate").description("ORDER DATE (yyy-MM-dd)")
                ),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("memberDto.id").description("MEMBER ID"),
                        fieldWithPath("memberDto.userId").description("MEMBER USER_ID"),
                        fieldWithPath("memberDto.name").description("MEMBER NAME"),
                        fieldWithPath("memberDto.gender").description("MEMBER GENDER"),
                        fieldWithPath("memberDto.authorities[].id").description("MEMBER AUTHORITIE ID"),
                        fieldWithPath("memberDto.authorities[].authority").description("MEMBER AUTHORITIE"),
                        fieldWithPath("itemDto.id").description("ITEM ID"),
                        fieldWithPath("itemDto.itemName").description("ITEM NAME"),
                        fieldWithPath("itemDto.price").description("ITEM PRICE"),
                        fieldWithPath("itemDto.itemType").description("ITEM ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("orderCount").description("ORDER COUNT"),
                        fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                        fieldWithPath("orderDate").description("ORDER DATE(yyyy-MM-dd)")
                )
        ));

    }



    @DisplayName("Get one Order")
    @Test
    void testGetOrder() throws Exception {
        //given
        insertDb();

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl + "/{id}", orderId)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(testToken.AUTHORIZATION,
                        String.format("%s%s", testToken.BEARER, getAccessToken()))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemDto.itemName").value("item01"))
                .andReturn();

        //docs
        result.andDo(document("get-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("ORDER ID")),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("memberDto.id").description("MEMBER ID"),
                        fieldWithPath("memberDto.userId").description("MEMBER USER_ID"),
                        fieldWithPath("memberDto.name").description("MEMBER NAME"),
                        fieldWithPath("memberDto.gender").description("MEMBER GENDER"),
                        fieldWithPath("memberDto.authorities[].id").description("MEMBER AUTHORITIE ID"),
                        fieldWithPath("memberDto.authorities[].authority").description("MEMBER AUTHORITIE"),
                        fieldWithPath("itemDto.id").description("ITEM ID"),
                        fieldWithPath("itemDto.itemName").description("ITEM NAME"),
                        fieldWithPath("itemDto.price").description("ITEM PRICE"),
                        fieldWithPath("itemDto.itemType").description("ITEM ITEM TYPE(FOOD, BOOK, CLOTHES"),
                        fieldWithPath("orderCount").description("ORDER COUNT"),
                        fieldWithPath("orderStatus").description("STATUS = [WAITING, DOING, DONE]"),
                        fieldWithPath("orderDate").description("ORDER DATE(yyyy-MM-dd)")
                )
        ));
    }



    @DisplayName("Delete Order")
    @Test
    void testDeleteOrder() throws Exception {
        //given
        insertDb();

        //when
        ResultActions result = mockMvc.perform(delete(prefixUrl+"/{id}", orderId)
                        .contextPath(contextPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testToken.AUTHORIZATION,
                                String.format("%s%s", testToken.BEARER, getAccessToken()))
        );

        //then
        result.andExpect(status().is2xxSuccessful()).andReturn();

        //docs
        result.andDo(document("delete-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("ORDER ID"))
        ));

    }

    void insertDb() {
        Member member = Member.builder()
                .name("test")
                .userId("test01")
                .password("1234")
                .gender(Gender.MALE)
                .build();

        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(Authority.ADMIN)
                .member(member)
                .build();

        member.getMemberAuthorityList().add(memberAuthority);
        memberRepository.save(member);
        memberId = member.getId();

        Item item = Item.builder()
                .itemName("item01")
                .itemType(ItemType.FOOD)
                .price(10L)
                .build();
        itemRepository.save(item);
        itemId = item.getId();

        Order order = Order.builder()
                .member(member)
                .item(item)
                .orderCount(1)
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.WAITING)
                .build();

        orderRepository.save(order);
        orderId = order.getId();
    }

}