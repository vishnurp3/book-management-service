package com.vishnurp3.bookmanagementservice.mapper;

import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.dto.BookResponseDto;
import com.vishnurp3.bookmanagementservice.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequestDto bookRequestDto);

    BookResponseDto toDto(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BookRequestDto dto, @MappingTarget Book entity);
}
