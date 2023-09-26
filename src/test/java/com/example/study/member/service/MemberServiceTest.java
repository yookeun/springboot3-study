package com.example.study.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.dto.MemberDto;
import com.example.study.member.dto.MemberDto.MemberRequestDto;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback(value = false)
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @DisplayName("When saving the account, it must be saved properly.")
    @Test
    void testSaveMember() {
        //given
        MemberRequestDto memberDto = MemberRequestDto.builder()
                .userId("hong")
                .password("1234")
                .gender(Gender.MALE)
                .name("HONG")
                .authorities(List.of(
                        MemberAuthorityRequestDto.builder().authority(Authority.ITEM).build(),
                        MemberAuthorityRequestDto.builder().authority(Authority.ORDER).build()))
                .build();

        //when
        MemberDto savedMemberDto = memberService.save(memberDto);

        //then
        assertThat(savedMemberDto.getId()).isNotNull();
    }

}