package my.project.dailybudget.mappers.account;

import my.project.dailybudget.entities.account.Account;
import my.project.library.dailybudget.dtos.account.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    AccountDto toDto(Account entity);

    List<AccountDto> toDtoList(List<Account> entity);
}
