package com.flextalk.we.cmmn.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * File의 생성, 삭제 등 관리하는 객체
 */
public class FileManager implements AutoCloseable {

    private FileOutputStream fout;

    private FileManager(String filePath, String fileName) throws FileNotFoundException {
        DirectoryManager.createDirectory(filePath);
        this.fout = new FileOutputStream(String.join(File.separator, filePath, fileName));
    }

    public void write(byte[] file) throws IOException {
        this.fout.write(file);
    }

    @Override
    public void close() throws IOException {
        this.fout.close();
    }

    /**
     * 파일생성
     * @param filePath 파일경로
     * @param fileName 파일명
     * @param file 파일 이진데이터
     * @return
     */
    //TODO : Log4j2 파일 생성되면 logging, custom exception을 생성해야하는지 여부 고민
    public static boolean create(String filePath, String fileName, byte[] file) {

        try(FileManager fileManager = new FileManager(filePath, fileName)) {
            fileManager.write(file);
            return true;
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 삭제
     * @param filePath 파일경로
     * @param fileName 파일명
     * @return 파일삭제여부
     */
    public static boolean delete(String filePath, String fileName) {
        File file = new File(String.join(File.separator, filePath, fileName));
        return file.delete();
    }

    /**
     * 파일명 추출함수
     * @param fileName 파일명
     * @return 파일명(파일형식 제외)
     * @throws IllegalArgumentException 파일명이 적절하지 않을경우 예외처리, 적절한 파일의 형식 test.png
     */
    public static String extractFileName(String fileName) {
        fileName = Objects.requireNonNull(fileName);

        int index = fileName.lastIndexOf(".");
        if(index <= 0) {
            throw new IllegalArgumentException(String.format("파일명이 적절하지 않습니다. fileName = %s", fileName));
        }
        return fileName.substring(0, index);
    }

    /**
     * 파일형식 추출함수
     * @param fileName 파일명
     * @return 파일형식
     * @throws IllegalArgumentException 파일명이 적절하지 않을경우 예외처리, 적절한 파일의 형식 test.png
     */
    public static String extractFileExt(String fileName) {
        fileName = Objects.requireNonNull(fileName);

        int index = fileName.lastIndexOf(".");
        if(index <= 0 || index >= fileName.length()-1) {
            throw new IllegalArgumentException(String.format("파일명이 적절하지 않습니다. fileName = %s", fileName));
        }
        return fileName.substring(index + 1);
    }

    /**
     * 파일의 크기 추출
     * @param fileName 파일명
     * @return 파일의 byte
     * @throws IllegalArgumentException 파일이 존재하지 않을경우
     * @throws IllegalStateException 파일의 크기를 가져오지 못하는 경우
     */
    public static long extractFileSize(String fileName) {
        fileName = Objects.requireNonNull(fileName);

        Path path = Paths.get(fileName);
        if(!Files.exists(path)) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다. fileName = " + fileName);
        }
        try {
            return Files.size(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("파일의 크기를 가져올 수 없습니다. fileName = " + fileName);
        }
    }
}
