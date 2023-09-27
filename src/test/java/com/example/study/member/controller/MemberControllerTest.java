package com.example.study.member.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.study.common.PageDescriptor;
import com.example.study.handler.JwtTokenHandler;
import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import com.example.study.member.respository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DisplayName("Get all member")
    @Test
    void testGetAllMember() throws Exception {
        //given
        int page = 0;
        int size = 10;
        String searchName = "";
        Gender gender = Gender.MALE;

        //when
        ResultActions result = mockMvc.perform(get("/api/member"
                + "?page={page}"
                + "&size={size}"
                + "&searchName={searchName}"
                + "&gender={gender}", page, size, searchName, gender)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, String.format("%s%s", TOKEN_PREFIX, getAccessToken()))
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$['content'].size()").value(2))
                .andReturn();

        //docs
        result.andDo(document("list-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(PageDescriptor.pageRequestFieldDescriptors)
                        .and(
                            parameterWithName("page").description("Page number").optional(),
                            parameterWithName("size").description("Page limit size").optional(),
                            parameterWithName("searchName").description("USER ID"),
                            parameterWithName("gender").description("[FEMALE] or [MALE]")
                        ),
                responseFields(PageDescriptor.pageResponseFieldDescriptors)
                        .and(fieldWithPath("content").description("CONTENT"))
                        .andWithPrefix("content[].",
                            fieldWithPath("id").description("ID"),
                            fieldWithPath("userId").description("USER ID"),
                            fieldWithPath("name").description("USER NAME"),
                            fieldWithPath("gender").description("USER GENDER"),
                            fieldWithPath("authorities").description("[ADMIN, ITEM, ORDER]")
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
                .memberAuthorityList(List.of(memberAuthority))
                .build();

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
                .memberAuthorityList(List.of(memberAuthority))
                .build();

        memberRepository.save(member);
        return jwtTokenHandler.generateToken(member);
    }
}