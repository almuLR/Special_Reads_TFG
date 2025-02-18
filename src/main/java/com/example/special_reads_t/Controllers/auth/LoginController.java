package com.example.special_reads_t.Controllers.auth;




import com.example.special_reads_t.Model.Book;
import com.example.special_reads_t.Security.jwt.AuthResponse;
import com.example.special_reads_t.Security.jwt.LoginRequest;
import com.example.special_reads_t.Security.jwt.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserLoginService userService;

    @Operation(summary = "Post a login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "login created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ids supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "login not created", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest) {

        return userService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Post a token ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "token refresh", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "token not refresh", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        return userService.refresh(refreshToken);
    }

    @Operation(summary = "Post logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "logout success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "logout not do", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, userService.logout(request, response)));
    }
}