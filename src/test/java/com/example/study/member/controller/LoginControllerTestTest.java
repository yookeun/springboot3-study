package com.example.study.member.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.study.member.domain.Member;
import com.example.study.member.domain.MemberAuthority;
import com.example.study.member.dto.LoginDto.LoginRequestDto;
import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import com.example.study.member.respository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
class LoginControllerTestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long memberId;

    @DisplayName("Save member")
    @Test
    void testSaveMember() throws Exception {
        //given
        MemberAuthorityRequestDto memberAuthorityRequestDto = MemberAuthorityRequestDto.builder()
                .authority(Authority.ADMIN)
                .build();

        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                .name("test")
                .password("1234")
                .userId("admin")
                .gender(Gender.MALE)
                .authorities(List.of(memberAuthorityRequestDto))
                .build();
        //when
        ResultActions result = mockMvc.perform(post("/member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andReturn();

        //docs
        result.andDo(document("save-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("name").description("member's name"),
                        fieldWithPath("userId").description("User's ID"),
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

    @DisplayName("Login member")
    @Test
    void testLogin() throws Exception {
        //given
        insertDb();

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .userId("admin")
                .password("1234")
                .build();

        //when
        ResultActions result = mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //.content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(loginRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("admin"))
                .andReturn();

        //docs
        result.andDo(document("login-member",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("userId").description("USER ID"),
                        fieldWithPath("password").description("PASSWORD")
                ),
                responseFields(
                        fieldWithPath("userId").description("USER ID"),
                        fieldWithPath("name").description("USER NAME"),
                        fieldWithPath("password").description("PASSWORD"),
                        fieldWithPath("accessToken").description("JWT TOKEN")
                )
        ));

    }

    private void insertDb() {
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
        memberId = member.getId();
    }

}