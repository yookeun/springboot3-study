package com.example.study.handler;

import com.example.study.member.dto.LoginDto;
import com.example.study.member.dto.UserTokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.util.StringUtils;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisTokenHandler {

    private final Long limitDays;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenHandler(@Value("${jwt.refresh-token-expired-days}") Long limitDays,
            RedisTemplate<String, Object> redisTemplate) {
        this.limitDays = limitDays;
        this.redisTemplate = redisTemplate;
    }

    public Optional<UserTokenInfo> findSavedAccessToken(String userId)
            throws JsonProcessingException {
        return findByRefreshToken(userId);
    }

    public void saveToken(LoginDto loginDto) throws JsonProcessingException {
        UserTokenInfo userTokenInfo = UserTokenInfo.builder()
                .userId(loginDto.getUserId())
                .accessToken(loginDto.getAccessToken())
                .refreshToken(loginDto.getRefreshToken())
                .build();
        saveRedis(userTokenInfo);
    }


    private Optional<UserTokenInfo> findByRefreshToken(String userId)
            throws JsonProcessingException {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String json = (String) valueOperations.get(userId);

        if (StringUtils.isNullOrEmpty(json)) {
            return Optional.empty();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return Optional.of(objectMapper.readValue(json, UserTokenInfo.class));

    }

    private void saveRedis(UserTokenInfo userTokenInfo) throws JsonProcessingException {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        valueOperations.set(userTokenInfo.getUserId(), objectMapper.writeValueAsString(userTokenInfo));
        redisTemplate.expire(userTokenInfo.getUserId(), limitDays, TimeUnit.DAYS);
    }

    public void updateRedis(UserTokenInfo userTokenInfo) throws JsonProcessingException {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        valueOperations.set(userTokenInfo.getUserId(), objectMapper.writeValueAsString(userTokenInfo));
    }

    public void deleteRedis(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(key);
    }

}
