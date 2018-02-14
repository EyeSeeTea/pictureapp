package org.eyeseetea.malariacare.domain.usecase;

import static org.eyeseetea.malariacare.domain.usecase.GetUrlForWebViewsUseCase.OPEN_TYPE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GetUrlForWebViewsUseCaseShould {

    private GetUrlForWebViewsUseCase userCase;

    private String url = "";

    private static final String TRANSLATION_PARAMETER = "lang=";

    @Mock
    private ICredentialsRepository mockCredentialRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Context context = InstrumentationRegistry.getTargetContext();
        when(mockCredentialRepository.getOrganisationCredentials()).thenReturn(
                new Credentials("serverURL", "username", "password"));
        userCase = new GetUrlForWebViewsUseCase(context, mockCredentialRepository);
    }

    @After
    public void tearDown() throws Exception {
        url = "";
    }

    @Test
    public void include_translation_parameter() {

        whenExecuteUseCase();

        assertURLContainTranslationParameter();

    }

    private void assertURLContainTranslationParameter() {
        assertThat(url, containsString(TRANSLATION_PARAMETER));
    }

    private void whenExecuteUseCase() {
        userCase.execute(OPEN_TYPE, new GetUrlForWebViewsUseCase.Callback() {
            @Override
            public void onGetUrl(String url) {
                GetUrlForWebViewsUseCaseShould.this.url = url;

            }
        });
    }

}