package com.example.imagic.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerStore {
    public String directory;
    public String preferedFileStore;
}
