package ru.nuthatch.filteringutility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nuthatch.filteringutility.common.ExecuteParameters;
import ru.nuthatch.filteringutility.common.SetupParameters;
import ru.nuthatch.filteringutility.exceptions.NoInputFilePresentException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SetupParametersTests {

    private ExecuteParameters parameters = ExecuteParameters.getInstance();
    private SetupParameters setupParameters = new SetupParameters();

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
    void setupWithEmptyArgumentArrayTest() {

        String[] testArgs = {"-if", "file_01", "file_02"};
        Path path = Paths.get("./");

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals("integers.txt", parameters.getIntegersFileName());
        assertEquals("floats.txt", parameters.getFloatsFileName());
        assertEquals("strings.txt", parameters.getStringsFileName());
        assertFalse(parameters.isAddIfFileExists());
        assertFalse(parameters.isFullStatistic());
        assertFalse(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertEquals("file_01", parameters.getFileList().getFirst());
        assertEquals("file_01", parameters.getFileList().getFirst());
    }

    @Test
    void setupWithAllArgumentsTest() {

        String[] testArgs = {"-o", "/home/eev", "-p", "result_", "-a", "-f", "-s", "-if", "file_01", "file_02"};
        Path path = Paths.get("/home/eev");

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals("result_integers.txt", parameters.getIntegersFileName());
        assertEquals("result_floats.txt", parameters.getFloatsFileName());
        assertEquals("result_strings.txt", parameters.getStringsFileName());
        assertTrue(parameters.isAddIfFileExists());
        assertTrue(parameters.isFullStatistic());
        assertTrue(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertEquals("file_01", parameters.getFileList().getFirst());
        assertEquals("file_01", parameters.getFileList().getFirst());
    }

    @Test
    void setupWithAllMixedArgumentsTest() {

        String[] testArgs = {"-if", "file_01", "file_02", "-p", "result_", "-a", "-f", "-s", "-o", "/home/eev"};
        Path path = Paths.get("/home/eev");

        setupParameters.setup(testArgs);

        assertEquals(path, parameters.getResultPath());
        assertEquals("result_integers.txt", parameters.getIntegersFileName());
        assertEquals("result_floats.txt", parameters.getFloatsFileName());
        assertEquals("result_strings.txt", parameters.getStringsFileName());
        assertTrue(parameters.isAddIfFileExists());
        assertTrue(parameters.isFullStatistic());
        assertTrue(parameters.isShortStatistic());

        assertEquals(2, parameters.getFileList().size());
        assertEquals("file_01", parameters.getFileList().getFirst());
        assertEquals("file_01", parameters.getFileList().getFirst());
    }
}
