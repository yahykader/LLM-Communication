package com.example.Test_AI_LLM.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenAiImageProperties - Tests Unitaires")
class OpenAiImagePropertiesTest {

    @Test
    @DisplayName("Devrait avoir des valeurs par défaut correctes")
    void shouldHaveCorrectDefaultValues() {
        // Given & When
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // Then
        assertThat(properties.getModel()).isEqualTo("dall-e-3");
        assertThat(properties.getDefaultQuality()).isEqualTo("hd");
        assertThat(properties.getDefaultSize()).isEqualTo("1024x1024");
    }

    @Test
    @DisplayName("Devrait permettre de modifier le model")
    void shouldAllowModelModification() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setModel("dall-e-2");

        // Then
        assertThat(properties.getModel()).isEqualTo("dall-e-2");
    }

    @Test
    @DisplayName("Devrait permettre de modifier la qualité par défaut")
    void shouldAllowDefaultQualityModification() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setDefaultQuality("standard");

        // Then
        assertThat(properties.getDefaultQuality()).isEqualTo("standard");
    }

    @Test
    @DisplayName("Devrait permettre de modifier la taille par défaut")
    void shouldAllowDefaultSizeModification() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setDefaultSize("1792x1024");

        // Then
        assertThat(properties.getDefaultSize()).isEqualTo("1792x1024");
    }

    @Test
    @DisplayName("Devrait permettre de définir toutes les propriétés")
    void shouldAllowSettingAllProperties() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setModel("dall-e-2");
        properties.setDefaultQuality("standard");
        properties.setDefaultSize("512x512");

        // Then
        assertThat(properties.getModel()).isEqualTo("dall-e-2");
        assertThat(properties.getDefaultQuality()).isEqualTo("standard");
        assertThat(properties.getDefaultSize()).isEqualTo("512x512");
    }

    @Test
    @DisplayName("Devrait avoir un toString() lisible")
    void shouldHaveReadableToString() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        String toString = properties.toString();

        // Then
        assertThat(toString)
                .contains("OpenAiImageProperties")
                .contains("model=dall-e-3")
                .contains("defaultQuality=hd")
                .contains("defaultSize=1024x1024");
    }

    @Test
    @DisplayName("Devrait tester l'égalité entre deux instances identiques")
    void shouldBeEqual_WhenPropertiesHaveSameValues() {
        // Given
        OpenAiImageProperties properties1 = new OpenAiImageProperties();
        OpenAiImageProperties properties2 = new OpenAiImageProperties();

        // Then
        assertThat(properties1).isEqualTo(properties2);
        assertThat(properties1.hashCode()).isEqualTo(properties2.hashCode());
    }

    @Test
    @DisplayName("Devrait être différent quand les valeurs diffèrent")
    void shouldNotBeEqual_WhenPropertiesHaveDifferentValues() {
        // Given
        OpenAiImageProperties properties1 = new OpenAiImageProperties();
        OpenAiImageProperties properties2 = new OpenAiImageProperties();
        properties2.setModel("dall-e-2");

        // Then
        assertThat(properties1).isNotEqualTo(properties2);
    }

    @Test
    @DisplayName("Devrait accepter des valeurs null")
    void shouldAcceptNullValues() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setModel(null);
        properties.setDefaultQuality(null);
        properties.setDefaultSize(null);

        // Then
        assertThat(properties.getModel()).isNull();
        assertThat(properties.getDefaultQuality()).isNull();
        assertThat(properties.getDefaultSize()).isNull();
    }

    @Test
    @DisplayName("Devrait accepter des chaînes vides")
    void shouldAcceptEmptyStrings() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();

        // When
        properties.setModel("");
        properties.setDefaultQuality("");
        properties.setDefaultSize("");

        // Then
        assertThat(properties.getModel()).isEmpty();
        assertThat(properties.getDefaultQuality()).isEmpty();
        assertThat(properties.getDefaultSize()).isEmpty();
    }

    // ===== Tests d'intégration avec Spring Context =====

    @SpringBootTest(classes = OpenAiImageProperties.class)
    @EnableConfigurationProperties(OpenAiImageProperties.class)
    @TestPropertySource(properties = {
            "openai.image.model=dall-e-3",
            "openai.image.default-quality=hd",
            "openai.image.default-size=1024x1024"
    })
    @DisplayName("Tests d'intégration Spring - Valeurs par défaut")
    static class SpringIntegrationDefaultTest {

        @Autowired
        private OpenAiImageProperties properties;

        @Test
        @DisplayName("Devrait charger les valeurs par défaut depuis application.properties")
        void shouldLoadDefaultValuesFromProperties() {
            assertThat(properties).isNotNull();
            assertThat(properties.getModel()).isEqualTo("dall-e-3");
            assertThat(properties.getDefaultQuality()).isEqualTo("hd");
            assertThat(properties.getDefaultSize()).isEqualTo("1024x1024");
        }
    }

    @SpringBootTest(classes = OpenAiImageProperties.class)
    @EnableConfigurationProperties(OpenAiImageProperties.class)
    @TestPropertySource(properties = {
            "openai.image.model=dall-e-2",
            "openai.image.default-quality=standard",
            "openai.image.default-size=512x512"
    })
    @DisplayName("Tests d'intégration Spring - Valeurs personnalisées")
    static class SpringIntegrationCustomTest {

        @Autowired
        private OpenAiImageProperties properties;

        @Test
        @DisplayName("Devrait charger des valeurs personnalisées depuis application.properties")
        void shouldLoadCustomValuesFromProperties() {
            assertThat(properties).isNotNull();
            assertThat(properties.getModel()).isEqualTo("dall-e-2");
            assertThat(properties.getDefaultQuality()).isEqualTo("standard");
            assertThat(properties.getDefaultSize()).isEqualTo("512x512");
        }
    }

    @SpringBootTest(classes = OpenAiImageProperties.class)
    @EnableConfigurationProperties(OpenAiImageProperties.class)
    @TestPropertySource(properties = {
            "openai.image.model=gpt-4-vision"
    })
    @DisplayName("Tests d'intégration Spring - Valeurs partielles")
    static class SpringIntegrationPartialTest {

        @Autowired
        private OpenAiImageProperties properties;

        @Test
        @DisplayName("Devrait utiliser les valeurs par défaut pour les propriétés non définies")
        void shouldUseDefaultValues_ForUndefinedProperties() {
            assertThat(properties).isNotNull();
            assertThat(properties.getModel()).isEqualTo("gpt-4-vision");
            assertThat(properties.getDefaultQuality()).isEqualTo("hd"); // valeur par défaut
            assertThat(properties.getDefaultSize()).isEqualTo("1024x1024"); // valeur par défaut
        }
    }

    @SpringBootTest(classes = OpenAiImageProperties.class)
    @EnableConfigurationProperties(OpenAiImageProperties.class)
    @TestPropertySource(properties = {
            "openai.image.model=",
            "openai.image.default-quality=",
            "openai.image.default-size="
    })
    @DisplayName("Tests d'intégration Spring - Valeurs vides")
    static class SpringIntegrationEmptyTest {

        @Autowired
        private OpenAiImageProperties properties;

        @Test
        @DisplayName("Devrait gérer les valeurs vides depuis application.properties")
        void shouldHandleEmptyValues_FromProperties() {
            assertThat(properties).isNotNull();
            assertThat(properties.getModel()).isEmpty();
            assertThat(properties.getDefaultQuality()).isEmpty();
            assertThat(properties.getDefaultSize()).isEmpty();
        }
    }

    // ===== Tests avec prefix alternatif =====

    @SpringBootTest(classes = OpenAiImageProperties.class)
    @EnableConfigurationProperties(OpenAiImageProperties.class)
    @TestPropertySource(properties = {
            "openai.image.model=dall-e-3",
            "openai.image.defaultQuality=hd", // camelCase
            "openai.image.defaultSize=1024x1024" // camelCase
    })
    @DisplayName("Tests d'intégration Spring - Notation camelCase")
    static class SpringIntegrationCamelCaseTest {

        @Autowired
        private OpenAiImageProperties properties;

        @Test
        @DisplayName("Devrait charger les propriétés avec notation camelCase")
        void shouldLoadPropertiesWithCamelCase() {
            assertThat(properties).isNotNull();
            assertThat(properties.getModel()).isEqualTo("dall-e-3");
            assertThat(properties.getDefaultQuality()).isEqualTo("hd");
            assertThat(properties.getDefaultSize()).isEqualTo("1024x1024");
        }
    }

    // ===== Test de validation des valeurs =====

    @Test
    @DisplayName("Devrait accepter différents modèles d'image")
    void shouldAcceptDifferentImageModels() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();
        String[] models = {"dall-e-2", "dall-e-3", "stable-diffusion", "midjourney"};

        // When & Then
        for (String model : models) {
            properties.setModel(model);
            assertThat(properties.getModel()).isEqualTo(model);
        }
    }

    @Test
    @DisplayName("Devrait accepter différentes qualités")
    void shouldAcceptDifferentQualities() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();
        String[] qualities = {"standard", "hd", "ultra-hd", "low"};

        // When & Then
        for (String quality : qualities) {
            properties.setDefaultQuality(quality);
            assertThat(properties.getDefaultQuality()).isEqualTo(quality);
        }
    }

    @Test
    @DisplayName("Devrait accepter différentes tailles")
    void shouldAcceptDifferentSizes() {
        // Given
        OpenAiImageProperties properties = new OpenAiImageProperties();
        String[] sizes = {"256x256", "512x512", "1024x1024", "1792x1024", "1024x1792"};

        // When & Then
        for (String size : sizes) {
            properties.setDefaultSize(size);
            assertThat(properties.getDefaultSize()).isEqualTo(size);
        }
    }
}
