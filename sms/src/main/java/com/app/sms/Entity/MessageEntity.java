package com.app.sms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // Использование JOIN не нужно в такой БД,
    // поэтому внешние ключи решено не создавать
    @Column(name = "from_user_name", nullable = false)
    private String fromUserName;

    // @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "to_user_name", nullable = false)
    private String toUserName;

    @Column(name = "time", nullable = false)
    @ColumnDefault("LOCALTIMESTAMP")
    private LocalDateTime time;

    @Column(name = "content", length = 500)
    private String content;

    public MessageEntity(String fromUserName, String toUserName, LocalDateTime time, String content) {
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
        this.time = time;
        this.content = content;
    }
}
