package com.example.Test_AI_LLM.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ImageGenerationRequest - Tests Unitaires")
class ImageGenerationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Devrait créer une requête valide avec tous les paramètres")
    void shouldCreateValidRequest_WithAllParameters() {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A beautiful sunset over the mountains",
                "hd",
                "1024x1024"
        );

        // Then
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
        assertThat(request.prompt()).isEqualTo("A beautiful sunset over the mountains");
        assertThat(request.quality()).isEqualTo("hd");
        assertThat(request.size()).isEqualTo("1024x1024");
    }

    @Test
    @DisplayName("Devrait appliquer les valeurs par défaut pour quality et size")
    void shouldApplyDefaultValues_WhenQualityAndSizeAreNull() {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A beautiful landscape",
                null,
                null
        );

        // Then
        assertThat(request.prompt()).isEqualTo("A beautiful landscape");
        assertThat(request.quality()).isEqualTo("hd");
        assertThat(request.size()).isEqualTo("1024x1024");
    }

    @Test
    @DisplayName("Devrait appliquer les valeurs par défaut pour quality et size vides")
    void shouldApplyDefaultValues_WhenQualityAndSizeAreBlank() {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A beautiful landscape",
                "   ",
                ""
        );

        // Then
        assertThat(request.quality()).isEqualTo("hd");
        assertThat(request.size()).isEqualTo("1024x1024");
    }

    @Test
    @DisplayName("Devrait conserver les valeurs personnalisées de quality et size")
    void shouldKeepCustomValues_WhenProvided() {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A futuristic city",
                "standard",
                "1792x1024"
        );

        // Then
        assertThat(request.quality()).isEqualTo("standard");
        assertThat(request.size()).isEqualTo("1792x1024");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Devrait échouer la validation quand le prompt est null ou vide")
    void shouldFailValidation_WhenPromptIsNullOrEmpty(String prompt) {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                prompt,
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Prompt cannot be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Devrait échouer la validation quand le prompt contient uniquement des espaces")
    void shouldFailValidation_WhenPromptIsBlank(String prompt) {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                prompt,
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Prompt cannot be empty",
                        "Prompt must be between 3 and 1000 characters"
                );
    }

    @Test
    @DisplayName("Devrait échouer la validation quand le prompt est trop court (moins de 3 caractères)")
    void shouldFailValidation_WhenPromptIsTooShort() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "ab",
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Prompt must be between 3 and 1000 characters");
    }

    @Test
    @DisplayName("Devrait échouer la validation quand le prompt est trop long (plus de 1000 caractères)")
    void shouldFailValidation_WhenPromptIsTooLong() {
        // Given
        String longPrompt = "a".repeat(1001);
        ImageGenerationRequest request = new ImageGenerationRequest(
                longPrompt,
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Prompt must be between 3 and 1000 characters");
    }

    @Test
    @DisplayName("Devrait valider un prompt à la limite minimale (3 caractères)")
    void shouldPassValidation_WhenPromptIsMinimumLength() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "abc",
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Devrait valider un prompt à la limite maximale (1000 caractères)")
    void shouldPassValidation_WhenPromptIsMaximumLength() {
        // Given
        String maxPrompt = "a".repeat(1000);
        ImageGenerationRequest request = new ImageGenerationRequest(
                maxPrompt,
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Devrait gérer correctement les caractères spéciaux et accents dans le prompt")
    void shouldHandleSpecialCharacters_InPrompt() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Un château français avec des éléments gothiques, très détaillé! 🏰",
                "hd",
                "1024x1024"
        );

        // When
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.prompt()).contains("château", "français", "éléments", "🏰");
    }

    @Test
    @DisplayName("Devrait tester l'égalité entre deux records identiques")
    void shouldBeEqual_WhenRecordsHaveSameValues() {
        // Given
        ImageGenerationRequest request1 = new ImageGenerationRequest(
                "A sunset",
                "hd",
                "1024x1024"
        );
        ImageGenerationRequest request2 = new ImageGenerationRequest(
                "A sunset",
                "hd",
                "1024x1024"
        );

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Devrait être différent quand les valeurs diffèrent")
    void shouldNotBeEqual_WhenRecordsHaveDifferentValues() {
        // Given
        ImageGenerationRequest request1 = new ImageGenerationRequest(
                "A sunset",
                "hd",
                "1024x1024"
        );
        ImageGenerationRequest request2 = new ImageGenerationRequest(
                "A sunrise",
                "hd",
                "1024x1024"
        );

        // Then
        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @DisplayName("Devrait avoir un toString() lisible")
    void shouldHaveReadableToString() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A landscape",
                "hd",
                "1024x1024"
        );

        // When
        String toString = request.toString();

        // Then
        assertThat(toString)
                .contains("ImageGenerationRequest")
                .contains("A landscape")
                .contains("hd")
                .contains("1024x1024");
    }

    @ParameterizedTest
    @ValueSource(strings = {"standard", "hd", "CUSTOM_QUALITY"})
    @DisplayName("Devrait accepter différentes valeurs de quality")
    void shouldAcceptDifferentQualityValues(String quality) {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A beautiful scene",
                quality,
                "1024x1024"
        );

        // Then
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
        assertThat(request.quality()).isEqualTo(quality);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1024x1024", "1792x1024", "1024x1792"})
    @DisplayName("Devrait accepter différentes valeurs de size")
    void shouldAcceptDifferentSizeValues(String size) {
        // Given & When
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A detailed image",
                "hd",
                size
        );

        // Then
        Set<ConstraintViolation<ImageGenerationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
        assertThat(request.size()).isEqualTo(size);
    }
}
