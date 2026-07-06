package com.cocky.cockyserver.domain.auth;

import com.cocky.cockyserver.domain.auth.dto.ReissueRequest;
import com.cocky.cockyserver.domain.auth.dto.ReissueResponse;
import com.cocky.cockyserver.domain.auth.dto.SigninRequest;
import com.cocky.cockyserver.domain.auth.dto.SigninResponse;
import com.cocky.cockyserver.domain.auth.service.AuthService;
import com.cocky.cockyserver.global.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authService.signin(request.code()));
    }

    @PutMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(authService.reissue(request.refreshToken()));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(@AuthenticationPrincipal UserPrincipal principal) {
        authService.signout(principal.userId());
        return ResponseEntity.ok().build();
    }
}
