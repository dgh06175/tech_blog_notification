package org.example.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataManager {
    // 상수 선언
    private static final String JSON_LOAD_FAILURE = "JSON 데이터 불러오기 실패: ";
    private static final String JSON_SAVE_FAILURE = "JSON 데이터 저장 실패: ";
    private static final String DATA_SAVED_TO = "데이터 %s 에 저장됨%n";
    private static final String PAST_DATA_FOLDER = "/pastData/";
    private static final String LINK_DATA_SUFFIX = "_link_data.json";

    /**
     * JSON 파일에서 데이터를 읽는다.
     *
     * @param blogName 읽을 파일 블로그 이름
     * @return 파일에서 읽은 데이터 목록
     */
    public Articles readArticlesFromJsonFile(String blogName) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(getFilePath(blogName));
        if (!file.exists()) {
            System.out.println("파일이 존재하지 않습니다: " + file.getPath());
            return null;
        }

        try {
            List<Map<String, String>> mapList = mapper.readValue(file, new TypeReference<>() {
            });
            Set<Article> articles = mapList.stream()
                    .map(map -> new Article(
                            map.get("link"),
                            map.get("title"),
                            map.get("author"),
                            map.get("date")))
                    .collect(Collectors.toSet());
            return new Articles(articles, blogName);
        } catch (IOException e) {
            System.err.println(JSON_LOAD_FAILURE + e.getMessage());
            return null;
        }
    }

    /**
     * 데이터를 JSON 형식으로 파일에 저장합니다.
     *
     * @param articles 변환할 데이터 목록
     */
    public void saveArticlesToJsonFile(Articles articles) {
        String filePath = getFilePath(articles.blogName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // 부모 디렉토리를 생성, 파일이 위치할 폴더가 없는 경우 생성

        try {
            mapper.writeValue(new File(filePath), articles.getArticles());
            System.out.printf(DATA_SAVED_TO, filePath);
        } catch (IOException e) {
            System.err.println(JSON_SAVE_FAILURE + e.getMessage());
        }
    }

    private String getFilePath(String blogName) {
        return Paths.get(System.getProperty("user.dir"), PAST_DATA_FOLDER, blogName + LINK_DATA_SUFFIX).toString();
    }
}
