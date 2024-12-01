package br.ueg.genericarchitecture.dto;


import java.util.List;

public interface DTOFile {
    List<FileDTO> getFiles();
    void setFiles(List<FileDTO> files);
}
