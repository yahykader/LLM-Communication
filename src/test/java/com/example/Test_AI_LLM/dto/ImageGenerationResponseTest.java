package com.example.Test_AI_LLM.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ImageGenerationResponse - Tests Unitaires")
class ImageGenerationResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Devrait créer une réponse avec tous les paramètres")
    void shouldCreateResponse_WithAllParameters() {
        // Given
        String imageUrl = "https://example.com/image.png";
        String prompt = "A beautiful sunset";
        String error = "Some error message";

        // When
        ImageGenerationResponse response = new ImageGenerationResponse(imageUrl, prompt, error);

        // Then
        assertThat(response.imageUrl()).isEqualTo(imageUrl);
        assertThat(response.prompt()).isEqualTo(prompt);
        assertThat(response.error()).isEqualTo(error);
    }

    @Test
    @DisplayName("Devrait créer une réponse de succès avec la méthode factory success()")
    void shouldCreateSuccessResponse_UsingFactoryMethod() {
        // Given
        String imageUrl = "https://example.com/generated-image.png";
        String prompt = "A futuristic city";

        // When
        ImageGenerationResponse response = ImageGenerationResponse.success(imageUrl, prompt);

        // Then
        assertThat(response.imageUrl()).isEqualTo(imageUrl);
        assertThat(response.prompt()).isEqualTo(prompt);
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Devrait créer une réponse d'erreur avec la méthode factory error()")
    void shouldCreateErrorResponse_UsingFactoryMethod() {
        // Given
        String prompt = "Invalid prompt";
        String errorMessage = "Prompt validation failed";

        // When
        ImageGenerationResponse response = ImageGenerationResponse.error(prompt, errorMessage);

        // Then
        assertThat(response.imageUrl()).isNull();
        assertThat(response.prompt()).isEqualTo(prompt);
        assertThat(response.error()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Devrait accepter des valeurs null pour tous les champs")
    void shouldAcceptNullValues_ForAllFields() {
        // When
        ImageGenerationResponse response = new ImageGenerationResponse(null, null, null);

        // Then
        assertThat(response.imageUrl()).isNull();
        assertThat(response.prompt()).isNull();
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON correctement une réponse de succès")
    void shouldSerializeToJson_SuccessResponse() throws JsonProcessingException {
        // Given
        ImageGenerationResponse response = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A mountain landscape"
        );

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json)
                .contains("\"imageUrl\":\"https://example.com/image.png\"")
                .contains("\"prompt\":\"A mountain landscape\"")
                .doesNotContain("\"error\""); // NON_NULL devrait exclure ce champ
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON correctement une réponse d'erreur")
    void shouldSerializeToJson_ErrorResponse() throws JsonProcessingException {
        // Given
        ImageGenerationResponse response = ImageGenerationResponse.error(
                "Invalid input",
                "Prompt is too short"
        );

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json)
                .contains("\"prompt\":\"Invalid input\"")
                .contains("\"error\":\"Prompt is too short\"")
                .doesNotContain("\"imageUrl\""); // NON_NULL devrait exclure ce champ
    }

    @Test
    @DisplayName("Devrait exclure les champs null lors de la sérialisation JSON")
    void shouldExcludeNullFields_WhenSerializingToJson() throws JsonProcessingException {
        // Given
        ImageGenerationResponse response = new ImageGenerationResponse(
                "https://example.com/image.png",
                null,
                null
        );

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json)
                .contains("\"imageUrl\":\"https://example.com/image.png\"")
                .doesNotContain("\"prompt\"")
                .doesNotContain("\"error\"");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON correctement")
    void shouldDeserializeFromJson_Correctly() throws JsonProcessingException {
        // Given
        String json = """
                {
                    "imageUrl": "https://example.com/image.png",
                    "prompt": "A sunset over the ocean",
                    "error": null
                }
                """;

        // When
        ImageGenerationResponse response = objectMapper.readValue(json, ImageGenerationResponse.class);

        // Then
        assertThat(response.imageUrl()).isEqualTo("https://example.com/image.png");
        assertThat(response.prompt()).isEqualTo("A sunset over the ocean");
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON avec des champs manquants")
    void shouldDeserializeFromJson_WithMissingFields() throws JsonProcessingException {
        // Given
        String json = """
                {
                    "imageUrl": "https://example.com/image.png"
                }
                """;

        // When
        ImageGenerationResponse response = objectMapper.readValue(json, ImageGenerationResponse.class);

        // Then
        assertThat(response.imageUrl()).isEqualTo("https://example.com/image.png");
        assertThat(response.prompt()).isNull();
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Devrait tester l'égalité entre deux records identiques")
    void shouldBeEqual_WhenRecordsHaveSameValues() {
        // Given
        ImageGenerationResponse response1 = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A sunset"
        );
        ImageGenerationResponse response2 = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A sunset"
        );

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Devrait être différent quand les valeurs diffèrent")
    void shouldNotBeEqual_WhenRecordsHaveDifferentValues() {
        // Given
        ImageGenerationResponse response1 = ImageGenerationResponse.success(
                "https://example.com/image1.png",
                "A sunset"
        );
        ImageGenerationResponse response2 = ImageGenerationResponse.success(
                "https://example.com/image2.png",
                "A sunset"
        );

        // Then
        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    @DisplayName("Devrait être différent quand on compare success et error")
    void shouldNotBeEqual_WhenComparingSuccessAndError() {
        // Given
        ImageGenerationResponse successResponse = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A sunset"
        );
        ImageGenerationResponse errorResponse = ImageGenerationResponse.error(
                "A sunset",
                "Generation failed"
        );

        // Then
        assertThat(successResponse).isNotEqualTo(errorResponse);
    }

    @Test
    @DisplayName("Devrait avoir un toString() lisible")
    void shouldHaveReadableToString() {
        // Given
        ImageGenerationResponse response = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A landscape"
        );

        // When
        String toString = response.toString();

        // Then
        assertThat(toString)
                .contains("ImageGenerationResponse")
                .contains("https://example.com/image.png")
                .contains("A landscape");
    }

    @Test
    @DisplayName("Devrait gérer les URLs longues")
    void shouldHandleLongUrls() {
        // Given
        String longUrl = "https://example.com/very/long/path/to/image/" + "x".repeat(200) + ".png";

        // When
        ImageGenerationResponse response = ImageGenerationResponse.success(longUrl, "Test prompt");

        // Then
        assertThat(response.imageUrl()).isEqualTo(longUrl);
        assertThat(response.imageUrl()).hasSize(longUrl.length());
    }

    @Test
    @DisplayName("Devrait gérer les caractères spéciaux dans le prompt")
    void shouldHandleSpecialCharacters_InPrompt() {
        // Given
        String specialPrompt = "Un château français avec des éléments gothiques! 🏰 \"quoted\"";

        // When
        ImageGenerationResponse response = ImageGenerationResponse.success(
                "https://example.com/image.png",
                specialPrompt
        );

        // Then
        assertThat(response.prompt()).isEqualTo(specialPrompt);
    }

    @Test
    @DisplayName("Devrait gérer les messages d'erreur multiligne")
    void shouldHandleMultilineErrorMessages() {
        // Given
        String multilineError = """
                Error occurred:
                - Invalid prompt length
                - API rate limit exceeded
                """;

        // When
        ImageGenerationResponse response = ImageGenerationResponse.error("Test", multilineError);

        // Then
        assertThat(response.error()).isEqualTo(multilineError);
        assertThat(response.error()).contains("Invalid prompt length", "API rate limit exceeded");
    }

    @Test
    @DisplayName("Devrait créer une réponse de succès avec des valeurs vides")
    void shouldCreateSuccessResponse_WithEmptyValues() {
        // When
        ImageGenerationResponse response = ImageGenerationResponse.success("", "");

        // Then
        assertThat(response.imageUrl()).isEmpty();
        assertThat(response.prompt()).isEmpty();
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Devrait créer une réponse d'erreur avec des valeurs vides")
    void shouldCreateErrorResponse_WithEmptyValues() {
        // When
        ImageGenerationResponse response = ImageGenerationResponse.error("", "");

        // Then
        assertThat(response.imageUrl()).isNull();
        assertThat(response.prompt()).isEmpty();
        assertThat(response.error()).isEmpty();
    }

    @Test
    @DisplayName("Devrait sérialiser et désérialiser correctement (round-trip)")
    void shouldSerializeAndDeserialize_RoundTrip() throws JsonProcessingException {
        // Given
        ImageGenerationResponse original = ImageGenerationResponse.success(
                "https://example.com/image.png",
                "A beautiful landscape with mountains"
        );

        // When
        String json = objectMapper.writeValueAsString(original);
        ImageGenerationResponse deserialized = objectMapper.readValue(json, ImageGenerationResponse.class);

        // Then
        assertThat(deserialized).isEqualTo(original);
    }

    @Test
    @DisplayName("Devrait vérifier que les méthodes factory sont des constructeurs valides")
    void shouldVerifyFactoryMethods_AreValidConstructors() {
        // Given
        String url = "https://example.com/image.png";
        String prompt = "Test prompt";
        String error = "Test error";

        // When
        ImageGenerationResponse successResponse = ImageGenerationResponse.success(url, prompt);
        ImageGenerationResponse errorResponse = ImageGenerationResponse.error(prompt, error);
        ImageGenerationResponse directResponse = new ImageGenerationResponse(url, prompt, error);

        // Then
        // Vérifier que les factory methods créent des objets valides
        assertThat(successResponse).isNotNull();
        assertThat(errorResponse).isNotNull();
        assertThat(directResponse).isNotNull();

        // Vérifier la structure
        assertThat(successResponse.imageUrl()).isNotNull();
        assertThat(successResponse.error()).isNull();

        assertThat(errorResponse.imageUrl()).isNull();
        assertThat(errorResponse.error()).isNotNull();
    }
}
