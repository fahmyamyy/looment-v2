package com.looment.authservice.services;

import com.looment.authservice.dtos.requests.*;
import com.looment.authservice.dtos.responses.TokenResponse;
import com.looment.authservice.dtos.responses.UserResponse;
import com.looment.authservice.entities.UserSecurity;
import com.looment.authservice.entities.Users;
import com.looment.authservice.entities.UsersInfo;
import com.looment.authservice.exceptions.*;
import com.looment.authservice.repositories.UserInfoRepository;
import com.looment.authservice.repositories.UserRepository;
import com.looment.authservice.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtEncoder jwtEncoder;
    private final KafkaTemplate<String, Object> template;

    private TokenResponse generateToken(Users users) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(users.getId().toString())
                .claim("authorities", "USER")
                .build();
        return new TokenResponse(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
    }

    @Override
    public void validateUser(UserLogin userLogin) {
        Users users = userRepository.findByUsernameEqualsIgnoreCaseAndDeletedAtIsNull(userLogin.getUsername())
                .orElseThrow(UserNotExists::new);

        if (users.getLockedAt() != null && users.getLockedAt().plusMinutes(15).isAfter(LocalDateTime.now())) {
            throw new UserLocked();
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword()));
        } catch (Exception e) {
            badCredentials(userLogin.getUsername());
        }

        String otp = generateOtp();
        users.setOtp(otp);
        users.setOtpExpired(LocalDateTime.now().plusMinutes(10));
        users.setAttemp(0);

        EmailOTP emailOTP = EmailOTP.builder()
                .username(users.getUsername())
                .email(users.getEmail())
                .otp(otp)
                .build();

        userRepository.save(users);
//TODO:        template.send("user-otp", emailOTP.toString());
    }

    @Override
    public UserResponse register(UserRegister userRegister) {
        Optional<Users> usersOptional = userRepository.findByEmailEqualsIgnoreCaseAndDeletedAtIsNull(userRegister.getEmail());
        if (usersOptional.isPresent()) {
            throw new UserEmailExists();
        }

        LocalDate dob = userRegister.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(dob, LocalDate.now());

        if (period.getYears() < 18) {
            throw new UserUnderage();
        }
        if (Boolean.FALSE.equals(PasswordValidator.isValid(userRegister.getPassword()))) {
            throw new UserPasswordInvalid();
        }
        if (Boolean.FALSE.equals(EmailValidator.isValid(userRegister.getEmail()))) {
            throw new UserEmailInvalid();
        }

        Users newUsers = modelMapper.map(userRegister, Users.class);
        newUsers.setPassword(passwordEncoder.encode(userRegister.getPassword()));

        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsers(newUsers);

        userRepository.save(newUsers);
        userInfoRepository.save(usersInfo);

        return modelMapper.map(newUsers, UserResponse.class);
    }

    @Override
    public UserResponse info(UUID userId) {
        Users users = userRepository.findByDeletedAtIsNullAndIdEquals(userId)
                .orElseThrow(UserNotExists::new);

        return modelMapper.map(users, UserResponse.class);
    }

    @Override
    public TokenResponse verifyOtp(UserLoginOTP userLoginOTP) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginOTP.getUsername(), userLoginOTP.getPassword()));
        UserSecurity userSecurity = (UserSecurity) authentication.getPrincipal();
        Users users = userSecurity.getUser();

        boolean valid = passwordEncoder.matches(
                userLoginOTP.getOtp().toString(),
                users.getOtp()
        );

        if (users.getOtp() == null || !valid) {
            throw new UserOtpInvalid();
        }
        if (users.getOtpExpired().isBefore(LocalDateTime.now())) {
            throw new UserOtpExpired();
        }

        users.setAttemp(0);
        users.setOtp(null);
        users.setOtpExpired(null);
        userRepository.save(users);

        return generateToken(users);
    }

    @Override
    public void badCredentials(String username) {
        Users users = userRepository.findByUsernameEqualsIgnoreCaseAndDeletedAtIsNull(username)
                .orElseThrow(UserNotExists::new);

        if (users.getAttemp() == 2) {
            if (users.getLockedAt() == null) {
                users.setAttemp(0);
                users.setLockedAt(LocalDateTime.now());
            } else {
                users.setAttemp(0);
                users.setLockedAt(users.getLockedAt().plusDays(1));
            }
            userRepository.save(users);

            EmailLocked emailLocked = EmailLocked.builder()
                    .email(users.getEmail())
                    .username(users.getUsername())
                    .build();

            template.send("user-locked", emailLocked.toString());
            throw new UserLocked();
        } else {
            users.setAttemp(users.getAttemp() + 1);
        }

        userRepository.save(users);
        throw new BadCredentials();
    }

    @Override
    public void resetPassword(UUID userId) {
        Users users = userRepository.findByDeletedAtIsNullAndIdEquals(userId)
                .orElseThrow(UserNotExists::new);

        String randomPass = RandomPassword.generate();

        users.setPassword(passwordEncoder.encode(randomPass));
        users.setAttemp(0);
        users.setLockedAt(null);
        userRepository.save(users);

        EmailReset emailReset = EmailReset.builder()
                .email(users.getEmail())
                .username(users.getUsername())
                .password(randomPass)
                .build();
        template.send("user-reset-password", emailReset.toString());
    }

    @Override
    public void changePassword(UUID userId, PasswordRequest passwordRequest) {
        Users users = userRepository.findByDeletedAtIsNullAndIdEquals(userId)
                .orElseThrow(UserNotExists::new);

        if (Boolean.FALSE.equals(PasswordValidator.isValid(passwordRequest.getPassword()))) {
            throw new UserPasswordInvalid();
        }

        users.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
        userRepository.save(users);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        log.info("OTP: {}", otp);

        return passwordEncoder.encode(Integer.toString(otp));
    }
}