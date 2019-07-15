package com.example.demo.model.DTO.ID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfIDDto {

   private ArrayList<IDDto> listOfIDDto = new ArrayList<>();

}
