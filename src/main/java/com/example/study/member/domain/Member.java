package com.example.study.member.domain;

import com.example.study.common.BaseEntity;
import com.example.study.common.EncDecConverter;
import com.example.study.member.dto.MemberAuthorityDto.MemberAuthorityRequestDto;
import com.example.study.member.enums.Authority;
import com.example.study.member.enums.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "MEMBER")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, length = 20, unique = true)
    private String userId;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Column(name = "NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "GENDER", nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(name = "PHONE")
    @Convert(converter = EncDecConverter.class)
    private String phone;

    @Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<MemberAuthority> memberAuthorityList = new ArrayList<>();

    public void addAuthorities(List<MemberAuthorityRequestDto> memberAuthorityDtoList) {
        memberAuthorityDtoList.forEach(memberAuthorityRequestDto -> {
            memberAuthorityRequestDto.setMember(this);
            memberAuthorityList.add(memberAuthorityRequestDto.toEntity());
        });
    }

    public List<Authority> getAuthorities() {
        return memberAuthorityList.stream()
                .map(MemberAuthority::getAuthority).collect(Collectors.toList());
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateAuthorities(List<MemberAuthorityRequestDto> memberAuthorityRequestDtoList) {
        this.memberAuthorityList.clear();
        addAuthorities(memberAuthorityRequestDtoList);
    }
}
