package ru.nuthatch.filteringutility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nuthatch.filteringutility.common.ExecuteParameters;
import ru.nuthatch.filteringutility.common.SetupParameters;
import ru.nuthatch.filteringutility.exceptions.NoInputFilePresentException;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SetupParametersTests {

    private ExecuteParameters parameters = ExecuteParameters.getInstance();
    private SetupParameters setupParameters = new SetupParameters();
    private final File sampleFileIn1 = new File("./sample_files/in1.txt");
    private final File sampleFileIn2 = new File("./sample_files/in2.txt");

    @BeforeEach
    void prepareTests() {
        parameters = ExecuteParameters.getInstance();
        setupParameters = new SetupParameters();
    }

    @AfterEach
    void dropParameters() {
        parameters.dropParameters();
    }

    @Test
    void setupWithNoInputFilePresentTest() {
        String[] testArgs = {"-o", "/home/eev", "-p", "result_", "-a", "-f", "-s"};

        Exception exception = assertThrows(NoInputFilePresentException.class,
                () -> setupParameters.setup(testArgs));
        assertEquals("Не заданы файлы с исходными данными. Дальнейшее выполнение невозможно",
                exception.getMessage());
    }

    @Test
    void setupWithWrongFilePrefixTest() {

        String[] testArgs = {"-p", "wrong*prefix?", "-if", "./sample_files/in1.txt", "./sample_files/in2.txt"};
        String path = Paths.get("./").toString();


        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals(new File(path, "integers.txt"), parameters.getIntegersFile());
        assertEquals(new File(path, "floats.txt"), parameters.getFloatsFile());
        assertEquals(new File(path,"strings.txt"), parameters.getStringsFile());
        assertFalse(parameters.isAppendIfFileExists());
        assertFalse(parameters.isFullStatistic());
        assertFalse(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertTrue(parameters.getFileList().contains(sampleFileIn1));
        assertTrue(parameters.getFileList().contains(sampleFileIn2));
    }

    @Test
    void setupWithWrongResultPathTest() {

        String[] testArgs = {"-o", "wrong*path?", "-if", "./sample_files/in1.txt", "./sample_files/in2.txt"};
        String path = Paths.get("./").toString();

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals(new File(path, "integers.txt"), parameters.getIntegersFile());
        assertEquals(new File(path, "floats.txt"), parameters.getFloatsFile());
        assertEquals(new File(path, "strings.txt"), parameters.getStringsFile());
        assertFalse(parameters.isAppendIfFileExists());
        assertFalse(parameters.isFullStatistic());
        assertFalse(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertTrue(parameters.getFileList().contains(sampleFileIn1));
        assertTrue(parameters.getFileList().contains(sampleFileIn2));
    }

    @Test
    void setupWithEmptyArgumentArrayTest() {

        String[] testArgs = {"-if", "./sample_files/in1.txt", "./sample_files/in2.txt"};
        String path = Paths.get("./").toString();

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals(new File(path, "integers.txt"), parameters.getIntegersFile());
        assertEquals(new File(path, "floats.txt"), parameters.getFloatsFile());
        assertEquals(new File(path, "strings.txt"), parameters.getStringsFile());
        assertFalse(parameters.isAppendIfFileExists());
        assertFalse(parameters.isFullStatistic());
        assertFalse(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertTrue(parameters.getFileList().contains(sampleFileIn1));
        assertTrue(parameters.getFileList().contains(sampleFileIn2));
    }

    @Test
    void setupWithAllArgumentsTest() {

        String[] testArgs = {"-o", "/home/eev", "-p", "result_", "-a", "-f", "-s",
                "-if", "./sample_files/in1.txt", "./sample_files/in2.txt"};
        String path = Paths.get("/home/eev").toString();

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals(new File(path, "result_integers.txt"), parameters.getIntegersFile());
        assertEquals(new File(path, "result_floats.txt"), parameters.getFloatsFile());
        assertEquals(new File(path, "result_strings.txt"), parameters.getStringsFile());
        assertTrue(parameters.isAppendIfFileExists());
        assertTrue(parameters.isFullStatistic());
        assertTrue(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertTrue(parameters.getFileList().contains(sampleFileIn1));
        assertTrue(parameters.getFileList().contains(sampleFileIn2));
    }

    @Test
    void setupWithAllMixedArgumentsTest() {

        String[] testArgs = {"-if", "./sample_files/in1.txt", "./sample_files/in2.txt",
                "-p", "result_", "-a", "-f", "-s", "-o", "/home/eev"};
        String path = Paths.get("/home/eev").toString();

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals(new File(path, "result_integers.txt"), parameters.getIntegersFile());
        assertEquals(new File(path, "result_floats.txt"), parameters.getFloatsFile());
        assertEquals(new File(path, "result_strings.txt"), parameters.getStringsFile());
        assertTrue(parameters.isAppendIfFileExists());
        assertTrue(parameters.isFullStatistic());
        assertTrue(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertTrue(parameters.getFileList().contains(sampleFileIn1));
        assertTrue(parameters.getFileList().contains(sampleFileIn2));
    }
}
