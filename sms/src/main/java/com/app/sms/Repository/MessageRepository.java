package com.app.sms.Repository;

import com.app.sms.Entity.MessageEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByFromUserName(String name);

    @Query(value = """
             SELECT * FROM messages
             WHERE (from_user_name = :name1 AND to_user_name = :name2)
             OR (from_user_name = :name2 AND to_user_name = :name1)
             ORDER BY time ASC
            """, nativeQuery = true)
    List<MessageEntity> getDialogMessages(@Param("name1") String name1, @Param("name2") String name2);

    @Query(value = """
            SELECT DISTINCT to_user_name FROM messages
            WHERE from_user_name = :name 
            UNION 
            SELECT DISTINCT from_user_name FROM messages 
            WHERE to_user_name = :name
            """, nativeQuery = true)
    List<String> getContactsOfUser(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE messages
            SET content = :newText
            WHERE id = :id
            """, nativeQuery = true)
    int updateMessageText(@Param("id") Long id, @Param("newText") String newText);

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM messages
            WHERE from_user_name = :deletedName OR to_user_name = :deletedName
            """, nativeQuery = true)
    int deleteMessagesOfDeletedAccount(@Param("deletedName") String deletedName);
}
