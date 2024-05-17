package com.store.service;

import com.store.constants.CodeType;
import com.store.entity.Code;
import com.store.exception.DataNotFoundException;
import com.store.exception.InvalidDataException;
import com.store.repository.CodeRepository;
import com.store.utils.OneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public void validateCode(String email, int value, CodeType codeType) {
        Code code = codeRepository.findCodeByUserEmailAndTypeAndValue(email, codeType.name(), value).orElseThrow(() ->
                new DataNotFoundException("Code was not found"));

        if (ZonedDateTime.now().isAfter(code.getExpireAt())) {
            throw new InvalidDataException("code is expired");
        }

        codeRepository.delete(code);
    }

    public Code createCode(String userEmail, CodeType codeType) throws InvalidKeyException {
        Code code = new Code();
        code.setUserEmail(userEmail);
        code.setValue(OneTimePasswordGenerator.generateOTP());
        code.setType(codeType.name());
        code.setExpireAt(ZonedDateTime.now().plusMinutes(5));

        codeRepository.save(code);

        checkCodeExpireAt();

        return code;
    }

    @Scheduled(cron = " 0 * * * * *")
    protected void checkCodeExpireAt() {
        ZonedDateTime now = ZonedDateTime.now();
        codeRepository.deleteAll(codeRepository.findCodesByExpireAtBefore(now));
    }
}