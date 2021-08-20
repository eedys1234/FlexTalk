package com.flextalk.we.cmmn.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            return false;
        }
    }
}
