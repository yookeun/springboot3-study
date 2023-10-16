package com.example.study.config;

import com.example.study.annotation.ItemCheck;
import com.example.study.item.dto.ItemDto;
import com.example.study.item.repository.ItemRepository;
import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAspect {

    private final ItemRepository itemRepository;

    @AfterReturning(value = "@annotation(com.example.study.annotation.ItemCheck)", returning = "result")
    public void itemCheckSave(Object result) {
        Field[] fields = result.getClass().getDeclaredFields();

        Arrays.stream(fields).forEach(field -> {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ItemCheck.class)) {
                try {
                    Object value = field.get(result);
                    if (value != null) {
                        checkCheckSave(value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private <T> void checkCheckSave(T obj) {
        if (obj.getClass() == ItemDto.class) {
            log.info("ITEM >>> {}", obj);
            itemRepository.updateUsedCount(((ItemDto) obj).getId());
        } else {
            log.error("Nof found itemDto");
        }
    }

}
