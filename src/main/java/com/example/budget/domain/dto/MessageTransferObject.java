package com.example.budget.domain.dto;


import lombok.Data;

@Data
public class MessageTransferObject {

    private String message;

    private boolean chekResult = true;
}
