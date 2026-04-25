package com.unimib.assignment3;

import com.unimib.assignment3.POJO.UserChatMapping;
import com.unimib.assignment3.repository.UserChatMappingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

@DataJpaTest
class UserChatMappingRepositoryTest {

    @Autowired
    private UserChatMappingRepository repository;

    @Test
    void shouldReturnRoomIdsForUser() {

        // given
        UserChatMapping mapping1 = new UserChatMapping();
        mapping1.setUserId(1L);
        mapping1 = repository.save(mapping1);

        mapping1.setRoomIds(List.of(10L, 20L, 30L));

        UserChatMapping mapping2 = new UserChatMapping();
        mapping2.setUserId(2L);
        mapping2 = repository.save(mapping2);
        mapping2.setRoomIds(List.of(10L));

        // when
        List<Long> roomIds = repository.findRoomIdsByUserId(1L);

        // then
        assertThat(roomIds)
                .isNotNull()
                .hasSize(3)
                .containsExactlyInAnyOrder(10L, 20L, 30L);

        assertThat(repository.findRoomIdsByUserId(2L))
                .isNotNull()
                .hasSize(1)
                .containsExactly(10L);
    }
}