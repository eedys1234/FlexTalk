package com.flextalk.we.cmmn.file;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class DirectoryManager {

    private DirectoryManager() {

    }

    /**
     * 디렉토리 생성 함수
     * @param filePath 파일경로
     * @return 생성 여부
     */
    public static boolean createDirectory(String filePath) {
        try {
            Files.createDirectories(Paths.get(filePath));
            return true;
        } catch (IOException e) {
            log.error("디렉토리 생성 실패");
            log.error(e.getMessage());
            return false;
        }
    }
}
