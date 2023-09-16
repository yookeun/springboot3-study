package com.example.study.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.study.member.dto.MemberAuthorityDto;
import com.example.study.member.dto.MemberDto;
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
        MemberDto memberDto = MemberDto.builder()
                .userId("hong")
                .password("1234")
                .gender(Gender.MALE)
                .name("HONG")
                .authorities(List.of(
                        MemberAuthorityDto.builder().authority(Authority.ITEM).build(),
                        MemberAuthorityDto.builder().authority(Authority.ORDER).build()))
                .build();

        //when
        MemberDto savedMemberDto = memberService.save(memberDto);

        //then
        assertThat(savedMemberDto.getMemberId()).isNotNull();
    }

    @Test
    void testSaveMember100() {
        for (int i = 0; i < 100; i++) {
            //given
            MemberDto memberDto = MemberDto.builder()
                    .userId("hong_"+i)
                    .password("1234")
                    .gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                    .name("HONG"+i)
                    .authorities(List.of(
                            MemberAuthorityDto.builder().authority(Authority.ITEM).build(),
                            MemberAuthorityDto.builder().authority(Authority.ORDER).build()))
                    .build();

            //when
            MemberDto savedMemberDto = memberService.save(memberDto);
        }
    }

}