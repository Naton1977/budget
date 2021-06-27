package com.example.budget.domain.dto;


import com.example.budget.domain.entity.Family;
import lombok.Data;

@Data
public class GetClientMessage {

    private String message;

    private Family family;
}
