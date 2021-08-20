package com.flextalk.we.cmmn.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FileManagerTest {

    private final String orgFileName = "파일_테스트.txt";

    @DisplayName("파일명 추출 테스트")
    @Test
    public void extractFileNameTest() {

        //given

        //when
        String extractFileName = FileManager.extractFileName(orgFileName);

        //then
        assertThat(extractFileName, equalTo("파일_테스트"));
    }
    
    @DisplayName("파일명 추출 실패 테스트")
    @Test
    public void extractFileNameInValidExceptionTest() {

        //given
        String orgFileName = "txt";

        //then
        assertThrows(IllegalArgumentException.class,  () -> FileManager.extractFileName(orgFileName));

    }

    @DisplayName("파일형식 추출 테스트")
    @Test
    public void extractFileExtTest() {

        //given

        //when
        String extractFileExt = FileManager.extractFileExt(orgFileName);

        //then
        assertThat(extractFileExt, equalTo("txt"));
    }

    @DisplayName("파일형식 추출 실패 테스트")
    @Test
    public void extractFileExtInvalidExceptionTest() {

        //given
        String orgFileName = "테스트.";

        //then
        assertThrows(IllegalArgumentException.class, () -> FileManager.extractFileExt(orgFileName));
    }

    @DisplayName("파일 사이즈 추출 실패 테스트")
    public void extractFileSizeInvalidExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> FileManager.extractFileSize(orgFileName));
    }
}
