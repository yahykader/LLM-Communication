package com.example.Test_AI_LLM.service;

import com.example.Test_AI_LLM.config.OpenAiImageProperties;
import com.example.Test_AI_LLM.dto.ImageGenerationRequest;
import com.example.Test_AI_LLM.dto.ImageGenerationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageGenerationService - Tests Unitaires")
class ImageGenerationServiceTest {

    @Mock
    private OpenAiImageModel openAiImageModel;

    @Mock
    private OpenAiImageProperties imageProperties;

    @InjectMocks
    private ImageGenerationService imageGenerationService;

    @Captor
    private ArgumentCaptor<ImagePrompt> imagePromptCaptor;

    @BeforeEach
    void setUp() {
        // Configuration par défaut des propriétés
        when(imageProperties.getModel()).thenReturn("dall-e-3");
    }

    @Test
    @DisplayName("Devrait générer une image avec succès")
    void shouldGenerateImage_Successfully() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "A beautiful sunset over mountains",
                "hd",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.imageUrl()).isEqualTo("https://example.com/image.png");
        assertThat(response.prompt()).isEqualTo("A beautiful sunset over mountains");
        assertThat(response.error()).isNull();

        verify(openAiImageModel, times(1)).call(any(ImagePrompt.class));
    }

    @Test
    @DisplayName("Devrait construire ImagePrompt avec les bonnes options")
    void shouldBuildImagePrompt_WithCorrectOptions() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1792x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel).call(imagePromptCaptor.capture());
        ImagePrompt capturedPrompt = imagePromptCaptor.getValue();

        assertThat(capturedPrompt.getInstructions()).isEqualTo("Test prompt");

        OpenAiImageOptions options = (OpenAiImageOptions) capturedPrompt.getOptions();
        assertThat(options.getQuality()).isEqualTo("hd");
        assertThat(options.getModel()).isEqualTo("dall-e-3");
        assertThat(options.getWidth()).isEqualTo(1792);
        assertThat(options.getHeight()).isEqualTo(1024);
    }

    @Test
    @DisplayName("Devrait utiliser le modèle depuis les propriétés")
    void shouldUseModel_FromProperties() {
        // Given
        when(imageProperties.getModel()).thenReturn("dall-e-2");

        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "standard",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel).call(imagePromptCaptor.capture());
        OpenAiImageOptions options = (OpenAiImageOptions) imagePromptCaptor.getValue().getOptions();
        assertThat(options.getModel()).isEqualTo("dall-e-2");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1024x1024", "1792x1024", "1024x1792"})
    @DisplayName("Devrait accepter toutes les tailles valides")
    void shouldAcceptAllValidSizes(String size) {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                size
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.imageUrl()).isNotNull();
        verify(openAiImageModel, times(1)).call(any(ImagePrompt.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"standard", "hd"})
    @DisplayName("Devrait accepter toutes les qualités valides")
    void shouldAcceptAllValidQualities(String quality) {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                quality,
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.imageUrl()).isNotNull();
    }

    @Test
    @DisplayName("Devrait lancer IllegalArgumentException pour qualité invalide")
    void shouldThrowException_WhenQualityIsInvalid() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "ultra-hd",
                "1024x1024"
        );

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quality must be 'standard' or 'hd'")
                .hasMessageContaining("ultra-hd");

        verify(openAiImageModel, never()).call(any(ImagePrompt.class));
    }

    @Test
    @DisplayName("Devrait lancer IllegalArgumentException pour taille invalide")
    void shouldThrowException_WhenSizeIsInvalid() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "512x512"
        );

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size must be '1024x1024', '1792x1024', or '1024x1792'")
                .hasMessageContaining("512x512");

        verify(openAiImageModel, never()).call(any(ImagePrompt.class));
    }

    @Test
    @DisplayName("Devrait lancer RuntimeException quand la réponse est null")
    void shouldThrowException_WhenResponseIsNull() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid response from OpenAI API");
    }

    @Test
    @DisplayName("Devrait lancer RuntimeException quand result est null")
    void shouldThrowException_WhenResultIsNull() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        ImageResponse mockResponse = mock(ImageResponse.class);
        when(mockResponse.getResult()).thenReturn(null);
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid response from OpenAI API");
    }

    @Test
    @DisplayName("Devrait lancer RuntimeException quand output est null")
    void shouldThrowException_WhenOutputIsNull() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        // Créer un ModelResult avec output null
        org.springframework.ai.model.ModelResult<Image> mockGeneration =
                new org.springframework.ai.model.ModelResult<>(null);

        ImageResponse mockResponse = new ImageResponse(java.util.List.of(mockGeneration));
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid response from OpenAI API");
    }

    @Test
    @DisplayName("Devrait extraire correctement la largeur de la taille 1024x1024")
    void shouldExtractWidth_From1024x1024() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel).call(imagePromptCaptor.capture());
        OpenAiImageOptions options = (OpenAiImageOptions) imagePromptCaptor.getValue().getOptions();
        assertThat(options.getWidth()).isEqualTo(1024);
        assertThat(options.getHeight()).isEqualTo(1024);
    }

    @Test
    @DisplayName("Devrait extraire correctement la largeur de la taille 1792x1024")
    void shouldExtractWidth_From1792x1024() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1792x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel).call(imagePromptCaptor.capture());
        OpenAiImageOptions options = (OpenAiImageOptions) imagePromptCaptor.getValue().getOptions();
        assertThat(options.getWidth()).isEqualTo(1792);
        assertThat(options.getHeight()).isEqualTo(1024);
    }

    @Test
    @DisplayName("Devrait extraire correctement la largeur de la taille 1024x1792")
    void shouldExtractWidth_From1024x1792() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1792"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel).call(imagePromptCaptor.capture());
        OpenAiImageOptions options = (OpenAiImageOptions) imagePromptCaptor.getValue().getOptions();
        assertThat(options.getWidth()).isEqualTo(1024);
        assertThat(options.getHeight()).isEqualTo(1792);
    }

    @Test
    @DisplayName("Devrait gérer un prompt avec caractères spéciaux")
    void shouldHandlePrompt_WithSpecialCharacters() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Un château français avec des éléments gothiques! 🏰",
                "hd",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.prompt()).contains("château", "français", "🏰");
    }

    @Test
    @DisplayName("Devrait gérer un prompt très long")
    void shouldHandleLongPrompt() {
        // Given
        String longPrompt = "A beautiful landscape ".repeat(50);
        ImageGenerationRequest request = new ImageGenerationRequest(
                longPrompt,
                "hd",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        ImageGenerationResponse response = imageGenerationService.generateImage(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.prompt()).isEqualTo(longPrompt);
    }

    @Test
    @DisplayName("Devrait appeler l'API une seule fois par génération")
    void shouldCallAPI_OnlyOnce() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        ImageResponse mockImageResponse = createMockImageResponse("https://example.com/image.png");
        when(openAiImageModel.call(any(ImagePrompt.class))).thenReturn(mockImageResponse);

        // When
        imageGenerationService.generateImage(request);

        // Then
        verify(openAiImageModel, times(1)).call(any(ImagePrompt.class));
        verifyNoMoreInteractions(openAiImageModel);
    }

    @Test
    @DisplayName("Devrait propager les exceptions de l'API")
    void shouldPropagateAPIExceptions() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest(
                "Test prompt",
                "hd",
                "1024x1024"
        );

        when(openAiImageModel.call(any(ImagePrompt.class)))
                .thenThrow(new RuntimeException("API Error: Rate limit exceeded"));

        // When & Then
        assertThatThrownBy(() -> imageGenerationService.generateImage(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API Error: Rate limit exceeded");
    }

    @Test
    @DisplayName("Devrait générer des images différentes pour des prompts différents")
    void shouldGenerateDifferentImages_ForDifferentPrompts() {
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

        when(openAiImageModel.call(any(ImagePrompt.class)))
                .thenReturn(createMockImageResponse("https://example.com/image1.png"))
                .thenReturn(createMockImageResponse("https://example.com/image2.png"));

        // When
        ImageGenerationResponse response1 = imageGenerationService.generateImage(request1);
        ImageGenerationResponse response2 = imageGenerationService.generateImage(request2);

        // Then
        assertThat(response1.prompt()).isEqualTo("A sunset");
        assertThat(response2.prompt()).isEqualTo("A sunrise");
        verify(openAiImageModel, times(2)).call(any(ImagePrompt.class));
    }

    // ===== Méthodes utilitaires =====

    private ImageResponse createMockImageResponse(String imageUrl) {
        // Créer un mock de Image
        Image mockImage = mock(Image.class);
        when(mockImage.getUrl()).thenReturn(imageUrl);

        // Créer un mock de Generation (Spring AI 1.1.0)
        org.springframework.ai.model.ModelResult<Image> mockGeneration =
                new org.springframework.ai.model.ModelResult<>(mockImage);

        // Créer le mock de ImageResponse avec une liste de générations
        ImageResponse mockResponse = new ImageResponse(java.util.List.of(mockGeneration));

        return mockResponse;
    }
}