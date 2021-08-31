package com.flextalk.we.message.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import com.flextalk.we.cmmn.file.FileManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_message_file")
public class MessageFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_file_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "message_file_path", length = 150)
    private String filePath;

    @Column(name = "new_message_file_name", length = 32)
    private String newFileName;

    @Column(name = "org_message_file_name", length = 200)
    private String orgFileName;

    @Column(name = "message_file_ext", length = 30)
    private String fileExt;

    @Column(name = "message_file_size")
    private long fileSize;

    /**
     * 생성자
     * @param message 메시지
     * @param filePath 파일경로
     * @param orgFileName 원본 파일명
     */
    private MessageFile(Message message, String filePath, String orgFileName) {
        this.message = Objects.requireNonNull(message);
        this.filePath = Objects.requireNonNull(filePath);

        this.orgFileName = FileManager.extractFileName(orgFileName);
        this.fileExt = FileManager.extractFileName(orgFileName);
        this.newFileName = generateNewFileName();
    }

    /**
     * 메시지파일 생성 함수
     * @param message 메시지
     * @param filePath 파일경로
     * @param orgFileName 원본파일명
     * @return 메시지파일
     */
    public static MessageFile of(Message message, String filePath, String orgFileName) {
        MessageFile imageFile = new MessageFile(message, filePath, orgFileName);
        return imageFile;
    }

    /**
     * UUID 생성 함수
     * @return UUID로 만든 32자리 수
     */
    private String generateNewFileName() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public void updateFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
