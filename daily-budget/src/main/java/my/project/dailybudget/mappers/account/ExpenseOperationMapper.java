package my.project.dailybudget.mappers.account;

import my.project.dailybudget.entities.account.ExpenseOperation;
import my.project.library.dailybudget.dtos.account.AccountDto;
import my.project.library.dailybudget.dtos.account.ExpenseOperationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = AccountDto.class)
public interface ExpenseOperationMapper {

    ExpenseOperationDto toDto(ExpenseOperation entity);

    List<ExpenseOperationDto> toDtoList(List<ExpenseOperation> entity);
}
