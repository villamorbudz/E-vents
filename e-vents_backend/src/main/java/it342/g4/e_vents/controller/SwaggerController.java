package it342.g4.e_vents.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for Swagger UI redirection
 */
@Controller
@Tag(name = "Swagger", description = "Swagger UI redirection")
public class SwaggerController {

    /**
     * Redirects /swagger to the Swagger UI
     * @return RedirectView to Swagger UI
     */
    @GetMapping("/swagger")
    @Operation(summary = "Redirect to Swagger UI", description = "Redirects to the Swagger UI documentation page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirects to Swagger UI")
    })
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
