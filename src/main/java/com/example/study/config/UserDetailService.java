package com.example.study.config;

import com.example.study.member.domain.Member;
import com.example.study.member.enums.Authority;
import com.example.study.member.respository.MemberRepository;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByUserId(username);
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("UsernameNotFound [" + username + "]");
        }
        return new User(optionalMember.get());
    }

    static class User implements UserDetails {

        private final String username;
        private final String password;
        private final List<String> authorities;

        public User(Member member) {
            this.username = member.getUserId();
            this.password = member.getPassword();
            authorities = Arrays.stream(Authority.values())
                    .map(Authority::name)
                    .collect(Collectors.toList());
        }


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities.stream().map(SimpleGrantedAuthority::new).collect(
                    Collectors.toSet());
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return null;
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

}
