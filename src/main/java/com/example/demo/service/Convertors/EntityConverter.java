package com.example.demo.service.Convertors;

public interface EntityConverter<Model, Dto> {

    Dto convert(Model model);

    Model convertRevers(Dto dto);

}
