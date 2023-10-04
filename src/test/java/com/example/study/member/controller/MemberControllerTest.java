package com.example.study.member.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.dto.MemberDto.MemberUpdateDto;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import com.example.study.member.respository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long memberId;

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private final String contextPath;
    private final String prefixUrl;



    public MemberControllerTest(@Value("${server.servlet.context-path}") String contextPath) {
        this.contextPath = contextPath;
        this.prefixUrl = contextPath + "/api/member";
    }

    @DisplayName("Get all member")
    @Test
    void testGetAllMember() throws Exception {
        //given
        int page = 0;
        int size = 10;
        String searchName = "";
        Gender gender = Gender.MALE;

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl
                + "?page={page}"
                + "&size={size}"
                + "&searchName={searchName}"
                + "&gender={gender}", page, size, searchName, gender)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, String.format("%s%s", TOKEN_PREFIX, getAccessToken()))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$['content'].size()").isNotEmpty())
                .andReturn();

        //docs
        result.andDo(document("list-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(PageDescriptor.requestDescriptor)
                        .and(
                            parameterWithName("page").description("Page number").optional(),
                            parameterWithName("size").description("Page limit size").optional(),
                            parameterWithName("searchName").description("USER ID"),
                            parameterWithName("gender").description("[FEMALE] or [MALE]")
                        ),
                responseFields(PageDescriptor.responseDescriptor)
                        .and(fieldWithPath("content").description("CONTENT"))
                        .andWithPrefix("content[].",
                            fieldWithPath("id").description("ID"),
                            fieldWithPath("userId").description("USER ID"),
                            fieldWithPath("name").description("USER NAME"),
                            fieldWithPath("gender").description("USER GENDER"),
                            fieldWithPath("authorities[].id").description("ID"),
                            fieldWithPath("authorities[].authority").description("[ADMIN, ITEM, ORDER]")
                        )
        ));
    }

    @DisplayName("Get one member")
    @Test
    void testViewMember() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get(prefixUrl+"/{id}", memberId)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, String.format("%s%s", TOKEN_PREFIX, getAccessToken()))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test2"))
                .andReturn();

        //docs
        result.andDo(document("get-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("MEMBER ID")),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("userId").description("USER ID"),
                        fieldWithPath("name").description("USER NAME"),
                        fieldWithPath("gender").description("USER GENDER"),
                        fieldWithPath("authorities[].id").description("ID"),
                        fieldWithPath("authorities[].authority").description("[ADMIN, ITEM, ORDER]")
                )
        ));
    }

    @DisplayName("Update member")
    @Test
    void testUpdateMember() throws Exception {
        //given
        MemberAuthorityRequestDto memberAuthorityRequestDto = MemberAuthorityRequestDto.builder()
                .authority(Authority.ADMIN)
                .build();

        MemberUpdateDto memberUpdateDto = MemberUpdateDto.builder()
                .name("test3")
                .password("1234")
                .gender(Gender.MALE)
                .authorities(List.of(memberAuthorityRequestDto))
                .build();

        //when
        ResultActions result = mockMvc.perform(patch(prefixUrl+"/{id}", memberId)
                .contextPath(contextPath).contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, String.format("%s%s", TOKEN_PREFIX, getAccessToken()))
                .content(objectMapper.writeValueAsString(memberUpdateDto))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test3"))
                .andReturn();

        //docs
        result.andDo(document("update-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("MEMBER ID")),
                requestFields(
                        fieldWithPath("name").description("member's name"),
                        fieldWithPath("password").description("password"),
                        fieldWithPath("gender").description("MALE or FEMALE"),
                        fieldWithPath("authorities[].authority").description("Authorities = [ADMIN, ORDER, ITEM]")

                ),
                responseFields(
                        fieldWithPath("id").description("ID"),
                        fieldWithPath("userId").description("USER ID"),
                        fieldWithPath("name").description("NAME"),
                        fieldWithPath("gender").description("GENDER"),
                        fieldWithPath("authorities[].id").ignored(),
                        fieldWithPath("authorities[].authority").description("AUTHORITY")
                )
        ));
    }


    @BeforeEach
    void insertDb() {
        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(Authority.ADMIN)
                .build();

        Member member = Member.builder()
                .userId("admin2")
                .password(passwordEncoder.encode("1234"))
                .name("test2")
                .gender(Gender.MALE)
                .build();

        member.getMemberAuthorityList().add(memberAuthority);
        memberRepository.save(member);
        memberId = member.getId();
    }

    private String getAccessToken() {
        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(Authority.ADMIN)
                .build();

        Member member = Member.builder()
                .userId("admin")
                .password(passwordEncoder.encode("1234"))
                .name("test")
                .gender(Gender.MALE)
                .build();

        member.getMemberAuthorityList().add(memberAuthority);
        memberRepository.save(member);
        return jwtTokenHandler.generateToken(member);
    }
}