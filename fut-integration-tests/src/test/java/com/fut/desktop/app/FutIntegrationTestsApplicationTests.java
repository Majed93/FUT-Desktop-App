package com.fut.desktop.app;

import com.fut.api.fut.FutIOApplication;
import com.fut.desktop.app.futservice.FutServiceApplication;
import com.fut.desktop.app.futsimulator.FutSimulatorApplication;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {FutServiceApplication.class,
        FutIOApplication.class,
        FutSimulatorApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("INTEGRATION_TEST")
public class FutIntegrationTestsApplicationTests extends JUnitStories {

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void configureEmbedder() {
        configuredEmbedder().useMetaFilters(Collections.singletonList("-skip"));
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                // Produce a standard report on the console and as a text file.
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats()
                        .withFormats(Format.CONSOLE, Format.TXT, Format.HTML, Format.IDE_CONSOLE, Format.STATS)
                );
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        Configuration configuration = configuration();
        configuration.storyControls().doMetaByRow(true);

        return new SpringStepsFactory(configuration, applicationContext);
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()),
                "**/*.story", "");
        }
        }
