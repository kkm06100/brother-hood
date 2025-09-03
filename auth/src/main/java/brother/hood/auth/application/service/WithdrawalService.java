package brother.hood.auth.application.service;

import brother.hood.auth.global.auth.AuthenticatedUserProvider;
import brother.hood.auth.persistence.User;
import brother.hood.auth.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static brother.hood.auth.global.exception.error.ErrorCodes.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawalService {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final UserJpaRepository userJpaRepository;

    public void execute() {
        Long id = authenticatedUserProvider.getCurrentUserId();
        userJpaRepository.findById(id).orElseThrow(USER_NOT_FOUND::throwException);
        userJpaRepository.deleteById(id);
    }
}
