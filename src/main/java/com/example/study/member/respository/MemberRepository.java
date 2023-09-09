package com.example.study.member.respository;

import com.example.study.member.domain.Member;
import java.util.Optional;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface MemberRepository extends Repository<Member, Long> {
    Optional<Member> findByUserId(String userID);
    Member save(Member member);
}