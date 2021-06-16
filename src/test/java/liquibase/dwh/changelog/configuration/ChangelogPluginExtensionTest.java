package liquibase.dwh.changelog.configuration;

import org.gradle.internal.impldep.org.junit.Assert;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

//TODO это функциональный тест
class ChangelogPluginExtensionTest {

    public GradleRunner setup(String testConfig) throws IOException {
        Path testProjectDir = Files.createTempDirectory("ChangelogPlugin");
        File buildFile = testProjectDir.resolve("build.gradle").toFile();
        buildFile.createNewFile();
        try (FileWriter writer = new FileWriter(buildFile)) {
            writer.append((
                    " plugins {\n" +
                            "    id 'groovy'\n" +
                            "    id 'java-gradle-plugin'\n" +
                            "    id 'liquibase.dwh.changelog'\n" +
                            "}\n" +
                            "gradlePlugin {\n" +
                            "    plugins {\n" +
                            "        simplePlugin {\n" +
                            "            id = 'liquibase.dwh.changelog'\n" +
                            "            implementationClass = 'liquibase.dwh.changelog.ChangelogPlugin'\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n" +
                            testConfig
            )
                    .trim());
        }

        final File testKit = testProjectDir.resolve("testKit").toFile();
        testKit.mkdirs();
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withTestKitDir(testKit);
    }

//    @Test
//    public void checkTestSystem() throws IOException {
//        final String testConfig =
//                "changelog {\n" +
//                        "    dwhTest {\n" +
//                        "        system false\n" +
//                        "        type 'gp'\n" +
//                        "    }\n" +
//                        "    gpadminTest {\n" +
//                        "        system true\n" +
//                        "        type 'gp'\n" +
//                        "        location \"src/main/gp/databases/gpadmin\"\n" +
//                        "    }\n" +
//                        "}";
//
//        BuildResult result = setup(testConfig).withArguments("printConfig").build();
//        System.out.println(result.getOutput());
//        final List<String> database = new BufferedReader(new StringReader(result.getOutput())).lines().filter(s -> s.contains("Database")).collect(Collectors.toList());
//        Assert.assertTrue("gpadmin is not system", database.stream().filter(s -> s.contains("system=true")).findFirst().orElseThrow(() -> new RuntimeException("system not set true")).contains("name=gpadminTest"));
//        Assert.assertTrue("dwh is system", database.stream().filter(s -> s.contains("system=false")).findFirst().orElseThrow(() -> new RuntimeException("system not set false")).contains("name=dwhTest"));
//
//    }
//
//    @Test
//    public void checkTestBasic() throws IOException {
//        final String testConfig =
//                "changelog {\n" +
//                        "    oneTest {\n" +
//                        "        type 'gp'\n" +
//                        "    }\n" +
//                        "    twoTest {\n" +
//                        "        type 'gp'\n" +
//                        "    }\n" +
//                        "}";
//
//        BuildResult result = setup(testConfig).withArguments("printConfig").build();
//        System.out.println(result.getOutput());
//        final List<String> database = new BufferedReader(new StringReader(result.getOutput())).lines().filter(s -> s.contains("Database")).collect(Collectors.toList());
//        Assert.assertTrue("system not set false", database.stream().filter(s -> s.contains("name=oneTest")).findFirst().orElseThrow(() -> new RuntimeException("oneTest not found")).contains("system=false"));
//        Assert.assertTrue("system not set false", database.stream().filter(s -> s.contains("name=twoTest")).findFirst().orElseThrow(() -> new RuntimeException("twoTest not found")).contains("system=false"));
//
//    }

}