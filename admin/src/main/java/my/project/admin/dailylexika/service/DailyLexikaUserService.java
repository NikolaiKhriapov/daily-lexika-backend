package my.project.admin.dailylexika.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.project.library.util.api.BaseUrlConfig;
import my.project.library.util.api.RestTemplateService;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.util.pageable.CustomPageImpl;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyLexikaUserService {

    private final RestTemplateService restTemplateService;
    private final BaseUrlConfig baseUrlConfig;

    public Page<UserDto> getAllUsers(HttpServletRequest request, int page, int size, String sortDirection) {
        String url = baseUrlConfig.getDailyLexika() + "/api/v1/users?page=" + page + "&size=" + size + "&sort=" + sortDirection;

        ResponseEntity<CustomPageImpl<UserDto>> response = restTemplateService.get(
                url,
                restTemplateService.getHeadersWithJsonContentTypeAndJwtToken(request),
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
