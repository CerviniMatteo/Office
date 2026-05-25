package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.UnreadMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnreadMessagesRepository extends JpaRepository<UnreadMessages, Long> {

}
