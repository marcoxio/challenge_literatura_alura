package com.alura.challenges.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Book {
    private int id;
    private String title;
    private List<Author> authors;
    private List<String> languages;
    @JsonProperty("download_count") private int downloadCount;


}